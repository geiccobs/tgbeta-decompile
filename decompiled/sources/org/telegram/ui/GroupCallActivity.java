package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.voip.NativeInstance;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GroupCallInvitedCell;
import org.telegram.ui.Cells.GroupCallTextCell;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FillLastGridLayoutManager;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.GroupCallRecordAlert;
import org.telegram.ui.Components.GroupVoipInviteAlert;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.Components.voip.GroupCallGridCell;
import org.telegram.ui.Components.voip.GroupCallMiniTextureView;
import org.telegram.ui.Components.voip.GroupCallRenderersContainer;
import org.telegram.ui.Components.voip.GroupCallStatusIcon;
import org.telegram.ui.Components.voip.PrivateVideoPreviewDialog;
import org.telegram.ui.Components.voip.RTMPStreamPipOverlay;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.PinchToZoomHelper;
/* loaded from: classes4.dex */
public class GroupCallActivity extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
    public static final Property<GroupCallActivity, Float> COLOR_PROGRESS = new AnimationProperties.FloatProperty<GroupCallActivity>("colorProgress") { // from class: org.telegram.ui.GroupCallActivity.2
        public void setValue(GroupCallActivity object, float value) {
            object.setColorProgress(value);
        }

        public Float get(GroupCallActivity object) {
            return Float.valueOf(object.getColorProgress());
        }
    };
    public static final float MAX_AMPLITUDE = 8500.0f;
    private static final int MUTE_BUTTON_STATE_CANCEL_REMINDER = 7;
    private static final int MUTE_BUTTON_STATE_CONNECTING = 3;
    private static final int MUTE_BUTTON_STATE_MUTE = 1;
    private static final int MUTE_BUTTON_STATE_MUTED_BY_ADMIN = 2;
    private static final int MUTE_BUTTON_STATE_RAISED_HAND = 4;
    private static final int MUTE_BUTTON_STATE_SET_REMINDER = 6;
    private static final int MUTE_BUTTON_STATE_START_NOW = 5;
    private static final int MUTE_BUTTON_STATE_UNMUTE = 0;
    public static final int TABLET_LIST_SIZE = 320;
    public static final long TRANSITION_DURATION = 350;
    private static final int admin_can_speak_item = 2;
    public static int currentScreenOrientation = 0;
    private static final int edit_item = 6;
    private static final int eveyone_can_speak_item = 1;
    public static GroupCallActivity groupCallInstance = null;
    public static boolean groupCallUiVisible = false;
    public static boolean isLandscapeMode = false;
    public static boolean isTabletMode = false;
    private static final int leave_item = 4;
    private static final int noise_item = 11;
    public static boolean paused = false;
    private static final int permission_item = 7;
    private static final int screen_capture_item = 9;
    private static final int share_invite_link_item = 3;
    private static final int sound_item = 10;
    private static final int start_record_item = 5;
    private static final int user_item = 8;
    private static final int user_item_gap = 0;
    private View accountGap;
    private AccountInstance accountInstance;
    private AccountSelectCell accountSelectCell;
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarBackground;
    private View actionBarShadow;
    ObjectAnimator additionalSubtitleYAnimator;
    private ActionBarMenuSubItem adminItem;
    private float amplitude;
    private float animateAmplitudeDiff;
    boolean animateButtonsOnNextLayout;
    private float animateToAmplitude;
    private boolean anyEnterEventSent;
    private final AvatarPreviewPagerIndicator avatarPagerIndicator;
    private final FrameLayout avatarPreviewContainer;
    private boolean avatarPriviewTransitionInProgress;
    AvatarUpdaterDelegate avatarUpdaterDelegate;
    private boolean avatarsPreviewShowed;
    private final ProfileGalleryView avatarsViewPager;
    private int backgroundColor;
    private RLottieDrawable bigMicDrawable;
    private final BlobDrawable bigWaveDrawable;
    private View blurredView;
    private GradientDrawable buttonsBackgroundGradient;
    private final View buttonsBackgroundGradientView;
    private final View buttonsBackgroundGradientView2;
    private FrameLayout buttonsContainer;
    private int buttonsVisibility;
    public ChatObject.Call call;
    private boolean callInitied;
    private VoIPToggleButton cameraButton;
    private float cameraButtonScale;
    private boolean changingPermissions;
    private float colorProgress;
    private boolean contentFullyOverlayed;
    private long creatingServiceTime;
    ImageUpdater currentAvatarUpdater;
    private int currentCallState;
    public TLRPC.Chat currentChat;
    private ViewGroup currentOptionsLayout;
    private WeavingState currentState;
    private boolean delayedGroupCallUpdated;
    private boolean drawSpeakingSubtitle;
    public boolean drawingForBlur;
    private ActionBarMenuSubItem editTitleItem;
    private boolean enterEventSent;
    private ActionBarMenuSubItem everyoneItem;
    private ValueAnimator expandAnimator;
    private ImageView expandButton;
    private ValueAnimator expandSizeAnimator;
    private VoIPToggleButton flipButton;
    private final RLottieDrawable flipIcon;
    private int flipIconCurrentEndFrame;
    GroupCallFullscreenAdapter fullscreenAdapter;
    private final DefaultItemAnimator fullscreenListItemAnimator;
    ValueAnimator fullscreenModeAnimator;
    RecyclerListView fullscreenUsersListView;
    private GroupVoipInviteAlert groupVoipInviteAlert;
    private RLottieDrawable handDrawables;
    private boolean hasScrimAnchorView;
    private boolean hasVideo;
    private ActionBarMenuSubItem inviteItem;
    private GroupCallItemAnimator itemAnimator;
    private long lastUpdateTime;
    private FillLastGridLayoutManager layoutManager;
    private VoIPToggleButton leaveButton;
    private ActionBarMenuSubItem leaveItem;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private final LinearLayout menuItemsContainer;
    private ImageView minimizeButton;
    private RLottieImageView muteButton;
    private ValueAnimator muteButtonAnimator;
    private ActionBarMenuSubItem noiseItem;
    private int oldAddMemberRow;
    private int oldCount;
    private int oldInvitedEndRow;
    private int oldInvitedStartRow;
    private int oldUsersEndRow;
    private int oldUsersStartRow;
    private int oldUsersVideoEndRow;
    private int oldUsersVideoStartRow;
    private int oldVideoDividerRow;
    private int oldVideoNotAvailableRow;
    private ActionBarMenuItem otherItem;
    private LaunchActivity parentActivity;
    private ActionBarMenuSubItem permissionItem;
    PinchToZoomHelper pinchToZoomHelper;
    private ActionBarMenuItem pipItem;
    private boolean playingHandAnimation;
    private boolean pressed;
    private WeavingState prevState;
    PrivateVideoPreviewDialog previewDialog;
    private boolean previewTextureTransitionEnabled;
    private float progressToAvatarPreview;
    float progressToHideUi;
    private RadialGradient radialGradient;
    private final Matrix radialMatrix;
    private final Paint radialPaint;
    private RadialProgressView radialProgressView;
    private RecordCallDrawable recordCallDrawable;
    private HintView recordHintView;
    private ActionBarMenuSubItem recordItem;
    private HintView reminderHintView;
    private GroupCallRenderersContainer renderersContainer;
    ViewTreeObserver.OnPreDrawListener requestFullscreenListener;
    private ValueAnimator scheduleAnimator;
    private TextView scheduleButtonTextView;
    private float scheduleButtonsScale;
    private boolean scheduleHasFewPeers;
    private TextView scheduleInfoTextView;
    private TLRPC.InputPeer schedulePeer;
    private int scheduleStartAt;
    private SimpleTextView scheduleStartAtTextView;
    private SimpleTextView scheduleStartInTextView;
    private SimpleTextView scheduleTimeTextView;
    private LinearLayout scheduleTimerContainer;
    private boolean scheduled;
    private String scheduledHash;
    private ActionBarMenuSubItem screenItem;
    private ActionBarMenuItem screenShareItem;
    private AnimatorSet scrimAnimatorSet;
    private GroupCallFullscreenAdapter.GroupCallUserCell scrimFullscreenView;
    private GroupCallGridCell scrimGridView;
    private Paint scrimPaint;
    private View scrimPopupLayout;
    private ActionBarPopupWindow scrimPopupWindow;
    private GroupCallMiniTextureView scrimRenderer;
    private GroupCallUserCell scrimView;
    private boolean scrimViewAttached;
    private float scrollOffsetY;
    private TLRPC.Peer selfPeer;
    private Drawable shadowDrawable;
    private ShareAlert shareAlert;
    private float showLightingProgress;
    private float showWavesProgress;
    private VoIPToggleButton soundButton;
    private float soundButtonScale;
    private ActionBarMenuSubItem soundItem;
    private View soundItemDivider;
    private final GridLayoutManager.SpanSizeLookup spanSizeLookup;
    private boolean startingGroupCall;
    ObjectAnimator subtitleYAnimator;
    private float switchToButtonInt2;
    private float switchToButtonProgress;
    GroupCallTabletGridAdapter tabletGridAdapter;
    RecyclerListView tabletVideoGridView;
    private final BlobDrawable tinyWaveDrawable;
    private AudioPlayerAlert.ClippingTextViewSwitcher titleTextView;
    private Runnable updateCallRecordRunnable;
    private boolean useBlur;
    private TLObject userSwitchObject;
    private Boolean wasNotInLayoutFullscreen;
    private TextView[] muteLabel = new TextView[2];
    private UndoView[] undoView = new UndoView[2];
    public final ArrayList<ChatObject.VideoParticipant> visibleVideoParticipants = new ArrayList<>();
    private RectF rect = new RectF();
    private Paint listViewBackgroundPaint = new Paint(1);
    private ArrayList<TLRPC.TL_groupCallParticipant> oldParticipants = new ArrayList<>();
    private ArrayList<ChatObject.VideoParticipant> oldVideoParticipants = new ArrayList<>();
    private ArrayList<Long> oldInvited = new ArrayList<>();
    private int muteButtonState = 0;
    private boolean animatingToFullscreenExpand = false;
    private Paint paint = new Paint(7);
    private Paint paintTmp = new Paint(7);
    private Paint leaveBackgroundPaint = new Paint(1);
    private WeavingState[] states = new WeavingState[8];
    private float switchProgress = 1.0f;
    private int shaderBitmapSize = 200;
    private boolean invalidateColors = true;
    private final int[] colorsTmp = new int[3];
    private final ArrayList<GroupCallMiniTextureView> attachedRenderers = new ArrayList<>();
    private final ArrayList<GroupCallMiniTextureView> attachedRenderersTmp = new ArrayList<>();
    private Boolean wasExpandBigSize = true;
    public CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
    public final ArrayList<GroupCallStatusIcon> statusIconPool = new ArrayList<>();
    private HashMap<View, Float> buttonsAnimationParamsX = new HashMap<>();
    private HashMap<View, Float> buttonsAnimationParamsY = new HashMap<>();
    private Runnable onUserLeaveHintListener = new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda30
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallActivity.this.onUserLeaveHint();
        }
    };
    private Runnable updateSchedeulRunnable = new Runnable() { // from class: org.telegram.ui.GroupCallActivity.1
        @Override // java.lang.Runnable
        public void run() {
            int time;
            if (GroupCallActivity.this.scheduleTimeTextView == null || GroupCallActivity.this.isDismissed()) {
                return;
            }
            if (GroupCallActivity.this.call == null) {
                time = GroupCallActivity.this.scheduleStartAt;
            } else {
                time = GroupCallActivity.this.call.call.schedule_date;
            }
            if (time != 0) {
                int diff = time - GroupCallActivity.this.accountInstance.getConnectionsManager().getCurrentTime();
                if (diff >= 86400) {
                    GroupCallActivity.this.scheduleTimeTextView.setText(LocaleController.formatPluralString("Days", Math.round(diff / 86400.0f), new Object[0]));
                } else {
                    GroupCallActivity.this.scheduleTimeTextView.setText(AndroidUtilities.formatFullDuration(Math.abs(diff)));
                    if (diff < 0 && GroupCallActivity.this.scheduleStartInTextView.getTag() == null) {
                        GroupCallActivity.this.scheduleStartInTextView.setTag(1);
                        GroupCallActivity.this.scheduleStartInTextView.setText(LocaleController.getString("VoipChatLateBy", R.string.VoipChatLateBy));
                    }
                }
                GroupCallActivity.this.scheduleStartAtTextView.setText(LocaleController.formatStartsTime(time, 3));
                AndroidUtilities.runOnUIThread(GroupCallActivity.this.updateSchedeulRunnable, 1000L);
            }
        }
    };
    private Runnable unmuteRunnable = GroupCallActivity$$ExternalSyntheticLambda42.INSTANCE;
    private Runnable pressRunnable = new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda26
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallActivity.this.m3474lambda$new$1$orgtelegramuiGroupCallActivity();
        }
    };
    LongSparseIntArray visiblePeerIds = new LongSparseIntArray();
    private int[] gradientColors = new int[2];
    private boolean listViewVideoVisibility = true;
    private String[] invites = new String[2];
    private int popupAnimationIndex = -1;
    private DiffUtil.Callback diffUtilsCallback = new DiffUtil.Callback() { // from class: org.telegram.ui.GroupCallActivity.58
        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getOldListSize() {
            return GroupCallActivity.this.oldCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getNewListSize() {
            return GroupCallActivity.this.listAdapter.rowsCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (GroupCallActivity.this.listAdapter.addMemberRow >= 0) {
                if (oldItemPosition == GroupCallActivity.this.oldAddMemberRow && newItemPosition == GroupCallActivity.this.listAdapter.addMemberRow) {
                    return true;
                }
                if ((oldItemPosition == GroupCallActivity.this.oldAddMemberRow && newItemPosition != GroupCallActivity.this.listAdapter.addMemberRow) || (oldItemPosition != GroupCallActivity.this.oldAddMemberRow && newItemPosition == GroupCallActivity.this.listAdapter.addMemberRow)) {
                    return false;
                }
            }
            if (GroupCallActivity.this.listAdapter.videoNotAvailableRow >= 0) {
                if (oldItemPosition == GroupCallActivity.this.oldVideoNotAvailableRow && newItemPosition == GroupCallActivity.this.listAdapter.videoNotAvailableRow) {
                    return true;
                }
                if ((oldItemPosition == GroupCallActivity.this.oldVideoNotAvailableRow && newItemPosition != GroupCallActivity.this.listAdapter.videoNotAvailableRow) || (oldItemPosition != GroupCallActivity.this.oldVideoNotAvailableRow && newItemPosition == GroupCallActivity.this.listAdapter.videoNotAvailableRow)) {
                    return false;
                }
            }
            if (GroupCallActivity.this.listAdapter.videoGridDividerRow >= 0 && GroupCallActivity.this.listAdapter.videoGridDividerRow == newItemPosition && oldItemPosition == GroupCallActivity.this.oldVideoDividerRow) {
                return true;
            }
            if (oldItemPosition == GroupCallActivity.this.oldCount - 1 && newItemPosition == GroupCallActivity.this.listAdapter.rowsCount - 1) {
                return true;
            }
            if (oldItemPosition == GroupCallActivity.this.oldCount - 1 || newItemPosition == GroupCallActivity.this.listAdapter.rowsCount - 1) {
                return false;
            }
            if (newItemPosition >= GroupCallActivity.this.listAdapter.usersVideoGridStartRow && newItemPosition < GroupCallActivity.this.listAdapter.usersVideoGridEndRow && oldItemPosition >= GroupCallActivity.this.oldUsersVideoStartRow && oldItemPosition < GroupCallActivity.this.oldUsersVideoEndRow) {
                ChatObject.VideoParticipant oldItem = (ChatObject.VideoParticipant) GroupCallActivity.this.oldVideoParticipants.get(oldItemPosition - GroupCallActivity.this.oldUsersVideoStartRow);
                ChatObject.VideoParticipant newItem = GroupCallActivity.this.visibleVideoParticipants.get(newItemPosition - GroupCallActivity.this.listAdapter.usersVideoGridStartRow);
                return oldItem.equals(newItem);
            } else if (newItemPosition >= GroupCallActivity.this.listAdapter.usersStartRow && newItemPosition < GroupCallActivity.this.listAdapter.usersEndRow && oldItemPosition >= GroupCallActivity.this.oldUsersStartRow && oldItemPosition < GroupCallActivity.this.oldUsersEndRow) {
                TLRPC.TL_groupCallParticipant oldItem2 = (TLRPC.TL_groupCallParticipant) GroupCallActivity.this.oldParticipants.get(oldItemPosition - GroupCallActivity.this.oldUsersStartRow);
                TLRPC.TL_groupCallParticipant newItem2 = GroupCallActivity.this.call.visibleParticipants.get(newItemPosition - GroupCallActivity.this.listAdapter.usersStartRow);
                if (MessageObject.getPeerId(oldItem2.peer) != MessageObject.getPeerId(newItem2.peer)) {
                    return false;
                }
                return oldItemPosition == newItemPosition || oldItem2.lastActiveDate == ((long) oldItem2.active_date);
            } else if (newItemPosition < GroupCallActivity.this.listAdapter.invitedStartRow || newItemPosition >= GroupCallActivity.this.listAdapter.invitedEndRow || oldItemPosition < GroupCallActivity.this.oldInvitedStartRow || oldItemPosition >= GroupCallActivity.this.oldInvitedEndRow) {
                return false;
            } else {
                Long newItem3 = GroupCallActivity.this.call.invitedUsers.get(newItemPosition - GroupCallActivity.this.listAdapter.invitedStartRow);
                return ((Long) GroupCallActivity.this.oldInvited.get(oldItemPosition - GroupCallActivity.this.oldInvitedStartRow)).equals(newItem3);
            }
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true;
        }
    };

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    static /* synthetic */ float access$10516(GroupCallActivity x0, float x1) {
        float f = x0.amplitude + x1;
        x0.amplitude = f;
        return f;
    }

    static /* synthetic */ float access$13116(GroupCallActivity x0, float x1) {
        float f = x0.switchProgress + x1;
        x0.switchProgress = f;
        return f;
    }

    static /* synthetic */ float access$13716(GroupCallActivity x0, float x1) {
        float f = x0.showWavesProgress + x1;
        x0.showWavesProgress = f;
        return f;
    }

    static /* synthetic */ float access$13724(GroupCallActivity x0, float x1) {
        float f = x0.showWavesProgress - x1;
        x0.showWavesProgress = f;
        return f;
    }

    static /* synthetic */ float access$13816(GroupCallActivity x0, float x1) {
        float f = x0.showLightingProgress + x1;
        x0.showLightingProgress = f;
        return f;
    }

    static /* synthetic */ float access$13824(GroupCallActivity x0, float x1) {
        float f = x0.showLightingProgress - x1;
        x0.showLightingProgress = f;
        return f;
    }

    public static /* synthetic */ void lambda$new$0() {
        if (VoIPService.getSharedInstance() == null) {
            return;
        }
        VoIPService.getSharedInstance().setMicMute(false, true, false);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3474lambda$new$1$orgtelegramuiGroupCallActivity() {
        if (this.call == null || !this.scheduled || VoIPService.getSharedInstance() == null) {
            return;
        }
        this.muteButton.performHapticFeedback(3, 2);
        updateMuteButton(1, true);
        AndroidUtilities.runOnUIThread(this.unmuteRunnable, 80L);
        this.scheduled = false;
        this.pressed = true;
    }

    /* loaded from: classes4.dex */
    public static class SmallRecordCallDrawable extends Drawable {
        private long lastUpdateTime;
        private View parentView;
        private int state;
        private Paint paint2 = new Paint(1);
        private float alpha = 1.0f;

        public SmallRecordCallDrawable(View parent) {
            this.parentView = parent;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(24.0f);
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(24.0f);
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            int cy;
            int cx = getBounds().centerX();
            int cy2 = getBounds().centerY();
            if (this.parentView instanceof SimpleTextView) {
                cy = cy2 + AndroidUtilities.dp(1.0f);
                cx -= AndroidUtilities.dp(3.0f);
            } else {
                cy = cy2 + AndroidUtilities.dp(2.0f);
            }
            this.paint2.setColor(-1147527);
            this.paint2.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle(cx, cy, AndroidUtilities.dp(4.0f), this.paint2);
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastUpdateTime = newTime;
            int i = this.state;
            if (i == 0) {
                float f = this.alpha + (((float) dt) / 2000.0f);
                this.alpha = f;
                if (f >= 1.0f) {
                    this.alpha = 1.0f;
                    this.state = 1;
                }
            } else if (i == 1) {
                float f2 = this.alpha - (((float) dt) / 2000.0f);
                this.alpha = f2;
                if (f2 < 0.5f) {
                    this.alpha = 0.5f;
                    this.state = 0;
                }
            }
            this.parentView.invalidate();
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

    /* loaded from: classes4.dex */
    public static class RecordCallDrawable extends Drawable {
        private long lastUpdateTime;
        private View parentView;
        private boolean recording;
        private int state;
        private Paint paint = new Paint(1);
        private Paint paint2 = new Paint(1);
        private float alpha = 1.0f;

        public RecordCallDrawable() {
            this.paint.setColor(-1);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(AndroidUtilities.dp(1.5f));
        }

        public void setParentView(View view) {
            this.parentView = view;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(24.0f);
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(24.0f);
        }

        public boolean isRecording() {
            return this.recording;
        }

        public void setRecording(boolean value) {
            this.recording = value;
            this.alpha = 1.0f;
            invalidateSelf();
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            int cx = getBounds().centerX();
            int cy = getBounds().centerY();
            canvas.drawCircle(cx, cy, AndroidUtilities.dp(10.0f), this.paint);
            this.paint2.setColor(this.recording ? -1147527 : -1);
            this.paint2.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle(cx, cy, AndroidUtilities.dp(5.0f), this.paint2);
            if (this.recording) {
                long newTime = SystemClock.elapsedRealtime();
                long dt = newTime - this.lastUpdateTime;
                if (dt > 17) {
                    dt = 17;
                }
                this.lastUpdateTime = newTime;
                int i = this.state;
                if (i == 0) {
                    float f = this.alpha + (((float) dt) / 2000.0f);
                    this.alpha = f;
                    if (f >= 1.0f) {
                        this.alpha = 1.0f;
                        this.state = 1;
                    }
                } else if (i == 1) {
                    float f2 = this.alpha - (((float) dt) / 2000.0f);
                    this.alpha = f2;
                    if (f2 < 0.5f) {
                        this.alpha = 0.5f;
                        this.state = 0;
                    }
                }
                this.parentView.invalidate();
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

    /* loaded from: classes4.dex */
    public class VolumeSlider extends FrameLayout {
        private boolean captured;
        private int currentColor;
        private TLRPC.TL_groupCallParticipant currentParticipant;
        private double currentProgress;
        private boolean dragging;
        private RLottieImageView imageView;
        private long lastUpdateTime;
        private int oldColor;
        private float sx;
        private float sy;
        private TextView textView;
        private int thumbX;
        private Paint paint = new Paint(1);
        private Paint paint2 = new Paint(1);
        private Path path = new Path();
        private float[] radii = new float[8];
        private RectF rect = new RectF();
        private float[] volumeAlphas = new float[3];
        private float colorChangeProgress = 1.0f;
        private RLottieDrawable speakerDrawable = new RLottieDrawable(R.raw.speaker, "2131558536", AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, null);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public VolumeSlider(Context context, TLRPC.TL_groupCallParticipant participant) {
            super(context);
            int p;
            GroupCallActivity.this = r20;
            setWillNotDraw(false);
            this.currentParticipant = participant;
            this.currentProgress = ChatObject.getParticipantVolume(participant) / 20000.0f;
            setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setAnimation(this.speakerDrawable);
            RLottieImageView rLottieImageView2 = this.imageView;
            double d = this.currentProgress;
            double d2 = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            rLottieImageView2.setTag(d == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 1 : null);
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(-2, 40.0f, (LocaleController.isRTL ? 5 : 3) | 16, 0.0f, 0.0f, 0.0f, 0.0f));
            this.speakerDrawable.setCustomEndFrame(this.currentProgress == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 17 : 34);
            RLottieDrawable rLottieDrawable = this.speakerDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(3);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
            this.textView.setTextSize(1, 16.0f);
            double participantVolume = ChatObject.getParticipantVolume(this.currentParticipant);
            Double.isNaN(participantVolume);
            double vol = participantVolume / 100.0d;
            TextView textView2 = this.textView;
            Locale locale = Locale.US;
            Object[] objArr = new Object[1];
            objArr[0] = Integer.valueOf((int) (vol > FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? Math.max(vol, 1.0d) : d2));
            textView2.setText(String.format(locale, "%d%%", objArr));
            this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(43.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(43.0f) : 0, 0);
            addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i) | 16));
            this.paint2.setStyle(Paint.Style.STROKE);
            this.paint2.setStrokeWidth(AndroidUtilities.dp(1.5f));
            this.paint2.setStrokeCap(Paint.Cap.ROUND);
            this.paint2.setColor(-1);
            double participantVolume2 = ChatObject.getParticipantVolume(this.currentParticipant);
            Double.isNaN(participantVolume2);
            int percent = (int) (participantVolume2 / 100.0d);
            int a = 0;
            while (true) {
                float[] fArr = this.volumeAlphas;
                if (a < fArr.length) {
                    if (a == 0) {
                        p = 0;
                    } else if (a == 1) {
                        p = 50;
                    } else {
                        p = 150;
                    }
                    if (percent > p) {
                        fArr[a] = 1.0f;
                    } else {
                        fArr[a] = 0.0f;
                    }
                    a++;
                } else {
                    return;
                }
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED));
            double size = View.MeasureSpec.getSize(widthMeasureSpec);
            double d = this.currentProgress;
            Double.isNaN(size);
            this.thumbX = (int) (size * d);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return onTouch(ev);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return onTouch(event);
        }

        boolean onTouch(MotionEvent ev) {
            if (ev.getAction() == 0) {
                this.sx = ev.getX();
                this.sy = ev.getY();
                return true;
            }
            if (ev.getAction() == 1 || ev.getAction() == 3) {
                this.captured = false;
                if (ev.getAction() == 1) {
                    if (Math.abs(ev.getY() - this.sy) < ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                        int x = (int) ev.getX();
                        this.thumbX = x;
                        if (x < 0) {
                            this.thumbX = 0;
                        } else if (x > getMeasuredWidth()) {
                            this.thumbX = getMeasuredWidth();
                        }
                        this.dragging = true;
                    }
                }
                if (this.dragging) {
                    if (ev.getAction() == 1) {
                        double d = this.thumbX;
                        double measuredWidth = getMeasuredWidth();
                        Double.isNaN(d);
                        Double.isNaN(measuredWidth);
                        onSeekBarDrag(d / measuredWidth, true);
                    }
                    this.dragging = false;
                    invalidate();
                    return true;
                }
            } else if (ev.getAction() == 2) {
                if (!this.captured) {
                    ViewConfiguration vc = ViewConfiguration.get(getContext());
                    if (Math.abs(ev.getY() - this.sy) <= vc.getScaledTouchSlop() && Math.abs(ev.getX() - this.sx) > vc.getScaledTouchSlop()) {
                        this.captured = true;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        if (ev.getY() >= 0.0f && ev.getY() <= getMeasuredHeight()) {
                            int x2 = (int) ev.getX();
                            this.thumbX = x2;
                            if (x2 < 0) {
                                this.thumbX = 0;
                            } else if (x2 > getMeasuredWidth()) {
                                this.thumbX = getMeasuredWidth();
                            }
                            this.dragging = true;
                            invalidate();
                            return true;
                        }
                    }
                } else if (this.dragging) {
                    int x3 = (int) ev.getX();
                    this.thumbX = x3;
                    if (x3 < 0) {
                        this.thumbX = 0;
                    } else if (x3 > getMeasuredWidth()) {
                        this.thumbX = getMeasuredWidth();
                    }
                    double d2 = this.thumbX;
                    double measuredWidth2 = getMeasuredWidth();
                    Double.isNaN(d2);
                    Double.isNaN(measuredWidth2);
                    onSeekBarDrag(d2 / measuredWidth2, false);
                    invalidate();
                    return true;
                }
            }
            return false;
        }

        private void onSeekBarDrag(double progress, boolean finalMove) {
            if (VoIPService.getSharedInstance() == null) {
                return;
            }
            this.currentProgress = progress;
            this.currentParticipant.volume = (int) (20000.0d * progress);
            int i = 0;
            this.currentParticipant.volume_by_admin = false;
            this.currentParticipant.flags |= 128;
            double participantVolume = ChatObject.getParticipantVolume(this.currentParticipant);
            Double.isNaN(participantVolume);
            double vol = participantVolume / 100.0d;
            TextView textView = this.textView;
            Locale locale = Locale.US;
            Object[] objArr = new Object[1];
            objArr[0] = Integer.valueOf((int) (vol > FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? Math.max(vol, 1.0d) : 0.0d));
            textView.setText(String.format(locale, "%d%%", objArr));
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.currentParticipant;
            sharedInstance.setParticipantVolume(tL_groupCallParticipant, tL_groupCallParticipant.volume);
            Integer newTag = null;
            if (finalMove) {
                long id = MessageObject.getPeerId(this.currentParticipant.peer);
                TLObject object = id > 0 ? GroupCallActivity.this.accountInstance.getMessagesController().getUser(Long.valueOf(id)) : GroupCallActivity.this.accountInstance.getMessagesController().getChat(Long.valueOf(-id));
                if (this.currentParticipant.volume == 0) {
                    if (GroupCallActivity.this.scrimPopupWindow != null) {
                        GroupCallActivity.this.scrimPopupWindow.dismiss();
                        GroupCallActivity.this.scrimPopupWindow = null;
                    }
                    GroupCallActivity.this.dismissAvatarPreview(true);
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    groupCallActivity.processSelectedOption(this.currentParticipant, id, ChatObject.canManageCalls(groupCallActivity.currentChat) ? 0 : 5);
                } else {
                    VoIPService.getSharedInstance().editCallMember(object, null, null, Integer.valueOf(this.currentParticipant.volume), null, null);
                }
            }
            if (this.currentProgress == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                newTag = 1;
            }
            if ((this.imageView.getTag() == null && newTag != null) || (this.imageView.getTag() != null && newTag == null)) {
                this.speakerDrawable.setCustomEndFrame(this.currentProgress == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 17 : 34);
                RLottieDrawable rLottieDrawable = this.speakerDrawable;
                if (this.currentProgress != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                    i = 17;
                }
                rLottieDrawable.setCurrentFrame(i);
                this.speakerDrawable.start();
                this.imageView.setTag(newTag);
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int color;
            int p;
            float rad;
            int prevColor = this.currentColor;
            double d = this.currentProgress;
            if (d < 0.25d) {
                this.currentColor = -3385513;
            } else if (d > 0.25d && d < 0.5d) {
                this.currentColor = -3562181;
            } else if (d < 0.5d || d > 0.75d) {
                this.currentColor = -11688225;
            } else {
                this.currentColor = -11027349;
            }
            if (prevColor != 0) {
                color = AndroidUtilities.getOffsetColor(this.oldColor, prevColor, this.colorChangeProgress, 1.0f);
                if (prevColor != this.currentColor) {
                    this.colorChangeProgress = 0.0f;
                    this.oldColor = color;
                }
            } else {
                color = this.currentColor;
                this.colorChangeProgress = 1.0f;
            }
            this.paint.setColor(color);
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastUpdateTime = newTime;
            float f = this.colorChangeProgress;
            if (f < 1.0f) {
                float f2 = f + (((float) dt) / 200.0f);
                this.colorChangeProgress = f2;
                if (f2 > 1.0f) {
                    this.colorChangeProgress = 1.0f;
                } else {
                    invalidate();
                }
            }
            this.path.reset();
            float[] fArr = this.radii;
            float f3 = 6.0f;
            float dp = AndroidUtilities.dp(6.0f);
            fArr[7] = dp;
            fArr[6] = dp;
            fArr[1] = dp;
            fArr[0] = dp;
            float rad2 = this.thumbX < AndroidUtilities.dp(12.0f) ? Math.max(0.0f, (this.thumbX - AndroidUtilities.dp(6.0f)) / AndroidUtilities.dp(6.0f)) : 1.0f;
            float[] fArr2 = this.radii;
            float dp2 = AndroidUtilities.dp(6.0f) * rad2;
            fArr2[5] = dp2;
            fArr2[4] = dp2;
            fArr2[3] = dp2;
            fArr2[2] = dp2;
            this.rect.set(0.0f, 0.0f, this.thumbX, getMeasuredHeight());
            this.path.addRoundRect(this.rect, this.radii, Path.Direction.CW);
            this.path.close();
            canvas.drawPath(this.path, this.paint);
            double participantVolume = ChatObject.getParticipantVolume(this.currentParticipant);
            Double.isNaN(participantVolume);
            int percent = (int) (participantVolume / 100.0d);
            int cx = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2) + AndroidUtilities.dp(5.0f);
            int cy = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
            int a = 0;
            while (a < this.volumeAlphas.length) {
                if (a == 0) {
                    p = 0;
                    rad = AndroidUtilities.dp(f3);
                } else if (a == 1) {
                    p = 50;
                    rad = AndroidUtilities.dp(10.0f);
                } else {
                    p = 150;
                    rad = AndroidUtilities.dp(14.0f);
                }
                float[] fArr3 = this.volumeAlphas;
                float offset = AndroidUtilities.dp(2.0f) * (1.0f - fArr3[a]);
                int prevColor2 = prevColor;
                this.paint2.setAlpha((int) (fArr3[a] * 255.0f));
                int color2 = color;
                long newTime2 = newTime;
                this.rect.set((cx - rad) + offset, (cy - rad) + offset, (cx + rad) - offset, (cy + rad) - offset);
                canvas.drawArc(this.rect, -50.0f, 100.0f, false, this.paint2);
                if (percent <= p) {
                    float[] fArr4 = this.volumeAlphas;
                    if (fArr4[a] > 0.0f) {
                        fArr4[a] = fArr4[a] - (((float) dt) / 180.0f);
                        if (fArr4[a] < 0.0f) {
                            fArr4[a] = 0.0f;
                        }
                        invalidate();
                    }
                } else {
                    float[] fArr5 = this.volumeAlphas;
                    if (fArr5[a] < 1.0f) {
                        fArr5[a] = fArr5[a] + (((float) dt) / 180.0f);
                        if (fArr5[a] > 1.0f) {
                            fArr5[a] = 1.0f;
                        }
                        invalidate();
                    }
                }
                a++;
                prevColor = prevColor2;
                color = color2;
                newTime = newTime2;
                f3 = 6.0f;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class WeavingState {
        public int currentState;
        private float duration;
        public Shader shader;
        private float startX;
        private float startY;
        private float time;
        private float targetX = -1.0f;
        private float targetY = -1.0f;
        private Matrix matrix = new Matrix();

        public WeavingState(int state) {
            this.currentState = state;
        }

        public void update(int top, int left, int size, long dt, float amplitude) {
            float s;
            if (this.shader == null) {
                return;
            }
            float f = this.duration;
            if (f == 0.0f || this.time >= f) {
                this.duration = Utilities.random.nextInt(200) + 1500;
                this.time = 0.0f;
                if (this.targetX == -1.0f) {
                    setTarget();
                }
                this.startX = this.targetX;
                this.startY = this.targetY;
                setTarget();
            }
            float f2 = this.time + (((float) dt) * (BlobDrawable.GRADIENT_SPEED_MIN + 0.5f)) + (((float) dt) * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * amplitude);
            this.time = f2;
            float f3 = this.duration;
            if (f2 > f3) {
                this.time = f3;
            }
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / this.duration);
            float f4 = this.startX;
            float x = (left + (size * (f4 + ((this.targetX - f4) * interpolation)))) - 200.0f;
            float f5 = this.startY;
            float y = (top + (size * (f5 + ((this.targetY - f5) * interpolation)))) - 200.0f;
            if (GroupCallActivity.isGradientState(this.currentState)) {
                s = 1.0f;
            } else {
                s = this.currentState == 1 ? 4.0f : 2.5f;
            }
            float scale = (AndroidUtilities.dp(122.0f) / 400.0f) * s;
            this.matrix.reset();
            this.matrix.postTranslate(x, y);
            this.matrix.postScale(scale, scale, x + 200.0f, 200.0f + y);
            this.shader.setLocalMatrix(this.matrix);
        }

        private void setTarget() {
            if (GroupCallActivity.isGradientState(this.currentState)) {
                this.targetX = ((Utilities.random.nextInt(100) * 0.2f) / 100.0f) + 0.85f;
                this.targetY = 1.0f;
            } else if (this.currentState == 1) {
                this.targetX = ((Utilities.random.nextInt(100) * 0.3f) / 100.0f) + 0.2f;
                this.targetY = ((Utilities.random.nextInt(100) * 0.3f) / 100.0f) + 0.7f;
            } else {
                this.targetX = ((Utilities.random.nextInt(100) / 100.0f) * 0.2f) + 0.8f;
                this.targetY = Utilities.random.nextInt(100) / 100.0f;
            }
        }
    }

    public static boolean isGradientState(int state) {
        return !(VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().groupCall == null || !VoIPService.getSharedInstance().groupCall.call.rtmp_stream) || state == 2 || state == 4 || state == 5 || state == 6 || state == 7;
    }

    /* loaded from: classes4.dex */
    private static class LabeledButton extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public LabeledButton(Context context, String text, int resId, int color) {
            super(context);
            this.imageView = new ImageView(context);
            if (Build.VERSION.SDK_INT >= 21) {
                this.imageView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(50.0f), color, 536870911));
            } else {
                this.imageView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(50.0f), color, color));
            }
            this.imageView.setImageResource(resId);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(50, 50, 49));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setGravity(1);
            this.textView.setText(text);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 55.0f, 0.0f, 0.0f));
        }

        public void setColor(int color) {
            Theme.setSelectorDrawableColor(this.imageView.getBackground(), color, false);
            if (Build.VERSION.SDK_INT < 21) {
                Theme.setSelectorDrawableColor(this.imageView.getBackground(), color, true);
            }
            this.imageView.invalidate();
        }
    }

    private void prepareBlurBitmap() {
        if (this.blurredView == null) {
            return;
        }
        int w = (int) ((this.containerView.getMeasuredWidth() - (this.backgroundPaddingLeft * 2)) / 6.0f);
        int h = (int) ((this.containerView.getMeasuredHeight() - AndroidUtilities.statusBarHeight) / 6.0f);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        canvas.save();
        canvas.translate(0.0f, -AndroidUtilities.statusBarHeight);
        this.parentActivity.getActionBarLayout().draw(canvas);
        canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, 76));
        canvas.restore();
        canvas.save();
        canvas.translate(this.containerView.getX(), -AndroidUtilities.statusBarHeight);
        this.drawingForBlur = true;
        this.containerView.draw(canvas);
        this.drawingForBlur = false;
        Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(w, h) / 180));
        this.blurredView.setBackground(new BitmapDrawable(bitmap));
        this.blurredView.setAlpha(0.0f);
        this.blurredView.setVisibility(0);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean onCustomOpenAnimation() {
        groupCallUiVisible = true;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        GroupCallPip.updateVisibility(getContext());
        return super.onCustomOpenAnimation();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        this.parentActivity.removeOnUserLeaveHintListener(this.onUserLeaveHintListener);
        this.parentActivity.setRequestedOrientation(-1);
        groupCallUiVisible = false;
        GroupVoipInviteAlert groupVoipInviteAlert = this.groupVoipInviteAlert;
        if (groupVoipInviteAlert != null) {
            groupVoipInviteAlert.dismiss();
        }
        this.delayedGroupCallUpdated = true;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.needShowAlert);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.didLoadChatAdmins);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.applyGroupCallVisibleParticipants);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.mainUserInfoChanged);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.groupCallScreencastStateChanged);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.groupCallSpeakingUsersUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
        super.dismiss();
    }

    public boolean isStillConnecting() {
        int i = this.currentCallState;
        return i == 1 || i == 2 || i == 6 || i == 5;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        TLRPC.TL_groupCallParticipant participant;
        String str;
        int i;
        TLRPC.TL_groupCallParticipant participant2;
        String str2;
        int i2;
        String error;
        int i3;
        int i4;
        boolean raisedHand = false;
        if (id == NotificationCenter.groupCallUpdated) {
            Long callId = (Long) args[1];
            ChatObject.Call call = this.call;
            if (call != null && call.call.id == callId.longValue()) {
                if (this.call.call instanceof TLRPC.TL_groupCallDiscarded) {
                    dismiss();
                    return;
                }
                if (this.creatingServiceTime == 0 && (((i4 = this.muteButtonState) == 7 || i4 == 5 || i4 == 6) && !this.call.isScheduled())) {
                    try {
                        Intent intent = new Intent(this.parentActivity, VoIPService.class);
                        intent.putExtra(ChatReactionsEditActivity.KEY_CHAT_ID, this.currentChat.id);
                        intent.putExtra("createGroupCall", false);
                        intent.putExtra("hasFewPeers", this.scheduleHasFewPeers);
                        intent.putExtra("peerChannelId", this.schedulePeer.channel_id);
                        intent.putExtra("peerChatId", this.schedulePeer.chat_id);
                        intent.putExtra("peerUserId", this.schedulePeer.user_id);
                        intent.putExtra("hash", this.scheduledHash);
                        intent.putExtra("peerAccessHash", this.schedulePeer.access_hash);
                        intent.putExtra("is_outgoing", true);
                        intent.putExtra("start_incall_activity", false);
                        intent.putExtra("account", this.accountInstance.getCurrentAccount());
                        intent.putExtra("scheduleDate", this.scheduleStartAt);
                        this.parentActivity.startService(intent);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    this.creatingServiceTime = SystemClock.elapsedRealtime();
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda25
                        @Override // java.lang.Runnable
                        public final void run() {
                            GroupCallActivity.this.m3462xf4b9f1ce();
                        }
                    }, 3000L);
                }
                if (!this.callInitied && VoIPService.getSharedInstance() != null) {
                    this.call.addSelfDummyParticipant(false);
                    initCreatedGroupCall();
                    VoIPService.getSharedInstance().playConnectedSound();
                }
                updateItems();
                int N = this.listView.getChildCount();
                for (int a = 0; a < N; a++) {
                    View child = this.listView.getChildAt(a);
                    if (child instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) child).applyParticipantChanges(true);
                    }
                }
                if (this.scrimView != null) {
                    this.delayedGroupCallUpdated = true;
                } else {
                    applyCallParticipantUpdates(true);
                }
                updateSubtitle();
                boolean selfUpdate = ((Boolean) args[2]).booleanValue();
                if (this.muteButtonState == 4) {
                    raisedHand = true;
                }
                updateState(true, selfUpdate);
                updateTitle(true);
                if (raisedHand && ((i3 = this.muteButtonState) == 1 || i3 == 0)) {
                    getUndoView().showWithAction(0L, 38, (Runnable) null);
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().playAllowTalkSound();
                    }
                }
                if (args.length >= 4) {
                    long justJoinedId = ((Long) args[3]).longValue();
                    if (justJoinedId != 0 && !isRtmpStream()) {
                        boolean hasInDialogs = false;
                        try {
                            ArrayList<TLRPC.Dialog> dialogs = this.accountInstance.getMessagesController().getAllDialogs();
                            if (dialogs != null) {
                                Iterator<TLRPC.Dialog> it = dialogs.iterator();
                                while (true) {
                                    if (!it.hasNext()) {
                                        break;
                                    }
                                    TLRPC.Dialog dialog = it.next();
                                    if (dialog.id == justJoinedId) {
                                        hasInDialogs = true;
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e2) {
                        }
                        if (DialogObject.isUserDialog(justJoinedId)) {
                            TLRPC.User user = this.accountInstance.getMessagesController().getUser(Long.valueOf(justJoinedId));
                            if (user != null) {
                                if (this.call.call.participants_count < 250 || UserObject.isContact(user) || user.verified || hasInDialogs) {
                                    getUndoView().showWithAction(0L, 44, user, this.currentChat, (Runnable) null, (Runnable) null);
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-justJoinedId));
                        if (chat != null) {
                            if (this.call.call.participants_count < 250 || !ChatObject.isNotInChat(chat) || chat.verified || hasInDialogs) {
                                getUndoView().showWithAction(0L, 44, chat, this.currentChat, (Runnable) null, (Runnable) null);
                            }
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.groupCallSpeakingUsersUpdated) {
            if (this.renderersContainer.inFullscreenMode && this.call != null) {
                boolean autoPinEnabled = this.renderersContainer.autoPinEnabled();
                if (this.call != null && this.renderersContainer.inFullscreenMode && this.renderersContainer.fullscreenParticipant != null && this.call.participants.get(MessageObject.getPeerId(this.renderersContainer.fullscreenParticipant.participant.peer)) == null) {
                    autoPinEnabled = true;
                }
                if (autoPinEnabled) {
                    ChatObject.VideoParticipant currentSpeaker = null;
                    for (int i5 = 0; i5 < this.visibleVideoParticipants.size(); i5++) {
                        ChatObject.VideoParticipant participant3 = this.visibleVideoParticipants.get(i5);
                        boolean newSpeaking = this.call.currentSpeakingPeers.get(MessageObject.getPeerId(participant3.participant.peer), null) != null;
                        if (newSpeaking && !participant3.participant.muted_by_you && this.renderersContainer.fullscreenPeerId != MessageObject.getPeerId(participant3.participant.peer)) {
                            currentSpeaker = participant3;
                        }
                    }
                    if (currentSpeaker != null) {
                        fullscreenFor(currentSpeaker);
                    }
                }
            }
            this.renderersContainer.setVisibleParticipant(true);
            updateSubtitle();
        } else if (id == NotificationCenter.webRtcMicAmplitudeEvent) {
            float amplitude = ((Float) args[0]).floatValue();
            setMicAmplitude(amplitude);
        } else if (id == NotificationCenter.needShowAlert) {
            int num = ((Integer) args[0]).intValue();
            if (num == 6) {
                String text = (String) args[1];
                if ("GROUPCALL_PARTICIPANTS_TOO_MUCH".equals(text)) {
                    if (ChatObject.isChannelOrGiga(this.currentChat)) {
                        error = LocaleController.getString("VoipChannelTooMuch", R.string.VoipChannelTooMuch);
                    } else {
                        error = LocaleController.getString("VoipGroupTooMuch", R.string.VoipGroupTooMuch);
                    }
                } else if (!"ANONYMOUS_CALLS_DISABLED".equals(text) && !"GROUPCALL_ANONYMOUS_FORBIDDEN".equals(text)) {
                    error = LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + text;
                } else if (ChatObject.isChannelOrGiga(this.currentChat)) {
                    error = LocaleController.getString("VoipChannelJoinAnonymousAdmin", R.string.VoipChannelJoinAnonymousAdmin);
                } else {
                    error = LocaleController.getString("VoipGroupJoinAnonymousAdmin", R.string.VoipGroupJoinAnonymousAdmin);
                }
                AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString("VoipGroupVoiceChat", R.string.VoipGroupVoiceChat), error);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda65
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        GroupCallActivity.this.m3463x5b92b18f(dialogInterface);
                    }
                });
                try {
                    builder.show();
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            }
        } else if (id == NotificationCenter.didEndCall) {
            if (VoIPService.getSharedInstance() == null) {
                dismiss();
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            if (chatFull.id == this.currentChat.id) {
                updateItems();
                updateState(isShowing(), false);
            }
            long selfId = MessageObject.getPeerId(this.selfPeer);
            if (this.call != null && chatFull.id == (-selfId) && (participant2 = this.call.participants.get(selfId)) != null) {
                participant2.about = chatFull.about;
                applyCallParticipantUpdates(true);
                AndroidUtilities.updateVisibleRows(this.listView);
                if (this.currentOptionsLayout != null) {
                    for (int i6 = 0; i6 < this.currentOptionsLayout.getChildCount(); i6++) {
                        View child2 = this.currentOptionsLayout.getChildAt(i6);
                        if ((child2 instanceof ActionBarMenuSubItem) && child2.getTag() != null && ((Integer) child2.getTag()).intValue() == 10) {
                            ActionBarMenuSubItem actionBarMenuSubItem = (ActionBarMenuSubItem) child2;
                            if (TextUtils.isEmpty(participant2.about)) {
                                i2 = R.string.VoipAddDescription;
                                str2 = "VoipAddDescription";
                            } else {
                                i2 = R.string.VoipEditDescription;
                                str2 = "VoipEditDescription";
                            }
                            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(str2, i2), TextUtils.isEmpty(participant2.about) ? R.drawable.msg_addbio : R.drawable.msg_info);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.didLoadChatAdmins) {
            long chatId = ((Long) args[0]).longValue();
            if (chatId == this.currentChat.id) {
                updateItems();
                updateState(isShowing(), false);
            }
        } else if (id == NotificationCenter.applyGroupCallVisibleParticipants) {
            int count = this.listView.getChildCount();
            long time = ((Long) args[0]).longValue();
            for (int a2 = 0; a2 < count; a2++) {
                RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(this.listView.getChildAt(a2));
                if (holder != null && (holder.itemView instanceof GroupCallUserCell)) {
                    GroupCallUserCell cell = (GroupCallUserCell) holder.itemView;
                    cell.getParticipant().lastVisibleDate = time;
                }
            }
        } else if (id == NotificationCenter.userInfoDidLoad) {
            Long uid = (Long) args[0];
            long selfId2 = MessageObject.getPeerId(this.selfPeer);
            if (this.call != null && selfId2 == uid.longValue() && (participant = this.call.participants.get(selfId2)) != null) {
                TLRPC.UserFull userInfo = (TLRPC.UserFull) args[1];
                participant.about = userInfo.about;
                applyCallParticipantUpdates(true);
                AndroidUtilities.updateVisibleRows(this.listView);
                if (this.currentOptionsLayout != null) {
                    for (int i7 = 0; i7 < this.currentOptionsLayout.getChildCount(); i7++) {
                        View child3 = this.currentOptionsLayout.getChildAt(i7);
                        if ((child3 instanceof ActionBarMenuSubItem) && child3.getTag() != null && ((Integer) child3.getTag()).intValue() == 10) {
                            ActionBarMenuSubItem actionBarMenuSubItem2 = (ActionBarMenuSubItem) child3;
                            if (TextUtils.isEmpty(participant.about)) {
                                i = R.string.VoipAddBio;
                                str = "VoipAddBio";
                            } else {
                                i = R.string.VoipEditBio;
                                str = "VoipEditBio";
                            }
                            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(str, i), TextUtils.isEmpty(participant.about) ? R.drawable.msg_addbio : R.drawable.msg_info);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.mainUserInfoChanged) {
            applyCallParticipantUpdates(true);
            AndroidUtilities.updateVisibleRows(this.listView);
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((MessagesController.UPDATE_MASK_CHAT_NAME & mask) != 0) {
                applyCallParticipantUpdates(true);
                AndroidUtilities.updateVisibleRows(this.listView);
            }
        } else {
            int mask2 = NotificationCenter.groupCallScreencastStateChanged;
            if (id == mask2) {
                PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
                if (privateVideoPreviewDialog != null) {
                    privateVideoPreviewDialog.dismiss(true, true);
                }
                updateItems();
            }
        }
    }

    /* renamed from: lambda$didReceivedNotification$2$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3462xf4b9f1ce() {
        if (!isStillConnecting()) {
            return;
        }
        updateState(true, false);
    }

    /* renamed from: lambda$didReceivedNotification$3$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3463x5b92b18f(DialogInterface dialog) {
        dismiss();
    }

    private void setMicAmplitude(float amplitude) {
        TLRPC.TL_groupCallParticipant participant;
        RecyclerView.ViewHolder holder;
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
            amplitude = 0.0f;
        }
        setAmplitude(4000.0f * amplitude);
        ChatObject.Call call = this.call;
        if (call != null && this.listView != null && (participant = call.participants.get(MessageObject.getPeerId(this.selfPeer))) != null) {
            if (!this.renderersContainer.inFullscreenMode) {
                ArrayList<TLRPC.TL_groupCallParticipant> array = this.delayedGroupCallUpdated ? this.oldParticipants : this.call.visibleParticipants;
                int idx = array.indexOf(participant);
                if (idx >= 0 && (holder = this.listView.findViewHolderForAdapterPosition(this.listAdapter.usersStartRow + idx)) != null && (holder.itemView instanceof GroupCallUserCell)) {
                    ((GroupCallUserCell) holder.itemView).setAmplitude(amplitude * 15.0f);
                    if (holder.itemView == this.scrimView && !this.contentFullyOverlayed) {
                        this.containerView.invalidate();
                    }
                }
            } else {
                for (int i = 0; i < this.fullscreenUsersListView.getChildCount(); i++) {
                    GroupCallFullscreenAdapter.GroupCallUserCell cell = (GroupCallFullscreenAdapter.GroupCallUserCell) this.fullscreenUsersListView.getChildAt(i);
                    if (MessageObject.getPeerId(cell.getParticipant().peer) == MessageObject.getPeerId(participant.peer)) {
                        cell.setAmplitude(amplitude * 15.0f);
                    }
                }
            }
            this.renderersContainer.setAmplitude(participant, 15.0f * amplitude);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:106:0x0278  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x01ae A[EDGE_INSN: B:121:0x01ae->B:69:0x01ae ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:127:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0122  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0166  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x01dd  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x01ed  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0201  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x020a  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x021a  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x0234 A[LOOP:2: B:92:0x022c->B:94:0x0234, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void applyCallParticipantUpdates(boolean r25) {
        /*
            Method dump skipped, instructions count: 645
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.applyCallParticipantUpdates(boolean):void");
    }

    private void updateVideoParticipantList() {
        this.visibleVideoParticipants.clear();
        if (isTabletMode) {
            if (this.renderersContainer.inFullscreenMode) {
                this.visibleVideoParticipants.addAll(this.call.visibleVideoParticipants);
                if (this.renderersContainer.fullscreenParticipant != null) {
                    this.visibleVideoParticipants.remove(this.renderersContainer.fullscreenParticipant);
                    return;
                }
                return;
            }
            return;
        }
        this.visibleVideoParticipants.addAll(this.call.visibleVideoParticipants);
    }

    private void updateRecordCallText() {
        if (this.call == null) {
            return;
        }
        int time = this.accountInstance.getConnectionsManager().getCurrentTime() - this.call.call.record_start_date;
        if (this.call.recording) {
            this.recordItem.setSubtext(AndroidUtilities.formatDuration(time, false));
        } else {
            this.recordItem.setSubtext(null);
        }
    }

    public void updateItems() {
        String str;
        int i;
        int margin;
        TLObject object;
        boolean mutedByAdmin;
        int i2;
        TLRPC.TL_groupCallParticipant participant;
        ChatObject.Call call = this.call;
        if (call == null || call.isScheduled()) {
            this.pipItem.setVisibility(4);
            this.screenShareItem.setVisibility(8);
            if (this.call == null) {
                this.otherItem.setVisibility(8);
                return;
            }
        }
        if (!this.changingPermissions) {
            TLRPC.Chat newChat = this.accountInstance.getMessagesController().getChat(Long.valueOf(this.currentChat.id));
            if (newChat != null) {
                this.currentChat = newChat;
            }
            if (ChatObject.canUserDoAdminAction(this.currentChat, 3) || (((!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) && (!TextUtils.isEmpty(this.currentChat.username) || ChatObject.canUserDoAdminAction(this.currentChat, 3))) || (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup && !TextUtils.isEmpty(this.currentChat.username)))) {
                this.inviteItem.setVisibility(0);
            } else {
                this.inviteItem.setVisibility(8);
            }
            TLRPC.TL_groupCallParticipant participant2 = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
            ChatObject.Call call2 = this.call;
            if (call2 == null || call2.isScheduled() || (participant2 != null && !participant2.can_self_unmute && participant2.muted)) {
                this.noiseItem.setVisibility(8);
            } else {
                this.noiseItem.setVisibility(0);
            }
            this.noiseItem.setIcon(SharedConfig.noiseSupression ? R.drawable.msg_noise_on : R.drawable.msg_noise_off);
            ActionBarMenuSubItem actionBarMenuSubItem = this.noiseItem;
            if (SharedConfig.noiseSupression) {
                i = R.string.VoipNoiseCancellationEnabled;
                str = "VoipNoiseCancellationEnabled";
            } else {
                i = R.string.VoipNoiseCancellationDisabled;
                str = "VoipNoiseCancellationDisabled";
            }
            actionBarMenuSubItem.setSubtext(LocaleController.getString(str, i));
            boolean z = true;
            if (ChatObject.canManageCalls(this.currentChat)) {
                this.leaveItem.setVisibility(0);
                this.editTitleItem.setVisibility(0);
                if (isRtmpStream()) {
                    this.recordItem.setVisibility(0);
                    this.screenItem.setVisibility(8);
                } else if (this.call.isScheduled()) {
                    this.recordItem.setVisibility(8);
                    this.screenItem.setVisibility(8);
                } else {
                    this.recordItem.setVisibility(0);
                }
                if (!this.call.canRecordVideo() || this.call.isScheduled() || Build.VERSION.SDK_INT < 21 || isRtmpStream()) {
                    this.screenItem.setVisibility(8);
                } else {
                    this.screenItem.setVisibility(0);
                }
                this.screenShareItem.setVisibility(8);
                this.recordCallDrawable.setRecording(this.call.recording);
                if (this.call.recording) {
                    if (this.updateCallRecordRunnable == null) {
                        Runnable runnable = new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda29
                            @Override // java.lang.Runnable
                            public final void run() {
                                GroupCallActivity.this.m3508lambda$updateItems$4$orgtelegramuiGroupCallActivity();
                            }
                        };
                        this.updateCallRecordRunnable = runnable;
                        participant = participant2;
                        AndroidUtilities.runOnUIThread(runnable, 1000L);
                    } else {
                        participant = participant2;
                    }
                    this.recordItem.setText(LocaleController.getString("VoipGroupStopRecordCall", R.string.VoipGroupStopRecordCall));
                } else {
                    participant = participant2;
                    Runnable runnable2 = this.updateCallRecordRunnable;
                    if (runnable2 != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable2);
                        this.updateCallRecordRunnable = null;
                    }
                    this.recordItem.setText(LocaleController.getString("VoipGroupRecordCall", R.string.VoipGroupRecordCall));
                }
                if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().getVideoState(true) == 2) {
                    this.screenItem.setTextAndIcon(LocaleController.getString("VoipChatStopScreenCapture", R.string.VoipChatStopScreenCapture), R.drawable.msg_screencast_off);
                } else {
                    this.screenItem.setTextAndIcon(LocaleController.getString("VoipChatStartScreenCapture", R.string.VoipChatStartScreenCapture), R.drawable.msg_screencast);
                }
                updateRecordCallText();
            } else {
                if (participant2 != null && !participant2.can_self_unmute && participant2.muted && !ChatObject.canManageCalls(this.currentChat)) {
                    mutedByAdmin = true;
                    if (VoIPService.getSharedInstance() != null || VoIPService.getSharedInstance().getVideoState(true) != 2) {
                        z = false;
                    }
                    boolean sharingScreen = z;
                    if (Build.VERSION.SDK_INT < 21 && !mutedByAdmin && ((this.call.canRecordVideo() || sharingScreen) && !this.call.isScheduled() && !isRtmpStream())) {
                        if (sharingScreen) {
                            this.screenShareItem.setVisibility(8);
                            this.screenItem.setVisibility(0);
                            this.screenItem.setTextAndIcon(LocaleController.getString("VoipChatStopScreenCapture", R.string.VoipChatStopScreenCapture), R.drawable.msg_screencast_off);
                            this.screenItem.setContentDescription(LocaleController.getString("VoipChatStopScreenCapture", R.string.VoipChatStopScreenCapture));
                            i2 = 8;
                        } else {
                            this.screenItem.setTextAndIcon(LocaleController.getString("VoipChatStartScreenCapture", R.string.VoipChatStartScreenCapture), R.drawable.msg_screencast);
                            this.screenItem.setContentDescription(LocaleController.getString("VoipChatStartScreenCapture", R.string.VoipChatStartScreenCapture));
                            this.screenShareItem.setVisibility(8);
                            this.screenItem.setVisibility(0);
                            i2 = 8;
                        }
                    } else {
                        i2 = 8;
                        this.screenShareItem.setVisibility(8);
                        this.screenItem.setVisibility(8);
                    }
                    this.leaveItem.setVisibility(i2);
                    this.editTitleItem.setVisibility(i2);
                    this.recordItem.setVisibility(i2);
                }
                mutedByAdmin = false;
                if (VoIPService.getSharedInstance() != null) {
                }
                z = false;
                boolean sharingScreen2 = z;
                if (Build.VERSION.SDK_INT < 21) {
                }
                i2 = 8;
                this.screenShareItem.setVisibility(8);
                this.screenItem.setVisibility(8);
                this.leaveItem.setVisibility(i2);
                this.editTitleItem.setVisibility(i2);
                this.recordItem.setVisibility(i2);
            }
            if (ChatObject.canManageCalls(this.currentChat) && this.call.call.can_change_join_muted) {
                this.permissionItem.setVisibility(0);
            } else {
                this.permissionItem.setVisibility(8);
            }
            this.soundItem.setVisibility(isRtmpStream() ? 8 : 0);
            if (this.editTitleItem.getVisibility() == 0 || this.permissionItem.getVisibility() == 0 || this.inviteItem.getVisibility() == 0 || this.screenItem.getVisibility() == 0 || this.recordItem.getVisibility() == 0 || this.leaveItem.getVisibility() == 0) {
                this.soundItemDivider.setVisibility(0);
            } else {
                this.soundItemDivider.setVisibility(8);
            }
            if (((VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().hasFewPeers) || this.scheduleHasFewPeers) && !isRtmpStream()) {
                this.accountSelectCell.setVisibility(0);
                this.accountGap.setVisibility(0);
                long peerId = MessageObject.getPeerId(this.selfPeer);
                if (DialogObject.isUserDialog(peerId)) {
                    object = this.accountInstance.getMessagesController().getUser(Long.valueOf(peerId));
                } else {
                    object = this.accountInstance.getMessagesController().getChat(Long.valueOf(-peerId));
                }
                this.accountSelectCell.setObject(object);
                margin = 48 + 48;
            } else {
                margin = 48 + 48;
                this.accountSelectCell.setVisibility(8);
                this.accountGap.setVisibility(8);
            }
            TLRPC.Chat chat = this.currentChat;
            if (chat != null && !ChatObject.isChannelOrGiga(chat) && isRtmpStream() && this.inviteItem.getVisibility() == 8) {
                this.otherItem.setVisibility(8);
            } else {
                this.otherItem.setVisibility(0);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.titleTextView.getLayoutParams();
            if (layoutParams.rightMargin != AndroidUtilities.dp(margin)) {
                layoutParams.rightMargin = AndroidUtilities.dp(margin);
                this.titleTextView.requestLayout();
            }
            ((FrameLayout.LayoutParams) this.menuItemsContainer.getLayoutParams()).rightMargin = 0;
            this.actionBar.setTitleRightMargin(AndroidUtilities.dp(48.0f) * 2);
        }
    }

    /* renamed from: lambda$updateItems$4$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3508lambda$updateItems$4$orgtelegramuiGroupCallActivity() {
        updateRecordCallText();
        AndroidUtilities.runOnUIThread(this.updateCallRecordRunnable, 1000L);
    }

    protected void makeFocusable(final BottomSheet bottomSheet, final AlertDialog alertDialog, final EditTextBoldCursor editText, final boolean showKeyboard) {
        if (!this.enterEventSent) {
            BaseFragment fragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
            if (fragment instanceof ChatActivity) {
                boolean keyboardVisible = ((ChatActivity) fragment).needEnterText();
                this.enterEventSent = true;
                this.anyEnterEventSent = true;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda19
                    @Override // java.lang.Runnable
                    public final void run() {
                        GroupCallActivity.lambda$makeFocusable$7(BottomSheet.this, editText, showKeyboard, alertDialog);
                    }
                }, keyboardVisible ? 200L : 0L);
                return;
            }
            this.enterEventSent = true;
            this.anyEnterEventSent = true;
            if (bottomSheet != null) {
                bottomSheet.setFocusable(true);
            } else if (alertDialog != null) {
                alertDialog.setFocusable(true);
            }
            if (showKeyboard) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda23
                    @Override // java.lang.Runnable
                    public final void run() {
                        GroupCallActivity.lambda$makeFocusable$8(EditTextBoldCursor.this);
                    }
                }, 100L);
            }
        }
    }

    public static /* synthetic */ void lambda$makeFocusable$7(BottomSheet bottomSheet, final EditTextBoldCursor editText, boolean showKeyboard, AlertDialog alertDialog) {
        if (bottomSheet != null && !bottomSheet.isDismissed()) {
            bottomSheet.setFocusable(true);
            editText.requestFocus();
            if (showKeyboard) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda20
                    @Override // java.lang.Runnable
                    public final void run() {
                        AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                    }
                });
            }
        } else if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.setFocusable(true);
            editText.requestFocus();
            if (showKeyboard) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda21
                    @Override // java.lang.Runnable
                    public final void run() {
                        AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$makeFocusable$8(EditTextBoldCursor editText) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    public static void create(LaunchActivity activity, AccountInstance account, TLRPC.Chat scheduleChat, TLRPC.InputPeer schedulePeer, boolean hasFewPeers, String scheduledHash) {
        TLRPC.Chat chat;
        if (groupCallInstance == null) {
            if (schedulePeer == null && VoIPService.getSharedInstance() == null) {
                return;
            }
            if (schedulePeer != null) {
                groupCallInstance = new GroupCallActivity(activity, account, account.getMessagesController().getGroupCall(scheduleChat.id, false), scheduleChat, schedulePeer, hasFewPeers, scheduledHash);
            } else {
                ChatObject.Call call = VoIPService.getSharedInstance().groupCall;
                if (call == null || (chat = account.getMessagesController().getChat(Long.valueOf(call.chatId))) == null) {
                    return;
                }
                call.addSelfDummyParticipant(true);
                groupCallInstance = new GroupCallActivity(activity, account, call, chat, null, hasFewPeers, scheduledHash);
            }
            GroupCallActivity groupCallActivity = groupCallInstance;
            groupCallActivity.parentActivity = activity;
            groupCallActivity.show();
        }
    }

    private GroupCallActivity(final Context context, final AccountInstance account, ChatObject.Call groupCall, final TLRPC.Chat chat, TLRPC.InputPeer schedulePeer, boolean scheduleHasFewPeers, String scheduledHash) {
        super(context, false);
        TLRPC.InputPeer peer;
        String str;
        int i;
        String str2;
        int i2;
        ViewGroup decorView;
        this.accountInstance = account;
        this.call = groupCall;
        this.schedulePeer = schedulePeer;
        this.currentChat = chat;
        this.scheduledHash = scheduledHash;
        this.currentAccount = account.getCurrentAccount();
        this.scheduleHasFewPeers = scheduleHasFewPeers;
        this.fullWidth = true;
        isTabletMode = false;
        isLandscapeMode = false;
        paused = false;
        setDelegate(new BottomSheet.BottomSheetDelegateInterface() { // from class: org.telegram.ui.GroupCallActivity.3
            @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
            public void onOpenAnimationStart() {
            }

            @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
            public void onOpenAnimationEnd() {
                if (GroupCallActivity.this.muteButtonState == 6) {
                    GroupCallActivity.this.showReminderHint();
                }
            }

            @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
            public boolean canDismiss() {
                return true;
            }
        });
        this.drawDoubleNavigationBar = true;
        this.drawNavigationBar = true;
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setNavigationBarColor(-16777216);
        }
        this.scrollNavBar = true;
        this.navBarColorKey = null;
        this.scrimPaint = new Paint() { // from class: org.telegram.ui.GroupCallActivity.4
            @Override // android.graphics.Paint
            public void setAlpha(int a) {
                super.setAlpha(a);
                if (GroupCallActivity.this.containerView != null) {
                    GroupCallActivity.this.containerView.invalidate();
                }
            }
        };
        setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                GroupCallActivity.this.m3497lambda$new$9$orgtelegramuiGroupCallActivity(dialogInterface);
            }
        });
        setDimBehindAlpha(75);
        this.listAdapter = new ListAdapter(context);
        final RecordStatusDrawable recordStatusDrawable = new RecordStatusDrawable(true);
        recordStatusDrawable.setColor(Theme.getColor(Theme.key_voipgroup_speakingText));
        recordStatusDrawable.start();
        ActionBar actionBar = new ActionBar(context) { // from class: org.telegram.ui.GroupCallActivity.5
            @Override // android.view.View
            public void setAlpha(float alpha) {
                if (getAlpha() != alpha) {
                    super.setAlpha(alpha);
                    GroupCallActivity.this.containerView.invalidate();
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBar, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (getAdditionalSubtitleTextView().getVisibility() == 0) {
                    canvas.save();
                    canvas.translate(getSubtitleTextView().getLeft(), getSubtitleTextView().getY() - AndroidUtilities.dp(1.0f));
                    recordStatusDrawable.setAlpha((int) (getAdditionalSubtitleTextView().getAlpha() * 255.0f));
                    recordStatusDrawable.draw(canvas);
                    canvas.restore();
                    invalidate();
                }
            }
        };
        this.actionBar = actionBar;
        actionBar.setSubtitle("");
        this.actionBar.getSubtitleTextView().setVisibility(0);
        this.actionBar.createAdditionalSubtitleTextView();
        this.actionBar.getAdditionalSubtitleTextView().setPadding(AndroidUtilities.dp(24.0f), 0, 0, 0);
        AndroidUtilities.updateViewVisibilityAnimated(this.actionBar.getAdditionalSubtitleTextView(), this.drawSpeakingSubtitle, 1.0f, false);
        this.actionBar.getAdditionalSubtitleTextView().setTextColor(Theme.getColor(Theme.key_voipgroup_speakingText));
        this.actionBar.setSubtitleColor(Theme.getColor(Theme.key_voipgroup_lastSeenTextUnscrolled));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_voipgroup_actionBarItems), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), false);
        this.actionBar.setTitleColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
        this.actionBar.setSubtitleColor(Theme.getColor(Theme.key_voipgroup_lastSeenTextUnscrolled));
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass6(context));
        if (schedulePeer != null) {
            peer = schedulePeer;
        } else {
            peer = VoIPService.getSharedInstance().getGroupCallPeer();
        }
        if (peer == null) {
            TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
            this.selfPeer = tL_peerUser;
            tL_peerUser.user_id = this.accountInstance.getUserConfig().getClientUserId();
        } else if (peer instanceof TLRPC.TL_inputPeerChannel) {
            TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
            this.selfPeer = tL_peerChannel;
            tL_peerChannel.channel_id = peer.channel_id;
        } else if (peer instanceof TLRPC.TL_inputPeerUser) {
            TLRPC.TL_peerUser tL_peerUser2 = new TLRPC.TL_peerUser();
            this.selfPeer = tL_peerUser2;
            tL_peerUser2.user_id = peer.user_id;
        } else if (peer instanceof TLRPC.TL_inputPeerChat) {
            TLRPC.TL_peerChat tL_peerChat = new TLRPC.TL_peerChat();
            this.selfPeer = tL_peerChat;
            tL_peerChat.chat_id = peer.chat_id;
        }
        VoIPService.audioLevelsCallback = new NativeInstance.AudioLevelsCallback() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda43
            @Override // org.telegram.messenger.voip.NativeInstance.AudioLevelsCallback
            public final void run(int[] iArr, float[] fArr, boolean[] zArr) {
                GroupCallActivity.this.m3475lambda$new$10$orgtelegramuiGroupCallActivity(iArr, fArr, zArr);
            }
        };
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.needShowAlert);
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.didLoadChatAdmins);
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.applyGroupCallVisibleParticipants);
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.userInfoDidLoad);
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.mainUserInfoChanged);
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.groupCallScreencastStateChanged);
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.groupCallSpeakingUsersUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndCall);
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.bigMicDrawable = new RLottieDrawable(R.raw.voip_filled, "2131558588", AndroidUtilities.dp(72.0f), AndroidUtilities.dp(72.0f), true, null);
        this.handDrawables = new RLottieDrawable(R.raw.hand_2, "2131558459", AndroidUtilities.dp(72.0f), AndroidUtilities.dp(72.0f), true, null);
        this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.GroupCallActivity.7
            private int lastSize;
            boolean localHasVideo;
            private boolean updateRenderers;
            boolean wasLayout;
            private boolean ignoreLayout = false;
            private RectF rect = new RectF();
            HashMap<Object, View> listCells = new HashMap<>();

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int listViewPaddingBottom;
                int totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                this.ignoreLayout = true;
                boolean landscape = View.MeasureSpec.getSize(widthMeasureSpec) > totalHeight && !AndroidUtilities.isTablet();
                GroupCallActivity.this.renderersContainer.listWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                boolean tablet = AndroidUtilities.isTablet() && View.MeasureSpec.getSize(widthMeasureSpec) > totalHeight && !GroupCallActivity.this.isRtmpStream();
                if (GroupCallActivity.isLandscapeMode != landscape) {
                    GroupCallActivity.isLandscapeMode = landscape;
                    int measuredWidth = GroupCallActivity.this.muteButton.getMeasuredWidth();
                    if (measuredWidth == 0) {
                        measuredWidth = GroupCallActivity.this.muteButton.getLayoutParams().width;
                    }
                    float muteButtonScale = AndroidUtilities.dp(52.0f) / (measuredWidth - AndroidUtilities.dp(8.0f));
                    float multiplier = (!GroupCallActivity.isLandscapeMode && !GroupCallActivity.this.renderersContainer.inFullscreenMode) ? 1.0f : muteButtonScale;
                    boolean isExpanded = GroupCallActivity.this.renderersContainer.inFullscreenMode && (AndroidUtilities.isTablet() || GroupCallActivity.isLandscapeMode == GroupCallActivity.this.isRtmpLandscapeMode());
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    View expandView = isExpanded ? groupCallActivity.minimizeButton : groupCallActivity.expandButton;
                    GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                    View expandOtherView = isExpanded ? groupCallActivity2.expandButton : groupCallActivity2.minimizeButton;
                    expandView.setAlpha(1.0f);
                    expandView.setScaleX(multiplier);
                    expandView.setScaleY(multiplier);
                    expandOtherView.setAlpha(0.0f);
                    GroupCallActivity.this.muteLabel[0].setAlpha(1.0f);
                    GroupCallActivity.this.muteLabel[1].setAlpha(1.0f);
                    if (GroupCallActivity.this.renderersContainer.inFullscreenMode || (GroupCallActivity.isLandscapeMode && !AndroidUtilities.isTablet())) {
                        GroupCallActivity.this.muteLabel[0].setScaleX(0.687f);
                        GroupCallActivity.this.muteLabel[1].setScaleY(0.687f);
                    } else {
                        GroupCallActivity.this.muteLabel[0].setScaleX(1.0f);
                        GroupCallActivity.this.muteLabel[1].setScaleY(1.0f);
                    }
                    GroupCallActivity.this.invalidateLayoutFullscreen();
                    GroupCallActivity.this.layoutManager.setSpanCount(GroupCallActivity.isLandscapeMode ? 6 : 2);
                    GroupCallActivity.this.listView.invalidateItemDecorations();
                    GroupCallActivity.this.fullscreenUsersListView.invalidateItemDecorations();
                    this.updateRenderers = true;
                    if (GroupCallActivity.this.scheduleInfoTextView != null) {
                        GroupCallActivity.this.scheduleInfoTextView.setVisibility(!GroupCallActivity.isLandscapeMode ? 0 : 8);
                    }
                    boolean needFullscreen = GroupCallActivity.this.isRtmpLandscapeMode() == landscape;
                    if (needFullscreen && GroupCallActivity.this.isRtmpStream() && !GroupCallActivity.this.renderersContainer.inFullscreenMode && !GroupCallActivity.this.call.visibleVideoParticipants.isEmpty()) {
                        GroupCallActivity groupCallActivity3 = GroupCallActivity.this;
                        groupCallActivity3.fullscreenFor(groupCallActivity3.call.visibleVideoParticipants.get(0));
                        GroupCallActivity.this.renderersContainer.delayHideUi();
                    }
                }
                if (GroupCallActivity.isTabletMode != tablet) {
                    GroupCallActivity.isTabletMode = tablet;
                    GroupCallActivity.this.tabletVideoGridView.setVisibility(tablet ? 0 : 8);
                    GroupCallActivity.this.listView.invalidateItemDecorations();
                    GroupCallActivity.this.fullscreenUsersListView.invalidateItemDecorations();
                    this.updateRenderers = true;
                }
                if (this.updateRenderers) {
                    GroupCallActivity.this.applyCallParticipantUpdates(true);
                    GroupCallActivity.this.listAdapter.notifyDataSetChanged();
                    GroupCallActivity.this.fullscreenAdapter.update(false, GroupCallActivity.this.tabletVideoGridView);
                    if (GroupCallActivity.isTabletMode) {
                        GroupCallActivity.this.tabletGridAdapter.update(false, GroupCallActivity.this.tabletVideoGridView);
                    }
                    GroupCallActivity.this.tabletVideoGridView.setVisibility(GroupCallActivity.isTabletMode ? 0 : 8);
                    GroupCallActivity.this.tabletGridAdapter.setVisibility(GroupCallActivity.this.tabletVideoGridView, GroupCallActivity.isTabletMode && !GroupCallActivity.this.renderersContainer.inFullscreenMode, true);
                    GroupCallActivity.this.listViewVideoVisibility = !GroupCallActivity.isTabletMode || GroupCallActivity.this.renderersContainer.inFullscreenMode;
                    boolean fullscreenListVisibility = !GroupCallActivity.isTabletMode && GroupCallActivity.this.renderersContainer.inFullscreenMode;
                    GroupCallActivity.this.fullscreenAdapter.setVisibility(GroupCallActivity.this.fullscreenUsersListView, fullscreenListVisibility);
                    GroupCallActivity.this.fullscreenUsersListView.setVisibility(fullscreenListVisibility ? 0 : 8);
                    GroupCallActivity.this.listView.setVisibility((GroupCallActivity.isTabletMode || !GroupCallActivity.this.renderersContainer.inFullscreenMode) ? 0 : 8);
                    GroupCallActivity.this.layoutManager.setSpanCount(GroupCallActivity.isLandscapeMode ? 6 : 2);
                    GroupCallActivity.this.updateState(false, false);
                    GroupCallActivity.this.listView.invalidateItemDecorations();
                    GroupCallActivity.this.fullscreenUsersListView.invalidateItemDecorations();
                    AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
                    this.updateRenderers = false;
                    GroupCallActivity.this.attachedRenderersTmp.clear();
                    GroupCallActivity.this.attachedRenderersTmp.addAll(GroupCallActivity.this.attachedRenderers);
                    GroupCallActivity.this.renderersContainer.setIsTablet(GroupCallActivity.isTabletMode);
                    for (int i3 = 0; i3 < GroupCallActivity.this.attachedRenderersTmp.size(); i3++) {
                        ((GroupCallMiniTextureView) GroupCallActivity.this.attachedRenderersTmp.get(i3)).updateAttachState(true);
                    }
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    setPadding(GroupCallActivity.this.backgroundPaddingLeft, GroupCallActivity.this.getStatusBarHeight(), GroupCallActivity.this.backgroundPaddingLeft, 0);
                }
                int availableHeight = (totalHeight - getPaddingTop()) - AndroidUtilities.dp(245.0f);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) GroupCallActivity.this.renderersContainer.getLayoutParams();
                if (GroupCallActivity.isTabletMode) {
                    layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                } else {
                    layoutParams.topMargin = 0;
                }
                for (int a = 0; a < 2; a++) {
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) GroupCallActivity.this.undoView[a].getLayoutParams();
                    if (GroupCallActivity.isTabletMode) {
                        layoutParams2.rightMargin = AndroidUtilities.dp(328.0f);
                    } else {
                        layoutParams2.rightMargin = AndroidUtilities.dp(8.0f);
                    }
                }
                if (GroupCallActivity.this.tabletVideoGridView != null) {
                    ((FrameLayout.LayoutParams) GroupCallActivity.this.tabletVideoGridView.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
                }
                int buttonsGradientSize = AndroidUtilities.dp(150.0f);
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) GroupCallActivity.this.listView.getLayoutParams();
                if (GroupCallActivity.isTabletMode) {
                    layoutParams3.gravity = GroupCallActivity.this.hasVideo ? 5 : 1;
                    layoutParams3.width = AndroidUtilities.dp(320.0f);
                    int dp = AndroidUtilities.dp(4.0f);
                    layoutParams3.leftMargin = dp;
                    layoutParams3.rightMargin = dp;
                    layoutParams3.bottomMargin = buttonsGradientSize;
                    layoutParams3.topMargin = ActionBar.getCurrentActionBarHeight();
                    listViewPaddingBottom = AndroidUtilities.dp(60.0f);
                } else if (GroupCallActivity.isLandscapeMode) {
                    layoutParams3.gravity = 51;
                    layoutParams3.width = -1;
                    layoutParams3.topMargin = ActionBar.getCurrentActionBarHeight();
                    layoutParams3.bottomMargin = AndroidUtilities.dp(14.0f);
                    layoutParams3.rightMargin = AndroidUtilities.dp(90.0f);
                    layoutParams3.leftMargin = AndroidUtilities.dp(14.0f);
                    listViewPaddingBottom = 0;
                } else {
                    layoutParams3.gravity = 51;
                    layoutParams3.width = -1;
                    listViewPaddingBottom = AndroidUtilities.dp(60.0f);
                    layoutParams3.bottomMargin = buttonsGradientSize;
                    layoutParams3.topMargin = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(14.0f);
                    int dp2 = AndroidUtilities.dp(14.0f);
                    layoutParams3.leftMargin = dp2;
                    layoutParams3.rightMargin = dp2;
                }
                int i4 = 81;
                if (!GroupCallActivity.isLandscapeMode || GroupCallActivity.isTabletMode) {
                    GroupCallActivity.this.buttonsBackgroundGradientView.setVisibility(0);
                    FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) GroupCallActivity.this.buttonsBackgroundGradientView.getLayoutParams();
                    layoutParams4.bottomMargin = buttonsGradientSize;
                    if (GroupCallActivity.isTabletMode) {
                        layoutParams4.gravity = GroupCallActivity.this.hasVideo ? 85 : 81;
                        layoutParams4.width = AndroidUtilities.dp(328.0f);
                    } else {
                        layoutParams4.width = -1;
                    }
                    GroupCallActivity.this.buttonsBackgroundGradientView2.setVisibility(0);
                    FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) GroupCallActivity.this.buttonsBackgroundGradientView2.getLayoutParams();
                    layoutParams5.height = buttonsGradientSize;
                    if (GroupCallActivity.isTabletMode) {
                        layoutParams5.gravity = GroupCallActivity.this.hasVideo ? 85 : 81;
                        layoutParams5.width = AndroidUtilities.dp(328.0f);
                    } else {
                        layoutParams5.width = -1;
                    }
                } else {
                    GroupCallActivity.this.buttonsBackgroundGradientView.setVisibility(8);
                    GroupCallActivity.this.buttonsBackgroundGradientView2.setVisibility(8);
                }
                if (GroupCallActivity.isLandscapeMode) {
                    GroupCallActivity.this.fullscreenUsersListView.setPadding(0, AndroidUtilities.dp(9.0f), 0, AndroidUtilities.dp(9.0f));
                } else {
                    GroupCallActivity.this.fullscreenUsersListView.setPadding(AndroidUtilities.dp(9.0f), 0, AndroidUtilities.dp(9.0f), 0);
                }
                FrameLayout.LayoutParams layoutParams6 = (FrameLayout.LayoutParams) GroupCallActivity.this.buttonsContainer.getLayoutParams();
                if (GroupCallActivity.isTabletMode) {
                    layoutParams6.width = AndroidUtilities.dp(320.0f);
                    layoutParams6.height = AndroidUtilities.dp(200.0f);
                    if (GroupCallActivity.this.hasVideo) {
                        i4 = 85;
                    }
                    layoutParams6.gravity = i4;
                    layoutParams6.rightMargin = 0;
                } else if (GroupCallActivity.isLandscapeMode) {
                    layoutParams6.width = AndroidUtilities.dp(90.0f);
                    layoutParams6.height = -1;
                    layoutParams6.gravity = 53;
                } else {
                    layoutParams6.width = -1;
                    layoutParams6.height = AndroidUtilities.dp(200.0f);
                    layoutParams6.gravity = 81;
                    layoutParams6.rightMargin = 0;
                }
                if (GroupCallActivity.isLandscapeMode && !GroupCallActivity.isTabletMode) {
                    ((FrameLayout.LayoutParams) GroupCallActivity.this.actionBar.getLayoutParams()).rightMargin = AndroidUtilities.dp(90.0f);
                    ((FrameLayout.LayoutParams) GroupCallActivity.this.menuItemsContainer.getLayoutParams()).rightMargin = AndroidUtilities.dp(90.0f);
                    ((FrameLayout.LayoutParams) GroupCallActivity.this.actionBarBackground.getLayoutParams()).rightMargin = AndroidUtilities.dp(90.0f);
                    ((FrameLayout.LayoutParams) GroupCallActivity.this.actionBarShadow.getLayoutParams()).rightMargin = AndroidUtilities.dp(90.0f);
                } else {
                    ((FrameLayout.LayoutParams) GroupCallActivity.this.actionBar.getLayoutParams()).rightMargin = 0;
                    ((FrameLayout.LayoutParams) GroupCallActivity.this.menuItemsContainer.getLayoutParams()).rightMargin = 0;
                    ((FrameLayout.LayoutParams) GroupCallActivity.this.actionBarBackground.getLayoutParams()).rightMargin = 0;
                    ((FrameLayout.LayoutParams) GroupCallActivity.this.actionBarShadow.getLayoutParams()).rightMargin = 0;
                }
                FrameLayout.LayoutParams layoutParams7 = (FrameLayout.LayoutParams) GroupCallActivity.this.fullscreenUsersListView.getLayoutParams();
                if (GroupCallActivity.isLandscapeMode) {
                    if (((LinearLayoutManager) GroupCallActivity.this.fullscreenUsersListView.getLayoutManager()).getOrientation() != 1) {
                        ((LinearLayoutManager) GroupCallActivity.this.fullscreenUsersListView.getLayoutManager()).setOrientation(1);
                    }
                    layoutParams7.height = -1;
                    layoutParams7.width = AndroidUtilities.dp(80.0f);
                    layoutParams7.gravity = 53;
                    layoutParams7.rightMargin = AndroidUtilities.dp(100.0f);
                    layoutParams7.bottomMargin = 0;
                } else {
                    if (((LinearLayoutManager) GroupCallActivity.this.fullscreenUsersListView.getLayoutManager()).getOrientation() != 0) {
                        ((LinearLayoutManager) GroupCallActivity.this.fullscreenUsersListView.getLayoutManager()).setOrientation(0);
                    }
                    layoutParams7.height = AndroidUtilities.dp(80.0f);
                    layoutParams7.width = -1;
                    layoutParams7.gravity = 80;
                    layoutParams7.rightMargin = 0;
                    layoutParams7.bottomMargin = AndroidUtilities.dp(100.0f);
                }
                ((FrameLayout.LayoutParams) GroupCallActivity.this.actionBarShadow.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
                int contentSize = Math.max(AndroidUtilities.dp(259.0f), (availableHeight / 5) * 3);
                int padding = GroupCallActivity.isTabletMode ? 0 : Math.max(0, (availableHeight - contentSize) + AndroidUtilities.dp(8.0f));
                if (GroupCallActivity.this.listView.getPaddingTop() != padding || GroupCallActivity.this.listView.getPaddingBottom() != listViewPaddingBottom) {
                    GroupCallActivity.this.listView.setPadding(0, padding, 0, listViewPaddingBottom);
                }
                if (GroupCallActivity.this.scheduleStartAtTextView != null) {
                    int y = (((availableHeight - padding) + AndroidUtilities.dp(60.0f)) / 2) + padding;
                    FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) GroupCallActivity.this.scheduleStartInTextView.getLayoutParams();
                    layoutParams1.topMargin = y - AndroidUtilities.dp(30.0f);
                    FrameLayout.LayoutParams layoutParams22 = (FrameLayout.LayoutParams) GroupCallActivity.this.scheduleStartAtTextView.getLayoutParams();
                    layoutParams22.topMargin = AndroidUtilities.dp(80.0f) + y;
                    FrameLayout.LayoutParams layoutParams32 = (FrameLayout.LayoutParams) GroupCallActivity.this.scheduleTimeTextView.getLayoutParams();
                    if (layoutParams1.topMargin < ActionBar.getCurrentActionBarHeight() || layoutParams22.topMargin + AndroidUtilities.dp(20.0f) > totalHeight - AndroidUtilities.dp(231.0f)) {
                        GroupCallActivity.this.scheduleStartInTextView.setVisibility(4);
                        GroupCallActivity.this.scheduleStartAtTextView.setVisibility(4);
                        layoutParams32.topMargin = y - AndroidUtilities.dp(20.0f);
                    } else {
                        GroupCallActivity.this.scheduleStartInTextView.setVisibility(0);
                        GroupCallActivity.this.scheduleStartAtTextView.setVisibility(0);
                        layoutParams32.topMargin = y;
                    }
                }
                for (int i5 = 0; i5 < GroupCallActivity.this.attachedRenderers.size(); i5++) {
                    ((GroupCallMiniTextureView) GroupCallActivity.this.attachedRenderers.get(i5)).setFullscreenMode(GroupCallActivity.this.renderersContainer.inFullscreenMode, true);
                }
                this.ignoreLayout = false;
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, C.BUFFER_FLAG_ENCRYPTED));
                int currentSize = getMeasuredHeight() + (getMeasuredWidth() << 16);
                if (currentSize != this.lastSize) {
                    this.lastSize = currentSize;
                    GroupCallActivity.this.dismissAvatarPreview(false);
                }
                GroupCallActivity.this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                boolean needAnimate = false;
                float fromX = 0.0f;
                if (GroupCallActivity.isTabletMode && this.localHasVideo != GroupCallActivity.this.hasVideo && this.wasLayout) {
                    needAnimate = true;
                    fromX = GroupCallActivity.this.listView.getX();
                }
                this.localHasVideo = GroupCallActivity.this.hasVideo;
                GroupCallActivity.this.renderersContainer.inLayout = true;
                super.onLayout(changed, l, t, r, b);
                GroupCallActivity.this.renderersContainer.inLayout = false;
                GroupCallActivity.this.updateLayout(false);
                this.wasLayout = true;
                if (needAnimate && GroupCallActivity.this.listView.getLeft() != fromX) {
                    float dx = fromX - GroupCallActivity.this.listView.getLeft();
                    GroupCallActivity.this.listView.setTranslationX(dx);
                    GroupCallActivity.this.buttonsContainer.setTranslationX(dx);
                    GroupCallActivity.this.buttonsBackgroundGradientView.setTranslationX(dx);
                    GroupCallActivity.this.buttonsBackgroundGradientView2.setTranslationX(dx);
                    GroupCallActivity.this.listView.animate().translationX(0.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    GroupCallActivity.this.buttonsBackgroundGradientView.animate().translationX(0.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    GroupCallActivity.this.buttonsBackgroundGradientView2.animate().translationX(0.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    GroupCallActivity.this.buttonsContainer.animate().translationX(0.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                }
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (GroupCallActivity.this.scrimView != null && ev.getAction() == 0) {
                    float x = ev.getX();
                    float y = ev.getY();
                    boolean dismissScrim = true;
                    this.rect.set(GroupCallActivity.this.scrimPopupLayout.getX(), GroupCallActivity.this.scrimPopupLayout.getY(), GroupCallActivity.this.scrimPopupLayout.getX() + GroupCallActivity.this.scrimPopupLayout.getMeasuredWidth(), GroupCallActivity.this.scrimPopupLayout.getY() + GroupCallActivity.this.scrimPopupLayout.getMeasuredHeight());
                    if (this.rect.contains(x, y)) {
                        dismissScrim = false;
                    }
                    this.rect.set(GroupCallActivity.this.avatarPreviewContainer.getX(), GroupCallActivity.this.avatarPreviewContainer.getY(), GroupCallActivity.this.avatarPreviewContainer.getX() + GroupCallActivity.this.avatarPreviewContainer.getMeasuredWidth(), GroupCallActivity.this.avatarPreviewContainer.getY() + GroupCallActivity.this.avatarPreviewContainer.getMeasuredWidth() + GroupCallActivity.this.scrimView.getMeasuredHeight());
                    if (this.rect.contains(x, y)) {
                        dismissScrim = false;
                    }
                    if (dismissScrim) {
                        GroupCallActivity.this.dismissAvatarPreview(true);
                        return true;
                    }
                }
                if (ev.getAction() == 0 && GroupCallActivity.this.scrollOffsetY != 0.0f && ev.getY() < GroupCallActivity.this.scrollOffsetY - AndroidUtilities.dp(37.0f) && GroupCallActivity.this.actionBar.getAlpha() == 0.0f && !GroupCallActivity.this.avatarsPreviewShowed && GroupCallActivity.this.previewDialog == null && !GroupCallActivity.this.renderersContainer.inFullscreenMode) {
                    GroupCallActivity.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(ev);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                return !GroupCallActivity.this.isDismissed() && super.onTouchEvent(e);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                int offset = AndroidUtilities.dp(74.0f);
                float top = GroupCallActivity.this.scrollOffsetY - offset;
                int height = getMeasuredHeight() + AndroidUtilities.dp(15.0f) + GroupCallActivity.this.backgroundPaddingTop;
                float rad = 1.0f;
                if (GroupCallActivity.this.backgroundPaddingTop + top < ActionBar.getCurrentActionBarHeight()) {
                    int willMoveUpTo = (offset - GroupCallActivity.this.backgroundPaddingTop) - AndroidUtilities.dp(14.0f);
                    float moveProgress = Math.min(1.0f, ((ActionBar.getCurrentActionBarHeight() - top) - GroupCallActivity.this.backgroundPaddingTop) / willMoveUpTo);
                    int diff = (int) ((ActionBar.getCurrentActionBarHeight() - willMoveUpTo) * moveProgress);
                    top -= diff;
                    height += diff;
                    rad = 1.0f - moveProgress;
                }
                float top2 = top + getPaddingTop();
                if (GroupCallActivity.this.renderersContainer.progressToFullscreenMode != 1.0f) {
                    GroupCallActivity.this.shadowDrawable.setBounds(0, (int) top2, getMeasuredWidth(), height);
                    GroupCallActivity.this.shadowDrawable.draw(canvas);
                    if (rad != 1.0f) {
                        Theme.dialogs_onlineCirclePaint.setColor(GroupCallActivity.this.backgroundColor);
                        this.rect.set(GroupCallActivity.this.backgroundPaddingLeft, GroupCallActivity.this.backgroundPaddingTop + top2, getMeasuredWidth() - GroupCallActivity.this.backgroundPaddingLeft, GroupCallActivity.this.backgroundPaddingTop + top2 + AndroidUtilities.dp(24.0f));
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * rad, AndroidUtilities.dp(12.0f) * rad, Theme.dialogs_onlineCirclePaint);
                    }
                    int finalColor = Color.argb((int) (GroupCallActivity.this.actionBar.getAlpha() * 255.0f), (int) (Color.red(GroupCallActivity.this.backgroundColor) * 0.8f), (int) (Color.green(GroupCallActivity.this.backgroundColor) * 0.8f), (int) (Color.blue(GroupCallActivity.this.backgroundColor) * 0.8f));
                    Theme.dialogs_onlineCirclePaint.setColor(finalColor);
                    float bottom = GroupCallActivity.this.getStatusBarHeight();
                    canvas.drawRect(GroupCallActivity.this.backgroundPaddingLeft, 0.0f, getMeasuredWidth() - GroupCallActivity.this.backgroundPaddingLeft, bottom, Theme.dialogs_onlineCirclePaint);
                    if (GroupCallActivity.this.previewDialog != null) {
                        Theme.dialogs_onlineCirclePaint.setColor(GroupCallActivity.this.previewDialog.getBackgroundColor());
                        canvas.drawRect(GroupCallActivity.this.backgroundPaddingLeft, 0.0f, getMeasuredWidth() - GroupCallActivity.this.backgroundPaddingLeft, GroupCallActivity.this.getStatusBarHeight(), Theme.dialogs_onlineCirclePaint);
                    }
                }
                if (GroupCallActivity.this.renderersContainer.progressToFullscreenMode != 0.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_actionBar), (int) (GroupCallActivity.this.renderersContainer.progressToFullscreenMode * 255.0f)));
                    canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), Theme.dialogs_onlineCirclePaint);
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                float listTop;
                float listTop2;
                GroupCallUserCell scrimViewLocal;
                float childY;
                float childX;
                GroupCallUserCell scrimViewLocal2;
                Path roundPath;
                float x;
                float y;
                float toY;
                float toX;
                float fromY;
                float fromX;
                if (GroupCallActivity.isTabletMode) {
                    GroupCallActivity.this.buttonsContainer.setTranslationY(0.0f);
                    GroupCallActivity.this.fullscreenUsersListView.setTranslationY(0.0f);
                    GroupCallActivity.this.buttonsContainer.setTranslationX(0.0f);
                    GroupCallActivity.this.fullscreenUsersListView.setTranslationY(0.0f);
                } else if (GroupCallActivity.isLandscapeMode) {
                    GroupCallActivity.this.buttonsContainer.setTranslationY(0.0f);
                    GroupCallActivity.this.fullscreenUsersListView.setTranslationY(0.0f);
                    GroupCallActivity.this.buttonsContainer.setTranslationX(GroupCallActivity.this.progressToHideUi * AndroidUtilities.dp(94.0f));
                    GroupCallActivity.this.fullscreenUsersListView.setTranslationX(GroupCallActivity.this.progressToHideUi * AndroidUtilities.dp(94.0f));
                } else {
                    GroupCallActivity.this.buttonsContainer.setTranslationX(0.0f);
                    GroupCallActivity.this.fullscreenUsersListView.setTranslationX(0.0f);
                    GroupCallActivity.this.buttonsContainer.setTranslationY(GroupCallActivity.this.progressToHideUi * AndroidUtilities.dp(94.0f));
                    GroupCallActivity.this.fullscreenUsersListView.setTranslationY(GroupCallActivity.this.progressToHideUi * AndroidUtilities.dp(94.0f));
                }
                for (int i3 = 0; i3 < GroupCallActivity.this.listView.getChildCount(); i3++) {
                    View view = GroupCallActivity.this.listView.getChildAt(i3);
                    if (view instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) view).setDrawAvatar(true);
                    }
                    if (!(view instanceof GroupCallGridCell)) {
                        if (view.getMeasuredWidth() != GroupCallActivity.this.listView.getMeasuredWidth()) {
                            view.setTranslationX((GroupCallActivity.this.listView.getMeasuredWidth() - view.getMeasuredWidth()) >> 1);
                        } else {
                            view.setTranslationX(0.0f);
                        }
                    }
                }
                if (GroupCallActivity.this.renderersContainer.isAnimating()) {
                    if (GroupCallActivity.this.fullscreenUsersListView.getVisibility() == 0) {
                        this.listCells.clear();
                        for (int i4 = 0; i4 < GroupCallActivity.this.listView.getChildCount(); i4++) {
                            View view2 = GroupCallActivity.this.listView.getChildAt(i4);
                            if ((view2 instanceof GroupCallGridCell) && GroupCallActivity.this.listView.getChildAdapterPosition(view2) >= 0) {
                                GroupCallGridCell cell = (GroupCallGridCell) view2;
                                if (cell.getRenderer() != GroupCallActivity.this.renderersContainer.fullscreenTextureView) {
                                    this.listCells.put(cell.getParticipant(), view2);
                                }
                            } else if ((view2 instanceof GroupCallUserCell) && GroupCallActivity.this.listView.getChildAdapterPosition(view2) >= 0) {
                                GroupCallUserCell cell2 = (GroupCallUserCell) view2;
                                this.listCells.put(cell2.getParticipant(), cell2);
                            }
                        }
                        for (int i5 = 0; i5 < GroupCallActivity.this.fullscreenUsersListView.getChildCount(); i5++) {
                            GroupCallFullscreenAdapter.GroupCallUserCell cellTo = (GroupCallFullscreenAdapter.GroupCallUserCell) GroupCallActivity.this.fullscreenUsersListView.getChildAt(i5);
                            View cellFrom = this.listCells.get(cellTo.getVideoParticipant());
                            if (cellFrom == null) {
                                cellFrom = this.listCells.get(cellTo.getParticipant());
                            }
                            float progressToFullscreenMode = GroupCallActivity.this.renderersContainer.progressToFullscreenMode;
                            if (!GroupCallActivity.this.fullscreenListItemAnimator.isRunning()) {
                                cellTo.setAlpha(1.0f);
                            }
                            if (cellFrom != null) {
                                if (cellFrom instanceof GroupCallGridCell) {
                                    GroupCallGridCell gridCell = (GroupCallGridCell) cellFrom;
                                    fromX = (gridCell.getLeft() + GroupCallActivity.this.listView.getX()) - GroupCallActivity.this.renderersContainer.getLeft();
                                    fromY = (gridCell.getTop() + GroupCallActivity.this.listView.getY()) - GroupCallActivity.this.renderersContainer.getTop();
                                    toX = cellTo.getLeft() + GroupCallActivity.this.fullscreenUsersListView.getX();
                                    toY = cellTo.getTop() + GroupCallActivity.this.fullscreenUsersListView.getY();
                                } else {
                                    GroupCallUserCell userCell = (GroupCallUserCell) cellFrom;
                                    fromX = ((userCell.getLeft() + GroupCallActivity.this.listView.getX()) - GroupCallActivity.this.renderersContainer.getLeft()) + userCell.getAvatarImageView().getLeft() + (userCell.getAvatarImageView().getMeasuredWidth() >> 1);
                                    fromY = ((userCell.getTop() + GroupCallActivity.this.listView.getY()) - GroupCallActivity.this.renderersContainer.getTop()) + userCell.getAvatarImageView().getTop() + (userCell.getAvatarImageView().getMeasuredHeight() >> 1);
                                    toX = cellTo.getLeft() + GroupCallActivity.this.fullscreenUsersListView.getX() + (cellTo.getMeasuredWidth() >> 1);
                                    toY = cellTo.getTop() + GroupCallActivity.this.fullscreenUsersListView.getY() + (cellTo.getMeasuredHeight() >> 1);
                                    userCell.setDrawAvatar(false);
                                }
                                cellTo.setTranslationX((fromX - toX) * (1.0f - progressToFullscreenMode));
                                cellTo.setTranslationY((fromY - toY) * (1.0f - progressToFullscreenMode));
                                cellTo.setScaleX(1.0f);
                                cellTo.setScaleY(1.0f);
                                cellTo.setProgressToFullscreen(progressToFullscreenMode);
                            } else {
                                cellTo.setScaleX(1.0f);
                                cellTo.setScaleY(1.0f);
                                cellTo.setTranslationX(0.0f);
                                cellTo.setTranslationY(0.0f);
                                cellTo.setProgressToFullscreen(1.0f);
                                if (cellTo.getRenderer() == null) {
                                    cellTo.setAlpha(progressToFullscreenMode);
                                }
                            }
                        }
                    }
                } else {
                    for (int i6 = 0; i6 < GroupCallActivity.this.fullscreenUsersListView.getChildCount(); i6++) {
                        ((GroupCallFullscreenAdapter.GroupCallUserCell) GroupCallActivity.this.fullscreenUsersListView.getChildAt(i6)).setProgressToFullscreen(1.0f);
                    }
                }
                for (int i7 = 0; i7 < GroupCallActivity.this.attachedRenderers.size(); i7++) {
                    ((GroupCallMiniTextureView) GroupCallActivity.this.attachedRenderers.get(i7)).updatePosition(GroupCallActivity.this.listView, GroupCallActivity.this.tabletVideoGridView, GroupCallActivity.this.fullscreenUsersListView, GroupCallActivity.this.renderersContainer);
                }
                if (!GroupCallActivity.isTabletMode) {
                    GroupCallActivity.this.buttonsBackgroundGradientView.setAlpha(1.0f - GroupCallActivity.this.renderersContainer.progressToFullscreenMode);
                    GroupCallActivity.this.buttonsBackgroundGradientView2.setAlpha(1.0f - GroupCallActivity.this.renderersContainer.progressToFullscreenMode);
                } else {
                    GroupCallActivity.this.buttonsBackgroundGradientView.setAlpha(1.0f);
                    GroupCallActivity.this.buttonsBackgroundGradientView2.setAlpha(1.0f);
                }
                if (GroupCallActivity.this.renderersContainer.swipedBack) {
                    GroupCallActivity.this.listView.setAlpha(1.0f - GroupCallActivity.this.renderersContainer.progressToFullscreenMode);
                } else {
                    GroupCallActivity.this.listView.setAlpha(1.0f);
                }
                super.dispatchDraw(canvas);
                if (!GroupCallActivity.this.drawingForBlur) {
                    if (GroupCallActivity.this.avatarsPreviewShowed) {
                        if (GroupCallActivity.this.scrimView != null) {
                            if (!GroupCallActivity.this.useBlur) {
                                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), GroupCallActivity.this.scrimPaint);
                            }
                            float listTop3 = GroupCallActivity.this.listView.getY();
                            float[] radii = new float[8];
                            Path roundPath2 = new Path();
                            int count = GroupCallActivity.this.listView.getChildCount();
                            float viewClipBottom = GroupCallActivity.this.listView.getY() + GroupCallActivity.this.listView.getMeasuredHeight();
                            GroupCallUserCell scrimViewLocal3 = null;
                            if (!GroupCallActivity.this.hasScrimAnchorView) {
                                GroupCallUserCell scrimViewLocal4 = GroupCallActivity.this.scrimView;
                                scrimViewLocal = scrimViewLocal4;
                            } else {
                                int num = 0;
                                while (true) {
                                    if (num >= count) {
                                        break;
                                    } else if (GroupCallActivity.this.listView.getChildAt(num) == GroupCallActivity.this.scrimView) {
                                        scrimViewLocal3 = GroupCallActivity.this.scrimView;
                                        break;
                                    } else {
                                        num++;
                                    }
                                }
                                scrimViewLocal = scrimViewLocal3;
                            }
                            if (scrimViewLocal != null && listTop3 < viewClipBottom) {
                                canvas.save();
                                if (GroupCallActivity.this.scrimFullscreenView == null) {
                                    canvas.clipRect(0.0f, (1.0f - GroupCallActivity.this.progressToAvatarPreview) * listTop3, getMeasuredWidth(), ((1.0f - GroupCallActivity.this.progressToAvatarPreview) * viewClipBottom) + (getMeasuredHeight() * GroupCallActivity.this.progressToAvatarPreview));
                                }
                                if (!GroupCallActivity.this.hasScrimAnchorView) {
                                    childY = GroupCallActivity.this.avatarPreviewContainer.getTop() + GroupCallActivity.this.avatarPreviewContainer.getMeasuredWidth();
                                    childX = GroupCallActivity.this.avatarPreviewContainer.getLeft();
                                } else {
                                    childY = ((GroupCallActivity.this.listView.getY() + scrimViewLocal.getY()) * (1.0f - GroupCallActivity.this.progressToAvatarPreview)) + ((GroupCallActivity.this.avatarPreviewContainer.getTop() + GroupCallActivity.this.avatarPreviewContainer.getMeasuredWidth()) * GroupCallActivity.this.progressToAvatarPreview);
                                    childX = ((GroupCallActivity.this.listView.getLeft() + scrimViewLocal.getX()) * (1.0f - GroupCallActivity.this.progressToAvatarPreview)) + (GroupCallActivity.this.avatarPreviewContainer.getLeft() * GroupCallActivity.this.progressToAvatarPreview);
                                }
                                canvas.translate(childX, childY);
                                if (!GroupCallActivity.this.hasScrimAnchorView) {
                                    scrimViewLocal2 = scrimViewLocal;
                                    roundPath = roundPath2;
                                    canvas.saveLayerAlpha(0.0f, 0.0f, scrimViewLocal.getMeasuredWidth(), scrimViewLocal.getClipHeight(), (int) (GroupCallActivity.this.progressToAvatarPreview * 255.0f), 31);
                                } else {
                                    scrimViewLocal2 = scrimViewLocal;
                                    roundPath = roundPath2;
                                    canvas.save();
                                }
                                float pr = 1.0f - CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0f - GroupCallActivity.this.progressToAvatarPreview);
                                int h = (int) (scrimViewLocal2.getMeasuredHeight() + ((scrimViewLocal2.getClipHeight() - scrimViewLocal2.getMeasuredHeight()) * pr));
                                this.rect.set(0.0f, 0.0f, scrimViewLocal2.getMeasuredWidth(), h);
                                GroupCallUserCell scrimViewLocal5 = scrimViewLocal2;
                                scrimViewLocal5.setProgressToAvatarPreview(GroupCallActivity.this.hasScrimAnchorView ? GroupCallActivity.this.progressToAvatarPreview : 1.0f);
                                for (int i8 = 0; i8 < 4; i8++) {
                                    radii[i8] = AndroidUtilities.dp(13.0f) * (1.0f - GroupCallActivity.this.progressToAvatarPreview);
                                    radii[i8 + 4] = AndroidUtilities.dp(13.0f);
                                }
                                roundPath.reset();
                                roundPath.addRoundRect(this.rect, radii, Path.Direction.CW);
                                roundPath.close();
                                canvas.drawPath(roundPath, GroupCallActivity.this.listViewBackgroundPaint);
                                scrimViewLocal5.draw(canvas);
                                canvas.restore();
                                canvas.restore();
                                if (GroupCallActivity.this.scrimPopupLayout != null) {
                                    float y2 = childY + h;
                                    float x2 = (getMeasuredWidth() - GroupCallActivity.this.scrimPopupLayout.getMeasuredWidth()) - AndroidUtilities.dp(14.0f);
                                    if (GroupCallActivity.this.progressToAvatarPreview != 1.0f) {
                                        x = x2;
                                        y = y2;
                                        canvas.saveLayerAlpha(x2, y2, x2 + GroupCallActivity.this.scrimPopupLayout.getMeasuredWidth(), y2 + GroupCallActivity.this.scrimPopupLayout.getMeasuredHeight(), (int) (GroupCallActivity.this.progressToAvatarPreview * 255.0f), 31);
                                    } else {
                                        x = x2;
                                        y = y2;
                                        canvas.save();
                                    }
                                    GroupCallActivity.this.scrimPopupLayout.setTranslationX(x - GroupCallActivity.this.scrimPopupLayout.getLeft());
                                    GroupCallActivity.this.scrimPopupLayout.setTranslationY(y - GroupCallActivity.this.scrimPopupLayout.getTop());
                                    float scale = (GroupCallActivity.this.progressToAvatarPreview * 0.2f) + 0.8f;
                                    canvas.scale(scale, scale, x + (GroupCallActivity.this.scrimPopupLayout.getMeasuredWidth() / 2.0f), y);
                                    canvas.translate(x, y);
                                    GroupCallActivity.this.scrimPopupLayout.draw(canvas);
                                    canvas.restore();
                                }
                            }
                            if (!GroupCallActivity.this.pinchToZoomHelper.isInOverlayMode()) {
                                canvas.save();
                                if (GroupCallActivity.this.hasScrimAnchorView && GroupCallActivity.this.scrimFullscreenView == null) {
                                    canvas.clipRect(0.0f, (1.0f - GroupCallActivity.this.progressToAvatarPreview) * listTop3, getMeasuredWidth(), ((1.0f - GroupCallActivity.this.progressToAvatarPreview) * viewClipBottom) + (getMeasuredHeight() * GroupCallActivity.this.progressToAvatarPreview));
                                }
                                canvas.scale(GroupCallActivity.this.avatarPreviewContainer.getScaleX(), GroupCallActivity.this.avatarPreviewContainer.getScaleY(), GroupCallActivity.this.avatarPreviewContainer.getX(), GroupCallActivity.this.avatarPreviewContainer.getY());
                                canvas.translate(GroupCallActivity.this.avatarPreviewContainer.getX(), GroupCallActivity.this.avatarPreviewContainer.getY());
                                GroupCallActivity.this.avatarPreviewContainer.draw(canvas);
                                canvas.restore();
                            }
                        }
                        if (GroupCallActivity.this.progressToAvatarPreview != 1.0f && GroupCallActivity.this.scrimFullscreenView == null) {
                            canvas.saveLayerAlpha((int) GroupCallActivity.this.buttonsBackgroundGradientView2.getX(), (int) GroupCallActivity.this.buttonsBackgroundGradientView.getY(), (int) (GroupCallActivity.this.buttonsBackgroundGradientView2.getX() + GroupCallActivity.this.buttonsBackgroundGradientView2.getMeasuredWidth()), getMeasuredHeight(), (int) ((1.0f - GroupCallActivity.this.progressToAvatarPreview) * 255.0f), 31);
                            canvas.save();
                            canvas.translate(GroupCallActivity.this.buttonsBackgroundGradientView2.getX(), GroupCallActivity.this.buttonsBackgroundGradientView2.getY());
                            GroupCallActivity.this.buttonsBackgroundGradientView2.draw(canvas);
                            canvas.restore();
                            canvas.save();
                            canvas.translate(GroupCallActivity.this.buttonsBackgroundGradientView.getX(), GroupCallActivity.this.buttonsBackgroundGradientView.getY());
                            GroupCallActivity.this.buttonsBackgroundGradientView.draw(canvas);
                            canvas.restore();
                            canvas.save();
                            canvas.translate(GroupCallActivity.this.buttonsContainer.getX(), GroupCallActivity.this.buttonsContainer.getY());
                            GroupCallActivity.this.buttonsContainer.draw(canvas);
                            canvas.restore();
                            for (int i9 = 0; i9 < 2; i9++) {
                                if (GroupCallActivity.this.undoView[i9].getVisibility() == 0) {
                                    canvas.save();
                                    canvas.translate(GroupCallActivity.this.undoView[1].getX(), GroupCallActivity.this.undoView[1].getY());
                                    GroupCallActivity.this.undoView[1].draw(canvas);
                                    canvas.restore();
                                }
                            }
                            canvas.restore();
                        }
                    } else if (GroupCallActivity.this.scrimView != null) {
                        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), GroupCallActivity.this.scrimPaint);
                        float listTop4 = GroupCallActivity.this.listView.getY();
                        float y3 = GroupCallActivity.this.listView.getY() + GroupCallActivity.this.listView.getMeasuredHeight();
                        if (GroupCallActivity.this.hasScrimAnchorView) {
                            int count2 = GroupCallActivity.this.listView.getChildCount();
                            int num2 = 0;
                            while (num2 < count2) {
                                View child = GroupCallActivity.this.listView.getChildAt(num2);
                                if (child == GroupCallActivity.this.scrimView) {
                                    float viewClipLeft = Math.max(GroupCallActivity.this.listView.getLeft(), GroupCallActivity.this.listView.getLeft() + child.getX());
                                    float viewClipTop = Math.max(listTop4, GroupCallActivity.this.listView.getY() + child.getY());
                                    float viewClipRight = Math.min(GroupCallActivity.this.listView.getRight(), GroupCallActivity.this.listView.getLeft() + child.getX() + child.getMeasuredWidth());
                                    float viewClipBottom2 = Math.min(GroupCallActivity.this.listView.getY() + GroupCallActivity.this.listView.getMeasuredHeight(), GroupCallActivity.this.listView.getY() + child.getY() + GroupCallActivity.this.scrimView.getClipHeight());
                                    if (viewClipTop >= viewClipBottom2) {
                                        listTop = listTop4;
                                    } else {
                                        if (child.getAlpha() != 1.0f) {
                                            listTop = listTop4;
                                            listTop2 = viewClipRight;
                                            canvas.saveLayerAlpha(viewClipLeft, viewClipTop, viewClipRight, viewClipBottom2, (int) (child.getAlpha() * 255.0f), 31);
                                        } else {
                                            listTop = listTop4;
                                            listTop2 = viewClipRight;
                                            canvas.save();
                                        }
                                        canvas.clipRect(viewClipLeft, viewClipTop, listTop2, getMeasuredHeight());
                                        canvas.translate(GroupCallActivity.this.listView.getLeft() + child.getX(), GroupCallActivity.this.listView.getY() + child.getY());
                                        float progress = GroupCallActivity.this.scrimPaint.getAlpha() / 100.0f;
                                        float pr2 = 1.0f - CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0f - progress);
                                        this.rect.set(0.0f, 0.0f, child.getMeasuredWidth(), (int) (GroupCallActivity.this.scrimView.getMeasuredHeight() + ((GroupCallActivity.this.scrimView.getClipHeight() - GroupCallActivity.this.scrimView.getMeasuredHeight()) * pr2)));
                                        GroupCallActivity.this.scrimView.setAboutVisibleProgress(GroupCallActivity.this.listViewBackgroundPaint.getColor(), progress);
                                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), GroupCallActivity.this.listViewBackgroundPaint);
                                        child.draw(canvas);
                                        canvas.restore();
                                    }
                                } else {
                                    listTop = listTop4;
                                }
                                num2++;
                                listTop4 = listTop;
                            }
                        } else if (GroupCallActivity.this.scrimFullscreenView == null) {
                            if (GroupCallActivity.this.scrimRenderer != null && GroupCallActivity.this.scrimRenderer.isAttached()) {
                                canvas.save();
                                float x3 = GroupCallActivity.this.scrimRenderer.getX() + GroupCallActivity.this.renderersContainer.getX();
                                float y4 = GroupCallActivity.this.scrimRenderer.getY() + GroupCallActivity.this.renderersContainer.getY();
                                canvas.translate(x3, y4);
                                GroupCallActivity.this.scrimRenderer.draw(canvas);
                                canvas.restore();
                            }
                        } else {
                            canvas.save();
                            float x4 = GroupCallActivity.this.scrimFullscreenView.getX() + GroupCallActivity.this.fullscreenUsersListView.getX() + GroupCallActivity.this.renderersContainer.getX();
                            float y5 = GroupCallActivity.this.scrimFullscreenView.getY() + GroupCallActivity.this.fullscreenUsersListView.getY() + GroupCallActivity.this.renderersContainer.getY();
                            canvas.translate(x4, y5);
                            if (GroupCallActivity.this.scrimFullscreenView.getRenderer() == null || !GroupCallActivity.this.scrimFullscreenView.getRenderer().isAttached() || GroupCallActivity.this.scrimFullscreenView.getRenderer().showingInFullscreen) {
                                GroupCallActivity.this.scrimFullscreenView.draw(canvas);
                            } else {
                                GroupCallActivity.this.scrimFullscreenView.getRenderer().draw(canvas);
                            }
                            GroupCallActivity.this.scrimFullscreenView.drawOverlays(canvas);
                            canvas.restore();
                        }
                    }
                }
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (!GroupCallActivity.isTabletMode && GroupCallActivity.this.renderersContainer.progressToFullscreenMode == 1.0f && (child == GroupCallActivity.this.actionBar || child == GroupCallActivity.this.actionBarShadow || child == GroupCallActivity.this.actionBarBackground || child == GroupCallActivity.this.titleTextView || child == GroupCallActivity.this.menuItemsContainer)) {
                    return true;
                }
                if (!GroupCallActivity.this.drawingForBlur || child != GroupCallActivity.this.renderersContainer) {
                    if (child == GroupCallActivity.this.avatarPreviewContainer || child == GroupCallActivity.this.scrimPopupLayout || child == GroupCallActivity.this.scrimView) {
                        return true;
                    }
                    if (GroupCallActivity.this.contentFullyOverlayed && GroupCallActivity.this.useBlur && (child == GroupCallActivity.this.listView || child == GroupCallActivity.this.buttonsContainer)) {
                        return true;
                    }
                    if (GroupCallActivity.this.scrimFullscreenView == null && !GroupCallActivity.this.drawingForBlur && GroupCallActivity.this.avatarsPreviewShowed && (child == GroupCallActivity.this.buttonsBackgroundGradientView2 || child == GroupCallActivity.this.buttonsBackgroundGradientView || child == GroupCallActivity.this.buttonsContainer || child == GroupCallActivity.this.undoView[0] || child == GroupCallActivity.this.undoView[1])) {
                        return true;
                    }
                    return super.drawChild(canvas, child, drawingTime);
                }
                canvas.save();
                canvas.translate(GroupCallActivity.this.renderersContainer.getX() + GroupCallActivity.this.fullscreenUsersListView.getX(), GroupCallActivity.this.renderersContainer.getY() + GroupCallActivity.this.fullscreenUsersListView.getY());
                GroupCallActivity.this.fullscreenUsersListView.draw(canvas);
                canvas.restore();
                return true;
            }

            @Override // android.view.View, android.view.KeyEvent.Callback
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (GroupCallActivity.this.scrimView != null && keyCode == 4) {
                    GroupCallActivity.this.dismissAvatarPreview(true);
                    return true;
                }
                return super.onKeyDown(keyCode, event);
            }
        };
        this.containerView.setFocusable(true);
        this.containerView.setFocusableInTouchMode(true);
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        this.containerView.setKeepScreenOn(true);
        this.containerView.setClipChildren(false);
        if (schedulePeer != null) {
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.scheduleStartInTextView = simpleTextView;
            simpleTextView.setGravity(17);
            this.scheduleStartInTextView.setTextColor(-1);
            this.scheduleStartInTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.scheduleStartInTextView.setTextSize(18);
            this.scheduleStartInTextView.setText(LocaleController.getString("VoipChatStartsIn", R.string.VoipChatStartsIn));
            this.containerView.addView(this.scheduleStartInTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 21.0f, 0.0f, 21.0f, 311.0f));
            SimpleTextView simpleTextView2 = new SimpleTextView(context) { // from class: org.telegram.ui.GroupCallActivity.8
                private float duration;
                private float gradientWidth;
                private int lastTextWidth;
                private long lastUpdateTime;
                private LinearGradient linearGradient;
                private float startX;
                private float time;
                private Matrix matrix = new Matrix();
                private float targetX = -1.0f;

                private void setTarget() {
                    this.targetX = ((Utilities.random.nextInt(100) - 50) * 0.2f) / 50.0f;
                }

                @Override // org.telegram.ui.ActionBar.SimpleTextView
                public boolean createLayout(int width) {
                    boolean result = super.createLayout(width);
                    int w = getTextWidth();
                    if (w != this.lastTextWidth) {
                        this.gradientWidth = w * 1.3f;
                        this.linearGradient = new LinearGradient(0.0f, getTextHeight(), 2.0f * w, 0.0f, new int[]{Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient), Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient3), Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient2), Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient2)}, new float[]{0.0f, 0.38f, 0.76f, 1.0f}, Shader.TileMode.CLAMP);
                        getPaint().setShader(this.linearGradient);
                        this.lastTextWidth = w;
                    }
                    return result;
                }

                @Override // org.telegram.ui.ActionBar.SimpleTextView, android.view.View
                public void onDraw(Canvas canvas) {
                    float moveProgress = 0.0f;
                    if (this.linearGradient != null) {
                        if (GroupCallActivity.this.call != null && GroupCallActivity.this.call.isScheduled()) {
                            long diff = (GroupCallActivity.this.call.call.schedule_date * 1000) - GroupCallActivity.this.accountInstance.getConnectionsManager().getCurrentTimeMillis();
                            if (diff < 0) {
                                moveProgress = 1.0f;
                            } else if (diff < DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                                moveProgress = 1.0f - (((float) diff) / 5000.0f);
                            }
                        }
                        this.matrix.reset();
                        this.matrix.postTranslate((-this.lastTextWidth) * 0.7f * moveProgress, 0.0f);
                        long newTime = SystemClock.elapsedRealtime();
                        long dt = newTime - this.lastUpdateTime;
                        if (dt > 20) {
                            dt = 17;
                        }
                        this.lastUpdateTime = newTime;
                        float f = this.duration;
                        if (f == 0.0f || this.time >= f) {
                            this.duration = Utilities.random.nextInt(200) + 1500;
                            this.time = 0.0f;
                            if (this.targetX == -1.0f) {
                                setTarget();
                            }
                            this.startX = this.targetX;
                            setTarget();
                        }
                        float f2 = this.time + (((float) dt) * (BlobDrawable.GRADIENT_SPEED_MIN + 0.5f)) + (((float) dt) * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * GroupCallActivity.this.amplitude);
                        this.time = f2;
                        float f3 = this.duration;
                        if (f2 > f3) {
                            this.time = f3;
                        }
                        float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / this.duration);
                        float f4 = this.gradientWidth;
                        float f5 = this.startX;
                        float x = ((f5 + ((this.targetX - f5) * interpolation)) * f4) - (f4 / 2.0f);
                        this.matrix.postTranslate(x, 0.0f);
                        this.linearGradient.setLocalMatrix(this.matrix);
                        invalidate();
                    }
                    super.onDraw(canvas);
                }
            };
            this.scheduleTimeTextView = simpleTextView2;
            simpleTextView2.setGravity(17);
            this.scheduleTimeTextView.setTextColor(-1);
            this.scheduleTimeTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.scheduleTimeTextView.setTextSize(60);
            this.containerView.addView(this.scheduleTimeTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 21.0f, 0.0f, 21.0f, 231.0f));
            SimpleTextView simpleTextView3 = new SimpleTextView(context);
            this.scheduleStartAtTextView = simpleTextView3;
            simpleTextView3.setGravity(17);
            this.scheduleStartAtTextView.setTextColor(-1);
            this.scheduleStartAtTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.scheduleStartAtTextView.setTextSize(18);
            this.containerView.addView(this.scheduleStartAtTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 21.0f, 0.0f, 21.0f, 201.0f));
        }
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.GroupCallActivity.9
            private final LongSparseIntArray visiblePeerTmp = new LongSparseIntArray();

            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child == GroupCallActivity.this.scrimView) {
                    return false;
                }
                return super.drawChild(canvas, child, drawingTime);
            }

            /* JADX WARN: Removed duplicated region for block: B:34:0x00ab  */
            /* JADX WARN: Removed duplicated region for block: B:37:0x00d1  */
            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void dispatchDraw(android.graphics.Canvas r16) {
                /*
                    Method dump skipped, instructions count: 452
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.AnonymousClass9.dispatchDraw(android.graphics.Canvas):void");
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
            public void setVisibility(int visibility) {
                if (getVisibility() != visibility) {
                    for (int i3 = 0; i3 < getChildCount(); i3++) {
                        View child = getChildAt(i3);
                        if (child instanceof GroupCallGridCell) {
                            GroupCallActivity.this.attachRenderer((GroupCallGridCell) child, visibility == 0);
                        }
                    }
                }
                super.setVisibility(visibility);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                GroupCallActivity.this.itemAnimator.updateBackgroundBeforeAnimation();
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipToPadding(false);
        this.listView.setClipChildren(false);
        GroupCallItemAnimator groupCallItemAnimator = new GroupCallItemAnimator();
        this.itemAnimator = groupCallItemAnimator;
        groupCallItemAnimator.setTranslationInterpolator(CubicBezierInterpolator.DEFAULT);
        this.itemAnimator.setRemoveDuration(350L);
        this.itemAnimator.setAddDuration(350L);
        this.itemAnimator.setMoveDuration(350L);
        this.itemAnimator.setDelayAnimations(false);
        this.listView.setItemAnimator(this.itemAnimator);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.GroupCallActivity.10
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (GroupCallActivity.this.listView.getChildCount() <= 0 || GroupCallActivity.this.call == null) {
                    return;
                }
                if (!GroupCallActivity.this.call.loadingMembers && !GroupCallActivity.this.call.membersLoadEndReached && GroupCallActivity.this.layoutManager.findLastVisibleItemPosition() > GroupCallActivity.this.listAdapter.getItemCount() - 5) {
                    GroupCallActivity.this.call.loadMembers(false);
                }
                GroupCallActivity.this.updateLayout(true);
                GroupCallActivity.this.containerView.invalidate();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState != 0) {
                    if (GroupCallActivity.this.recordHintView != null) {
                        GroupCallActivity.this.recordHintView.hide();
                    }
                    if (GroupCallActivity.this.reminderHintView != null) {
                        GroupCallActivity.this.reminderHintView.hide();
                        return;
                    }
                    return;
                }
                int offset = AndroidUtilities.dp(74.0f);
                float top = GroupCallActivity.this.scrollOffsetY - offset;
                if (GroupCallActivity.this.backgroundPaddingTop + top < ActionBar.getCurrentActionBarHeight() && GroupCallActivity.this.listView.canScrollVertically(1)) {
                    GroupCallActivity.this.listView.getChildAt(0);
                    RecyclerListView.Holder holder = (RecyclerListView.Holder) GroupCallActivity.this.listView.findViewHolderForAdapterPosition(0);
                    if (holder != null && holder.itemView.getTop() > 0) {
                        GroupCallActivity.this.listView.smoothScrollBy(0, holder.itemView.getTop());
                    }
                }
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        FillLastGridLayoutManager fillLastGridLayoutManager = new FillLastGridLayoutManager(getContext(), isLandscapeMode ? 6 : 2, 1, false, 0, this.listView);
        this.layoutManager = fillLastGridLayoutManager;
        recyclerListView2.setLayoutManager(fillLastGridLayoutManager);
        FillLastGridLayoutManager fillLastGridLayoutManager2 = this.layoutManager;
        GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.GroupCallActivity.11
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                int spanSize = GroupCallActivity.isLandscapeMode ? 6 : 2;
                if (!GroupCallActivity.isTabletMode && position >= GroupCallActivity.this.listAdapter.usersVideoGridStartRow && position < GroupCallActivity.this.listAdapter.usersVideoGridEndRow) {
                    int spanCount = 1;
                    int size = GroupCallActivity.this.listAdapter.usersVideoGridEndRow - GroupCallActivity.this.listAdapter.usersVideoGridStartRow;
                    if (position == GroupCallActivity.this.listAdapter.usersVideoGridEndRow - 1) {
                        if (GroupCallActivity.isLandscapeMode) {
                            spanCount = 2;
                        } else if (size % 2 == 0) {
                            spanCount = 1;
                        } else {
                            spanCount = 2;
                        }
                    }
                    if (GroupCallActivity.isLandscapeMode) {
                        if (size == 1) {
                            return 6;
                        }
                        return size == 2 ? 3 : 2;
                    }
                    return spanCount;
                }
                return spanSize;
            }
        };
        this.spanSizeLookup = spanSizeLookup;
        fillLastGridLayoutManager2.setSpanSizeLookup(spanSizeLookup);
        this.listView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.GroupCallActivity.12
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position >= 0) {
                    outRect.setEmpty();
                    if (position < GroupCallActivity.this.listAdapter.usersVideoGridStartRow || position >= GroupCallActivity.this.listAdapter.usersVideoGridEndRow) {
                        return;
                    }
                    int userPosition = position - GroupCallActivity.this.listAdapter.usersVideoGridStartRow;
                    int cellCount = GroupCallActivity.isLandscapeMode ? 6 : 2;
                    int index = userPosition % cellCount;
                    if (index == 0) {
                        outRect.right = AndroidUtilities.dp(2.0f);
                    } else if (index == cellCount - 1) {
                        outRect.left = AndroidUtilities.dp(2.0f);
                    } else {
                        outRect.left = AndroidUtilities.dp(1.0f);
                    }
                }
            }
        });
        this.layoutManager.setBind(false);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 14.0f, 14.0f, 14.0f, 231.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setTopBottomSelectorRadius(13);
        this.listView.setSelectorDrawableColor(Theme.getColor(Theme.key_voipgroup_listSelector));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda59
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view, int i3) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i3);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view, int i3, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i3, f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i3, float f, float f2) {
                GroupCallActivity.this.m3477lambda$new$12$orgtelegramuiGroupCallActivity(view, i3, f, f2);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda60
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i3) {
                return GroupCallActivity.this.m3478lambda$new$13$orgtelegramuiGroupCallActivity(view, i3);
            }
        });
        this.tabletVideoGridView = new RecyclerListView(context);
        this.containerView.addView(this.tabletVideoGridView, LayoutHelper.createFrame(-1, -1.0f, 51, 14.0f, 14.0f, 324.0f, 14.0f));
        RecyclerListView recyclerListView3 = this.tabletVideoGridView;
        GroupCallTabletGridAdapter groupCallTabletGridAdapter = new GroupCallTabletGridAdapter(groupCall, this.currentAccount, this);
        this.tabletGridAdapter = groupCallTabletGridAdapter;
        recyclerListView3.setAdapter(groupCallTabletGridAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 6, 1, false);
        this.tabletVideoGridView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.GroupCallActivity.14
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                return GroupCallActivity.this.tabletGridAdapter.getSpanCount(position);
            }
        });
        this.tabletVideoGridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda57
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i3) {
                GroupCallActivity.this.m3479lambda$new$14$orgtelegramuiGroupCallActivity(view, i3);
            }
        });
        DefaultItemAnimator tabletGridItemAnimator = new DefaultItemAnimator();
        tabletGridItemAnimator.setDelayAnimations(false);
        tabletGridItemAnimator.setTranslationInterpolator(CubicBezierInterpolator.DEFAULT);
        tabletGridItemAnimator.setRemoveDuration(350L);
        tabletGridItemAnimator.setAddDuration(350L);
        tabletGridItemAnimator.setMoveDuration(350L);
        this.tabletVideoGridView.setItemAnimator(new DefaultItemAnimator() { // from class: org.telegram.ui.GroupCallActivity.15
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected void onMoveAnimationUpdate(RecyclerView.ViewHolder holder) {
                GroupCallActivity.this.listView.invalidate();
                GroupCallActivity.this.renderersContainer.invalidate();
                GroupCallActivity.this.containerView.invalidate();
                GroupCallActivity.this.updateLayout(true);
            }
        });
        this.tabletVideoGridView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.GroupCallActivity.16
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GroupCallActivity.this.containerView.invalidate();
            }
        });
        this.tabletGridAdapter.setVisibility(this.tabletVideoGridView, false, false);
        this.tabletVideoGridView.setVisibility(8);
        this.buttonsContainer = new AnonymousClass17(context);
        int color = Theme.getColor(Theme.key_voipgroup_unmuteButton2);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        this.radialMatrix = new Matrix();
        this.radialGradient = new RadialGradient(0.0f, 0.0f, AndroidUtilities.dp(160.0f), new int[]{Color.argb(50, r, g, b), Color.argb(0, r, g, b)}, (float[]) null, Shader.TileMode.CLAMP);
        Paint paint = new Paint(1);
        this.radialPaint = paint;
        paint.setShader(this.radialGradient);
        BlobDrawable blobDrawable = new BlobDrawable(9);
        this.tinyWaveDrawable = blobDrawable;
        BlobDrawable blobDrawable2 = new BlobDrawable(12);
        this.bigWaveDrawable = blobDrawable2;
        blobDrawable.minRadius = AndroidUtilities.dp(62.0f);
        blobDrawable.maxRadius = AndroidUtilities.dp(72.0f);
        blobDrawable.generateBlob();
        blobDrawable2.minRadius = AndroidUtilities.dp(65.0f);
        blobDrawable2.maxRadius = AndroidUtilities.dp(75.0f);
        blobDrawable2.generateBlob();
        blobDrawable.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_unmuteButton), 38));
        blobDrawable2.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_unmuteButton), 76));
        VoIPToggleButton voIPToggleButton = new VoIPToggleButton(context);
        this.soundButton = voIPToggleButton;
        voIPToggleButton.setCheckable(true);
        this.soundButton.setTextSize(12);
        this.buttonsContainer.addView(this.soundButton, LayoutHelper.createFrame(68, 80.0f));
        this.soundButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallActivity.this.m3480lambda$new$15$orgtelegramuiGroupCallActivity(view);
            }
        });
        VoIPToggleButton voIPToggleButton2 = new VoIPToggleButton(context);
        this.cameraButton = voIPToggleButton2;
        voIPToggleButton2.setCheckable(true);
        this.cameraButton.setTextSize(12);
        this.cameraButton.showText(false, false);
        this.cameraButton.setCrossOffset(-AndroidUtilities.dpf2(3.5f));
        this.cameraButton.setVisibility(8);
        this.buttonsContainer.addView(this.cameraButton, LayoutHelper.createFrame(68, 80.0f));
        VoIPToggleButton voIPToggleButton3 = new VoIPToggleButton(context);
        this.flipButton = voIPToggleButton3;
        voIPToggleButton3.setCheckable(true);
        this.flipButton.setTextSize(12);
        this.flipButton.showText(false, false);
        RLottieImageView flipIconView = new RLottieImageView(context);
        this.flipButton.addView(flipIconView, LayoutHelper.createFrame(32, 32.0f, 0, 18.0f, 10.0f, 18.0f, 0.0f));
        RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.camera_flip, "2131558414", AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, null);
        this.flipIcon = rLottieDrawable;
        flipIconView.setAnimation(rLottieDrawable);
        this.flipButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallActivity.this.m3481lambda$new$16$orgtelegramuiGroupCallActivity(view);
            }
        });
        this.flipButton.setVisibility(8);
        this.buttonsContainer.addView(this.flipButton, LayoutHelper.createFrame(68, 80.0f));
        VoIPToggleButton voIPToggleButton4 = new VoIPToggleButton(context);
        this.leaveButton = voIPToggleButton4;
        voIPToggleButton4.setDrawBackground(false);
        this.leaveButton.setTextSize(12);
        this.leaveButton.setData((this.call == null || !isRtmpStream()) ? R.drawable.calls_decline : R.drawable.msg_voiceclose, -1, Theme.getColor(Theme.key_voipgroup_leaveButton), 0.3f, false, LocaleController.getString("VoipGroupLeave", R.string.VoipGroupLeave), false, false);
        this.buttonsContainer.addView(this.leaveButton, LayoutHelper.createFrame(68, 80.0f));
        this.leaveButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda14
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallActivity.this.m3482lambda$new$17$orgtelegramuiGroupCallActivity(context, view);
            }
        });
        RLottieImageView rLottieImageView = new RLottieImageView(context) { // from class: org.telegram.ui.GroupCallActivity.18
            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (GroupCallActivity.this.isRtmpStream()) {
                    return super.onTouchEvent(event);
                }
                if (event.getAction() == 0 && GroupCallActivity.this.muteButtonState == 0 && GroupCallActivity.this.call != null) {
                    AndroidUtilities.runOnUIThread(GroupCallActivity.this.pressRunnable, 300L);
                    GroupCallActivity.this.scheduled = true;
                } else if (event.getAction() == 1 || event.getAction() == 3) {
                    if (GroupCallActivity.this.scheduled) {
                        AndroidUtilities.cancelRunOnUIThread(GroupCallActivity.this.pressRunnable);
                        GroupCallActivity.this.scheduled = false;
                    } else if (GroupCallActivity.this.pressed) {
                        AndroidUtilities.cancelRunOnUIThread(GroupCallActivity.this.unmuteRunnable);
                        GroupCallActivity.this.updateMuteButton(0, true);
                        if (VoIPService.getSharedInstance() != null) {
                            VoIPService.getSharedInstance().setMicMute(true, true, false);
                            GroupCallActivity.this.muteButton.performHapticFeedback(3, 2);
                        }
                        GroupCallActivity.this.attachedRenderersTmp.clear();
                        GroupCallActivity.this.attachedRenderersTmp.addAll(GroupCallActivity.this.attachedRenderers);
                        for (int i3 = 0; i3 < GroupCallActivity.this.attachedRenderersTmp.size(); i3++) {
                            ((GroupCallMiniTextureView) GroupCallActivity.this.attachedRenderersTmp.get(i3)).updateAttachState(true);
                        }
                        GroupCallActivity.this.pressed = false;
                        MotionEvent cancel = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                        super.onTouchEvent(cancel);
                        cancel.recycle();
                        return true;
                    }
                }
                return super.onTouchEvent(event);
            }

            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setClassName(Button.class.getName());
                info.setEnabled(GroupCallActivity.this.muteButtonState == 0 || GroupCallActivity.this.muteButtonState == 1);
                if (GroupCallActivity.this.muteButtonState == 1 && Build.VERSION.SDK_INT >= 21) {
                    info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("VoipMute", R.string.VoipMute)));
                }
            }
        };
        this.muteButton = rLottieImageView;
        rLottieImageView.setAnimation(this.bigMicDrawable);
        this.muteButton.setScaleType(ImageView.ScaleType.CENTER);
        this.buttonsContainer.addView(this.muteButton, LayoutHelper.createFrame(122, 122, 49));
        this.muteButton.setOnClickListener(new AnonymousClass19());
        int padding = AndroidUtilities.dp(38.0f);
        ImageView imageView = new ImageView(context);
        this.expandButton = imageView;
        imageView.setScaleX(0.1f);
        this.expandButton.setScaleY(0.1f);
        this.expandButton.setAlpha(0.0f);
        this.expandButton.setImageResource(R.drawable.voice_expand);
        this.expandButton.setPadding(padding, padding, padding, padding);
        this.buttonsContainer.addView(this.expandButton, LayoutHelper.createFrame(122, 122, 49));
        ImageView imageView2 = new ImageView(context);
        this.minimizeButton = imageView2;
        imageView2.setScaleX(0.1f);
        this.minimizeButton.setScaleY(0.1f);
        this.minimizeButton.setAlpha(0.0f);
        this.minimizeButton.setImageResource(R.drawable.voice_minimize);
        this.minimizeButton.setPadding(padding, padding, padding, padding);
        this.buttonsContainer.addView(this.minimizeButton, LayoutHelper.createFrame(122, 122, 49));
        if (this.call != null && isRtmpStream() && !this.call.isScheduled()) {
            this.expandButton.setAlpha(1.0f);
            this.expandButton.setScaleX(1.0f);
            this.expandButton.setScaleY(1.0f);
            this.muteButton.setAlpha(0.0f);
        }
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.radialProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(110.0f));
        this.radialProgressView.setStrokeWidth(4.0f);
        this.radialProgressView.setProgressColor(Theme.getColor(Theme.key_voipgroup_connectingProgress));
        for (int a = 0; a < 2; a++) {
            this.muteLabel[a] = new TextView(context);
            this.muteLabel[a].setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
            this.muteLabel[a].setTextSize(1, 18.0f);
            this.muteLabel[a].setGravity(1);
            this.buttonsContainer.addView(this.muteLabel[a], LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 26.0f));
        }
        this.actionBar.setAlpha(0.0f);
        this.actionBar.getBackButton().setScaleX(0.9f);
        this.actionBar.getBackButton().setScaleY(0.9f);
        this.actionBar.getBackButton().setTranslationX(-AndroidUtilities.dp(14.0f));
        this.actionBar.getTitleTextView().setTranslationY(AndroidUtilities.dp(23.0f));
        this.actionBar.getSubtitleTextView().setTranslationY(AndroidUtilities.dp(20.0f));
        this.actionBar.getAdditionalSubtitleTextView().setTranslationY(AndroidUtilities.dp(20.0f));
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, null, 0, Theme.getColor(Theme.key_voipgroup_actionBarItems));
        this.otherItem = actionBarMenuItem;
        actionBarMenuItem.setLongClickEnabled(false);
        this.otherItem.setIcon(R.drawable.ic_ab_other);
        this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.otherItem.setSubMenuOpenSide(2);
        this.otherItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda50
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i3) {
                GroupCallActivity.this.m3483lambda$new$18$orgtelegramuiGroupCallActivity(i3);
            }
        });
        this.otherItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_voipgroup_actionBarItemsSelector), 6));
        this.otherItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda15
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallActivity.this.m3484lambda$new$19$orgtelegramuiGroupCallActivity(context, view);
            }
        });
        this.otherItem.setPopupItemsColor(Theme.getColor(Theme.key_voipgroup_actionBarItems), false);
        this.otherItem.setPopupItemsColor(Theme.getColor(Theme.key_voipgroup_actionBarItems), true);
        ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, null, 0, Theme.getColor(Theme.key_voipgroup_actionBarItems));
        this.pipItem = actionBarMenuItem2;
        actionBarMenuItem2.setLongClickEnabled(false);
        this.pipItem.setIcon((this.call == null || !isRtmpStream()) ? R.drawable.msg_voice_pip : R.drawable.ic_goinline);
        this.pipItem.setContentDescription(LocaleController.getString("AccDescrPipMode", R.string.AccDescrPipMode));
        this.pipItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_voipgroup_actionBarItemsSelector), 6));
        this.pipItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallActivity.this.m3485lambda$new$20$orgtelegramuiGroupCallActivity(view);
            }
        });
        ActionBarMenuItem actionBarMenuItem3 = new ActionBarMenuItem(context, null, 0, Theme.getColor(Theme.key_voipgroup_actionBarItems));
        this.screenShareItem = actionBarMenuItem3;
        actionBarMenuItem3.setLongClickEnabled(false);
        this.screenShareItem.setIcon(R.drawable.msg_screencast);
        this.screenShareItem.setContentDescription(LocaleController.getString("AccDescrPipMode", R.string.AccDescrPipMode));
        this.screenShareItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_voipgroup_actionBarItemsSelector), 6));
        this.screenShareItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallActivity.this.m3486lambda$new$21$orgtelegramuiGroupCallActivity(view);
            }
        });
        this.titleTextView = new AnonymousClass20(context, context);
        View view = new View(context) { // from class: org.telegram.ui.GroupCallActivity.21
            @Override // android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), ActionBar.getCurrentActionBarHeight());
            }
        };
        this.actionBarBackground = view;
        view.setAlpha(0.0f);
        this.containerView.addView(this.actionBarBackground, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.containerView.addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 23.0f, 0.0f, 48.0f, 0.0f));
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.menuItemsContainer = linearLayout;
        linearLayout.setOrientation(0);
        linearLayout.addView(this.screenShareItem, LayoutHelper.createLinear(48, 48));
        linearLayout.addView(this.pipItem, LayoutHelper.createLinear(48, 48));
        linearLayout.addView(this.otherItem, LayoutHelper.createLinear(48, 48));
        this.containerView.addView(linearLayout, LayoutHelper.createFrame(-2, 48, 53));
        View view2 = new View(context);
        this.actionBarShadow = view2;
        view2.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
        for (int a2 = 0; a2 < 2; a2++) {
            this.undoView[a2] = new UndoView(context) { // from class: org.telegram.ui.GroupCallActivity.22
                @Override // org.telegram.ui.Components.UndoView
                public void showWithAction(long did, int action, Object infoObject, Object infoObject2, Runnable actionRunnable, Runnable cancelRunnable) {
                    if (GroupCallActivity.this.previewDialog != null) {
                        return;
                    }
                    super.showWithAction(did, action, infoObject, infoObject2, actionRunnable, cancelRunnable);
                }
            };
            this.undoView[a2].setAdditionalTranslationY(AndroidUtilities.dp(10.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                this.undoView[a2].setTranslationZ(AndroidUtilities.dp(5.0f));
            }
            this.containerView.addView(this.undoView[a2], LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        AccountSelectCell accountSelectCell = new AccountSelectCell(context, true);
        this.accountSelectCell = accountSelectCell;
        accountSelectCell.setTag(R.id.width_tag, Integer.valueOf((int) PsExtractor.VIDEO_STREAM_MASK));
        this.otherItem.addSubItem(8, this.accountSelectCell, -2, AndroidUtilities.dp(48.0f));
        this.otherItem.setShowSubmenuByMove(false);
        this.accountSelectCell.setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_voipgroup_listSelector), 6, 6));
        this.accountGap = this.otherItem.addGap(0);
        ActionBarMenuSubItem addSubItem = this.otherItem.addSubItem(1, 0, (CharSequence) LocaleController.getString("VoipGroupAllCanSpeak", R.string.VoipGroupAllCanSpeak), true);
        this.everyoneItem = addSubItem;
        addSubItem.updateSelectorBackground(true, false);
        ActionBarMenuSubItem addSubItem2 = this.otherItem.addSubItem(2, 0, (CharSequence) LocaleController.getString("VoipGroupOnlyAdminsCanSpeak", R.string.VoipGroupOnlyAdminsCanSpeak), true);
        this.adminItem = addSubItem2;
        addSubItem2.updateSelectorBackground(false, true);
        this.everyoneItem.setCheckColor(Theme.key_voipgroup_checkMenu);
        this.everyoneItem.setColors(Theme.getColor(Theme.key_voipgroup_checkMenu), Theme.getColor(Theme.key_voipgroup_checkMenu));
        this.adminItem.setCheckColor(Theme.key_voipgroup_checkMenu);
        this.adminItem.setColors(Theme.getColor(Theme.key_voipgroup_checkMenu), Theme.getColor(Theme.key_voipgroup_checkMenu));
        Paint soundDrawablePaint = new Paint(1);
        soundDrawablePaint.setColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
        soundDrawablePaint.setStyle(Paint.Style.STROKE);
        soundDrawablePaint.setStrokeWidth(AndroidUtilities.dp(1.5f));
        soundDrawablePaint.setStrokeCap(Paint.Cap.ROUND);
        ActionBarMenuSubItem addSubItem3 = this.otherItem.addSubItem(10, R.drawable.msg_voice_speaker, null, LocaleController.getString("VoipGroupAudio", R.string.VoipGroupAudio), true, false);
        this.soundItem = addSubItem3;
        addSubItem3.setItemHeight(56);
        ActionBarMenuSubItem addSubItem4 = this.otherItem.addSubItem(11, R.drawable.msg_noise_on, null, LocaleController.getString("VoipNoiseCancellation", R.string.VoipNoiseCancellation), true, false);
        this.noiseItem = addSubItem4;
        addSubItem4.setItemHeight(56);
        View addDivider = this.otherItem.addDivider(ColorUtils.blendARGB(Theme.getColor(Theme.key_voipgroup_actionBar), -16777216, 0.3f));
        this.soundItemDivider = addDivider;
        ((ViewGroup.MarginLayoutParams) addDivider.getLayoutParams()).topMargin = 0;
        ((ViewGroup.MarginLayoutParams) this.soundItemDivider.getLayoutParams()).bottomMargin = 0;
        ActionBarMenuItem actionBarMenuItem4 = this.otherItem;
        RecordCallDrawable recordCallDrawable = this.recordCallDrawable;
        if (ChatObject.isChannelOrGiga(this.currentChat)) {
            i = R.string.VoipChannelEditTitle;
            str = "VoipChannelEditTitle";
        } else {
            i = R.string.VoipGroupEditTitle;
            str = "VoipGroupEditTitle";
        }
        this.editTitleItem = actionBarMenuItem4.addSubItem(6, R.drawable.msg_edit, recordCallDrawable, LocaleController.getString(str, i), true, false);
        this.permissionItem = this.otherItem.addSubItem(7, R.drawable.msg_permissions, this.recordCallDrawable, LocaleController.getString("VoipGroupEditPermissions", R.string.VoipGroupEditPermissions), false, false);
        this.inviteItem = this.otherItem.addSubItem(3, R.drawable.msg_link, LocaleController.getString("VoipGroupShareInviteLink", R.string.VoipGroupShareInviteLink));
        this.recordCallDrawable = new RecordCallDrawable();
        this.screenItem = this.otherItem.addSubItem(9, R.drawable.msg_screencast, LocaleController.getString("VoipChatStartScreenCapture", R.string.VoipChatStartScreenCapture));
        ActionBarMenuSubItem addSubItem5 = this.otherItem.addSubItem(5, 0, this.recordCallDrawable, LocaleController.getString("VoipGroupRecordCall", R.string.VoipGroupRecordCall), true, false);
        this.recordItem = addSubItem5;
        this.recordCallDrawable.setParentView(addSubItem5.getImageView());
        ActionBarMenuItem actionBarMenuItem5 = this.otherItem;
        if (ChatObject.isChannelOrGiga(this.currentChat)) {
            i2 = R.string.VoipChannelEndChat;
            str2 = "VoipChannelEndChat";
        } else {
            i2 = R.string.VoipGroupEndChat;
            str2 = "VoipGroupEndChat";
        }
        this.leaveItem = actionBarMenuItem5.addSubItem(4, R.drawable.msg_endcall, LocaleController.getString(str2, i2));
        this.otherItem.setPopupItemsSelectorColor(Theme.getColor(Theme.key_voipgroup_listSelector));
        this.otherItem.getPopupLayout().setFitItems(true);
        this.soundItem.setColors(Theme.getColor(Theme.key_voipgroup_actionBarItems), Theme.getColor(Theme.key_voipgroup_actionBarItems));
        this.noiseItem.setColors(Theme.getColor(Theme.key_voipgroup_actionBarItems), Theme.getColor(Theme.key_voipgroup_actionBarItems));
        this.leaveItem.setColors(Theme.getColor(Theme.key_voipgroup_leaveCallMenu), Theme.getColor(Theme.key_voipgroup_leaveCallMenu));
        this.inviteItem.setColors(Theme.getColor(Theme.key_voipgroup_actionBarItems), Theme.getColor(Theme.key_voipgroup_actionBarItems));
        this.editTitleItem.setColors(Theme.getColor(Theme.key_voipgroup_actionBarItems), Theme.getColor(Theme.key_voipgroup_actionBarItems));
        this.permissionItem.setColors(Theme.getColor(Theme.key_voipgroup_actionBarItems), Theme.getColor(Theme.key_voipgroup_actionBarItems));
        this.recordItem.setColors(Theme.getColor(Theme.key_voipgroup_actionBarItems), Theme.getColor(Theme.key_voipgroup_actionBarItems));
        this.screenItem.setColors(Theme.getColor(Theme.key_voipgroup_actionBarItems), Theme.getColor(Theme.key_voipgroup_actionBarItems));
        if (this.call != null) {
            initCreatedGroupCall();
        }
        this.leaveBackgroundPaint.setColor(Theme.getColor(Theme.key_voipgroup_leaveButton));
        updateTitle(false);
        this.actionBar.getTitleTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                GroupCallActivity.this.m3487lambda$new$22$orgtelegramuiGroupCallActivity(view3);
            }
        });
        this.fullscreenUsersListView = new RecyclerListView(context) { // from class: org.telegram.ui.GroupCallActivity.23
            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                GroupCallFullscreenAdapter.GroupCallUserCell cell = (GroupCallFullscreenAdapter.GroupCallUserCell) child;
                if (!GroupCallActivity.this.renderersContainer.isAnimating() && !GroupCallActivity.this.fullscreenListItemAnimator.isRunning()) {
                    cell.setAlpha(1.0f);
                    cell.setTranslationX(0.0f);
                    cell.setTranslationY(0.0f);
                }
                if (cell.isRemoving(GroupCallActivity.this.fullscreenUsersListView) && cell.getRenderer() != null) {
                    return true;
                }
                if (cell.getTranslationY() != 0.0f && cell.getRenderer() != null && cell.getRenderer().primaryView != null) {
                    float listTop = GroupCallActivity.this.listView.getTop() - getTop();
                    float listBottom = GroupCallActivity.this.listView.getMeasuredHeight() + listTop;
                    float progress = GroupCallActivity.this.renderersContainer.progressToFullscreenMode;
                    canvas.save();
                    canvas.clipRect(0.0f, (1.0f - progress) * listTop, getMeasuredWidth(), ((1.0f - progress) * listBottom) + (getMeasuredHeight() * progress));
                    boolean r2 = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return r2;
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() { // from class: org.telegram.ui.GroupCallActivity.24
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected void onMoveAnimationUpdate(RecyclerView.ViewHolder holder) {
                GroupCallActivity.this.listView.invalidate();
                GroupCallActivity.this.renderersContainer.invalidate();
                GroupCallActivity.this.containerView.invalidate();
                GroupCallActivity.this.updateLayout(true);
            }
        };
        this.fullscreenListItemAnimator = defaultItemAnimator;
        this.fullscreenUsersListView.setClipToPadding(false);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setTranslationInterpolator(CubicBezierInterpolator.DEFAULT);
        defaultItemAnimator.setRemoveDuration(350L);
        defaultItemAnimator.setAddDuration(350L);
        defaultItemAnimator.setMoveDuration(350L);
        this.fullscreenUsersListView.setItemAnimator(defaultItemAnimator);
        this.fullscreenUsersListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.GroupCallActivity.25
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GroupCallActivity.this.containerView.invalidate();
                GroupCallActivity.this.renderersContainer.invalidate();
            }
        });
        this.fullscreenUsersListView.setClipChildren(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(0);
        this.fullscreenUsersListView.setLayoutManager(layoutManager);
        RecyclerListView recyclerListView4 = this.fullscreenUsersListView;
        GroupCallFullscreenAdapter groupCallFullscreenAdapter = new GroupCallFullscreenAdapter(groupCall, this.currentAccount, this);
        this.fullscreenAdapter = groupCallFullscreenAdapter;
        recyclerListView4.setAdapter(groupCallFullscreenAdapter);
        this.fullscreenAdapter.setVisibility(this.fullscreenUsersListView, false);
        this.fullscreenUsersListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda58
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view3, int i3) {
                GroupCallActivity.this.m3488lambda$new$23$orgtelegramuiGroupCallActivity(view3, i3);
            }
        });
        this.fullscreenUsersListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda61
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view3, int i3) {
                return GroupCallActivity.this.m3489lambda$new$24$orgtelegramuiGroupCallActivity(view3, i3);
            }
        });
        this.fullscreenUsersListView.setVisibility(8);
        this.fullscreenUsersListView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.GroupCallActivity.26
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect outRect, View view3, RecyclerView parent, RecyclerView.State state) {
                parent.getChildAdapterPosition(view3);
                if (!GroupCallActivity.isLandscapeMode) {
                    outRect.set(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                } else {
                    outRect.set(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
                }
            }
        });
        AnonymousClass27 anonymousClass27 = new AnonymousClass27(context, this.listView, this.fullscreenUsersListView, this.attachedRenderers, this.call, this);
        this.renderersContainer = anonymousClass27;
        anonymousClass27.setClipChildren(false);
        this.fullscreenAdapter.setRenderersPool(this.attachedRenderers, this.renderersContainer);
        if (this.tabletVideoGridView != null) {
            this.tabletGridAdapter.setRenderersPool(this.attachedRenderers, this.renderersContainer);
        }
        AvatarPreviewPagerIndicator avatarPreviewPagerIndicator = new AvatarPreviewPagerIndicator(context);
        this.avatarPagerIndicator = avatarPreviewPagerIndicator;
        ProfileGalleryView profileGalleryView = new ProfileGalleryView(context, this.actionBar, this.listView, avatarPreviewPagerIndicator) { // from class: org.telegram.ui.GroupCallActivity.28
            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                GroupCallActivity.this.containerView.invalidate();
            }
        };
        this.avatarsViewPager = profileGalleryView;
        profileGalleryView.setImagesLayerNum(8192);
        profileGalleryView.setInvalidateWithParent(true);
        avatarPreviewPagerIndicator.setProfileGalleryView(profileGalleryView);
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.GroupCallActivity.29
            Rect rect = new Rect();
            RectF rectF = new RectF();
            Path path = new Path();

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int size = Math.min(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(getPaddingBottom() + size, C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                if (GroupCallActivity.this.progressToAvatarPreview != 1.0f) {
                    if (GroupCallActivity.this.scrimView == null || !GroupCallActivity.this.hasScrimAnchorView) {
                        if (GroupCallActivity.this.scrimFullscreenView != null && GroupCallActivity.this.scrimRenderer == null && GroupCallActivity.this.previewTextureTransitionEnabled) {
                            canvas.save();
                            float s = getMeasuredHeight() / GroupCallActivity.this.scrimFullscreenView.getAvatarImageView().getMeasuredHeight();
                            float fromRadius = (GroupCallActivity.this.scrimFullscreenView.getAvatarImageView().getMeasuredHeight() / 2.0f) * s;
                            int topRad = (int) (((1.0f - GroupCallActivity.this.progressToAvatarPreview) * fromRadius) + (AndroidUtilities.dp(13.0f) * GroupCallActivity.this.progressToAvatarPreview));
                            int bottomRad = (int) ((1.0f - GroupCallActivity.this.progressToAvatarPreview) * fromRadius);
                            GroupCallActivity.this.scrimFullscreenView.getAvatarImageView().getImageReceiver().setImageCoords(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                            GroupCallActivity.this.scrimFullscreenView.getAvatarImageView().setRoundRadius(topRad, topRad, bottomRad, bottomRad);
                            GroupCallActivity.this.scrimFullscreenView.getAvatarImageView().getImageReceiver().draw(canvas);
                            GroupCallActivity.this.scrimFullscreenView.getAvatarImageView().setRoundRadius(GroupCallActivity.this.scrimFullscreenView.getAvatarImageView().getMeasuredHeight() / 2);
                            canvas.restore();
                        }
                    } else {
                        canvas.save();
                        float s2 = getMeasuredHeight() / GroupCallActivity.this.scrimView.getAvatarImageView().getMeasuredHeight();
                        float fromRadius2 = (GroupCallActivity.this.scrimView.getAvatarImageView().getMeasuredHeight() / 2.0f) * s2;
                        int topRad2 = (int) (((1.0f - GroupCallActivity.this.progressToAvatarPreview) * fromRadius2) + (AndroidUtilities.dp(13.0f) * GroupCallActivity.this.progressToAvatarPreview));
                        int bottomRad2 = (int) ((1.0f - GroupCallActivity.this.progressToAvatarPreview) * fromRadius2);
                        GroupCallActivity.this.scrimView.getAvatarWavesDrawable().draw(canvas, GroupCallActivity.this.scrimView.getAvatarImageView().getMeasuredHeight() / 2, GroupCallActivity.this.scrimView.getAvatarImageView().getMeasuredHeight() / 2, this);
                        GroupCallActivity.this.scrimView.getAvatarImageView().getImageReceiver().setImageCoords(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                        GroupCallActivity.this.scrimView.getAvatarImageView().setRoundRadius(topRad2, topRad2, bottomRad2, bottomRad2);
                        GroupCallActivity.this.scrimView.getAvatarImageView().getImageReceiver().draw(canvas);
                        GroupCallActivity.this.scrimView.getAvatarImageView().setRoundRadius(GroupCallActivity.this.scrimView.getAvatarImageView().getMeasuredHeight() / 2);
                        canvas.restore();
                    }
                }
                GroupCallActivity.this.avatarsViewPager.setAlpha(GroupCallActivity.this.progressToAvatarPreview);
                this.path.reset();
                this.rectF.set(0.0f, 0.0f, getMeasuredHeight(), getMeasuredWidth());
                this.path.addRoundRect(this.rectF, new float[]{AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), 0.0f, 0.0f, 0.0f, 0.0f}, Path.Direction.CCW);
                canvas.save();
                canvas.clipPath(this.path);
                View textureView = GroupCallActivity.this.avatarsViewPager.findVideoActiveView();
                if (textureView != null && GroupCallActivity.this.scrimRenderer != null && GroupCallActivity.this.scrimRenderer.isAttached() && !GroupCallActivity.this.drawingForBlur) {
                    canvas.save();
                    this.rect.setEmpty();
                    GroupCallActivity.this.avatarsViewPager.getChildVisibleRect(textureView, this.rect, null);
                    int left = this.rect.left;
                    if (left < (-GroupCallActivity.this.avatarsViewPager.getMeasuredWidth())) {
                        left += GroupCallActivity.this.avatarsViewPager.getMeasuredWidth() * 2;
                    } else if (left > GroupCallActivity.this.avatarsViewPager.getMeasuredWidth()) {
                        left -= GroupCallActivity.this.avatarsViewPager.getMeasuredWidth() * 2;
                    }
                    canvas.translate(left, 0.0f);
                    GroupCallActivity.this.scrimRenderer.draw(canvas);
                    canvas.restore();
                }
                super.dispatchDraw(canvas);
                canvas.restore();
            }

            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                GroupCallActivity.this.containerView.invalidate();
            }
        };
        this.avatarPreviewContainer = frameLayout;
        frameLayout.setVisibility(8);
        profileGalleryView.setVisibility(0);
        profileGalleryView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.GroupCallActivity.30
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                GroupCallActivity.this.avatarsViewPager.getRealPosition(position);
                GroupCallActivity.this.avatarPagerIndicator.saveCurrentPageProgress();
                GroupCallActivity.this.avatarPagerIndicator.invalidate();
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
            }
        });
        this.blurredView = new View(context) { // from class: org.telegram.ui.GroupCallActivity.31
            @Override // android.view.View
            public void setAlpha(float alpha) {
                if (getAlpha() != alpha) {
                    super.setAlpha(alpha);
                    GroupCallActivity.this.checkContentOverlayed();
                }
            }
        };
        this.containerView.addView(this.renderersContainer);
        this.renderersContainer.addView(this.fullscreenUsersListView, LayoutHelper.createFrame(-1, 80.0f, 80, 0.0f, 0.0f, 0.0f, 100.0f));
        this.buttonsContainer.setWillNotDraw(false);
        View view3 = new View(context);
        this.buttonsBackgroundGradientView = view3;
        int[] iArr = this.gradientColors;
        iArr[0] = this.backgroundColor;
        iArr[1] = 0;
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, this.gradientColors);
        this.buttonsBackgroundGradient = gradientDrawable;
        view3.setBackground(gradientDrawable);
        this.containerView.addView(view3, LayoutHelper.createFrame(-1, 60, 83));
        View view4 = new View(context);
        this.buttonsBackgroundGradientView2 = view4;
        view4.setBackgroundColor(this.gradientColors[0]);
        this.containerView.addView(view4, LayoutHelper.createFrame(-1, 0, 83));
        this.containerView.addView(this.buttonsContainer, LayoutHelper.createFrame(-1, 200, 81));
        this.containerView.addView(this.blurredView);
        frameLayout.addView(profileGalleryView, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout.addView(avatarPreviewPagerIndicator, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
        this.containerView.addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f, 0, 14.0f, 14.0f, 14.0f, 14.0f));
        applyCallParticipantUpdates(false);
        this.listAdapter.notifyDataSetChanged();
        if (isTabletMode) {
            this.tabletGridAdapter.update(false, this.tabletVideoGridView);
        }
        this.oldCount = this.listAdapter.getItemCount();
        if (schedulePeer != null) {
            TextView textView = new TextView(context);
            this.scheduleInfoTextView = textView;
            textView.setGravity(17);
            this.scheduleInfoTextView.setTextColor(-8682615);
            this.scheduleInfoTextView.setTextSize(1, 14.0f);
            if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup) {
                this.scheduleInfoTextView.setTag(1);
            }
            this.containerView.addView(this.scheduleInfoTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 21.0f, 0.0f, 21.0f, 100.0f));
            final NumberPicker dayPicker = new NumberPicker(context);
            dayPicker.setTextColor(-1);
            dayPicker.setSelectorColor(-9598483);
            dayPicker.setTextOffset(AndroidUtilities.dp(10.0f));
            dayPicker.setItemCount(5);
            final NumberPicker hourPicker = new NumberPicker(context) { // from class: org.telegram.ui.GroupCallActivity.32
                @Override // org.telegram.ui.Components.NumberPicker
                protected CharSequence getContentDescription(int value) {
                    return LocaleController.formatPluralString("Hours", value, new Object[0]);
                }
            };
            hourPicker.setItemCount(5);
            hourPicker.setTextColor(-1);
            hourPicker.setSelectorColor(-9598483);
            hourPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
            final NumberPicker minutePicker = new NumberPicker(context) { // from class: org.telegram.ui.GroupCallActivity.33
                @Override // org.telegram.ui.Components.NumberPicker
                protected CharSequence getContentDescription(int value) {
                    return LocaleController.formatPluralString("Minutes", value, new Object[0]);
                }
            };
            minutePicker.setItemCount(5);
            minutePicker.setTextColor(-1);
            minutePicker.setSelectorColor(-9598483);
            minutePicker.setTextOffset(-AndroidUtilities.dp(34.0f));
            TextView textView2 = new TextView(context);
            this.scheduleButtonTextView = textView2;
            textView2.setLines(1);
            this.scheduleButtonTextView.setSingleLine(true);
            this.scheduleButtonTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.scheduleButtonTextView.setGravity(17);
            this.scheduleButtonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), 0, 1056964608));
            this.scheduleButtonTextView.setTextColor(-1);
            this.scheduleButtonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.scheduleButtonTextView.setTextSize(1, 14.0f);
            this.containerView.addView(this.scheduleButtonTextView, LayoutHelper.createFrame(-1, 48.0f, 81, 21.0f, 0.0f, 21.0f, 20.5f));
            final TLRPC.InputPeer inputPeer = peer;
            this.scheduleButtonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda17
                @Override // android.view.View.OnClickListener
                public final void onClick(View view5) {
                    GroupCallActivity.this.m3494lambda$new$29$orgtelegramuiGroupCallActivity(dayPicker, hourPicker, minutePicker, chat, account, inputPeer, view5);
                }
            });
            LinearLayout linearLayout2 = new LinearLayout(context) { // from class: org.telegram.ui.GroupCallActivity.35
                boolean ignoreLayout = false;

                @Override // android.widget.LinearLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    this.ignoreLayout = true;
                    dayPicker.setItemCount(5);
                    hourPicker.setItemCount(5);
                    minutePicker.setItemCount(5);
                    dayPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * 5;
                    hourPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * 5;
                    minutePicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * 5;
                    this.ignoreLayout = false;
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                @Override // android.view.View, android.view.ViewParent
                public void requestLayout() {
                    if (this.ignoreLayout) {
                        return;
                    }
                    super.requestLayout();
                }
            };
            this.scheduleTimerContainer = linearLayout2;
            linearLayout2.setWeightSum(1.0f);
            this.scheduleTimerContainer.setOrientation(0);
            this.containerView.addView(this.scheduleTimerContainer, LayoutHelper.createFrame(-1, 270.0f, 51, 0.0f, 50.0f, 0.0f, 0.0f));
            final long currentTime = System.currentTimeMillis();
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentTime);
            final int currentYear = calendar.get(1);
            int currentDay = calendar.get(6);
            this.scheduleTimerContainer.addView(dayPicker, LayoutHelper.createLinear(0, 270, 0.5f));
            dayPicker.setMinValue(0);
            dayPicker.setMaxValue(365);
            dayPicker.setWrapSelectorWheel(false);
            dayPicker.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda52
                @Override // org.telegram.ui.Components.NumberPicker.Formatter
                public final String format(int i3) {
                    return GroupCallActivity.lambda$new$30(currentTime, calendar, currentYear, i3);
                }
            });
            NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda56
                @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
                public final void onValueChange(NumberPicker numberPicker, int i3, int i4) {
                    GroupCallActivity.this.m3495lambda$new$31$orgtelegramuiGroupCallActivity(dayPicker, hourPicker, minutePicker, numberPicker, i3, i4);
                }
            };
            dayPicker.setOnValueChangedListener(onValueChangeListener);
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(23);
            this.scheduleTimerContainer.addView(hourPicker, LayoutHelper.createLinear(0, 270, 0.2f));
            hourPicker.setFormatter(GroupCallActivity$$ExternalSyntheticLambda53.INSTANCE);
            hourPicker.setOnValueChangedListener(onValueChangeListener);
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59);
            minutePicker.setValue(0);
            minutePicker.setFormatter(GroupCallActivity$$ExternalSyntheticLambda54.INSTANCE);
            this.scheduleTimerContainer.addView(minutePicker, LayoutHelper.createLinear(0, 270, 0.3f));
            minutePicker.setOnValueChangedListener(onValueChangeListener);
            calendar.setTimeInMillis(10800000 + currentTime);
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            int nextDay = calendar.get(6);
            int minute = calendar.get(12);
            int hour = calendar.get(11);
            dayPicker.setValue(currentDay != nextDay ? 1 : 0);
            minutePicker.setValue(minute);
            hourPicker.setValue(hour);
            AlertsCreator.checkScheduleDate(this.scheduleButtonTextView, this.scheduleInfoTextView, 604800L, 2, dayPicker, hourPicker, minutePicker);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            decorView = (ViewGroup) getWindow().getDecorView();
        } else {
            decorView = this.containerView;
        }
        PinchToZoomHelper pinchToZoomHelper = new PinchToZoomHelper(decorView, this.containerView) { // from class: org.telegram.ui.GroupCallActivity.36
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.PinchToZoomHelper
            public void invalidateViews() {
                super.invalidateViews();
                for (int i3 = 0; i3 < GroupCallActivity.this.avatarsViewPager.getChildCount(); i3++) {
                    GroupCallActivity.this.avatarsViewPager.getChildAt(i3).invalidate();
                }
            }

            @Override // org.telegram.ui.PinchToZoomHelper
            protected void drawOverlays(Canvas canvas, float alpha, float parentOffsetX, float parentOffsetY, float clipTop, float clipBottom) {
                if (alpha > 0.0f) {
                    float x = GroupCallActivity.this.avatarPreviewContainer.getX() + GroupCallActivity.this.containerView.getX();
                    float y = GroupCallActivity.this.avatarPreviewContainer.getY() + GroupCallActivity.this.containerView.getY();
                    AndroidUtilities.rectTmp.set(x, y, GroupCallActivity.this.avatarsViewPager.getMeasuredWidth() + x, GroupCallActivity.this.avatarsViewPager.getMeasuredHeight() + y);
                    canvas.saveLayerAlpha(AndroidUtilities.rectTmp, (int) (255.0f * alpha), 31);
                    canvas.translate(x, y);
                    GroupCallActivity.this.avatarPreviewContainer.draw(canvas);
                    canvas.restore();
                }
            }
        };
        this.pinchToZoomHelper = pinchToZoomHelper;
        pinchToZoomHelper.setCallback(new PinchToZoomHelper.Callback() { // from class: org.telegram.ui.GroupCallActivity.37
            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public /* synthetic */ TextureView getCurrentTextureView() {
                return PinchToZoomHelper.Callback.CC.$default$getCurrentTextureView(this);
            }

            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public void onZoomStarted(MessageObject messageObject) {
                GroupCallActivity.this.listView.cancelClickRunnables(true);
                GroupCallActivity.this.pinchToZoomHelper.getPhotoImage().setRoundRadius(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), 0, 0);
                GroupCallActivity.this.containerView.invalidate();
            }

            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public void onZoomFinished(MessageObject messageObject) {
                GroupCallActivity.this.containerView.invalidate();
            }
        });
        profileGalleryView.setPinchToZoomHelper(this.pinchToZoomHelper);
        this.cameraButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda16
            @Override // android.view.View.OnClickListener
            public final void onClick(View view5) {
                GroupCallActivity.this.m3496lambda$new$34$orgtelegramuiGroupCallActivity(context, view5);
            }
        });
        updateScheduleUI(false);
        updateItems();
        updateSpeakerPhoneIcon(false);
        updateState(false, false);
        setColorProgress(0.0f);
        updateSubtitle();
    }

    /* renamed from: lambda$new$9$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3497lambda$new$9$orgtelegramuiGroupCallActivity(DialogInterface dialog) {
        BaseFragment fragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
        if (this.anyEnterEventSent && (fragment instanceof ChatActivity)) {
            ((ChatActivity) fragment).onEditTextDialogClose(true, true);
        }
    }

    /* renamed from: org.telegram.ui.GroupCallActivity$6 */
    /* loaded from: classes4.dex */
    public class AnonymousClass6 extends ActionBar.ActionBarMenuOnItemClick {
        final /* synthetic */ Context val$context;

        AnonymousClass6(Context context) {
            GroupCallActivity.this = this$0;
            this.val$context = context;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int id) {
            final VoIPService service;
            int selectedPosition;
            int i;
            String str;
            int i2;
            if (id == -1) {
                GroupCallActivity.this.onBackPressed();
            } else if (id == 1) {
                GroupCallActivity.this.call.call.join_muted = false;
                GroupCallActivity.this.toggleAdminSpeak();
            } else if (id == 2) {
                GroupCallActivity.this.call.call.join_muted = true;
                GroupCallActivity.this.toggleAdminSpeak();
            } else if (id == 3) {
                GroupCallActivity.this.getLink(false);
            } else if (id == 4) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupCallActivity.this.getContext());
                if (ChatObject.isChannelOrGiga(GroupCallActivity.this.currentChat)) {
                    builder.setTitle(LocaleController.getString("VoipChannelEndAlertTitle", R.string.VoipChannelEndAlertTitle));
                    builder.setMessage(LocaleController.getString("VoipChannelEndAlertText", R.string.VoipChannelEndAlertText));
                } else {
                    builder.setTitle(LocaleController.getString("VoipGroupEndAlertTitle", R.string.VoipGroupEndAlertTitle));
                    builder.setMessage(LocaleController.getString("VoipGroupEndAlertText", R.string.VoipGroupEndAlertText));
                }
                builder.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
                builder.setPositiveButton(LocaleController.getString("VoipGroupEnd", R.string.VoipGroupEnd), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$6$$ExternalSyntheticLambda3
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        GroupCallActivity.AnonymousClass6.this.m3520lambda$onItemClick$1$orgtelegramuiGroupCallActivity$6(dialogInterface, i3);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog dialog = builder.create();
                dialog.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
                dialog.show();
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_voipgroup_leaveCallMenu));
                }
                dialog.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
            } else if (id == 9) {
                GroupCallActivity.this.screenShareItem.callOnClick();
            } else if (id == 5) {
                if (!GroupCallActivity.this.call.recording) {
                    GroupCallRecordAlert alert = new AnonymousClass1(GroupCallActivity.this.getContext(), GroupCallActivity.this.currentChat, GroupCallActivity.this.hasVideo);
                    if (GroupCallActivity.this.isRtmpStream()) {
                        alert.onStartRecord(2);
                        return;
                    } else {
                        alert.show();
                        return;
                    }
                }
                final boolean video = GroupCallActivity.this.call.call.record_video_active;
                AlertDialog.Builder builder2 = new AlertDialog.Builder(GroupCallActivity.this.getContext());
                builder2.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
                builder2.setTitle(LocaleController.getString("VoipGroupStopRecordingTitle", R.string.VoipGroupStopRecordingTitle));
                if (ChatObject.isChannelOrGiga(GroupCallActivity.this.currentChat)) {
                    builder2.setMessage(LocaleController.getString("VoipChannelStopRecordingText", R.string.VoipChannelStopRecordingText));
                } else {
                    builder2.setMessage(LocaleController.getString("VoipGroupStopRecordingText", R.string.VoipGroupStopRecordingText));
                }
                builder2.setPositiveButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$6$$ExternalSyntheticLambda5
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        GroupCallActivity.AnonymousClass6.this.m3521lambda$onItemClick$2$orgtelegramuiGroupCallActivity$6(video, dialogInterface, i3);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog dialog2 = builder2.create();
                dialog2.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
                dialog2.show();
                dialog2.setTextColor(Theme.getColor(Theme.key_voipgroup_nameText));
            } else if (id == 7) {
                GroupCallActivity.this.changingPermissions = true;
                GroupCallActivity.this.everyoneItem.setVisibility(0);
                GroupCallActivity.this.adminItem.setVisibility(0);
                GroupCallActivity.this.accountGap.setVisibility(8);
                GroupCallActivity.this.inviteItem.setVisibility(8);
                GroupCallActivity.this.leaveItem.setVisibility(8);
                GroupCallActivity.this.permissionItem.setVisibility(8);
                GroupCallActivity.this.editTitleItem.setVisibility(8);
                GroupCallActivity.this.recordItem.setVisibility(8);
                GroupCallActivity.this.screenItem.setVisibility(8);
                GroupCallActivity.this.accountSelectCell.setVisibility(8);
                GroupCallActivity.this.soundItem.setVisibility(8);
                GroupCallActivity.this.noiseItem.setVisibility(8);
                GroupCallActivity.this.otherItem.forceUpdatePopupPosition();
            } else if (id == 6) {
                GroupCallActivity.this.enterEventSent = false;
                final EditTextBoldCursor editText = new EditTextBoldCursor(GroupCallActivity.this.getContext());
                editText.setBackgroundDrawable(Theme.createEditTextDrawable(GroupCallActivity.this.getContext(), true));
                final AlertDialog.Builder builder3 = new AlertDialog.Builder(GroupCallActivity.this.getContext());
                builder3.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
                if (ChatObject.isChannelOrGiga(GroupCallActivity.this.currentChat)) {
                    builder3.setTitle(LocaleController.getString("VoipChannelTitle", R.string.VoipChannelTitle));
                } else {
                    builder3.setTitle(LocaleController.getString("VoipGroupTitle", R.string.VoipGroupTitle));
                }
                builder3.setCheckFocusable(false);
                builder3.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$6$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
                    }
                });
                LinearLayout linearLayout = new LinearLayout(GroupCallActivity.this.getContext());
                linearLayout.setOrientation(1);
                builder3.setView(linearLayout);
                editText.setTextSize(1, 16.0f);
                editText.setTextColor(Theme.getColor(Theme.key_voipgroup_nameText));
                editText.setMaxLines(1);
                editText.setLines(1);
                editText.setInputType(16385);
                editText.setGravity(51);
                editText.setSingleLine(true);
                editText.setImeOptions(6);
                editText.setHint(GroupCallActivity.this.currentChat.title);
                editText.setHintTextColor(Theme.getColor(Theme.key_voipgroup_lastSeenText));
                editText.setCursorColor(Theme.getColor(Theme.key_voipgroup_nameText));
                editText.setCursorSize(AndroidUtilities.dp(20.0f));
                editText.setCursorWidth(1.5f);
                editText.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                linearLayout.addView(editText, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.GroupCallActivity$6$$ExternalSyntheticLambda8
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView, int i3, KeyEvent keyEvent) {
                        return GroupCallActivity.AnonymousClass6.lambda$onItemClick$4(AlertDialog.Builder.this, textView, i3, keyEvent);
                    }
                });
                editText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.GroupCallActivity.6.2
                    boolean ignoreTextChange;

                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        if (!this.ignoreTextChange && s.length() > 40) {
                            this.ignoreTextChange = true;
                            s.delete(40, s.length());
                            AndroidUtilities.shakeView(editText, 2.0f, 0);
                            editText.performHapticFeedback(3, 2);
                            this.ignoreTextChange = false;
                        }
                    }
                });
                if (!TextUtils.isEmpty(GroupCallActivity.this.call.call.title)) {
                    editText.setText(GroupCallActivity.this.call.call.title);
                    editText.setSelection(editText.length());
                }
                builder3.setPositiveButton(LocaleController.getString("Save", R.string.Save), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$6$$ExternalSyntheticLambda4
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        GroupCallActivity.AnonymousClass6.this.m3522lambda$onItemClick$5$orgtelegramuiGroupCallActivity$6(editText, builder3, dialogInterface, i3);
                    }
                });
                final AlertDialog alertDialog = builder3.create();
                alertDialog.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_inviteMembersBackground));
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: org.telegram.ui.GroupCallActivity$6$$ExternalSyntheticLambda7
                    @Override // android.content.DialogInterface.OnShowListener
                    public final void onShow(DialogInterface dialogInterface) {
                        GroupCallActivity.AnonymousClass6.this.m3523lambda$onItemClick$6$orgtelegramuiGroupCallActivity$6(alertDialog, editText, dialogInterface);
                    }
                });
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.GroupCallActivity$6$$ExternalSyntheticLambda6
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
                    }
                });
                alertDialog.show();
                alertDialog.setTextColor(Theme.getColor(Theme.key_voipgroup_nameText));
                editText.requestFocus();
            } else if (id == 8) {
                JoinCallAlert.open(GroupCallActivity.this.getContext(), -GroupCallActivity.this.currentChat.id, GroupCallActivity.this.accountInstance, null, 2, GroupCallActivity.this.selfPeer, new JoinCallAlert.JoinCallAlertDelegate() { // from class: org.telegram.ui.GroupCallActivity$6$$ExternalSyntheticLambda1
                    @Override // org.telegram.ui.Components.JoinCallAlert.JoinCallAlertDelegate
                    public final void didSelectChat(TLRPC.InputPeer inputPeer, boolean z, boolean z2) {
                        GroupCallActivity.AnonymousClass6.this.m3524lambda$onItemClick$9$orgtelegramuiGroupCallActivity$6(inputPeer, z, z2);
                    }
                });
            } else if (id == 11) {
                SharedConfig.toggleNoiseSupression();
                VoIPService service2 = VoIPService.getSharedInstance();
                if (service2 == null) {
                    return;
                }
                service2.setNoiseSupressionEnabled(SharedConfig.noiseSupression);
            } else if (id == 10 && (service = VoIPService.getSharedInstance()) != null) {
                ArrayList<CharSequence> names = new ArrayList<>();
                ArrayList<Integer> icons = new ArrayList<>();
                final ArrayList<Integer> options = new ArrayList<>();
                names.add(LocaleController.getString("VoipAudioRoutingSpeaker", R.string.VoipAudioRoutingSpeaker));
                icons.add(Integer.valueOf((int) R.drawable.msg_voice_speaker));
                options.add(0);
                if (service.hasEarpiece()) {
                    if (service.isHeadsetPlugged()) {
                        i2 = R.string.VoipAudioRoutingHeadset;
                        str = "VoipAudioRoutingHeadset";
                    } else {
                        i2 = R.string.VoipAudioRoutingPhone;
                        str = "VoipAudioRoutingPhone";
                    }
                    names.add(LocaleController.getString(str, i2));
                    icons.add(Integer.valueOf(service.isHeadsetPlugged() ? R.drawable.msg_voice_headphones : R.drawable.msg_voice_phone));
                    options.add(1);
                }
                if (service.isBluetoothHeadsetConnected()) {
                    names.add(service.currentBluetoothDeviceName != null ? service.currentBluetoothDeviceName : LocaleController.getString("VoipAudioRoutingBluetooth", R.string.VoipAudioRoutingBluetooth));
                    icons.add(Integer.valueOf((int) R.drawable.msg_voice_bluetooth));
                    options.add(2);
                }
                int n = names.size();
                CharSequence[] itemsArray = new CharSequence[n];
                int[] iconsArray = new int[n];
                for (int i3 = 0; i3 < n; i3++) {
                    itemsArray[i3] = names.get(i3);
                    iconsArray[i3] = icons.get(i3).intValue();
                }
                BottomSheet.Builder builder4 = new BottomSheet.Builder(this.val$context).setTitle(LocaleController.getString("VoipSelectAudioOutput", R.string.VoipSelectAudioOutput), true).setItems(itemsArray, iconsArray, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$6$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i4) {
                        GroupCallActivity.AnonymousClass6.lambda$onItemClick$10(VoIPService.this, options, dialogInterface, i4);
                    }
                });
                BottomSheet bottomSheet = builder4.create();
                bottomSheet.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_listViewBackgroundUnscrolled));
                bottomSheet.fixNavigationBar(Theme.getColor(Theme.key_voipgroup_listViewBackgroundUnscrolled));
                if (service.getCurrentAudioRoute() == 1) {
                    selectedPosition = 0;
                } else {
                    int selectedPosition2 = service.getCurrentAudioRoute();
                    if (selectedPosition2 == 0) {
                        selectedPosition = 1;
                    } else {
                        selectedPosition = 2;
                    }
                }
                builder4.show();
                bottomSheet.setTitleColor(Theme.getColor(Theme.key_voipgroup_nameText));
                int i4 = 0;
                while (i4 < bottomSheet.getItemViews().size()) {
                    BottomSheet.BottomSheetCell cell = bottomSheet.getItemViews().get(i4);
                    if (i4 == selectedPosition) {
                        i = Theme.getColor(Theme.key_voipgroup_listeningText);
                        cell.isSelected = true;
                    } else {
                        i = Theme.getColor(Theme.key_voipgroup_nameText);
                    }
                    int color = i;
                    cell.setTextColor(color);
                    cell.setIconColor(color);
                    cell.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_actionBarItems), 12), 2));
                    i4++;
                    service = service;
                }
            }
        }

        /* renamed from: lambda$onItemClick$1$org-telegram-ui-GroupCallActivity$6 */
        public /* synthetic */ void m3520lambda$onItemClick$1$orgtelegramuiGroupCallActivity$6(DialogInterface dialogInterface, int i) {
            if (GroupCallActivity.this.call.isScheduled()) {
                TLRPC.ChatFull chatFull = GroupCallActivity.this.accountInstance.getMessagesController().getChatFull(GroupCallActivity.this.currentChat.id);
                if (chatFull != null) {
                    chatFull.flags &= -2097153;
                    chatFull.call = null;
                    GroupCallActivity.this.accountInstance.getNotificationCenter().postNotificationName(NotificationCenter.groupCallUpdated, Long.valueOf(GroupCallActivity.this.currentChat.id), Long.valueOf(GroupCallActivity.this.call.call.id), false);
                }
                TLRPC.TL_phone_discardGroupCall req = new TLRPC.TL_phone_discardGroupCall();
                req.call = GroupCallActivity.this.call.getInputGroupCall();
                GroupCallActivity.this.accountInstance.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$6$$ExternalSyntheticLambda9
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        GroupCallActivity.AnonymousClass6.this.m3519lambda$onItemClick$0$orgtelegramuiGroupCallActivity$6(tLObject, tL_error);
                    }
                });
            } else if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().hangUp(1);
            }
            GroupCallActivity.this.dismiss();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-GroupCallActivity$6 */
        public /* synthetic */ void m3519lambda$onItemClick$0$orgtelegramuiGroupCallActivity$6(TLObject response, TLRPC.TL_error error) {
            if (response instanceof TLRPC.TL_updates) {
                TLRPC.TL_updates updates = (TLRPC.TL_updates) response;
                GroupCallActivity.this.accountInstance.getMessagesController().processUpdates(updates, false);
            }
        }

        /* renamed from: lambda$onItemClick$2$org-telegram-ui-GroupCallActivity$6 */
        public /* synthetic */ void m3521lambda$onItemClick$2$orgtelegramuiGroupCallActivity$6(boolean video, DialogInterface dialogInterface, int i) {
            GroupCallActivity.this.call.toggleRecord(null, 0);
            GroupCallActivity.this.getUndoView().showWithAction(0L, video ? 101 : 40, (Runnable) null);
        }

        /* renamed from: org.telegram.ui.GroupCallActivity$6$1 */
        /* loaded from: classes4.dex */
        public class AnonymousClass1 extends GroupCallRecordAlert {
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass1(Context context, TLRPC.Chat chat, boolean hasVideo) {
                super(context, chat, hasVideo);
                AnonymousClass6.this = this$1;
            }

            @Override // org.telegram.ui.Components.GroupCallRecordAlert
            public void onStartRecord(final int type) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
                GroupCallActivity.this.enterEventSent = false;
                builder.setTitle(LocaleController.getString("VoipGroupStartRecordingTitle", R.string.VoipGroupStartRecordingTitle));
                if (type == 0) {
                    builder.setMessage(LocaleController.getString(GroupCallActivity.this.call.call.rtmp_stream ? R.string.VoipGroupStartRecordingRtmpText : R.string.VoipGroupStartRecordingText));
                } else {
                    boolean isChannelOrGiga = ChatObject.isChannelOrGiga(GroupCallActivity.this.currentChat);
                    int i = R.string.VoipGroupStartRecordingRtmpVideoText;
                    if (isChannelOrGiga) {
                        if (!GroupCallActivity.this.call.call.rtmp_stream) {
                            i = R.string.VoipChannelStartRecordingVideoText;
                        }
                        builder.setMessage(LocaleController.getString(i));
                    } else {
                        if (!GroupCallActivity.this.call.call.rtmp_stream) {
                            i = R.string.VoipGroupStartRecordingVideoText;
                        }
                        builder.setMessage(LocaleController.getString(i));
                    }
                }
                builder.setCheckFocusable(false);
                final EditTextBoldCursor editText = new EditTextBoldCursor(getContext());
                editText.setBackgroundDrawable(Theme.createEditTextDrawable(getContext(), Theme.getColor(Theme.key_voipgroup_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_voipgroup_windowBackgroundWhiteInputFieldActivated)));
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(1);
                builder.setView(linearLayout);
                editText.setTextSize(1, 16.0f);
                editText.setTextColor(Theme.getColor(Theme.key_voipgroup_nameText));
                editText.setMaxLines(1);
                editText.setLines(1);
                editText.setInputType(16385);
                editText.setGravity(51);
                editText.setSingleLine(true);
                editText.setHint(LocaleController.getString("VoipGroupSaveFileHint", R.string.VoipGroupSaveFileHint));
                editText.setImeOptions(6);
                editText.setHintTextColor(Theme.getColor(Theme.key_voipgroup_lastSeenText));
                editText.setCursorColor(Theme.getColor(Theme.key_voipgroup_nameText));
                editText.setCursorSize(AndroidUtilities.dp(20.0f));
                editText.setCursorWidth(1.5f);
                editText.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                linearLayout.addView(editText, LayoutHelper.createLinear(-1, 36, 51, 24, 0, 24, 12));
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.GroupCallActivity$6$1$$ExternalSyntheticLambda4
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                        return GroupCallActivity.AnonymousClass6.AnonymousClass1.lambda$onStartRecord$0(AlertDialog.Builder.this, textView, i2, keyEvent);
                    }
                });
                final AlertDialog alertDialog = builder.create();
                alertDialog.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_inviteMembersBackground));
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: org.telegram.ui.GroupCallActivity$6$1$$ExternalSyntheticLambda3
                    @Override // android.content.DialogInterface.OnShowListener
                    public final void onShow(DialogInterface dialogInterface) {
                        GroupCallActivity.AnonymousClass6.AnonymousClass1.this.m3525lambda$onStartRecord$1$orgtelegramuiGroupCallActivity$6$1(alertDialog, editText, dialogInterface);
                    }
                });
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.GroupCallActivity$6$1$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
                    }
                });
                builder.setPositiveButton(LocaleController.getString("Start", R.string.Start), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$6$1$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        GroupCallActivity.AnonymousClass6.AnonymousClass1.this.m3526lambda$onStartRecord$3$orgtelegramuiGroupCallActivity$6$1(editText, type, dialogInterface, i2);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$6$1$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
                dialog.show();
                dialog.setTextColor(Theme.getColor(Theme.key_voipgroup_nameText));
                editText.requestFocus();
            }

            public static /* synthetic */ boolean lambda$onStartRecord$0(AlertDialog.Builder builder, TextView textView, int i2, KeyEvent keyEvent) {
                AndroidUtilities.hideKeyboard(textView);
                builder.create().getButton(-1).callOnClick();
                return false;
            }

            /* renamed from: lambda$onStartRecord$1$org-telegram-ui-GroupCallActivity$6$1 */
            public /* synthetic */ void m3525lambda$onStartRecord$1$orgtelegramuiGroupCallActivity$6$1(AlertDialog alertDialog, EditTextBoldCursor editText, DialogInterface dialog) {
                GroupCallActivity.this.makeFocusable(null, alertDialog, editText, true);
            }

            /* renamed from: lambda$onStartRecord$3$org-telegram-ui-GroupCallActivity$6$1 */
            public /* synthetic */ void m3526lambda$onStartRecord$3$orgtelegramuiGroupCallActivity$6$1(EditTextBoldCursor editText, int type, DialogInterface dialogInterface, int i) {
                GroupCallActivity.this.call.toggleRecord(editText.getText().toString(), type);
                AndroidUtilities.hideKeyboard(editText);
                GroupCallActivity.this.getUndoView().showWithAction(0L, type == 0 ? 39 : 100, (Runnable) null);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().playStartRecordSound();
                }
            }
        }

        public static /* synthetic */ boolean lambda$onItemClick$4(AlertDialog.Builder builder, TextView textView, int i, KeyEvent keyEvent) {
            AndroidUtilities.hideKeyboard(textView);
            builder.create().getButton(-1).callOnClick();
            return false;
        }

        /* renamed from: lambda$onItemClick$5$org-telegram-ui-GroupCallActivity$6 */
        public /* synthetic */ void m3522lambda$onItemClick$5$orgtelegramuiGroupCallActivity$6(EditTextBoldCursor editText, AlertDialog.Builder builder, DialogInterface dialog, int which) {
            AndroidUtilities.hideKeyboard(editText);
            GroupCallActivity.this.call.setTitle(editText.getText().toString());
            builder.getDismissRunnable().run();
        }

        /* renamed from: lambda$onItemClick$6$org-telegram-ui-GroupCallActivity$6 */
        public /* synthetic */ void m3523lambda$onItemClick$6$orgtelegramuiGroupCallActivity$6(AlertDialog alertDialog, EditTextBoldCursor editText, DialogInterface dialog) {
            GroupCallActivity.this.makeFocusable(null, alertDialog, editText, true);
        }

        /* renamed from: lambda$onItemClick$9$org-telegram-ui-GroupCallActivity$6 */
        public /* synthetic */ void m3524lambda$onItemClick$9$orgtelegramuiGroupCallActivity$6(TLRPC.InputPeer peer1, boolean hasFewPeers, boolean schedule) {
            TLObject object;
            if (GroupCallActivity.this.call == null) {
                return;
            }
            if (peer1 instanceof TLRPC.TL_inputPeerUser) {
                object = GroupCallActivity.this.accountInstance.getMessagesController().getUser(Long.valueOf(peer1.user_id));
            } else {
                object = peer1 instanceof TLRPC.TL_inputPeerChat ? GroupCallActivity.this.accountInstance.getMessagesController().getChat(Long.valueOf(peer1.chat_id)) : GroupCallActivity.this.accountInstance.getMessagesController().getChat(Long.valueOf(peer1.channel_id));
            }
            if (GroupCallActivity.this.call.isScheduled()) {
                GroupCallActivity.this.getUndoView().showWithAction(0L, 37, object, GroupCallActivity.this.currentChat, (Runnable) null, (Runnable) null);
                if (peer1 instanceof TLRPC.TL_inputPeerChannel) {
                    GroupCallActivity.this.selfPeer = new TLRPC.TL_peerChannel();
                    GroupCallActivity.this.selfPeer.channel_id = peer1.channel_id;
                } else if (peer1 instanceof TLRPC.TL_inputPeerUser) {
                    GroupCallActivity.this.selfPeer = new TLRPC.TL_peerUser();
                    GroupCallActivity.this.selfPeer.user_id = peer1.user_id;
                } else if (peer1 instanceof TLRPC.TL_inputPeerChat) {
                    GroupCallActivity.this.selfPeer = new TLRPC.TL_peerChat();
                    GroupCallActivity.this.selfPeer.chat_id = peer1.chat_id;
                }
                GroupCallActivity.this.schedulePeer = peer1;
                TLRPC.ChatFull chatFull = GroupCallActivity.this.accountInstance.getMessagesController().getChatFull(GroupCallActivity.this.currentChat.id);
                if (chatFull != null) {
                    chatFull.groupcall_default_join_as = GroupCallActivity.this.selfPeer;
                    if (chatFull instanceof TLRPC.TL_chatFull) {
                        chatFull.flags |= 32768;
                    } else {
                        chatFull.flags |= ConnectionsManager.FileTypeFile;
                    }
                }
                TLRPC.TL_phone_saveDefaultGroupCallJoinAs req = new TLRPC.TL_phone_saveDefaultGroupCallJoinAs();
                req.peer = MessagesController.getInputPeer(GroupCallActivity.this.currentChat);
                req.join_as = peer1;
                GroupCallActivity.this.accountInstance.getConnectionsManager().sendRequest(req, GroupCallActivity$6$$ExternalSyntheticLambda10.INSTANCE);
                GroupCallActivity.this.updateItems();
            } else if (VoIPService.getSharedInstance() != null && hasFewPeers) {
                GroupCallActivity.this.call.participants.get(MessageObject.getPeerId(GroupCallActivity.this.selfPeer));
                VoIPService.getSharedInstance().setGroupCallPeer(peer1);
                GroupCallActivity.this.userSwitchObject = object;
            }
        }

        public static /* synthetic */ void lambda$onItemClick$8(TLObject response, TLRPC.TL_error error) {
        }

        public static /* synthetic */ void lambda$onItemClick$10(VoIPService service, ArrayList options, DialogInterface dialog, int which) {
            if (VoIPService.getSharedInstance() == null) {
                return;
            }
            service.setAudioOutput(((Integer) options.get(which)).intValue());
        }
    }

    /* renamed from: lambda$new$10$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3475lambda$new$10$orgtelegramuiGroupCallActivity(int[] uids, float[] levels, boolean[] voice) {
        RecyclerView.ViewHolder holder;
        for (int a = 0; a < uids.length; a++) {
            TLRPC.TL_groupCallParticipant participant = this.call.participantsBySources.get(uids[a]);
            if (participant != null) {
                if (!this.renderersContainer.inFullscreenMode) {
                    ArrayList<TLRPC.TL_groupCallParticipant> array = this.delayedGroupCallUpdated ? this.oldParticipants : this.call.visibleParticipants;
                    int idx = array.indexOf(participant);
                    if (idx >= 0 && (holder = this.listView.findViewHolderForAdapterPosition(this.listAdapter.usersStartRow + idx)) != null && (holder.itemView instanceof GroupCallUserCell)) {
                        ((GroupCallUserCell) holder.itemView).setAmplitude(levels[a] * 15.0f);
                        if (holder.itemView == this.scrimView && !this.contentFullyOverlayed) {
                            this.containerView.invalidate();
                        }
                    }
                } else {
                    for (int i = 0; i < this.fullscreenUsersListView.getChildCount(); i++) {
                        GroupCallFullscreenAdapter.GroupCallUserCell cell = (GroupCallFullscreenAdapter.GroupCallUserCell) this.fullscreenUsersListView.getChildAt(i);
                        if (MessageObject.getPeerId(cell.getParticipant().peer) == MessageObject.getPeerId(participant.peer)) {
                            cell.setAmplitude(levels[a] * 15.0f);
                        }
                    }
                }
                this.renderersContainer.setAmplitude(participant, levels[a] * 15.0f);
            }
        }
    }

    /* renamed from: lambda$new$12$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3477lambda$new$12$orgtelegramuiGroupCallActivity(View view, int position, float x, float y) {
        if (view instanceof GroupCallGridCell) {
            fullscreenFor(((GroupCallGridCell) view).getParticipant());
        } else if (view instanceof GroupCallUserCell) {
            showMenuForCell((GroupCallUserCell) view);
        } else if (view instanceof GroupCallInvitedCell) {
            GroupCallInvitedCell cell = (GroupCallInvitedCell) view;
            if (cell.getUser() == null) {
                return;
            }
            this.parentActivity.switchToAccount(this.currentAccount, true);
            Bundle args = new Bundle();
            args.putLong("user_id", cell.getUser().id);
            if (cell.hasAvatarSet()) {
                args.putBoolean("expandPhoto", true);
            }
            this.parentActivity.m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ProfileActivity(args));
            dismiss();
        } else if (position == this.listAdapter.addMemberRow) {
            if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup && !TextUtils.isEmpty(this.currentChat.username)) {
                getLink(false);
                return;
            }
            TLRPC.ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
            if (chatFull == null) {
                return;
            }
            this.enterEventSent = false;
            GroupVoipInviteAlert groupVoipInviteAlert = new GroupVoipInviteAlert(getContext(), this.accountInstance.getCurrentAccount(), this.currentChat, chatFull, this.call.participants, this.call.invitedUsersMap);
            this.groupVoipInviteAlert = groupVoipInviteAlert;
            groupVoipInviteAlert.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    GroupCallActivity.this.m3476lambda$new$11$orgtelegramuiGroupCallActivity(dialogInterface);
                }
            });
            this.groupVoipInviteAlert.setDelegate(new GroupVoipInviteAlert.GroupVoipInviteAlertDelegate() { // from class: org.telegram.ui.GroupCallActivity.13
                @Override // org.telegram.ui.Components.GroupVoipInviteAlert.GroupVoipInviteAlertDelegate
                public void copyInviteLink() {
                    GroupCallActivity.this.getLink(true);
                }

                @Override // org.telegram.ui.Components.GroupVoipInviteAlert.GroupVoipInviteAlertDelegate
                public void inviteUser(long id) {
                    GroupCallActivity.this.inviteUserToCall(id, true);
                }

                @Override // org.telegram.ui.Components.GroupVoipInviteAlert.GroupVoipInviteAlertDelegate
                public void needOpenSearch(MotionEvent ev, EditTextBoldCursor editText) {
                    if (!GroupCallActivity.this.enterEventSent) {
                        if (ev.getX() > editText.getLeft() && ev.getX() < editText.getRight() && ev.getY() > editText.getTop() && ev.getY() < editText.getBottom()) {
                            GroupCallActivity groupCallActivity = GroupCallActivity.this;
                            groupCallActivity.makeFocusable(groupCallActivity.groupVoipInviteAlert, null, editText, true);
                            return;
                        }
                        GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                        groupCallActivity2.makeFocusable(groupCallActivity2.groupVoipInviteAlert, null, editText, false);
                    }
                }
            });
            this.groupVoipInviteAlert.show();
        }
    }

    /* renamed from: lambda$new$11$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3476lambda$new$11$orgtelegramuiGroupCallActivity(DialogInterface dialog) {
        this.groupVoipInviteAlert = null;
    }

    /* renamed from: lambda$new$13$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ boolean m3478lambda$new$13$orgtelegramuiGroupCallActivity(View view, int position) {
        if (isRtmpStream()) {
            return false;
        }
        if (view instanceof GroupCallGridCell) {
            return showMenuForCell(view);
        }
        if (!(view instanceof GroupCallUserCell)) {
            return false;
        }
        updateItems();
        GroupCallUserCell cell = (GroupCallUserCell) view;
        return cell.clickMuteButton();
    }

    /* renamed from: lambda$new$14$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3479lambda$new$14$orgtelegramuiGroupCallActivity(View view, int position) {
        GroupCallGridCell cell = (GroupCallGridCell) view;
        if (cell.getParticipant() != null) {
            fullscreenFor(cell.getParticipant());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.GroupCallActivity$17 */
    /* loaded from: classes4.dex */
    public class AnonymousClass17 extends FrameLayout {
        AnimatorSet currentButtonsAnimation;
        int currentLightColor;
        final OvershootInterpolator overshootInterpolator = new OvershootInterpolator(1.5f);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass17(Context arg0) {
            super(arg0);
            GroupCallActivity.this = this$0;
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (!GroupCallActivity.isLandscapeMode) {
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(460.0f), View.MeasureSpec.getSize(widthMeasureSpec)), C.BUFFER_FLAG_ENCRYPTED);
            }
            for (int i = 0; i < 2; i++) {
                if (!GroupCallActivity.isLandscapeMode || GroupCallActivity.isTabletMode) {
                    GroupCallActivity.this.muteLabel[i].getLayoutParams().width = -2;
                } else {
                    GroupCallActivity.this.muteLabel[i].getLayoutParams().width = (int) (View.MeasureSpec.getSize(widthMeasureSpec) / 0.68f);
                }
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int cw = AndroidUtilities.dp(122.0f);
            int i = 2;
            int w = (getMeasuredWidth() - cw) / 2;
            int h = getMeasuredHeight();
            int buttonsCount = 5;
            if (GroupCallActivity.this.cameraButton.getVisibility() != 0) {
                buttonsCount = 5 - 1;
            }
            if (GroupCallActivity.this.soundButton.getVisibility() != 0) {
                buttonsCount--;
            }
            if (GroupCallActivity.this.flipButton.getVisibility() != 0) {
                buttonsCount--;
            }
            int i2 = 4;
            if (!GroupCallActivity.isLandscapeMode || GroupCallActivity.isTabletMode) {
                if (GroupCallActivity.this.renderersContainer.inFullscreenMode && !GroupCallActivity.isTabletMode) {
                    int part = getMeasuredWidth() / buttonsCount;
                    if (GroupCallActivity.this.soundButton.getVisibility() == 0) {
                        int x = (part / 2) - (GroupCallActivity.this.cameraButton.getMeasuredWidth() / 2);
                        int y = getMeasuredHeight() - GroupCallActivity.this.cameraButton.getMeasuredHeight();
                        GroupCallActivity.this.cameraButton.layout(x, y, GroupCallActivity.this.cameraButton.getMeasuredWidth() + x, GroupCallActivity.this.cameraButton.getMeasuredHeight() + y);
                        int partOffset = buttonsCount == 4 ? part : 0;
                        int x2 = ((part / 2) + partOffset) - (GroupCallActivity.this.leaveButton.getMeasuredWidth() / 2);
                        int y2 = getMeasuredHeight() - GroupCallActivity.this.soundButton.getMeasuredHeight();
                        GroupCallActivity.this.soundButton.layout(x2, y2, GroupCallActivity.this.soundButton.getMeasuredWidth() + x2, GroupCallActivity.this.soundButton.getMeasuredHeight() + y2);
                    } else {
                        int partOffset2 = buttonsCount == 4 ? part : 0;
                        int x3 = ((part / 2) + partOffset2) - (GroupCallActivity.this.cameraButton.getMeasuredWidth() / 2);
                        int y3 = getMeasuredHeight() - GroupCallActivity.this.cameraButton.getMeasuredHeight();
                        GroupCallActivity.this.cameraButton.layout(x3, y3, GroupCallActivity.this.cameraButton.getMeasuredWidth() + x3, GroupCallActivity.this.cameraButton.getMeasuredHeight() + y3);
                        int x4 = (part / 2) - (GroupCallActivity.this.flipButton.getMeasuredWidth() / 2);
                        int y4 = getMeasuredHeight() - GroupCallActivity.this.flipButton.getMeasuredHeight();
                        GroupCallActivity.this.flipButton.layout(x4, y4, GroupCallActivity.this.flipButton.getMeasuredWidth() + x4, GroupCallActivity.this.flipButton.getMeasuredHeight() + y4);
                    }
                    int partOffset3 = buttonsCount == 4 ? part * 3 : part * 2;
                    int x5 = ((part / 2) + partOffset3) - (GroupCallActivity.this.leaveButton.getMeasuredWidth() / 2);
                    int y5 = getMeasuredHeight() - GroupCallActivity.this.leaveButton.getMeasuredHeight();
                    GroupCallActivity.this.leaveButton.layout(x5, y5, GroupCallActivity.this.leaveButton.getMeasuredWidth() + x5, GroupCallActivity.this.leaveButton.getMeasuredHeight() + y5);
                    int partOffset4 = buttonsCount == 4 ? part * 2 : part;
                    int partOffset5 = part / 2;
                    int x6 = (partOffset5 + partOffset4) - (GroupCallActivity.this.muteButton.getMeasuredWidth() / 2);
                    int y6 = (getMeasuredHeight() - GroupCallActivity.this.leaveButton.getMeasuredHeight()) - ((GroupCallActivity.this.muteButton.getMeasuredWidth() - AndroidUtilities.dp(52.0f)) / 2);
                    GroupCallActivity.this.muteButton.layout(x6, y6, GroupCallActivity.this.muteButton.getMeasuredWidth() + x6, GroupCallActivity.this.muteButton.getMeasuredHeight() + y6);
                    GroupCallActivity.this.minimizeButton.layout(x6, y6, GroupCallActivity.this.minimizeButton.getMeasuredWidth() + x6, GroupCallActivity.this.minimizeButton.getMeasuredHeight() + y6);
                    GroupCallActivity.this.expandButton.layout(x6, y6, GroupCallActivity.this.expandButton.getMeasuredWidth() + x6, GroupCallActivity.this.expandButton.getMeasuredHeight() + y6);
                    float muteButtonScale = AndroidUtilities.dp(52.0f) / (GroupCallActivity.this.muteButton.getMeasuredWidth() - AndroidUtilities.dp(8.0f));
                    GroupCallActivity.this.muteButton.animate().scaleX(muteButtonScale).scaleY(muteButtonScale).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    int a = 0;
                    while (a < 2) {
                        int partOffset6 = buttonsCount == i2 ? part * 2 : part;
                        int x7 = partOffset6 + ((part - GroupCallActivity.this.muteLabel[a].getMeasuredWidth()) / 2);
                        int y7 = h - AndroidUtilities.dp(27.0f);
                        GroupCallActivity.this.muteLabel[a].layout(x7, y7, GroupCallActivity.this.muteLabel[a].getMeasuredWidth() + x7, GroupCallActivity.this.muteLabel[a].getMeasuredHeight() + y7);
                        GroupCallActivity.this.muteLabel[a].animate().scaleX(0.687f).scaleY(0.687f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                        a++;
                        i2 = 4;
                    }
                } else {
                    int buttonsYOffset = AndroidUtilities.dp(0.0f);
                    if (GroupCallActivity.this.soundButton.getVisibility() == 0) {
                        if (GroupCallActivity.this.cameraButton.getVisibility() == 0) {
                            int x8 = (w - GroupCallActivity.this.cameraButton.getMeasuredWidth()) / 2;
                            int y8 = (h - GroupCallActivity.this.cameraButton.getMeasuredHeight()) / 2;
                            GroupCallActivity.this.cameraButton.layout(x8, y8, GroupCallActivity.this.cameraButton.getMeasuredWidth() + x8, GroupCallActivity.this.cameraButton.getMeasuredHeight() + y8);
                            int x9 = (w - GroupCallActivity.this.soundButton.getMeasuredWidth()) / 2;
                            int y9 = (h - GroupCallActivity.this.leaveButton.getMeasuredHeight()) / 2;
                            GroupCallActivity.this.soundButton.layout(x9, y9, GroupCallActivity.this.soundButton.getMeasuredWidth() + x9, GroupCallActivity.this.soundButton.getMeasuredHeight() + y9);
                        } else {
                            int x10 = (w - GroupCallActivity.this.soundButton.getMeasuredWidth()) / 2;
                            int y10 = (h - GroupCallActivity.this.soundButton.getMeasuredHeight()) / 2;
                            GroupCallActivity.this.soundButton.layout(x10, y10, GroupCallActivity.this.soundButton.getMeasuredWidth() + x10, GroupCallActivity.this.soundButton.getMeasuredHeight() + y10);
                        }
                    } else {
                        int offset = GroupCallActivity.this.flipButton.getVisibility() == 0 ? AndroidUtilities.dp(28.0f) : 0;
                        int x11 = (w - GroupCallActivity.this.flipButton.getMeasuredWidth()) / 2;
                        int y11 = (((h - GroupCallActivity.this.flipButton.getMeasuredHeight()) / 2) + buttonsYOffset) - offset;
                        GroupCallActivity.this.flipButton.layout(x11, y11, GroupCallActivity.this.flipButton.getMeasuredWidth() + x11, GroupCallActivity.this.flipButton.getMeasuredHeight() + y11);
                        int x12 = (w - GroupCallActivity.this.cameraButton.getMeasuredWidth()) / 2;
                        int y12 = ((h - GroupCallActivity.this.cameraButton.getMeasuredHeight()) / 2) + buttonsYOffset + offset;
                        GroupCallActivity.this.cameraButton.layout(x12, y12, GroupCallActivity.this.cameraButton.getMeasuredWidth() + x12, GroupCallActivity.this.cameraButton.getMeasuredHeight() + y12);
                    }
                    int y13 = ((h - GroupCallActivity.this.leaveButton.getMeasuredHeight()) / 2) + buttonsYOffset;
                    int x13 = (getMeasuredWidth() - w) + ((w - GroupCallActivity.this.leaveButton.getMeasuredWidth()) / 2);
                    GroupCallActivity.this.leaveButton.layout(x13, y13, GroupCallActivity.this.leaveButton.getMeasuredWidth() + x13, GroupCallActivity.this.leaveButton.getMeasuredHeight() + y13);
                    int x14 = (getMeasuredWidth() - GroupCallActivity.this.muteButton.getMeasuredWidth()) / 2;
                    int y14 = ((h - GroupCallActivity.this.muteButton.getMeasuredHeight()) / 2) - AndroidUtilities.dp(9.0f);
                    GroupCallActivity.this.muteButton.layout(x14, y14, GroupCallActivity.this.muteButton.getMeasuredWidth() + x14, GroupCallActivity.this.muteButton.getMeasuredHeight() + y14);
                    GroupCallActivity.this.minimizeButton.layout(x14, y14, GroupCallActivity.this.minimizeButton.getMeasuredWidth() + x14, GroupCallActivity.this.minimizeButton.getMeasuredHeight() + y14);
                    GroupCallActivity.this.expandButton.layout(x14, y14, GroupCallActivity.this.expandButton.getMeasuredWidth() + x14, GroupCallActivity.this.expandButton.getMeasuredHeight() + y14);
                    GroupCallActivity.this.muteButton.animate().setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).scaleX(1.0f).scaleY(1.0f).start();
                    for (int a2 = 0; a2 < 2; a2++) {
                        int x15 = (getMeasuredWidth() - GroupCallActivity.this.muteLabel[a2].getMeasuredWidth()) / 2;
                        int y15 = (h - AndroidUtilities.dp(12.0f)) - GroupCallActivity.this.muteLabel[a2].getMeasuredHeight();
                        GroupCallActivity.this.muteLabel[a2].layout(x15, y15, GroupCallActivity.this.muteLabel[a2].getMeasuredWidth() + x15, GroupCallActivity.this.muteLabel[a2].getMeasuredHeight() + y15);
                        GroupCallActivity.this.muteLabel[a2].animate().scaleX(1.0f).scaleY(1.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    }
                }
            } else {
                int part2 = getMeasuredHeight() / buttonsCount;
                if (GroupCallActivity.this.soundButton.getVisibility() == 0) {
                    int y16 = (part2 / 2) - (GroupCallActivity.this.cameraButton.getMeasuredHeight() / 2);
                    int x16 = (getMeasuredWidth() - GroupCallActivity.this.cameraButton.getMeasuredWidth()) >> 1;
                    GroupCallActivity.this.cameraButton.layout(x16, y16, GroupCallActivity.this.cameraButton.getMeasuredWidth() + x16, GroupCallActivity.this.cameraButton.getMeasuredHeight() + y16);
                    int partOffset7 = buttonsCount == 4 ? part2 : 0;
                    int y17 = ((part2 / 2) + partOffset7) - (GroupCallActivity.this.soundButton.getMeasuredHeight() / 2);
                    int x17 = (getMeasuredWidth() - GroupCallActivity.this.soundButton.getMeasuredWidth()) >> 1;
                    GroupCallActivity.this.soundButton.layout(x17, y17, GroupCallActivity.this.soundButton.getMeasuredWidth() + x17, GroupCallActivity.this.soundButton.getMeasuredHeight() + y17);
                } else {
                    int y18 = (part2 / 2) - (GroupCallActivity.this.flipButton.getMeasuredHeight() / 2);
                    int x18 = (getMeasuredWidth() - GroupCallActivity.this.flipButton.getMeasuredWidth()) >> 1;
                    GroupCallActivity.this.flipButton.layout(x18, y18, GroupCallActivity.this.flipButton.getMeasuredWidth() + x18, GroupCallActivity.this.flipButton.getMeasuredHeight() + y18);
                    int partOffset8 = buttonsCount == 4 ? part2 : 0;
                    int y19 = ((part2 / 2) + partOffset8) - (GroupCallActivity.this.cameraButton.getMeasuredHeight() / 2);
                    int x19 = (getMeasuredWidth() - GroupCallActivity.this.cameraButton.getMeasuredWidth()) >> 1;
                    GroupCallActivity.this.cameraButton.layout(x19, y19, GroupCallActivity.this.cameraButton.getMeasuredWidth() + x19, GroupCallActivity.this.cameraButton.getMeasuredHeight() + y19);
                }
                int partOffset9 = buttonsCount == 4 ? part2 * 3 : part2 * 2;
                int y20 = ((part2 / 2) + partOffset9) - (GroupCallActivity.this.leaveButton.getMeasuredHeight() / 2);
                int x20 = (getMeasuredWidth() - GroupCallActivity.this.leaveButton.getMeasuredWidth()) >> 1;
                GroupCallActivity.this.leaveButton.layout(x20, y20, GroupCallActivity.this.leaveButton.getMeasuredWidth() + x20, GroupCallActivity.this.leaveButton.getMeasuredHeight() + y20);
                int partOffset10 = buttonsCount == 4 ? part2 * 2 : part2;
                int y21 = (((part2 / 2) + partOffset10) - (GroupCallActivity.this.muteButton.getMeasuredWidth() / 2)) - AndroidUtilities.dp(4.0f);
                int x21 = (getMeasuredWidth() - GroupCallActivity.this.muteButton.getMeasuredWidth()) >> 1;
                if (buttonsCount == 3) {
                    y21 -= AndroidUtilities.dp(6.0f);
                }
                GroupCallActivity.this.muteButton.layout(x21, y21, GroupCallActivity.this.muteButton.getMeasuredWidth() + x21, GroupCallActivity.this.muteButton.getMeasuredHeight() + y21);
                GroupCallActivity.this.minimizeButton.layout(x21, y21, GroupCallActivity.this.minimizeButton.getMeasuredWidth() + x21, GroupCallActivity.this.minimizeButton.getMeasuredHeight() + y21);
                GroupCallActivity.this.expandButton.layout(x21, y21, GroupCallActivity.this.expandButton.getMeasuredWidth() + x21, GroupCallActivity.this.expandButton.getMeasuredHeight() + y21);
                float muteButtonScale2 = AndroidUtilities.dp(52.0f) / (GroupCallActivity.this.muteButton.getMeasuredWidth() - AndroidUtilities.dp(8.0f));
                GroupCallActivity.this.muteButton.animate().cancel();
                GroupCallActivity.this.muteButton.setScaleX(muteButtonScale2);
                GroupCallActivity.this.muteButton.setScaleY(muteButtonScale2);
                for (int a3 = 0; a3 < 2; a3++) {
                    int x22 = (getMeasuredWidth() - GroupCallActivity.this.muteLabel[a3].getMeasuredWidth()) >> 1;
                    int partOffset11 = buttonsCount == 4 ? part2 * 2 : part2;
                    int y22 = (((part2 / 2) + partOffset11) - (GroupCallActivity.this.muteButton.getMeasuredWidth() / 2)) - AndroidUtilities.dp(4.0f);
                    if (buttonsCount == 3) {
                        y22 -= AndroidUtilities.dp(6.0f);
                    }
                    int y23 = (int) (y22 + (GroupCallActivity.this.muteButton.getMeasuredWidth() * 0.687f) + AndroidUtilities.dp(4.0f));
                    if (GroupCallActivity.this.muteLabel[a3].getMeasuredHeight() + y23 > partOffset11 + part2) {
                        y23 -= AndroidUtilities.dp(4.0f);
                    }
                    GroupCallActivity.this.muteLabel[a3].layout(x22, y23, GroupCallActivity.this.muteLabel[a3].getMeasuredWidth() + x22, GroupCallActivity.this.muteLabel[a3].getMeasuredHeight() + y23);
                    GroupCallActivity.this.muteLabel[a3].setScaleX(0.687f);
                    GroupCallActivity.this.muteLabel[a3].setScaleY(0.687f);
                }
            }
            if (GroupCallActivity.this.animateButtonsOnNextLayout) {
                AnimatorSet animatorSet = new AnimatorSet();
                boolean hasAnimation = false;
                int i3 = 0;
                while (i3 < getChildCount()) {
                    View child = getChildAt(i3);
                    Float fromX = (Float) GroupCallActivity.this.buttonsAnimationParamsX.get(child);
                    Float fromY = (Float) GroupCallActivity.this.buttonsAnimationParamsY.get(child);
                    if (fromX != null && fromY != null) {
                        hasAnimation = true;
                        Property property = TRANSLATION_X;
                        float[] fArr = new float[i];
                        fArr[0] = fromX.floatValue() - child.getLeft();
                        fArr[1] = 0.0f;
                        animatorSet.playTogether(ObjectAnimator.ofFloat(child, property, fArr));
                        animatorSet.playTogether(ObjectAnimator.ofFloat(child, TRANSLATION_Y, fromY.floatValue() - child.getTop(), 0.0f));
                    }
                    i3++;
                    i = 2;
                }
                if (hasAnimation) {
                    AnimatorSet animatorSet2 = this.currentButtonsAnimation;
                    if (animatorSet2 != null) {
                        animatorSet2.removeAllListeners();
                        this.currentButtonsAnimation.cancel();
                    }
                    this.currentButtonsAnimation = animatorSet;
                    animatorSet.setDuration(350L);
                    animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.17.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            AnonymousClass17.this.currentButtonsAnimation = null;
                            for (int i4 = 0; i4 < AnonymousClass17.this.getChildCount(); i4++) {
                                View child2 = AnonymousClass17.this.getChildAt(i4);
                                child2.setTranslationX(0.0f);
                                child2.setTranslationY(0.0f);
                            }
                        }
                    });
                    animatorSet.start();
                }
                GroupCallActivity.this.buttonsAnimationParamsX.clear();
                GroupCallActivity.this.buttonsAnimationParamsY.clear();
            }
            GroupCallActivity.this.animateButtonsOnNextLayout = false;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            long dt;
            float showWavesProgressInterpolated;
            long newTime;
            String str;
            String str2;
            boolean canSwitchProgress;
            float alpha;
            float translation;
            float scale;
            float scale2;
            float progress;
            float scale3;
            int soundButtonColorChecked;
            int oldLight;
            int lightingColor;
            if (GroupCallActivity.this.contentFullyOverlayed && GroupCallActivity.this.useBlur) {
                return;
            }
            int offset = (getMeasuredWidth() - getMeasuredHeight()) / 2;
            long newTime2 = SystemClock.elapsedRealtime();
            long dt2 = newTime2 - GroupCallActivity.this.lastUpdateTime;
            GroupCallActivity.this.lastUpdateTime = newTime2;
            if (dt2 <= 20) {
                dt = dt2;
            } else {
                dt = 17;
            }
            if (GroupCallActivity.this.currentState != null) {
                GroupCallActivity.this.currentState.update(0, offset, getMeasuredHeight(), dt, GroupCallActivity.this.amplitude);
            }
            GroupCallActivity.this.tinyWaveDrawable.minRadius = AndroidUtilities.dp(62.0f);
            GroupCallActivity.this.tinyWaveDrawable.maxRadius = AndroidUtilities.dp(62.0f) + (AndroidUtilities.dp(20.0f) * BlobDrawable.FORM_SMALL_MAX);
            GroupCallActivity.this.bigWaveDrawable.minRadius = AndroidUtilities.dp(65.0f);
            GroupCallActivity.this.bigWaveDrawable.maxRadius = AndroidUtilities.dp(65.0f) + (AndroidUtilities.dp(20.0f) * BlobDrawable.FORM_BIG_MAX);
            if (GroupCallActivity.this.animateToAmplitude != GroupCallActivity.this.amplitude) {
                GroupCallActivity groupCallActivity = GroupCallActivity.this;
                GroupCallActivity.access$10516(groupCallActivity, groupCallActivity.animateAmplitudeDiff * ((float) dt));
                if (GroupCallActivity.this.animateAmplitudeDiff > 0.0f) {
                    if (GroupCallActivity.this.amplitude > GroupCallActivity.this.animateToAmplitude) {
                        GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                        groupCallActivity2.amplitude = groupCallActivity2.animateToAmplitude;
                    }
                } else if (GroupCallActivity.this.amplitude < GroupCallActivity.this.animateToAmplitude) {
                    GroupCallActivity groupCallActivity3 = GroupCallActivity.this;
                    groupCallActivity3.amplitude = groupCallActivity3.animateToAmplitude;
                }
            }
            boolean canSwitchProgress2 = true;
            if (GroupCallActivity.this.prevState == null || GroupCallActivity.this.prevState.currentState != 3) {
                if (GroupCallActivity.this.prevState != null && GroupCallActivity.this.currentState != null && GroupCallActivity.this.currentState.currentState == 3) {
                    GroupCallActivity.this.radialProgressView.toCircle(true, false);
                }
            } else {
                GroupCallActivity.this.radialProgressView.toCircle(true, true);
                if (!GroupCallActivity.this.radialProgressView.isCircle()) {
                    canSwitchProgress2 = false;
                }
            }
            if (canSwitchProgress2) {
                if (GroupCallActivity.this.switchProgress != 1.0f) {
                    if (GroupCallActivity.this.prevState != null && GroupCallActivity.this.prevState.currentState == 3) {
                        GroupCallActivity.access$13116(GroupCallActivity.this, ((float) dt) / 100.0f);
                    } else {
                        GroupCallActivity.access$13116(GroupCallActivity.this, ((float) dt) / 180.0f);
                    }
                    if (GroupCallActivity.this.switchProgress >= 1.0f) {
                        GroupCallActivity.this.switchProgress = 1.0f;
                        GroupCallActivity.this.prevState = null;
                        if (GroupCallActivity.this.currentState != null && GroupCallActivity.this.currentState.currentState == 3) {
                            GroupCallActivity.this.radialProgressView.toCircle(false, true);
                        }
                    }
                    GroupCallActivity.this.invalidateColors = true;
                }
                if (GroupCallActivity.this.invalidateColors && GroupCallActivity.this.currentState != null) {
                    GroupCallActivity.this.invalidateColors = false;
                    if (GroupCallActivity.this.prevState != null) {
                        GroupCallActivity groupCallActivity4 = GroupCallActivity.this;
                        groupCallActivity4.fillColors(groupCallActivity4.prevState.currentState, GroupCallActivity.this.colorsTmp);
                        int oldLight2 = GroupCallActivity.this.colorsTmp[0];
                        int oldSound = GroupCallActivity.this.colorsTmp[1];
                        int oldSound2 = GroupCallActivity.this.colorsTmp[2];
                        GroupCallActivity groupCallActivity5 = GroupCallActivity.this;
                        groupCallActivity5.fillColors(groupCallActivity5.currentState.currentState, GroupCallActivity.this.colorsTmp);
                        lightingColor = ColorUtils.blendARGB(oldLight2, GroupCallActivity.this.colorsTmp[0], GroupCallActivity.this.switchProgress);
                        soundButtonColorChecked = ColorUtils.blendARGB(oldSound, GroupCallActivity.this.colorsTmp[1], GroupCallActivity.this.switchProgress);
                        oldLight = ColorUtils.blendARGB(oldSound2, GroupCallActivity.this.colorsTmp[2], GroupCallActivity.this.switchProgress);
                    } else {
                        GroupCallActivity groupCallActivity6 = GroupCallActivity.this;
                        groupCallActivity6.fillColors(groupCallActivity6.currentState.currentState, GroupCallActivity.this.colorsTmp);
                        lightingColor = GroupCallActivity.this.colorsTmp[0];
                        soundButtonColorChecked = GroupCallActivity.this.colorsTmp[1];
                        oldLight = GroupCallActivity.this.colorsTmp[2];
                    }
                    if (this.currentLightColor != lightingColor) {
                        GroupCallActivity.this.radialGradient = new RadialGradient(0.0f, 0.0f, AndroidUtilities.dp(100.0f), new int[]{ColorUtils.setAlphaComponent(lightingColor, 60), ColorUtils.setAlphaComponent(lightingColor, 0)}, (float[]) null, Shader.TileMode.CLAMP);
                        GroupCallActivity.this.radialPaint.setShader(GroupCallActivity.this.radialGradient);
                        this.currentLightColor = lightingColor;
                    }
                    GroupCallActivity.this.soundButton.setBackgroundColor(oldLight, soundButtonColorChecked);
                    GroupCallActivity.this.cameraButton.setBackgroundColor(oldLight, soundButtonColorChecked);
                    GroupCallActivity.this.flipButton.setBackgroundColor(oldLight, soundButtonColorChecked);
                }
                boolean showWaves = false;
                boolean showLighting = false;
                if (GroupCallActivity.this.currentState != null) {
                    showWaves = GroupCallActivity.this.currentState.currentState == 1 || GroupCallActivity.this.currentState.currentState == 0 || GroupCallActivity.isGradientState(GroupCallActivity.this.currentState.currentState);
                    showLighting = GroupCallActivity.this.currentState.currentState != 3;
                }
                if (GroupCallActivity.this.prevState != null && GroupCallActivity.this.currentState != null && GroupCallActivity.this.currentState.currentState == 3) {
                    GroupCallActivity.access$13724(GroupCallActivity.this, ((float) dt) / 180.0f);
                    if (GroupCallActivity.this.showWavesProgress < 0.0f) {
                        GroupCallActivity.this.showWavesProgress = 0.0f;
                    }
                } else if (showWaves && GroupCallActivity.this.showWavesProgress != 1.0f) {
                    GroupCallActivity.access$13716(GroupCallActivity.this, ((float) dt) / 350.0f);
                    if (GroupCallActivity.this.showWavesProgress > 1.0f) {
                        GroupCallActivity.this.showWavesProgress = 1.0f;
                    }
                } else if (!showWaves && GroupCallActivity.this.showWavesProgress != 0.0f) {
                    GroupCallActivity.access$13724(GroupCallActivity.this, ((float) dt) / 350.0f);
                    if (GroupCallActivity.this.showWavesProgress < 0.0f) {
                        GroupCallActivity.this.showWavesProgress = 0.0f;
                    }
                }
                if (showLighting && GroupCallActivity.this.showLightingProgress != 1.0f) {
                    GroupCallActivity.access$13816(GroupCallActivity.this, ((float) dt) / 350.0f);
                    if (GroupCallActivity.this.showLightingProgress > 1.0f) {
                        GroupCallActivity.this.showLightingProgress = 1.0f;
                    }
                } else if (!showLighting && GroupCallActivity.this.showLightingProgress != 0.0f) {
                    GroupCallActivity.access$13824(GroupCallActivity.this, ((float) dt) / 350.0f);
                    if (GroupCallActivity.this.showLightingProgress < 0.0f) {
                        GroupCallActivity.this.showLightingProgress = 0.0f;
                    }
                }
            }
            float showWavesProgressInterpolated2 = (0.6f * this.overshootInterpolator.getInterpolation(GroupCallActivity.this.showWavesProgress)) + 0.4f;
            GroupCallActivity.this.bigWaveDrawable.update(GroupCallActivity.this.amplitude, 1.0f);
            GroupCallActivity.this.tinyWaveDrawable.update(GroupCallActivity.this.amplitude, 1.0f);
            WeavingState weavingState = GroupCallActivity.this.prevState;
            String str3 = Theme.key_voipgroup_disabledButton;
            String str4 = Theme.key_voipgroup_listViewBackgroundUnscrolled;
            if (weavingState != null && GroupCallActivity.this.currentState != null && (GroupCallActivity.this.currentState.currentState == 3 || GroupCallActivity.this.prevState.currentState == 3)) {
                if (GroupCallActivity.this.currentState.currentState == 3) {
                    progress = GroupCallActivity.this.switchProgress;
                    GroupCallActivity.this.paint.setShader(GroupCallActivity.this.prevState.shader);
                } else {
                    progress = 1.0f - GroupCallActivity.this.switchProgress;
                    GroupCallActivity.this.paint.setShader(GroupCallActivity.this.currentState.shader);
                }
                GroupCallActivity.this.paintTmp.setColor(AndroidUtilities.getOffsetColor(Theme.getColor(str4), Theme.getColor(str3), GroupCallActivity.this.colorProgress, 1.0f));
                int cx = (int) (GroupCallActivity.this.muteButton.getX() + (GroupCallActivity.this.muteButton.getMeasuredWidth() / 2));
                int cy = (int) (GroupCallActivity.this.muteButton.getY() + (GroupCallActivity.this.muteButton.getMeasuredHeight() / 2));
                GroupCallActivity.this.radialMatrix.setTranslate(cx, cy);
                GroupCallActivity.this.radialGradient.setLocalMatrix(GroupCallActivity.this.radialMatrix);
                GroupCallActivity.this.paint.setAlpha(76);
                if (GroupCallActivity.this.call != null) {
                    float radius = AndroidUtilities.dp(52.0f) / 2.0f;
                    canvas.drawCircle(GroupCallActivity.this.leaveButton.getX() + (GroupCallActivity.this.leaveButton.getMeasuredWidth() / 2.0f), GroupCallActivity.this.leaveButton.getY() + radius, radius, GroupCallActivity.this.leaveBackgroundPaint);
                }
                canvas.save();
                canvas.scale(BlobDrawable.GLOBAL_SCALE * GroupCallActivity.this.muteButton.getScaleX(), BlobDrawable.GLOBAL_SCALE * GroupCallActivity.this.muteButton.getScaleY(), cx, cy);
                canvas.save();
                float scale4 = BlobDrawable.SCALE_BIG_MIN + (BlobDrawable.SCALE_BIG * GroupCallActivity.this.amplitude * 0.5f);
                canvas.scale(GroupCallActivity.this.showLightingProgress * scale4, GroupCallActivity.this.showLightingProgress * scale4, cx, cy);
                float scaleLight = BlobDrawable.LIGHT_GRADIENT_SIZE + 0.7f;
                canvas.save();
                canvas.scale(scaleLight, scaleLight, cx, cy);
                canvas.drawCircle(cx, cy, AndroidUtilities.dp(160.0f), GroupCallActivity.this.radialPaint);
                canvas.restore();
                canvas.restore();
                if (GroupCallActivity.this.call == null) {
                    scale3 = scale4;
                } else {
                    canvas.save();
                    float scale5 = BlobDrawable.SCALE_BIG_MIN + (BlobDrawable.SCALE_BIG * GroupCallActivity.this.amplitude * GroupCallActivity.this.scheduleButtonsScale);
                    canvas.scale(scale5 * showWavesProgressInterpolated2, scale5 * showWavesProgressInterpolated2, cx, cy);
                    GroupCallActivity.this.bigWaveDrawable.draw(cx, cy, canvas, GroupCallActivity.this.paint);
                    canvas.restore();
                    canvas.save();
                    float scale6 = BlobDrawable.SCALE_SMALL_MIN + (BlobDrawable.SCALE_SMALL * GroupCallActivity.this.amplitude * GroupCallActivity.this.scheduleButtonsScale);
                    canvas.scale(scale6 * showWavesProgressInterpolated2, scale6 * showWavesProgressInterpolated2, cx, cy);
                    GroupCallActivity.this.tinyWaveDrawable.draw(cx, cy, canvas, GroupCallActivity.this.paint);
                    canvas.restore();
                    scale3 = scale6;
                }
                GroupCallActivity.this.paint.setAlpha(255);
                if (canSwitchProgress2) {
                    canvas.drawCircle(cx, cy, AndroidUtilities.dp(57.0f), GroupCallActivity.this.paint);
                    GroupCallActivity.this.paint.setColor(Theme.getColor(Theme.key_voipgroup_connectingProgress));
                    if (progress != 0.0f) {
                        GroupCallActivity.this.paint.setAlpha((int) (progress * 255.0f));
                        GroupCallActivity.this.paint.setShader(null);
                        canvas.drawCircle(cx, cy, AndroidUtilities.dp(57.0f), GroupCallActivity.this.paint);
                    }
                }
                canvas.drawCircle(cx, cy, AndroidUtilities.dp(55.0f) * progress, GroupCallActivity.this.paintTmp);
                if (!canSwitchProgress2) {
                    GroupCallActivity.this.radialProgressView.draw(canvas, cx, cy);
                }
                canvas.restore();
            } else {
                int i = 0;
                while (i < 2) {
                    float buttonRadius = AndroidUtilities.dp(57.0f);
                    if (i == 0 && GroupCallActivity.this.prevState != null) {
                        GroupCallActivity.this.paint.setShader(GroupCallActivity.this.prevState.shader);
                        alpha = 1.0f - GroupCallActivity.this.switchProgress;
                        if (GroupCallActivity.this.prevState.currentState == 3) {
                            buttonRadius -= AndroidUtilities.dp(2.0f) * alpha;
                        }
                    } else if (i == 1 && GroupCallActivity.this.currentState != null) {
                        GroupCallActivity.this.paint.setShader(GroupCallActivity.this.currentState.shader);
                        alpha = GroupCallActivity.this.switchProgress;
                        if (GroupCallActivity.this.currentState.currentState == 3) {
                            buttonRadius -= AndroidUtilities.dp(2.0f) * alpha;
                        }
                    } else {
                        canSwitchProgress = canSwitchProgress2;
                        showWavesProgressInterpolated = showWavesProgressInterpolated2;
                        str2 = str3;
                        str = str4;
                        newTime = newTime2;
                        i++;
                        canSwitchProgress2 = canSwitchProgress;
                        str3 = str2;
                        str4 = str;
                        newTime2 = newTime;
                        showWavesProgressInterpolated2 = showWavesProgressInterpolated;
                    }
                    if (GroupCallActivity.this.paint.getShader() == null) {
                        canSwitchProgress = canSwitchProgress2;
                        str2 = str3;
                        GroupCallActivity.this.paint.setColor(AndroidUtilities.getOffsetColor(Theme.getColor(str4), Theme.getColor(str3), GroupCallActivity.this.colorProgress, 1.0f));
                    } else {
                        canSwitchProgress = canSwitchProgress2;
                        str2 = str3;
                    }
                    int cx2 = (int) (GroupCallActivity.this.muteButton.getX() + (GroupCallActivity.this.muteButton.getMeasuredWidth() / 2));
                    int cy2 = (int) (GroupCallActivity.this.muteButton.getY() + (GroupCallActivity.this.muteButton.getMeasuredHeight() / 2));
                    GroupCallActivity.this.radialMatrix.setTranslate(cx2, cy2);
                    GroupCallActivity.this.radialGradient.setLocalMatrix(GroupCallActivity.this.radialMatrix);
                    GroupCallActivity.this.paint.setAlpha((int) (76.0f * alpha * GroupCallActivity.this.switchToButtonProgress));
                    if (GroupCallActivity.this.switchToButtonProgress <= 0.0f) {
                        str = str4;
                        newTime = newTime2;
                    } else if (i == 1) {
                        int a = GroupCallActivity.this.leaveBackgroundPaint.getAlpha();
                        str = str4;
                        GroupCallActivity.this.leaveBackgroundPaint.setAlpha((int) (a * GroupCallActivity.this.switchToButtonProgress));
                        float radius2 = AndroidUtilities.dp(52.0f) / 2.0f;
                        newTime = newTime2;
                        canvas.drawCircle(GroupCallActivity.this.leaveButton.getX() + (GroupCallActivity.this.leaveButton.getMeasuredWidth() / 2), GroupCallActivity.this.leaveButton.getY() + radius2, radius2, GroupCallActivity.this.leaveBackgroundPaint);
                        GroupCallActivity.this.leaveBackgroundPaint.setAlpha(a);
                    } else {
                        str = str4;
                        newTime = newTime2;
                    }
                    canvas.save();
                    canvas.scale(BlobDrawable.GLOBAL_SCALE * GroupCallActivity.this.muteButton.getScaleX(), BlobDrawable.GLOBAL_SCALE * GroupCallActivity.this.muteButton.getScaleX(), cx2, cy2);
                    canvas.save();
                    if (!GroupCallActivity.isLandscapeMode) {
                        translation = AndroidUtilities.dp(65.0f) * (1.0f - GroupCallActivity.this.switchToButtonInt2);
                    } else {
                        translation = 0.0f;
                    }
                    float scale7 = BlobDrawable.SCALE_BIG_MIN + (BlobDrawable.SCALE_BIG * GroupCallActivity.this.amplitude * 0.5f);
                    canvas.scale(GroupCallActivity.this.showLightingProgress * scale7, GroupCallActivity.this.showLightingProgress * scale7, cx2, cy2);
                    if (i == 1) {
                        float scaleLight2 = (BlobDrawable.LIGHT_GRADIENT_SIZE * GroupCallActivity.this.scheduleButtonsScale) + 0.7f;
                        canvas.save();
                        canvas.scale(scaleLight2, scaleLight2, cx2, cy2);
                        int a2 = GroupCallActivity.this.radialPaint.getAlpha();
                        GroupCallActivity.this.radialPaint.setAlpha((int) (a2 * GroupCallActivity.this.switchToButtonProgress * (1.0f - GroupCallActivity.this.progressToHideUi)));
                        scale = scale7;
                        canvas.drawCircle(cx2, cy2, AndroidUtilities.dp(160.0f), GroupCallActivity.this.radialPaint);
                        GroupCallActivity.this.radialPaint.setAlpha(a2);
                        canvas.restore();
                    } else {
                        scale = scale7;
                    }
                    canvas.restore();
                    if (GroupCallActivity.this.switchToButtonProgress <= 0.0f) {
                        scale2 = scale;
                    } else {
                        canvas.save();
                        float scale8 = BlobDrawable.SCALE_BIG_MIN + (BlobDrawable.SCALE_BIG * GroupCallActivity.this.amplitude * showWavesProgressInterpolated2 * GroupCallActivity.this.scheduleButtonsScale);
                        canvas.scale(scale8, scale8, cx2, cy2);
                        GroupCallActivity.this.bigWaveDrawable.draw(cx2, cy2, canvas, GroupCallActivity.this.paint);
                        canvas.restore();
                        canvas.save();
                        scale2 = BlobDrawable.SCALE_SMALL_MIN + (BlobDrawable.SCALE_SMALL * GroupCallActivity.this.amplitude * showWavesProgressInterpolated2 * GroupCallActivity.this.scheduleButtonsScale);
                        canvas.scale(scale2, scale2, cx2, cy2);
                        GroupCallActivity.this.tinyWaveDrawable.draw(cx2, cy2, canvas, GroupCallActivity.this.paint);
                        canvas.restore();
                    }
                    if (GroupCallActivity.isLandscapeMode) {
                        if (i == 0) {
                            GroupCallActivity.this.paint.setAlpha((int) (GroupCallActivity.this.switchToButtonInt2 * 255.0f));
                        } else {
                            GroupCallActivity.this.paint.setAlpha((int) (alpha * 255.0f * GroupCallActivity.this.switchToButtonInt2));
                        }
                    } else if (i == 0) {
                        GroupCallActivity.this.paint.setAlpha(255);
                    } else {
                        GroupCallActivity.this.paint.setAlpha((int) (alpha * 255.0f));
                    }
                    if (this.currentButtonsAnimation == null) {
                        GroupCallActivity.this.muteButton.setTranslationY(translation);
                    }
                    float switchButtonProgrss = GroupCallActivity.isLandscapeMode ? 1.0f : GroupCallActivity.this.switchToButtonInt2;
                    float startX = (getMeasuredWidth() / 2) - AndroidUtilities.dp(21.0f);
                    float startY = AndroidUtilities.dp(24.0f);
                    float w = (startX + ((buttonRadius - startX) * switchButtonProgrss)) * GroupCallActivity.this.scheduleButtonsScale;
                    showWavesProgressInterpolated = showWavesProgressInterpolated2;
                    float h = (((buttonRadius - startY) * switchButtonProgrss) + startY) * GroupCallActivity.this.scheduleButtonsScale;
                    float translation2 = cx2;
                    GroupCallActivity.this.rect.set(translation2 - w, cy2 - h, cx2 + w, cy2 + h);
                    float rad = AndroidUtilities.dp(4.0f) + ((buttonRadius - AndroidUtilities.dp(4.0f)) * switchButtonProgrss);
                    canvas.drawRoundRect(GroupCallActivity.this.rect, rad, rad, GroupCallActivity.this.paint);
                    if (i == 1 && GroupCallActivity.this.currentState.currentState == 3) {
                        GroupCallActivity.this.radialProgressView.draw(canvas, cx2, cy2);
                    }
                    canvas.restore();
                    if (GroupCallActivity.isLandscapeMode && GroupCallActivity.this.switchToButtonInt2 == 0.0f) {
                        GroupCallActivity.this.paint.setAlpha(255);
                        float x = GroupCallActivity.this.scheduleButtonTextView.getX() - getX();
                        float y = GroupCallActivity.this.scheduleButtonTextView.getY() - getY();
                        GroupCallActivity.this.rect.set(x, y, GroupCallActivity.this.scheduleButtonTextView.getMeasuredWidth() + x, GroupCallActivity.this.scheduleButtonTextView.getMeasuredHeight() + y);
                        canvas.drawRoundRect(GroupCallActivity.this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), GroupCallActivity.this.paint);
                    }
                    i++;
                    canSwitchProgress2 = canSwitchProgress;
                    str3 = str2;
                    str4 = str;
                    newTime2 = newTime;
                    showWavesProgressInterpolated2 = showWavesProgressInterpolated;
                }
            }
            super.dispatchDraw(canvas);
            if (!GroupCallActivity.this.renderersContainer.isAnimating()) {
                invalidate();
            }
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child == GroupCallActivity.this.muteButton && child.getScaleX() != 1.0f) {
                canvas.save();
                float s = (((1.0f / GroupCallActivity.this.muteButton.getScaleX()) - 1.0f) * 0.2f) + 1.0f;
                canvas.scale(s, s, child.getX() + (child.getMeasuredWidth() / 2.0f), child.getY() + (child.getMeasuredHeight() / 2.0f));
                boolean b = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return b;
            }
            boolean b2 = super.drawChild(canvas, child, drawingTime);
            return b2;
        }
    }

    /* renamed from: lambda$new$15$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3480lambda$new$15$orgtelegramuiGroupCallActivity(View v) {
        ChatObject.Call call = this.call;
        if (call == null || call.isScheduled() || isRtmpStream()) {
            getLink(false);
        } else if (VoIPService.getSharedInstance() == null) {
        } else {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(getContext(), false);
        }
    }

    /* renamed from: lambda$new$16$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3481lambda$new$16$orgtelegramuiGroupCallActivity(View view) {
        this.renderersContainer.delayHideUi();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null && service.getVideoState(false) == 2) {
            service.switchCamera();
            if (this.flipIconCurrentEndFrame == 18) {
                RLottieDrawable rLottieDrawable = this.flipIcon;
                this.flipIconCurrentEndFrame = 39;
                rLottieDrawable.setCustomEndFrame(39);
                this.flipIcon.start();
            } else {
                this.flipIcon.setCurrentFrame(0, false);
                RLottieDrawable rLottieDrawable2 = this.flipIcon;
                this.flipIconCurrentEndFrame = 18;
                rLottieDrawable2.setCustomEndFrame(18);
                this.flipIcon.start();
            }
            for (int i = 0; i < this.attachedRenderers.size(); i++) {
                GroupCallMiniTextureView renderer = this.attachedRenderers.get(i);
                if (renderer.participant.participant.self && !renderer.participant.presentation) {
                    renderer.startFlipAnimation();
                }
            }
        }
    }

    /* renamed from: lambda$new$17$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3482lambda$new$17$orgtelegramuiGroupCallActivity(Context context, View v) {
        this.renderersContainer.delayHideUi();
        ChatObject.Call call = this.call;
        if (call == null || call.isScheduled()) {
            dismiss();
            return;
        }
        updateItems();
        onLeaveClick(context, new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                GroupCallActivity.this.dismiss();
            }
        }, false);
    }

    /* renamed from: org.telegram.ui.GroupCallActivity$19 */
    /* loaded from: classes4.dex */
    public class AnonymousClass19 implements View.OnClickListener {
        Runnable finishRunnable = new Runnable() { // from class: org.telegram.ui.GroupCallActivity.19.1
            @Override // java.lang.Runnable
            public void run() {
                GroupCallActivity.this.muteButton.setAnimation(GroupCallActivity.this.bigMicDrawable);
                GroupCallActivity.this.playingHandAnimation = false;
            }
        };

        AnonymousClass19() {
            GroupCallActivity.this = this$0;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            int endFrame;
            int startFrame;
            if (GroupCallActivity.this.call == null || GroupCallActivity.this.muteButtonState == 3) {
                return;
            }
            int i = 6;
            if (!GroupCallActivity.this.isRtmpStream() || GroupCallActivity.this.call.isScheduled()) {
                if (GroupCallActivity.this.muteButtonState == 5) {
                    if (GroupCallActivity.this.startingGroupCall) {
                        return;
                    }
                    v.performHapticFeedback(3, 2);
                    GroupCallActivity.this.startingGroupCall = true;
                    TLRPC.TL_phone_startScheduledGroupCall req = new TLRPC.TL_phone_startScheduledGroupCall();
                    req.call = GroupCallActivity.this.call.getInputGroupCall();
                    GroupCallActivity.this.accountInstance.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$19$$ExternalSyntheticLambda1
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            GroupCallActivity.AnonymousClass19.this.m3515lambda$onClick$1$orgtelegramuiGroupCallActivity$19(tLObject, tL_error);
                        }
                    });
                    return;
                } else if (GroupCallActivity.this.muteButtonState == 7 || GroupCallActivity.this.muteButtonState == 6) {
                    if (GroupCallActivity.this.muteButtonState == 6 && GroupCallActivity.this.reminderHintView != null) {
                        GroupCallActivity.this.reminderHintView.hide();
                    }
                    TLRPC.TL_phone_toggleGroupCallStartSubscription req2 = new TLRPC.TL_phone_toggleGroupCallStartSubscription();
                    req2.call = GroupCallActivity.this.call.getInputGroupCall();
                    GroupCallActivity.this.call.call.schedule_start_subscribed = !GroupCallActivity.this.call.call.schedule_start_subscribed;
                    req2.subscribed = GroupCallActivity.this.call.call.schedule_start_subscribed;
                    GroupCallActivity.this.accountInstance.getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$19$$ExternalSyntheticLambda2
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            GroupCallActivity.AnonymousClass19.this.m3516lambda$onClick$2$orgtelegramuiGroupCallActivity$19(tLObject, tL_error);
                        }
                    });
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    if (groupCallActivity.call.call.schedule_start_subscribed) {
                        i = 7;
                    }
                    groupCallActivity.updateMuteButton(i, true);
                    return;
                } else if (VoIPService.getSharedInstance() != null && !GroupCallActivity.this.isStillConnecting()) {
                    if (GroupCallActivity.this.muteButtonState == 2 || GroupCallActivity.this.muteButtonState == 4) {
                        if (!GroupCallActivity.this.playingHandAnimation) {
                            GroupCallActivity.this.playingHandAnimation = true;
                            AndroidUtilities.shakeView(GroupCallActivity.this.muteLabel[0], 2.0f, 0);
                            v.performHapticFeedback(3, 2);
                            int num = Utilities.random.nextInt(100);
                            if (num < 32) {
                                startFrame = 0;
                                endFrame = 120;
                            } else if (num < 64) {
                                startFrame = 120;
                                endFrame = PsExtractor.VIDEO_STREAM_MASK;
                            } else if (num < 97) {
                                startFrame = PsExtractor.VIDEO_STREAM_MASK;
                                endFrame = 420;
                            } else if (num == 98) {
                                startFrame = 420;
                                endFrame = 540;
                            } else {
                                startFrame = 540;
                                endFrame = 720;
                            }
                            GroupCallActivity.this.handDrawables.setCustomEndFrame(endFrame);
                            GroupCallActivity.this.handDrawables.setOnFinishCallback(this.finishRunnable, endFrame - 1);
                            GroupCallActivity.this.muteButton.setAnimation(GroupCallActivity.this.handDrawables);
                            GroupCallActivity.this.handDrawables.setCurrentFrame(startFrame);
                            GroupCallActivity.this.muteButton.playAnimation();
                            if (GroupCallActivity.this.muteButtonState == 2) {
                                long peerId = MessageObject.getPeerId(GroupCallActivity.this.call.participants.get(MessageObject.getPeerId(GroupCallActivity.this.selfPeer)).peer);
                                TLObject object = DialogObject.isUserDialog(peerId) ? GroupCallActivity.this.accountInstance.getMessagesController().getUser(Long.valueOf(peerId)) : GroupCallActivity.this.accountInstance.getMessagesController().getChat(Long.valueOf(-peerId));
                                VoIPService.getSharedInstance().editCallMember(object, null, null, null, true, null);
                                GroupCallActivity.this.updateMuteButton(4, true);
                                return;
                            }
                            return;
                        }
                        return;
                    } else if (GroupCallActivity.this.muteButtonState == 0) {
                        GroupCallActivity.this.updateMuteButton(1, true);
                        VoIPService.getSharedInstance().setMicMute(false, false, true);
                        GroupCallActivity.this.muteButton.performHapticFeedback(3, 2);
                        return;
                    } else {
                        GroupCallActivity.this.updateMuteButton(0, true);
                        VoIPService.getSharedInstance().setMicMute(true, false, true);
                        GroupCallActivity.this.muteButton.performHapticFeedback(3, 2);
                        return;
                    }
                } else {
                    return;
                }
            }
            boolean fullscreen = GroupCallActivity.this.renderersContainer != null && GroupCallActivity.this.renderersContainer.inFullscreenMode && (AndroidUtilities.isTablet() || GroupCallActivity.isLandscapeMode == GroupCallActivity.this.isRtmpLandscapeMode());
            if (fullscreen) {
                GroupCallActivity.this.fullscreenFor(null);
                if (GroupCallActivity.isLandscapeMode) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$19$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            GroupCallActivity.AnonymousClass19.this.m3514lambda$onClick$0$orgtelegramuiGroupCallActivity$19();
                        }
                    }, 200L);
                }
                GroupCallActivity.this.parentActivity.setRequestedOrientation(-1);
            } else if (!GroupCallActivity.this.visibleVideoParticipants.isEmpty()) {
                ChatObject.VideoParticipant participant = GroupCallActivity.this.visibleVideoParticipants.get(0);
                if (!AndroidUtilities.isTablet()) {
                    if (GroupCallActivity.isLandscapeMode == GroupCallActivity.this.isRtmpLandscapeMode()) {
                        GroupCallActivity.this.fullscreenFor(participant);
                    }
                    if (GroupCallActivity.this.isRtmpLandscapeMode()) {
                        GroupCallActivity.this.parentActivity.setRequestedOrientation(6);
                        return;
                    } else {
                        GroupCallActivity.this.parentActivity.setRequestedOrientation(1);
                        return;
                    }
                }
                GroupCallActivity.this.fullscreenFor(participant);
            }
        }

        /* renamed from: lambda$onClick$0$org-telegram-ui-GroupCallActivity$19 */
        public /* synthetic */ void m3514lambda$onClick$0$orgtelegramuiGroupCallActivity$19() {
            GroupCallActivity.this.wasNotInLayoutFullscreen = null;
            GroupCallActivity.this.wasExpandBigSize = null;
            GroupCallActivity groupCallActivity = GroupCallActivity.this;
            groupCallActivity.updateMuteButton(groupCallActivity.muteButtonState, true);
        }

        /* renamed from: lambda$onClick$1$org-telegram-ui-GroupCallActivity$19 */
        public /* synthetic */ void m3515lambda$onClick$1$orgtelegramuiGroupCallActivity$19(TLObject response, TLRPC.TL_error error) {
            if (response != null) {
                GroupCallActivity.this.accountInstance.getMessagesController().processUpdates((TLRPC.Updates) response, false);
            }
        }

        /* renamed from: lambda$onClick$2$org-telegram-ui-GroupCallActivity$19 */
        public /* synthetic */ void m3516lambda$onClick$2$orgtelegramuiGroupCallActivity$19(TLObject response, TLRPC.TL_error error) {
            if (response != null) {
                GroupCallActivity.this.accountInstance.getMessagesController().processUpdates((TLRPC.Updates) response, false);
            }
        }
    }

    /* renamed from: lambda$new$18$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3483lambda$new$18$orgtelegramuiGroupCallActivity(int id) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(id);
    }

    /* renamed from: lambda$new$19$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3484lambda$new$19$orgtelegramuiGroupCallActivity(Context context, View v) {
        if (this.call == null || this.renderersContainer.inFullscreenMode) {
            return;
        }
        if (this.call.call.join_muted) {
            this.everyoneItem.setColors(Theme.getColor(Theme.key_voipgroup_actionBarItems), Theme.getColor(Theme.key_voipgroup_actionBarItems));
            this.everyoneItem.setChecked(false);
            this.adminItem.setColors(Theme.getColor(Theme.key_voipgroup_checkMenu), Theme.getColor(Theme.key_voipgroup_checkMenu));
            this.adminItem.setChecked(true);
        } else {
            this.everyoneItem.setColors(Theme.getColor(Theme.key_voipgroup_checkMenu), Theme.getColor(Theme.key_voipgroup_checkMenu));
            this.everyoneItem.setChecked(true);
            this.adminItem.setColors(Theme.getColor(Theme.key_voipgroup_actionBarItems), Theme.getColor(Theme.key_voipgroup_actionBarItems));
            this.adminItem.setChecked(false);
        }
        this.changingPermissions = false;
        this.otherItem.hideSubItem(1);
        this.otherItem.hideSubItem(2);
        if (VoIPService.getSharedInstance() != null && (VoIPService.getSharedInstance().hasEarpiece() || VoIPService.getSharedInstance().isBluetoothHeadsetConnected())) {
            int rout = VoIPService.getSharedInstance().getCurrentAudioRoute();
            if (rout == 2) {
                this.soundItem.setIcon(R.drawable.msg_voice_bluetooth);
                this.soundItem.setSubtext(VoIPService.getSharedInstance().currentBluetoothDeviceName != null ? VoIPService.getSharedInstance().currentBluetoothDeviceName : LocaleController.getString("VoipAudioRoutingBluetooth", R.string.VoipAudioRoutingBluetooth));
            } else {
                int i = R.string.VoipAudioRoutingPhone;
                String str = "VoipAudioRoutingPhone";
                int i2 = R.drawable.msg_voice_phone;
                if (rout == 0) {
                    ActionBarMenuSubItem actionBarMenuSubItem = this.soundItem;
                    if (VoIPService.getSharedInstance().isHeadsetPlugged()) {
                        i2 = R.drawable.msg_voice_headphones;
                    }
                    actionBarMenuSubItem.setIcon(i2);
                    ActionBarMenuSubItem actionBarMenuSubItem2 = this.soundItem;
                    if (VoIPService.getSharedInstance().isHeadsetPlugged()) {
                        i = R.string.VoipAudioRoutingHeadset;
                        str = "VoipAudioRoutingHeadset";
                    }
                    actionBarMenuSubItem2.setSubtext(LocaleController.getString(str, i));
                } else if (rout == 1) {
                    AudioManager am = (AudioManager) context.getSystemService("audio");
                    if (am.isSpeakerphoneOn()) {
                        this.soundItem.setIcon(R.drawable.msg_voice_speaker);
                        this.soundItem.setSubtext(LocaleController.getString("VoipAudioRoutingSpeaker", R.string.VoipAudioRoutingSpeaker));
                    } else {
                        this.soundItem.setIcon(R.drawable.msg_voice_phone);
                        this.soundItem.setSubtext(LocaleController.getString(str, R.string.VoipAudioRoutingPhone));
                    }
                }
            }
        }
        updateItems();
        this.otherItem.toggleSubMenu();
    }

    /* renamed from: lambda$new$20$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3485lambda$new$20$orgtelegramuiGroupCallActivity(View v) {
        if (isRtmpStream()) {
            if (AndroidUtilities.checkInlinePermissions(this.parentActivity)) {
                RTMPStreamPipOverlay.show();
                dismiss();
                return;
            }
            AlertsCreator.createDrawOverlayPermissionDialog(this.parentActivity, null).show();
        } else if (AndroidUtilities.checkInlinePermissions(this.parentActivity)) {
            GroupCallPip.clearForce();
            dismiss();
        } else {
            AlertsCreator.createDrawOverlayGroupCallPermissionDialog(getContext()).show();
        }
    }

    /* renamed from: lambda$new$21$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3486lambda$new$21$orgtelegramuiGroupCallActivity(View v) {
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService == null) {
            return;
        }
        if (voIPService.getVideoState(true) == 2) {
            voIPService.stopScreenCapture();
        } else {
            startScreenCapture();
        }
    }

    /* renamed from: org.telegram.ui.GroupCallActivity$20 */
    /* loaded from: classes4.dex */
    public class AnonymousClass20 extends AudioPlayerAlert.ClippingTextViewSwitcher {
        final /* synthetic */ Context val$context;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass20(Context context, Context context2) {
            super(context);
            GroupCallActivity.this = this$0;
            this.val$context = context2;
        }

        @Override // org.telegram.ui.Components.AudioPlayerAlert.ClippingTextViewSwitcher
        protected TextView createTextView() {
            final TextView textView = new TextView(this.val$context);
            textView.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView.setGravity(51);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$20$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    GroupCallActivity.AnonymousClass20.this.m3517lambda$createTextView$0$orgtelegramuiGroupCallActivity$20(textView, view);
                }
            });
            return textView;
        }

        /* renamed from: lambda$createTextView$0$org-telegram-ui-GroupCallActivity$20 */
        public /* synthetic */ void m3517lambda$createTextView$0$orgtelegramuiGroupCallActivity$20(TextView textView, View v) {
            if (GroupCallActivity.this.call != null && GroupCallActivity.this.call.recording) {
                GroupCallActivity.this.showRecordHint(textView);
            }
        }
    }

    /* renamed from: lambda$new$22$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3487lambda$new$22$orgtelegramuiGroupCallActivity(View v) {
        ChatObject.Call call = this.call;
        if (call != null && call.recording) {
            showRecordHint(this.actionBar.getTitleTextView());
        }
    }

    /* renamed from: lambda$new$23$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3488lambda$new$23$orgtelegramuiGroupCallActivity(View view, int position) {
        GroupCallFullscreenAdapter.GroupCallUserCell userCell = (GroupCallFullscreenAdapter.GroupCallUserCell) view;
        if (userCell.getVideoParticipant() == null) {
            fullscreenFor(new ChatObject.VideoParticipant(userCell.getParticipant(), false, false));
        } else {
            fullscreenFor(userCell.getVideoParticipant());
        }
    }

    /* renamed from: lambda$new$24$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ boolean m3489lambda$new$24$orgtelegramuiGroupCallActivity(View view, int position) {
        if (showMenuForCell(view)) {
            this.listView.performHapticFeedback(0);
        }
        return false;
    }

    /* renamed from: org.telegram.ui.GroupCallActivity$27 */
    /* loaded from: classes4.dex */
    public class AnonymousClass27 extends GroupCallRenderersContainer {
        ValueAnimator uiVisibilityAnimator;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass27(Context context, RecyclerView listView, RecyclerView fullscreenListView, ArrayList arrayList, ChatObject.Call call, GroupCallActivity groupCallActivity) {
            super(context, listView, fullscreenListView, arrayList, call, groupCallActivity);
            GroupCallActivity.this = this$0;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.voip.GroupCallRenderersContainer
        public void update() {
            super.update();
            float finalColorProgress2 = Math.max(GroupCallActivity.this.colorProgress, GroupCallActivity.this.renderersContainer == null ? 0.0f : GroupCallActivity.this.renderersContainer.progressToFullscreenMode);
            GroupCallActivity.this.navBarColor = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_actionBarUnscrolled), Theme.getColor(Theme.key_voipgroup_actionBar), finalColorProgress2, 1.0f);
            GroupCallActivity.this.containerView.invalidate();
            GroupCallActivity groupCallActivity = GroupCallActivity.this;
            groupCallActivity.setColorProgress(groupCallActivity.colorProgress);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.voip.GroupCallRenderersContainer, android.view.ViewGroup
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child == GroupCallActivity.this.scrimRenderer) {
                return true;
            }
            return super.drawChild(canvas, child, drawingTime);
        }

        @Override // org.telegram.ui.Components.voip.GroupCallRenderersContainer
        protected void onFullScreenModeChanged(boolean startAnimation) {
            GroupCallActivity.this.delayedGroupCallUpdated = startAnimation;
            int i = 0;
            if (!GroupCallActivity.isTabletMode) {
                if (startAnimation) {
                    GroupCallActivity.this.undoView[0].hide(false, 1);
                    GroupCallActivity.this.renderersContainer.undoView[0].hide(false, 2);
                    if (!GroupCallActivity.this.renderersContainer.inFullscreenMode) {
                        GroupCallActivity.this.listView.setVisibility(0);
                        GroupCallActivity.this.actionBar.setVisibility(0);
                    }
                    GroupCallActivity.this.updateState(true, false);
                    GroupCallActivity.this.buttonsContainer.requestLayout();
                    if (GroupCallActivity.this.fullscreenUsersListView.getVisibility() != 0) {
                        GroupCallActivity.this.fullscreenUsersListView.setVisibility(0);
                        GroupCallActivity.this.fullscreenAdapter.setVisibility(GroupCallActivity.this.fullscreenUsersListView, true);
                        GroupCallActivity.this.fullscreenAdapter.update(false, GroupCallActivity.this.fullscreenUsersListView);
                    } else {
                        GroupCallActivity.this.fullscreenAdapter.setVisibility(GroupCallActivity.this.fullscreenUsersListView, true);
                        GroupCallActivity.this.applyCallParticipantUpdates(true);
                    }
                } else {
                    if (!GroupCallActivity.this.renderersContainer.inFullscreenMode) {
                        GroupCallActivity.this.fullscreenUsersListView.setVisibility(8);
                        GroupCallActivity.this.fullscreenAdapter.setVisibility(GroupCallActivity.this.fullscreenUsersListView, false);
                    } else {
                        GroupCallActivity.this.actionBar.setVisibility(8);
                        GroupCallActivity.this.listView.setVisibility(8);
                    }
                    if (GroupCallActivity.this.fullscreenUsersListView.getVisibility() == 0) {
                        for (int i2 = 0; i2 < GroupCallActivity.this.fullscreenUsersListView.getChildCount(); i2++) {
                            View child = GroupCallActivity.this.fullscreenUsersListView.getChildAt(i2);
                            child.setAlpha(1.0f);
                            child.setScaleX(1.0f);
                            child.setScaleY(1.0f);
                            child.setTranslationX(0.0f);
                            child.setTranslationY(0.0f);
                            ((GroupCallFullscreenAdapter.GroupCallUserCell) child).setProgressToFullscreen(GroupCallActivity.this.renderersContainer.progressToFullscreenMode);
                        }
                    }
                }
                View view = GroupCallActivity.this.buttonsBackgroundGradientView2;
                if (!startAnimation) {
                    i = 8;
                }
                view.setVisibility(i);
                if (!GroupCallActivity.this.delayedGroupCallUpdated) {
                    GroupCallActivity.this.applyCallParticipantUpdates(true);
                }
            } else if (!startAnimation && GroupCallActivity.this.renderersContainer.inFullscreenMode) {
                GroupCallActivity.this.tabletGridAdapter.setVisibility(GroupCallActivity.this.tabletVideoGridView, false, true);
            }
        }

        @Override // org.telegram.ui.Components.voip.GroupCallRenderersContainer
        public void onUiVisibilityChanged() {
            if (GroupCallActivity.this.renderersContainer != null) {
                final boolean uiVisible = GroupCallActivity.this.renderersContainer.isUiVisible();
                ValueAnimator valueAnimator = this.uiVisibilityAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.uiVisibilityAnimator.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = GroupCallActivity.this.progressToHideUi;
                fArr[1] = uiVisible ? 0.0f : 1.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.uiVisibilityAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.GroupCallActivity$27$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        GroupCallActivity.AnonymousClass27.this.m3518x64102039(valueAnimator2);
                    }
                });
                this.uiVisibilityAnimator.setDuration(350L);
                this.uiVisibilityAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.uiVisibilityAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.27.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                        GroupCallActivity.this.invalidateLayoutFullscreen();
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        AnonymousClass27.this.uiVisibilityAnimator = null;
                        GroupCallActivity.this.progressToHideUi = uiVisible ? 0.0f : 1.0f;
                        GroupCallActivity.this.renderersContainer.setProgressToHideUi(GroupCallActivity.this.progressToHideUi);
                        GroupCallActivity.this.fullscreenUsersListView.invalidate();
                        GroupCallActivity.this.containerView.invalidate();
                        GroupCallActivity.this.buttonsContainer.invalidate();
                    }
                });
                this.uiVisibilityAnimator.start();
            }
        }

        /* renamed from: lambda$onUiVisibilityChanged$0$org-telegram-ui-GroupCallActivity$27 */
        public /* synthetic */ void m3518x64102039(ValueAnimator valueAnimator) {
            GroupCallActivity.this.progressToHideUi = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallActivity.this.renderersContainer.setProgressToHideUi(GroupCallActivity.this.progressToHideUi);
            GroupCallActivity.this.fullscreenUsersListView.invalidate();
            GroupCallActivity.this.containerView.invalidate();
            GroupCallActivity.this.buttonsContainer.invalidate();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.voip.GroupCallRenderersContainer
        public boolean canHideUI() {
            return super.canHideUI() && GroupCallActivity.this.previewDialog == null;
        }

        @Override // org.telegram.ui.Components.voip.GroupCallRenderersContainer
        protected void onBackPressed() {
            GroupCallActivity.this.onBackPressed();
        }
    }

    /* renamed from: lambda$new$29$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3494lambda$new$29$orgtelegramuiGroupCallActivity(NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, final TLRPC.Chat chat, AccountInstance account, final TLRPC.InputPeer peer, View v) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.scheduleAnimator = ofFloat;
        ofFloat.setDuration(600L);
        this.scheduleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GroupCallActivity.this.m3490lambda$new$25$orgtelegramuiGroupCallActivity(valueAnimator);
            }
        });
        this.scheduleAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.34
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                GroupCallActivity.this.scheduleAnimator = null;
            }
        });
        this.scheduleAnimator.start();
        if (ChatObject.isChannelOrGiga(this.currentChat)) {
            this.titleTextView.setText(LocaleController.getString("VoipChannelVoiceChat", R.string.VoipChannelVoiceChat), true);
        } else {
            this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", R.string.VoipGroupVoiceChat), true);
        }
        Calendar calendar = Calendar.getInstance();
        boolean setSeconds = AlertsCreator.checkScheduleDate(null, null, 604800L, 3, dayPicker, hourPicker, minutePicker);
        calendar.setTimeInMillis(System.currentTimeMillis() + (dayPicker.getValue() * 24 * 3600 * 1000));
        calendar.set(11, hourPicker.getValue());
        calendar.set(12, minutePicker.getValue());
        if (setSeconds) {
            calendar.set(13, 0);
        }
        this.scheduleStartAt = (int) (calendar.getTimeInMillis() / 1000);
        updateScheduleUI(false);
        TLRPC.TL_phone_createGroupCall req = new TLRPC.TL_phone_createGroupCall();
        req.peer = MessagesController.getInputPeer(chat);
        req.random_id = Utilities.random.nextInt();
        req.schedule_date = this.scheduleStartAt;
        req.flags |= 2;
        account.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda48
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupCallActivity.this.m3493lambda$new$28$orgtelegramuiGroupCallActivity(chat, peer, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$new$25$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3490lambda$new$25$orgtelegramuiGroupCallActivity(ValueAnimator a) {
        this.switchToButtonProgress = ((Float) a.getAnimatedValue()).floatValue();
        updateScheduleUI(true);
        this.buttonsContainer.invalidate();
        this.listView.invalidate();
    }

    /* renamed from: lambda$new$28$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3493lambda$new$28$orgtelegramuiGroupCallActivity(final TLRPC.Chat chat, final TLRPC.InputPeer peer, TLObject response, final TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            int a = 0;
            while (true) {
                if (a >= updates.updates.size()) {
                    break;
                }
                TLRPC.Update update = updates.updates.get(a);
                if (!(update instanceof TLRPC.TL_updateGroupCall)) {
                    a++;
                } else {
                    final TLRPC.TL_updateGroupCall updateGroupCall = (TLRPC.TL_updateGroupCall) update;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda37
                        @Override // java.lang.Runnable
                        public final void run() {
                            GroupCallActivity.this.m3491lambda$new$26$orgtelegramuiGroupCallActivity(chat, peer, updateGroupCall);
                        }
                    });
                    break;
                }
            }
            this.accountInstance.getMessagesController().processUpdates(updates, false);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                GroupCallActivity.this.m3492lambda$new$27$orgtelegramuiGroupCallActivity(error);
            }
        });
    }

    /* renamed from: lambda$new$26$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3491lambda$new$26$orgtelegramuiGroupCallActivity(TLRPC.Chat chat, TLRPC.InputPeer peer, TLRPC.TL_updateGroupCall updateGroupCall) {
        ChatObject.Call call = new ChatObject.Call();
        this.call = call;
        call.call = new TLRPC.TL_groupCall();
        this.call.call.participants_count = 0;
        this.call.call.version = 1;
        this.call.call.can_start_video = true;
        this.call.call.can_change_join_muted = true;
        this.call.chatId = chat.id;
        this.call.call.schedule_date = this.scheduleStartAt;
        this.call.call.flags |= 128;
        this.call.currentAccount = this.accountInstance;
        this.call.setSelfPeer(peer);
        this.call.call.access_hash = updateGroupCall.call.access_hash;
        this.call.call.id = updateGroupCall.call.id;
        this.call.createNoVideoParticipant();
        this.fullscreenAdapter.setGroupCall(this.call);
        this.renderersContainer.setGroupCall(this.call);
        this.tabletGridAdapter.setGroupCall(this.call);
        this.accountInstance.getMessagesController().putGroupCall(this.call.chatId, this.call);
    }

    /* renamed from: lambda$new$27$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3492lambda$new$27$orgtelegramuiGroupCallActivity(TLRPC.TL_error error) {
        this.accountInstance.getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, 6, error.text);
        dismiss();
    }

    public static /* synthetic */ String lambda$new$30(long currentTime, Calendar calendar, int currentYear, int value) {
        if (value == 0) {
            return LocaleController.getString("MessageScheduleToday", R.string.MessageScheduleToday);
        }
        long date = (value * 86400000) + currentTime;
        calendar.setTimeInMillis(date);
        int year = calendar.get(1);
        if (year == currentYear) {
            return LocaleController.getInstance().formatterScheduleDay.format(date);
        }
        return LocaleController.getInstance().formatterScheduleYear.format(date);
    }

    /* renamed from: lambda$new$31$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3495lambda$new$31$orgtelegramuiGroupCallActivity(NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, NumberPicker picker, int oldVal, int newVal) {
        try {
            this.container.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        AlertsCreator.checkScheduleDate(this.scheduleButtonTextView, this.scheduleInfoTextView, 604800L, 2, dayPicker, hourPicker, minutePicker);
    }

    /* renamed from: lambda$new$34$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3496lambda$new$34$orgtelegramuiGroupCallActivity(Context context, View View) {
        LaunchActivity launchActivity;
        boolean z = false;
        if (Build.VERSION.SDK_INT >= 23 && (launchActivity = this.parentActivity) != null && launchActivity.checkSelfPermission("android.permission.CAMERA") != 0) {
            this.parentActivity.requestPermissions(new String[]{"android.permission.CAMERA"}, LocationRequest.PRIORITY_LOW_POWER);
        } else if (VoIPService.getSharedInstance() == null) {
        } else {
            if (VoIPService.getSharedInstance().getVideoState(false) != 2) {
                this.undoView[0].hide(false, 1);
                if (this.previewDialog == null) {
                    VoIPService voIPService = VoIPService.getSharedInstance();
                    if (voIPService != null) {
                        voIPService.createCaptureDevice(false);
                    }
                    if (VoIPService.getSharedInstance().getVideoState(true) != 2) {
                        z = true;
                    }
                    this.previewDialog = new PrivateVideoPreviewDialog(context, true, z) { // from class: org.telegram.ui.GroupCallActivity.38
                        @Override // org.telegram.ui.Components.voip.PrivateVideoPreviewDialog
                        public void onDismiss(boolean screencast, boolean apply) {
                            boolean showMicIcon = GroupCallActivity.this.previewDialog.micEnabled;
                            GroupCallActivity.this.previewDialog = null;
                            VoIPService service = VoIPService.getSharedInstance();
                            if (apply) {
                                if (service != null) {
                                    service.setupCaptureDevice(screencast, showMicIcon);
                                }
                                if (screencast && service != null) {
                                    service.setVideoState(false, 0);
                                }
                                GroupCallActivity.this.updateState(true, false);
                                GroupCallActivity.this.call.sortParticipants();
                                GroupCallActivity.this.applyCallParticipantUpdates(true);
                                GroupCallActivity.this.buttonsContainer.requestLayout();
                            } else if (service != null) {
                                service.setVideoState(false, 0);
                            }
                        }
                    };
                    this.container.addView(this.previewDialog);
                    if (voIPService != null && !voIPService.isFrontFaceCamera()) {
                        voIPService.switchCamera();
                        return;
                    }
                    return;
                }
                return;
            }
            VoIPService.getSharedInstance().setVideoState(false, 0);
            updateState(true, false);
            updateSpeakerPhoneIcon(false);
            this.call.sortParticipants();
            applyCallParticipantUpdates(true);
            this.buttonsContainer.requestLayout();
        }
    }

    public LaunchActivity getParentActivity() {
        return this.parentActivity;
    }

    public void invalidateLayoutFullscreen() {
        int systemUiVisibility;
        if (isRtmpStream()) {
            boolean notFullscreen = this.renderersContainer.isUiVisible() || !this.renderersContainer.inFullscreenMode || (isLandscapeMode != isRtmpLandscapeMode() && !AndroidUtilities.isTablet());
            Boolean bool = this.wasNotInLayoutFullscreen;
            if (bool != null && notFullscreen == bool.booleanValue()) {
                return;
            }
            int systemUiVisibility2 = this.containerView.getSystemUiVisibility();
            if (notFullscreen) {
                systemUiVisibility = systemUiVisibility2 & (-5) & (-3);
                getWindow().clearFlags(1024);
                setHideSystemVerticalInsets(false);
            } else {
                setHideSystemVerticalInsets(true);
                systemUiVisibility = systemUiVisibility2 | 4 | 2;
                getWindow().addFlags(1024);
            }
            this.containerView.setSystemUiVisibility(systemUiVisibility);
            this.wasNotInLayoutFullscreen = Boolean.valueOf(notFullscreen);
        }
    }

    public LinearLayout getMenuItemsContainer() {
        return this.menuItemsContainer;
    }

    public void fullscreenFor(final ChatObject.VideoParticipant videoParticipant) {
        if (videoParticipant == null) {
            this.parentActivity.setRequestedOrientation(-1);
        }
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService == null || this.renderersContainer.isAnimating()) {
            return;
        }
        if (isTabletMode) {
            if (this.requestFullscreenListener != null) {
                this.listView.getViewTreeObserver().removeOnPreDrawListener(this.requestFullscreenListener);
                this.requestFullscreenListener = null;
            }
            final ArrayList<ChatObject.VideoParticipant> activeSinks = new ArrayList<>();
            if (videoParticipant == null) {
                this.attachedRenderersTmp.clear();
                this.attachedRenderersTmp.addAll(this.attachedRenderers);
                for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
                    final GroupCallMiniTextureView miniTextureView = this.attachedRenderersTmp.get(i);
                    if (miniTextureView.primaryView != null) {
                        miniTextureView.primaryView.setRenderer(null);
                        if (miniTextureView.secondaryView != null) {
                            miniTextureView.secondaryView.setRenderer(null);
                        }
                        if (miniTextureView.tabletGridView != null) {
                            miniTextureView.tabletGridView.setRenderer(null);
                        }
                        activeSinks.add(miniTextureView.participant);
                        miniTextureView.forceDetach(false);
                        miniTextureView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.39
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (miniTextureView.getParent() != null) {
                                    GroupCallActivity.this.containerView.removeView(miniTextureView);
                                }
                            }
                        });
                    }
                }
                this.listViewVideoVisibility = false;
                this.tabletGridAdapter.setVisibility(this.tabletVideoGridView, true, true);
            } else {
                this.attachedRenderersTmp.clear();
                this.attachedRenderersTmp.addAll(this.attachedRenderers);
                for (int i2 = 0; i2 < this.attachedRenderersTmp.size(); i2++) {
                    final GroupCallMiniTextureView miniTextureView2 = this.attachedRenderersTmp.get(i2);
                    if (miniTextureView2.tabletGridView != null && (miniTextureView2.participant == null || !miniTextureView2.participant.equals(videoParticipant))) {
                        activeSinks.add(miniTextureView2.participant);
                        miniTextureView2.forceDetach(false);
                        if (miniTextureView2.secondaryView != null) {
                            miniTextureView2.secondaryView.setRenderer(null);
                        }
                        if (miniTextureView2.primaryView != null) {
                            miniTextureView2.primaryView.setRenderer(null);
                        }
                        miniTextureView2.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.40
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (miniTextureView2.getParent() != null) {
                                    GroupCallActivity.this.containerView.removeView(miniTextureView2);
                                }
                            }
                        });
                    }
                }
                this.listViewVideoVisibility = true;
                this.tabletGridAdapter.setVisibility(this.tabletVideoGridView, false, false);
                if (!activeSinks.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda34
                        @Override // java.lang.Runnable
                        public final void run() {
                            GroupCallActivity.this.m3464lambda$fullscreenFor$35$orgtelegramuiGroupCallActivity(activeSinks);
                        }
                    });
                }
            }
            final boolean updateScroll = !this.renderersContainer.inFullscreenMode;
            ViewTreeObserver viewTreeObserver = this.listView.getViewTreeObserver();
            ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.GroupCallActivity.41
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    GroupCallActivity.this.requestFullscreenListener = null;
                    GroupCallActivity.this.renderersContainer.requestFullscreen(videoParticipant);
                    if (GroupCallActivity.this.delayedGroupCallUpdated) {
                        GroupCallActivity.this.delayedGroupCallUpdated = false;
                        GroupCallActivity.this.applyCallParticipantUpdates(true);
                        if (updateScroll && videoParticipant != null) {
                            GroupCallActivity.this.listView.scrollToPosition(0);
                        }
                        GroupCallActivity.this.delayedGroupCallUpdated = true;
                    } else {
                        GroupCallActivity.this.applyCallParticipantUpdates(true);
                    }
                    return false;
                }
            };
            this.requestFullscreenListener = onPreDrawListener;
            viewTreeObserver.addOnPreDrawListener(onPreDrawListener);
            return;
        }
        if (this.requestFullscreenListener != null) {
            this.listView.getViewTreeObserver().removeOnPreDrawListener(this.requestFullscreenListener);
            this.requestFullscreenListener = null;
        }
        if (videoParticipant != null) {
            if (this.fullscreenUsersListView.getVisibility() != 0) {
                this.fullscreenUsersListView.setVisibility(0);
                this.fullscreenAdapter.update(false, this.fullscreenUsersListView);
                this.delayedGroupCallUpdated = true;
                if (!this.renderersContainer.inFullscreenMode) {
                    this.fullscreenAdapter.scrollTo(videoParticipant, this.fullscreenUsersListView);
                }
                ViewTreeObserver viewTreeObserver2 = this.listView.getViewTreeObserver();
                ViewTreeObserver.OnPreDrawListener onPreDrawListener2 = new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.GroupCallActivity.42
                    @Override // android.view.ViewTreeObserver.OnPreDrawListener
                    public boolean onPreDraw() {
                        GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                        GroupCallActivity.this.requestFullscreenListener = null;
                        GroupCallActivity.this.renderersContainer.requestFullscreen(videoParticipant);
                        AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                        return false;
                    }
                };
                this.requestFullscreenListener = onPreDrawListener2;
                viewTreeObserver2.addOnPreDrawListener(onPreDrawListener2);
                return;
            }
            this.renderersContainer.requestFullscreen(videoParticipant);
            AndroidUtilities.updateVisibleRows(this.fullscreenUsersListView);
        } else if (this.listView.getVisibility() != 0) {
            this.listView.setVisibility(0);
            applyCallParticipantUpdates(false);
            this.delayedGroupCallUpdated = true;
            ViewTreeObserver viewTreeObserver3 = this.listView.getViewTreeObserver();
            ViewTreeObserver.OnPreDrawListener onPreDrawListener3 = new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.GroupCallActivity.43
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    GroupCallActivity.this.renderersContainer.requestFullscreen(null);
                    AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                    return false;
                }
            };
            this.requestFullscreenListener = onPreDrawListener3;
            viewTreeObserver3.addOnPreDrawListener(onPreDrawListener3);
        } else {
            ViewTreeObserver viewTreeObserver4 = this.listView.getViewTreeObserver();
            ViewTreeObserver.OnPreDrawListener onPreDrawListener4 = new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.GroupCallActivity.44
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    GroupCallActivity.this.renderersContainer.requestFullscreen(null);
                    AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                    return false;
                }
            };
            this.requestFullscreenListener = onPreDrawListener4;
            viewTreeObserver4.addOnPreDrawListener(onPreDrawListener4);
        }
    }

    /* renamed from: lambda$fullscreenFor$35$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3464lambda$fullscreenFor$35$orgtelegramuiGroupCallActivity(ArrayList activeSinks) {
        for (int i = 0; i < this.attachedRenderers.size(); i++) {
            if (this.attachedRenderers.get(i).participant != null) {
                activeSinks.remove(this.attachedRenderers.get(i).participant);
            }
        }
        for (int i2 = 0; i2 < activeSinks.size(); i2++) {
            ChatObject.VideoParticipant participant = (ChatObject.VideoParticipant) activeSinks.get(i2);
            if (participant.participant.self) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setLocalSink(null, participant.presentation);
                }
            } else if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().removeRemoteSink(participant.participant, participant.presentation);
            }
        }
    }

    public void enableCamera() {
        this.cameraButton.callOnClick();
    }

    public void checkContentOverlayed() {
        boolean overlayed = !this.avatarPriviewTransitionInProgress && this.blurredView.getVisibility() == 0 && this.blurredView.getAlpha() == 1.0f;
        if (this.contentFullyOverlayed != overlayed) {
            this.contentFullyOverlayed = overlayed;
            this.buttonsContainer.invalidate();
            this.containerView.invalidate();
            this.listView.invalidate();
        }
    }

    private void updateScheduleUI(boolean animation) {
        float alpha;
        float scheduleButtonsScale2;
        LinearLayout linearLayout = this.scheduleTimerContainer;
        float f = 1.0f;
        if ((linearLayout == null || this.call != null) && this.scheduleAnimator == null) {
            this.scheduleButtonsScale = 1.0f;
            this.switchToButtonInt2 = 1.0f;
            this.switchToButtonProgress = 1.0f;
            if (linearLayout == null) {
                return;
            }
        }
        int newVisibility = 4;
        if (!animation) {
            AndroidUtilities.cancelRunOnUIThread(this.updateSchedeulRunnable);
            this.updateSchedeulRunnable.run();
            ChatObject.Call call = this.call;
            if (call == null || call.isScheduled()) {
                this.listView.setVisibility(4);
            } else {
                this.listView.setVisibility(0);
            }
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                this.leaveItem.setText(LocaleController.getString("VoipChannelCancelChat", R.string.VoipChannelCancelChat));
            } else {
                this.leaveItem.setText(LocaleController.getString("VoipGroupCancelChat", R.string.VoipGroupCancelChat));
            }
        }
        if (this.switchToButtonProgress > 0.6f) {
            float interpolation = 1.05f - (CubicBezierInterpolator.DEFAULT.getInterpolation((this.switchToButtonProgress - 0.6f) / 0.4f) * 0.05f);
            scheduleButtonsScale2 = interpolation;
            this.scheduleButtonsScale = interpolation;
            this.switchToButtonInt2 = 1.0f;
            alpha = 1.0f;
        } else {
            this.scheduleButtonsScale = (CubicBezierInterpolator.DEFAULT.getInterpolation(this.switchToButtonProgress / 0.6f) * 0.05f) + 1.0f;
            this.switchToButtonInt2 = CubicBezierInterpolator.DEFAULT.getInterpolation(this.switchToButtonProgress / 0.6f);
            scheduleButtonsScale2 = CubicBezierInterpolator.DEFAULT.getInterpolation(this.switchToButtonProgress / 0.6f) * 1.05f;
            alpha = this.switchToButtonProgress / 0.6f;
        }
        float muteButtonScale = isLandscapeMode ? (AndroidUtilities.dp(52.0f) * scheduleButtonsScale2) / (this.muteButton.getMeasuredWidth() - AndroidUtilities.dp(8.0f)) : scheduleButtonsScale2;
        float reversedAlpha = 1.0f - alpha;
        this.leaveButton.setAlpha(alpha);
        VoIPToggleButton voIPToggleButton = this.soundButton;
        if (!voIPToggleButton.isEnabled()) {
            f = 0.5f;
        }
        voIPToggleButton.setAlpha(f * alpha);
        this.muteButton.setAlpha(alpha);
        this.scheduleTimerContainer.setAlpha(reversedAlpha);
        this.scheduleStartInTextView.setAlpha(alpha);
        this.scheduleStartAtTextView.setAlpha(alpha);
        this.scheduleTimeTextView.setAlpha(alpha);
        this.muteLabel[0].setAlpha(alpha);
        this.scheduleTimeTextView.setScaleX(scheduleButtonsScale2);
        this.scheduleTimeTextView.setScaleY(scheduleButtonsScale2);
        this.leaveButton.setScaleX(scheduleButtonsScale2);
        this.leaveButton.setScaleY(scheduleButtonsScale2);
        this.soundButton.setScaleX(scheduleButtonsScale2);
        this.soundButton.setScaleY(scheduleButtonsScale2);
        this.muteButton.setScaleX(muteButtonScale);
        this.muteButton.setScaleY(muteButtonScale);
        this.scheduleButtonTextView.setScaleX(reversedAlpha);
        this.scheduleButtonTextView.setScaleY(reversedAlpha);
        this.scheduleButtonTextView.setAlpha(reversedAlpha);
        this.scheduleInfoTextView.setAlpha(reversedAlpha);
        this.cameraButton.setAlpha(alpha);
        this.cameraButton.setScaleY(scheduleButtonsScale2);
        this.cameraButton.setScaleX(scheduleButtonsScale2);
        this.flipButton.setAlpha(alpha);
        this.flipButton.setScaleY(scheduleButtonsScale2);
        this.flipButton.setScaleX(scheduleButtonsScale2);
        this.otherItem.setAlpha(alpha);
        if (reversedAlpha != 0.0f) {
            newVisibility = 0;
        }
        if (newVisibility != this.scheduleTimerContainer.getVisibility()) {
            this.scheduleTimerContainer.setVisibility(newVisibility);
            this.scheduleButtonTextView.setVisibility(newVisibility);
        }
    }

    private void initCreatedGroupCall() {
        VoIPService service;
        String str;
        int i;
        if (this.callInitied || (service = VoIPService.getSharedInstance()) == null) {
            return;
        }
        this.callInitied = true;
        this.oldParticipants.addAll(this.call.visibleParticipants);
        this.oldVideoParticipants.addAll(this.visibleVideoParticipants);
        this.oldInvited.addAll(this.call.invitedUsers);
        this.currentCallState = service.getCallState();
        if (this.call == null) {
            ChatObject.Call call = service.groupCall;
            this.call = call;
            this.fullscreenAdapter.setGroupCall(call);
            this.renderersContainer.setGroupCall(this.call);
            this.tabletGridAdapter.setGroupCall(this.call);
        }
        this.actionBar.setTitleRightMargin(AndroidUtilities.dp(48.0f) * 2);
        this.call.saveActiveDates();
        VoIPService.getSharedInstance().registerStateListener(this);
        SimpleTextView simpleTextView = this.scheduleTimeTextView;
        if (simpleTextView != null && simpleTextView.getVisibility() == 0) {
            this.leaveButton.setData(isRtmpStream() ? R.drawable.msg_voiceclose : R.drawable.calls_decline, -1, Theme.getColor(Theme.key_voipgroup_leaveButton), 0.3f, false, LocaleController.getString("VoipGroupLeave", R.string.VoipGroupLeave), false, true);
            updateSpeakerPhoneIcon(true);
            ActionBarMenuSubItem actionBarMenuSubItem = this.leaveItem;
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                i = R.string.VoipChannelEndChat;
                str = "VoipChannelEndChat";
            } else {
                i = R.string.VoipGroupEndChat;
                str = "VoipGroupEndChat";
            }
            actionBarMenuSubItem.setText(LocaleController.getString(str, i));
            this.listView.setVisibility(0);
            this.pipItem.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.listView, View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.listView, View.TRANSLATION_Y, AndroidUtilities.dp(200.0f), 0.0f), ObjectAnimator.ofFloat(this.scheduleTimeTextView, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.scheduleTimeTextView, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.scheduleTimeTextView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartInTextView, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartInTextView, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartInTextView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.pipItem, View.SCALE_X, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.pipItem, View.SCALE_Y, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.pipItem, View.ALPHA, 0.0f, 1.0f));
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.45
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    GroupCallActivity.this.scheduleTimeTextView.setVisibility(4);
                    GroupCallActivity.this.scheduleStartAtTextView.setVisibility(4);
                    GroupCallActivity.this.scheduleStartInTextView.setVisibility(4);
                }
            });
            animatorSet.setDuration(300L);
            animatorSet.start();
        }
    }

    public void updateSubtitle() {
        boolean drawStatus;
        if (this.actionBar == null || this.call == null) {
            return;
        }
        SpannableStringBuilder spannableStringBuilder = null;
        int speakingIndex = 0;
        for (int i = 0; i < this.call.currentSpeakingPeers.size(); i++) {
            long key = this.call.currentSpeakingPeers.keyAt(i);
            TLRPC.TL_groupCallParticipant participant = this.call.currentSpeakingPeers.get(key);
            if (!participant.self && !this.renderersContainer.isVisible(participant) && this.visiblePeerIds.get(key, 0) != 1) {
                long peerId = MessageObject.getPeerId(participant.peer);
                if (spannableStringBuilder == null) {
                    spannableStringBuilder = new SpannableStringBuilder();
                }
                if (speakingIndex < 2) {
                    TLRPC.User user = peerId > 0 ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerId)) : null;
                    TLRPC.Chat chat = peerId <= 0 ? MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(peerId)) : null;
                    if (user != null || chat != null) {
                        if (speakingIndex != 0) {
                            spannableStringBuilder.append((CharSequence) ", ");
                        }
                        if (user != null) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                spannableStringBuilder.append(UserObject.getFirstName(user), new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), 0);
                            } else {
                                spannableStringBuilder.append((CharSequence) UserObject.getFirstName(user));
                            }
                        } else if (Build.VERSION.SDK_INT >= 21) {
                            spannableStringBuilder.append(chat.title, new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), 0);
                        } else {
                            spannableStringBuilder.append((CharSequence) chat.title);
                        }
                    }
                }
                speakingIndex++;
                if (speakingIndex == 2) {
                    break;
                }
            }
        }
        if (speakingIndex > 0) {
            String s = LocaleController.getPluralString("MembersAreSpeakingToast", speakingIndex);
            int replaceIndex = s.indexOf("un1");
            SpannableStringBuilder spannableStringBuilder1 = new SpannableStringBuilder(s);
            spannableStringBuilder1.replace(replaceIndex, replaceIndex + 3, (CharSequence) spannableStringBuilder);
            this.actionBar.getAdditionalSubtitleTextView().setText(spannableStringBuilder1);
            drawStatus = true;
        } else {
            drawStatus = false;
        }
        this.actionBar.getSubtitleTextView().setText(LocaleController.formatPluralString(isRtmpStream() ? "ViewersWatching" : "Participants", this.call.call.participants_count + (this.listAdapter.addSelfToCounter() ? 1 : 0), new Object[0]));
        if (drawStatus != this.drawSpeakingSubtitle) {
            this.drawSpeakingSubtitle = drawStatus;
            this.actionBar.invalidate();
            float f = 0.0f;
            this.actionBar.getSubtitleTextView().setPivotX(0.0f);
            this.actionBar.getSubtitleTextView().setPivotY(this.actionBar.getMeasuredHeight() >> 1);
            ViewPropertyAnimator scaleY = this.actionBar.getSubtitleTextView().animate().scaleX(this.drawSpeakingSubtitle ? 0.98f : 1.0f).scaleY(this.drawSpeakingSubtitle ? 0.9f : 1.0f);
            if (!this.drawSpeakingSubtitle) {
                f = 1.0f;
            }
            scaleY.alpha(f).setDuration(150L);
            AndroidUtilities.updateViewVisibilityAnimated(this.actionBar.getAdditionalSubtitleTextView(), this.drawSpeakingSubtitle);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 2048);
        super.show();
        if (RTMPStreamPipOverlay.isVisible()) {
            RTMPStreamPipOverlay.dismiss();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        if (this.renderersContainer != null) {
            if (this.requestFullscreenListener != null) {
                this.listView.getViewTreeObserver().removeOnPreDrawListener(this.requestFullscreenListener);
                this.requestFullscreenListener = null;
            }
            this.attachedRenderersTmp.clear();
            this.attachedRenderersTmp.addAll(this.attachedRenderers);
            for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
                this.attachedRenderersTmp.get(i).saveThumb();
                this.renderersContainer.removeView(this.attachedRenderersTmp.get(i));
                this.attachedRenderersTmp.get(i).release();
                this.attachedRenderersTmp.get(i).forceDetach(true);
            }
            this.attachedRenderers.clear();
            if (this.renderersContainer.getParent() != null) {
                this.attachedRenderers.clear();
                this.containerView.removeView(this.renderersContainer);
            }
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 2048);
        super.dismissInternal();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
            VoIPService.getSharedInstance().setSinks(null, null);
        }
        if (groupCallInstance == this) {
            groupCallInstance = null;
        }
        groupCallUiVisible = false;
        VoIPService.audioLevelsCallback = null;
        GroupCallPip.updateVisibility(getContext());
        ChatObject.Call call = this.call;
        if (call != null) {
            call.clearVideFramesInfo();
        }
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().clearRemoteSinks();
        }
    }

    private void setAmplitude(double value) {
        float min = (float) (Math.min(8500.0d, value) / 8500.0d);
        this.animateToAmplitude = min;
        this.animateAmplitudeDiff = (min - this.amplitude) / ((BlobDrawable.AMPLITUDE_SPEED * 500.0f) + 100.0f);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onStateChanged(int state) {
        this.currentCallState = state;
        updateState(isShowing(), false);
    }

    public UndoView getUndoView() {
        if (!isTabletMode && this.renderersContainer.inFullscreenMode) {
            return this.renderersContainer.getUndoView();
        }
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView old = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = old;
            old.hide(true, 2);
            this.containerView.removeView(this.undoView[0]);
            this.containerView.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    public float getColorProgress() {
        return this.colorProgress;
    }

    private void updateTitle(boolean animated) {
        ChatObject.Call call = this.call;
        if (call == null) {
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                this.titleTextView.setText(LocaleController.getString("VoipChannelScheduleVoiceChat", R.string.VoipChannelScheduleVoiceChat), animated);
                return;
            } else {
                this.titleTextView.setText(LocaleController.getString("VoipGroupScheduleVoiceChat", R.string.VoipGroupScheduleVoiceChat), animated);
                return;
            }
        }
        if (!TextUtils.isEmpty(call.call.title)) {
            if (!this.call.call.title.equals(this.actionBar.getTitle())) {
                if (animated) {
                    this.actionBar.setTitleAnimated(this.call.call.title, true, 180L);
                    this.actionBar.getTitleTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda10
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            GroupCallActivity.this.m3512lambda$updateTitle$36$orgtelegramuiGroupCallActivity(view);
                        }
                    });
                } else {
                    this.actionBar.setTitle(this.call.call.title);
                }
                this.titleTextView.setText(this.call.call.title, animated);
            }
        } else if (!this.currentChat.title.equals(this.actionBar.getTitle())) {
            if (animated) {
                this.actionBar.setTitleAnimated(this.currentChat.title, true, 180L);
                this.actionBar.getTitleTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda12
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        GroupCallActivity.this.m3513lambda$updateTitle$37$orgtelegramuiGroupCallActivity(view);
                    }
                });
            } else {
                this.actionBar.setTitle(this.currentChat.title);
            }
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                this.titleTextView.setText(LocaleController.getString("VoipChannelVoiceChat", R.string.VoipChannelVoiceChat), animated);
            } else {
                this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", R.string.VoipGroupVoiceChat), animated);
            }
        }
        SimpleTextView textView = this.actionBar.getTitleTextView();
        if (this.call.recording) {
            if (textView.getRightDrawable() == null) {
                textView.setRightDrawable(new SmallRecordCallDrawable(textView));
                TextView tv = this.titleTextView.getTextView();
                tv.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, new SmallRecordCallDrawable(tv), (Drawable) null);
                TextView tv2 = this.titleTextView.getNextTextView();
                tv2.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, new SmallRecordCallDrawable(tv2), (Drawable) null);
            }
        } else if (textView.getRightDrawable() != null) {
            textView.setRightDrawable((Drawable) null);
            this.titleTextView.getTextView().setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            this.titleTextView.getNextTextView().setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
        }
    }

    /* renamed from: lambda$updateTitle$36$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3512lambda$updateTitle$36$orgtelegramuiGroupCallActivity(View v) {
        ChatObject.Call call = this.call;
        if (call != null && call.recording) {
            showRecordHint(this.actionBar.getTitleTextView());
        }
    }

    /* renamed from: lambda$updateTitle$37$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3513lambda$updateTitle$37$orgtelegramuiGroupCallActivity(View v) {
        ChatObject.Call call = this.call;
        if (call != null && call.recording) {
            showRecordHint(this.actionBar.getTitleTextView());
        }
    }

    public void setColorProgress(float progress) {
        this.colorProgress = progress;
        float finalColorProgress = this.colorProgress;
        GroupCallRenderersContainer groupCallRenderersContainer = this.renderersContainer;
        float finalColorProgress2 = Math.max(progress, groupCallRenderersContainer == null ? 0.0f : groupCallRenderersContainer.progressToFullscreenMode);
        int offsetColor = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_actionBarUnscrolled), Theme.getColor(Theme.key_voipgroup_actionBar), finalColorProgress, 1.0f);
        this.backgroundColor = offsetColor;
        this.actionBarBackground.setBackgroundColor(offsetColor);
        this.otherItem.redrawPopup(-14472653);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
        this.navBarColor = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_actionBarUnscrolled), Theme.getColor(Theme.key_voipgroup_actionBar), finalColorProgress2, 1.0f);
        int color = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_listViewBackgroundUnscrolled), Theme.getColor(Theme.key_voipgroup_listViewBackground), finalColorProgress, 1.0f);
        this.listViewBackgroundPaint.setColor(color);
        this.listView.setGlowColor(color);
        int i = this.muteButtonState;
        if (i == 3 || isGradientState(i)) {
            this.muteButton.invalidate();
        }
        if (this.buttonsBackgroundGradientView != null) {
            int[] iArr = this.gradientColors;
            iArr[0] = this.backgroundColor;
            iArr[1] = 0;
            if (Build.VERSION.SDK_INT > 29) {
                this.buttonsBackgroundGradient.setColors(this.gradientColors);
            } else {
                View view = this.buttonsBackgroundGradientView;
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, this.gradientColors);
                this.buttonsBackgroundGradient = gradientDrawable;
                view.setBackground(gradientDrawable);
            }
            this.buttonsBackgroundGradientView2.setBackgroundColor(this.gradientColors[0]);
        }
        int color2 = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_leaveButton), Theme.getColor(Theme.key_voipgroup_leaveButtonScrolled), finalColorProgress, 1.0f);
        this.leaveButton.setBackgroundColor(color2, color2);
        int color3 = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_lastSeenTextUnscrolled), Theme.getColor(Theme.key_voipgroup_lastSeenText), finalColorProgress, 1.0f);
        int color22 = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_mutedIconUnscrolled), Theme.getColor(Theme.key_voipgroup_mutedIcon), finalColorProgress, 1.0f);
        int N = this.listView.getChildCount();
        for (int a = 0; a < N; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof GroupCallTextCell) {
                GroupCallTextCell cell = (GroupCallTextCell) child;
                cell.setColors(color22, color3);
            } else if (child instanceof GroupCallUserCell) {
                GroupCallUserCell cell2 = (GroupCallUserCell) child;
                cell2.setGrayIconColor(this.actionBar.getTag() != null ? Theme.key_voipgroup_mutedIcon : Theme.key_voipgroup_mutedIconUnscrolled, color22);
            } else if (child instanceof GroupCallInvitedCell) {
                GroupCallInvitedCell cell3 = (GroupCallInvitedCell) child;
                cell3.setGrayIconColor(this.actionBar.getTag() != null ? Theme.key_voipgroup_mutedIcon : Theme.key_voipgroup_mutedIconUnscrolled, color22);
            }
        }
        this.containerView.invalidate();
        this.listView.invalidate();
        this.container.invalidate();
    }

    public void getLink(final boolean copy) {
        String url;
        TLRPC.Chat newChat = this.accountInstance.getMessagesController().getChat(Long.valueOf(this.currentChat.id));
        if (newChat != null && TextUtils.isEmpty(newChat.username)) {
            final TLRPC.ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
            if (!TextUtils.isEmpty(this.currentChat.username)) {
                url = this.accountInstance.getMessagesController().linkPrefix + "/" + this.currentChat.username;
            } else {
                url = (chatFull == null || chatFull.exported_invite == null) ? null : chatFull.exported_invite.link;
            }
            if (TextUtils.isEmpty(url)) {
                TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
                req.peer = MessagesController.getInputPeer(this.currentChat);
                this.accountInstance.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda49
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        GroupCallActivity.this.m3466lambda$getLink$39$orgtelegramuiGroupCallActivity(chatFull, copy, tLObject, tL_error);
                    }
                });
                return;
            }
            openShareAlert(true, null, url, copy);
        } else if (this.call == null) {
        } else {
            int a = 0;
            while (a < 2) {
                final int num = a;
                TLRPC.TL_phone_exportGroupCallInvite req2 = new TLRPC.TL_phone_exportGroupCallInvite();
                req2.call = this.call.getInputGroupCall();
                req2.can_self_unmute = a == 1;
                this.accountInstance.getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda46
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        GroupCallActivity.this.m3468lambda$getLink$41$orgtelegramuiGroupCallActivity(num, copy, tLObject, tL_error);
                    }
                });
                a++;
            }
        }
    }

    /* renamed from: lambda$getLink$39$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3466lambda$getLink$39$orgtelegramuiGroupCallActivity(final TLRPC.ChatFull chatFull, final boolean copy, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                GroupCallActivity.this.m3465lambda$getLink$38$orgtelegramuiGroupCallActivity(response, chatFull, copy);
            }
        });
    }

    /* renamed from: lambda$getLink$38$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3465lambda$getLink$38$orgtelegramuiGroupCallActivity(TLObject response, TLRPC.ChatFull chatFull, boolean copy) {
        if (response instanceof TLRPC.TL_chatInviteExported) {
            TLRPC.TL_chatInviteExported invite = (TLRPC.TL_chatInviteExported) response;
            if (chatFull != null) {
                chatFull.exported_invite = invite;
            } else {
                openShareAlert(true, null, invite.link, copy);
            }
        }
    }

    /* renamed from: lambda$getLink$41$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3468lambda$getLink$41$orgtelegramuiGroupCallActivity(final int num, final boolean copy, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                GroupCallActivity.this.m3467lambda$getLink$40$orgtelegramuiGroupCallActivity(response, num, copy);
            }
        });
    }

    /* renamed from: lambda$getLink$40$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3467lambda$getLink$40$orgtelegramuiGroupCallActivity(TLObject response, int num, boolean copy) {
        if (response instanceof TLRPC.TL_phone_exportedGroupCallInvite) {
            TLRPC.TL_phone_exportedGroupCallInvite invite = (TLRPC.TL_phone_exportedGroupCallInvite) response;
            this.invites[num] = invite.link;
        } else {
            this.invites[num] = "";
        }
        for (int b = 0; b < 2; b++) {
            String[] strArr = this.invites;
            if (strArr[b] == null) {
                return;
            }
            if (strArr[b].length() == 0) {
                this.invites[b] = null;
            }
        }
        if (!copy && ChatObject.canManageCalls(this.currentChat) && !this.call.call.join_muted) {
            this.invites[0] = null;
        }
        String[] strArr2 = this.invites;
        if (strArr2[0] == null && strArr2[1] == null && !TextUtils.isEmpty(this.currentChat.username)) {
            openShareAlert(true, null, this.accountInstance.getMessagesController().linkPrefix + "/" + this.currentChat.username, copy);
            return;
        }
        String[] strArr3 = this.invites;
        openShareAlert(false, strArr3[0], strArr3[1], copy);
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x00d6  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00d9  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void openShareAlert(boolean r19, java.lang.String r20, java.lang.String r21, boolean r22) {
        /*
            Method dump skipped, instructions count: 225
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.openShareAlert(boolean, java.lang.String, java.lang.String, boolean):void");
    }

    /* renamed from: lambda$openShareAlert$42$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3498lambda$openShareAlert$42$orgtelegramuiGroupCallActivity(DialogInterface dialog) {
        this.shareAlert = null;
    }

    /* renamed from: lambda$openShareAlert$43$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3499lambda$openShareAlert$43$orgtelegramuiGroupCallActivity() {
        ShareAlert shareAlert = this.shareAlert;
        if (shareAlert != null) {
            shareAlert.show();
        }
    }

    public void inviteUserToCall(final long id, final boolean shouldAdd) {
        final TLRPC.User user;
        if (this.call == null || (user = this.accountInstance.getMessagesController().getUser(Long.valueOf(id))) == null) {
            return;
        }
        final AlertDialog[] progressDialog = {new AlertDialog(getContext(), 3)};
        final TLRPC.TL_phone_inviteToGroupCall req = new TLRPC.TL_phone_inviteToGroupCall();
        req.call = this.call.getInputGroupCall();
        TLRPC.TL_inputUser inputUser = new TLRPC.TL_inputUser();
        inputUser.user_id = user.id;
        inputUser.access_hash = user.access_hash;
        req.users.add(inputUser);
        final int requestId = this.accountInstance.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda47
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupCallActivity.this.m3471lambda$inviteUserToCall$46$orgtelegramuiGroupCallActivity(id, progressDialog, user, shouldAdd, req, tLObject, tL_error);
            }
        });
        if (requestId != 0) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda39
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCallActivity.this.m3473lambda$inviteUserToCall$48$orgtelegramuiGroupCallActivity(progressDialog, requestId);
                }
            }, 500L);
        }
    }

    /* renamed from: lambda$inviteUserToCall$46$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3471lambda$inviteUserToCall$46$orgtelegramuiGroupCallActivity(final long id, final AlertDialog[] progressDialog, final TLRPC.User user, final boolean shouldAdd, final TLRPC.TL_phone_inviteToGroupCall req, TLObject response, final TLRPC.TL_error error) {
        if (response != null) {
            this.accountInstance.getMessagesController().processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCallActivity.this.m3469lambda$inviteUserToCall$44$orgtelegramuiGroupCallActivity(id, progressDialog, user);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda40
            @Override // java.lang.Runnable
            public final void run() {
                GroupCallActivity.this.m3470lambda$inviteUserToCall$45$orgtelegramuiGroupCallActivity(progressDialog, shouldAdd, error, id, req);
            }
        });
    }

    /* renamed from: lambda$inviteUserToCall$44$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3469lambda$inviteUserToCall$44$orgtelegramuiGroupCallActivity(long id, AlertDialog[] progressDialog, TLRPC.User user) {
        ChatObject.Call call = this.call;
        if (call != null && !this.delayedGroupCallUpdated) {
            call.addInvitedUser(id);
            applyCallParticipantUpdates(true);
            GroupVoipInviteAlert groupVoipInviteAlert = this.groupVoipInviteAlert;
            if (groupVoipInviteAlert != null) {
                groupVoipInviteAlert.dismiss();
            }
            try {
                progressDialog[0].dismiss();
            } catch (Throwable th) {
            }
            progressDialog[0] = null;
            getUndoView().showWithAction(0L, 34, user, this.currentChat, (Runnable) null, (Runnable) null);
        }
    }

    /* renamed from: lambda$inviteUserToCall$45$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3470lambda$inviteUserToCall$45$orgtelegramuiGroupCallActivity(AlertDialog[] progressDialog, boolean shouldAdd, TLRPC.TL_error error, long id, TLRPC.TL_phone_inviteToGroupCall req) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        if (shouldAdd && "USER_NOT_PARTICIPANT".equals(error.text)) {
            processSelectedOption(null, id, 3);
            return;
        }
        BaseFragment fragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
        AlertsCreator.processError(this.currentAccount, error, fragment, req, new Object[0]);
    }

    /* renamed from: lambda$inviteUserToCall$48$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3473lambda$inviteUserToCall$48$orgtelegramuiGroupCallActivity(AlertDialog[] progressDialog, final int requestId) {
        if (progressDialog[0] == null) {
            return;
        }
        progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda55
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                GroupCallActivity.this.m3472lambda$inviteUserToCall$47$orgtelegramuiGroupCallActivity(requestId, dialogInterface);
            }
        });
        progressDialog[0].show();
    }

    /* renamed from: lambda$inviteUserToCall$47$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3472lambda$inviteUserToCall$47$orgtelegramuiGroupCallActivity(int requestId, DialogInterface dialog) {
        this.accountInstance.getConnectionsManager().cancelRequest(requestId, true);
    }

    public void invalidateActionBarAlpha() {
        ActionBar actionBar = this.actionBar;
        actionBar.setAlpha((actionBar.getTag() != null ? 1.0f : 0.0f) * (1.0f - this.renderersContainer.progressToFullscreenMode));
    }

    public void updateLayout(boolean animated) {
        float minY = 2.14748365E9f;
        int N = this.listView.getChildCount();
        for (int a = 0; a < N; a++) {
            View child = this.listView.getChildAt(a);
            if (this.listView.getChildAdapterPosition(child) >= 0) {
                minY = Math.min(minY, child.getTop());
            }
        }
        int a2 = 0;
        if (minY < 0.0f || minY == 2.14748365E9f) {
            minY = N != 0 ? 0.0f : this.listView.getPaddingTop();
        }
        boolean z = false;
        final boolean show = minY <= ((float) (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(14.0f)));
        float minY2 = minY + ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(14.0f);
        if ((show && this.actionBar.getTag() == null) || (!show && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(show ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            setUseLightStatusBar(this.actionBar.getTag() == null);
            float f = 0.9f;
            ViewPropertyAnimator scaleX = this.actionBar.getBackButton().animate().scaleX(show ? 1.0f : 0.9f);
            if (show) {
                f = 1.0f;
            }
            scaleX.scaleY(f).translationX(show ? 0.0f : -AndroidUtilities.dp(14.0f)).setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.actionBar.getTitleTextView().animate().translationY(show ? 0.0f : AndroidUtilities.dp(23.0f)).setDuration(300L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            ObjectAnimator objectAnimator = this.subtitleYAnimator;
            if (objectAnimator != null) {
                objectAnimator.removeAllListeners();
                this.subtitleYAnimator.cancel();
            }
            SimpleTextView subtitleTextView = this.actionBar.getSubtitleTextView();
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[2];
            fArr[0] = this.actionBar.getSubtitleTextView().getTranslationY();
            fArr[1] = show ? 0.0f : AndroidUtilities.dp(20.0f);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(subtitleTextView, property, fArr);
            this.subtitleYAnimator = ofFloat;
            ofFloat.setDuration(300L);
            this.subtitleYAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.subtitleYAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.48
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    GroupCallActivity.this.subtitleYAnimator = null;
                    GroupCallActivity.this.actionBar.getSubtitleTextView().setTranslationY(show ? 0.0f : AndroidUtilities.dp(20.0f));
                }
            });
            this.subtitleYAnimator.start();
            ObjectAnimator objectAnimator2 = this.additionalSubtitleYAnimator;
            if (objectAnimator2 != null) {
                objectAnimator2.cancel();
            }
            SimpleTextView additionalSubtitleTextView = this.actionBar.getAdditionalSubtitleTextView();
            Property property2 = View.TRANSLATION_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 0.0f : AndroidUtilities.dp(20.0f);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(additionalSubtitleTextView, property2, fArr2);
            this.additionalSubtitleYAnimator = ofFloat2;
            ofFloat2.setDuration(300L);
            this.additionalSubtitleYAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.additionalSubtitleYAnimator.start();
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimation = animatorSet2;
            animatorSet2.setDuration(140L);
            AnimatorSet animatorSet3 = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[3];
            ActionBar actionBar = this.actionBar;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar, property3, fArr3);
            View view = this.actionBarBackground;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = show ? 1.0f : 0.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property4, fArr4);
            View view2 = this.actionBarShadow;
            Property property5 = View.ALPHA;
            float[] fArr5 = new float[1];
            if (show) {
                a2 = 1065353216;
            }
            fArr5[0] = a2;
            animatorArr[2] = ObjectAnimator.ofFloat(view2, property5, fArr5);
            animatorSet3.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.49
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    GroupCallActivity.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
            ImageView imageView = this.renderersContainer.pipView;
            if (!show || isLandscapeMode) {
                z = true;
            }
            imageView.setClickable(z);
        }
        if (this.scrollOffsetY != minY2) {
            setScrollOffsetY(minY2);
        }
    }

    public void invalidateScrollOffsetY() {
        setScrollOffsetY(this.scrollOffsetY);
    }

    private void setScrollOffsetY(float scrollOffsetY) {
        int diff;
        this.scrollOffsetY = scrollOffsetY;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        this.listView.setTopGlowOffset((int) (scrollOffsetY - layoutParams.topMargin));
        int offset = AndroidUtilities.dp(74.0f);
        float t = scrollOffsetY - offset;
        if (this.backgroundPaddingTop + t < ActionBar.getCurrentActionBarHeight() * 2) {
            int willMoveUpTo = ((offset - this.backgroundPaddingTop) - AndroidUtilities.dp(14.0f)) + ActionBar.getCurrentActionBarHeight();
            float moveProgress = Math.min(1.0f, (((ActionBar.getCurrentActionBarHeight() * 2) - t) - this.backgroundPaddingTop) / willMoveUpTo);
            diff = (int) (AndroidUtilities.dp(AndroidUtilities.isTablet() ? 17.0f : 13.0f) * moveProgress);
            float newProgress = Math.min(1.0f, moveProgress);
            if (Math.abs(newProgress - this.colorProgress) > 1.0E-4f) {
                setColorProgress(Math.min(1.0f, moveProgress));
            }
            this.titleTextView.setScaleX(Math.max(0.9f, 1.0f - ((moveProgress * 0.1f) * 1.2f)));
            this.titleTextView.setScaleY(Math.max(0.9f, 1.0f - ((0.1f * moveProgress) * 1.2f)));
            this.titleTextView.setAlpha(Math.max(0.0f, 1.0f - (1.2f * moveProgress)) * (1.0f - this.renderersContainer.progressToFullscreenMode));
        } else {
            diff = 0;
            this.titleTextView.setScaleX(1.0f);
            this.titleTextView.setScaleY(1.0f);
            this.titleTextView.setAlpha(1.0f - this.renderersContainer.progressToFullscreenMode);
            if (this.colorProgress > 1.0E-4f) {
                setColorProgress(0.0f);
            }
        }
        this.menuItemsContainer.setTranslationY(Math.max(AndroidUtilities.dp(4.0f), (scrollOffsetY - AndroidUtilities.dp(53.0f)) - diff));
        this.titleTextView.setTranslationY(Math.max(AndroidUtilities.dp(4.0f), (scrollOffsetY - AndroidUtilities.dp(44.0f)) - diff));
        LinearLayout linearLayout = this.scheduleTimerContainer;
        if (linearLayout != null) {
            linearLayout.setTranslationY(Math.max(AndroidUtilities.dp(4.0f), (scrollOffsetY - AndroidUtilities.dp(44.0f)) - diff));
        }
        this.containerView.invalidate();
    }

    private void cancelMutePress() {
        if (this.scheduled) {
            this.scheduled = false;
            AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
        }
        if (this.pressed) {
            this.pressed = false;
            MotionEvent cancel = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
            this.muteButton.onTouchEvent(cancel);
            cancel.recycle();
        }
    }

    public void updateState(boolean animated, boolean selfUpdated) {
        int state;
        boolean soundButtonVisible;
        boolean cameraButtonVisible;
        int i;
        int i2;
        float cameraScale;
        float flipButtonScale;
        GroupCallRenderersContainer groupCallRenderersContainer;
        int i3;
        ChatObject.Call call = this.call;
        int i4 = 6;
        boolean z = false;
        if (call == null || call.isScheduled()) {
            if (ChatObject.canManageCalls(this.currentChat)) {
                state = 5;
            } else {
                if (this.call.call.schedule_start_subscribed) {
                    i4 = 7;
                }
                state = i4;
            }
            updateMuteButton(state, animated);
            this.leaveButton.setData(isRtmpStream() ? R.drawable.msg_voiceclose : R.drawable.calls_decline, -1, Theme.getColor(Theme.key_voipgroup_leaveButton), 0.3f, false, LocaleController.getString("Close", R.string.Close), false, false);
            updateScheduleUI(false);
            return;
        }
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService == null) {
            return;
        }
        int i5 = 4;
        if (!voIPService.isSwitchingStream() && ((this.creatingServiceTime == 0 || Math.abs(SystemClock.elapsedRealtime() - this.creatingServiceTime) > 3000) && ((i3 = this.currentCallState) == 1 || i3 == 2 || i3 == 6 || i3 == 5))) {
            cancelMutePress();
            updateMuteButton(3, animated);
        } else {
            if (this.userSwitchObject != null) {
                getUndoView().showWithAction(0L, 37, this.userSwitchObject, this.currentChat, (Runnable) null, (Runnable) null);
                this.userSwitchObject = null;
            }
            TLRPC.TL_groupCallParticipant participant = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
            if (!voIPService.micSwitching && participant != null && !participant.can_self_unmute && participant.muted && !ChatObject.canManageCalls(this.currentChat)) {
                cancelMutePress();
                if (participant.raise_hand_rating != 0) {
                    updateMuteButton(4, animated);
                } else {
                    updateMuteButton(2, animated);
                }
                voIPService.setMicMute(true, false, false);
            } else {
                boolean micMuted = voIPService.isMicMute();
                if (!voIPService.micSwitching && selfUpdated && participant != null && participant.muted && !micMuted) {
                    cancelMutePress();
                    voIPService.setMicMute(true, false, false);
                    micMuted = true;
                }
                if (micMuted) {
                    updateMuteButton(0, animated);
                } else {
                    updateMuteButton(1, animated);
                }
            }
        }
        boolean outgoingVideoIsActive = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().getVideoState(false) == 2;
        TLRPC.TL_groupCallParticipant participant2 = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
        boolean mutedByAdmin = participant2 != null && !participant2.can_self_unmute && participant2.muted && !ChatObject.canManageCalls(this.currentChat);
        if (((!mutedByAdmin && this.call.canRecordVideo()) || outgoingVideoIsActive) && !isRtmpStream()) {
            cameraButtonVisible = true;
            soundButtonVisible = false;
        } else {
            cameraButtonVisible = false;
            soundButtonVisible = true;
        }
        if (outgoingVideoIsActive) {
            if (animated && this.flipButton.getVisibility() != 0) {
                this.flipButton.setScaleX(0.3f);
                this.flipButton.setScaleY(0.3f);
            }
            i = 1;
        } else {
            i = 0;
        }
        int i6 = i + (soundButtonVisible ? 2 : 0);
        if (!cameraButtonVisible) {
            i5 = 0;
        }
        int i7 = i6 + i5;
        GroupCallRenderersContainer groupCallRenderersContainer2 = this.renderersContainer;
        int newButtonsVisibility = i7 + ((groupCallRenderersContainer2 == null || !groupCallRenderersContainer2.inFullscreenMode) ? 0 : 8);
        int i8 = this.buttonsVisibility;
        if (i8 != 0 && i8 != newButtonsVisibility && animated) {
            for (int i9 = 0; i9 < this.buttonsContainer.getChildCount(); i9++) {
                View child = this.buttonsContainer.getChildAt(i9);
                if (child.getVisibility() == 0) {
                    this.buttonsAnimationParamsX.put(child, Float.valueOf(child.getX()));
                    this.buttonsAnimationParamsY.put(child, Float.valueOf(child.getY()));
                }
            }
            this.animateButtonsOnNextLayout = true;
        }
        boolean soundButtonChanged = (this.buttonsVisibility | 2) != (newButtonsVisibility | 2);
        this.buttonsVisibility = newButtonsVisibility;
        if (!cameraButtonVisible) {
            i2 = 8;
            this.cameraButton.setVisibility(8);
        } else {
            this.cameraButton.setData(R.drawable.calls_video, -1, 0, 1.0f, true, LocaleController.getString("VoipCamera", R.string.VoipCamera), !outgoingVideoIsActive, animated);
            this.cameraButton.setChecked(true, false);
            i2 = 8;
        }
        if (i != 0) {
            this.flipButton.setData(0, -1, 0, 1.0f, true, LocaleController.getString("VoipFlip", R.string.VoipFlip), false, false);
            this.flipButton.setChecked(true, false);
        } else {
            this.flipButton.setVisibility(i2);
        }
        boolean soundButtonWasVisible = this.soundButton.getVisibility() == 0;
        this.soundButton.setVisibility(soundButtonVisible ? 0 : 8);
        if (soundButtonChanged && soundButtonVisible) {
            updateSpeakerPhoneIcon(false);
        }
        float f = 1.0f;
        if (soundButtonChanged) {
            float s = soundButtonVisible ? 1.0f : 0.3f;
            if (!animated) {
                this.soundButton.animate().cancel();
                this.soundButton.setScaleX(s);
                this.soundButton.setScaleY(s);
            } else {
                if (soundButtonVisible && !soundButtonWasVisible) {
                    this.soundButton.setScaleX(0.3f);
                    this.soundButton.setScaleY(0.3f);
                }
                this.soundButton.animate().scaleX(s).scaleY(s).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            }
        }
        if (this.cameraButton.getVisibility() == 0) {
            cameraScale = 1.0f;
            this.cameraButton.showText(1.0f == 1.0f, animated);
        } else {
            cameraScale = 0.3f;
        }
        if (this.cameraButtonScale != cameraScale) {
            this.cameraButtonScale = cameraScale;
            if (animated) {
                this.cameraButton.animate().scaleX(cameraScale).scaleY(cameraScale).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            } else {
                this.cameraButton.animate().cancel();
                this.cameraButton.setScaleX(cameraScale);
                this.cameraButton.setScaleY(cameraScale);
            }
        }
        if (isTabletMode) {
            flipButtonScale = 0.8f;
        } else {
            flipButtonScale = (isLandscapeMode || ((groupCallRenderersContainer = this.renderersContainer) != null && groupCallRenderersContainer.inFullscreenMode)) ? 1.0f : 0.8f;
        }
        if (!outgoingVideoIsActive) {
            flipButtonScale = 0.3f;
        }
        if (animated) {
            this.flipButton.animate().scaleX(flipButtonScale).scaleY(flipButtonScale).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        } else {
            this.flipButton.animate().cancel();
            this.flipButton.setScaleX(flipButtonScale);
            this.flipButton.setScaleY(flipButtonScale);
        }
        VoIPToggleButton voIPToggleButton = this.flipButton;
        if (flipButtonScale == 1.0f) {
            z = true;
        }
        voIPToggleButton.showText(z, animated);
        if (outgoingVideoIsActive) {
            f = 0.3f;
        }
        float soundButtonScale = f;
        if (this.soundButtonScale != soundButtonScale) {
            this.soundButtonScale = soundButtonScale;
            if (animated) {
                this.soundButton.animate().scaleX(soundButtonScale).scaleY(soundButtonScale).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                return;
            }
            this.soundButton.animate().cancel();
            this.soundButton.setScaleX(soundButtonScale);
            this.soundButton.setScaleY(soundButtonScale);
        }
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onAudioSettingsChanged() {
        updateSpeakerPhoneIcon(true);
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
            setMicAmplitude(0.0f);
        }
        if (this.listView.getVisibility() == 0) {
            AndroidUtilities.updateVisibleRows(this.listView);
        }
        if (this.fullscreenUsersListView.getVisibility() == 0) {
            AndroidUtilities.updateVisibleRows(this.fullscreenUsersListView);
        }
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            this.attachedRenderersTmp.get(i).updateAttachState(true);
        }
    }

    private void updateSpeakerPhoneIcon(boolean animated) {
        VoIPToggleButton voIPToggleButton = this.soundButton;
        if (voIPToggleButton == null || voIPToggleButton.getVisibility() != 0) {
            return;
        }
        VoIPService service = VoIPService.getSharedInstance();
        boolean checked = false;
        if (service == null || isRtmpStream()) {
            this.soundButton.setData(R.drawable.msg_voiceshare, -1, 0, 0.3f, true, LocaleController.getString("VoipChatShare", R.string.VoipChatShare), false, animated);
            this.soundButton.setEnabled(!TextUtils.isEmpty(this.currentChat.username) || (ChatObject.hasAdminRights(this.currentChat) && ChatObject.canAddUsers(this.currentChat)), false);
            this.soundButton.setChecked(true, false);
            return;
        }
        this.soundButton.setEnabled(true, animated);
        boolean bluetooth = service.isBluetoothOn() || service.isBluetoothWillOn();
        if (!bluetooth && service.isSpeakerphoneOn()) {
            checked = true;
        }
        if (bluetooth) {
            this.soundButton.setData(R.drawable.calls_bluetooth, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingBluetooth", R.string.VoipAudioRoutingBluetooth), false, animated);
        } else if (checked) {
            this.soundButton.setData(R.drawable.calls_speaker, -1, 0, 0.3f, true, LocaleController.getString("VoipSpeaker", R.string.VoipSpeaker), false, animated);
        } else if (!service.isHeadsetPlugged()) {
            this.soundButton.setData(R.drawable.calls_speaker, -1, 0, 0.1f, true, LocaleController.getString("VoipSpeaker", R.string.VoipSpeaker), false, animated);
        } else {
            this.soundButton.setData(R.drawable.calls_headphones, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingHeadset", R.string.VoipAudioRoutingHeadset), false, animated);
        }
        this.soundButton.setChecked(checked, animated);
    }

    public void updateMuteButton(int state, boolean animated) {
        boolean changed;
        String newSubtext;
        String newText;
        int i;
        float f;
        float multiplier;
        float f2;
        float multiplier2;
        boolean changed2;
        GroupCallRenderersContainer groupCallRenderersContainer = this.renderersContainer;
        boolean fullscreen = groupCallRenderersContainer != null && groupCallRenderersContainer.inFullscreenMode && (AndroidUtilities.isTablet() || isLandscapeMode == isRtmpLandscapeMode());
        if (!isRtmpStream() && this.muteButtonState == state && animated) {
            return;
        }
        ValueAnimator valueAnimator = this.muteButtonAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.muteButtonAnimator = null;
        }
        ValueAnimator valueAnimator2 = this.expandAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
            this.expandAnimator = null;
        }
        boolean mutedByAdmin = false;
        if (state == 7) {
            newText = LocaleController.getString("VoipGroupCancelReminder", R.string.VoipGroupCancelReminder);
            newSubtext = "";
            changed = this.bigMicDrawable.setCustomEndFrame(SearchViewPager.deleteItemId);
        } else if (state == 6) {
            newText = LocaleController.getString("VoipGroupSetReminder", R.string.VoipGroupSetReminder);
            newSubtext = "";
            changed = this.bigMicDrawable.setCustomEndFrame(344);
        } else if (state == 5) {
            newText = LocaleController.getString("VoipGroupStartNow", R.string.VoipGroupStartNow);
            newSubtext = "";
            changed = this.bigMicDrawable.setCustomEndFrame(377);
        } else if (state == 0) {
            newText = LocaleController.getString("VoipGroupUnmute", R.string.VoipGroupUnmute);
            newSubtext = LocaleController.getString("VoipHoldAndTalk", R.string.VoipHoldAndTalk);
            int i2 = this.muteButtonState;
            if (i2 == 3) {
                int endFrame = this.bigMicDrawable.getCustomEndFrame();
                if (endFrame == 136 || endFrame == 173 || endFrame == 274 || endFrame == 311) {
                    changed = this.bigMicDrawable.setCustomEndFrame(99);
                } else {
                    changed = false;
                }
            } else if (i2 == 5) {
                changed = this.bigMicDrawable.setCustomEndFrame(WalletConstants.ERROR_CODE_INVALID_PARAMETERS);
            } else if (i2 == 7) {
                changed = this.bigMicDrawable.setCustomEndFrame(376);
            } else if (i2 == 6) {
                changed = this.bigMicDrawable.setCustomEndFrame(237);
            } else if (i2 == 2) {
                changed = this.bigMicDrawable.setCustomEndFrame(36);
            } else {
                changed = this.bigMicDrawable.setCustomEndFrame(99);
            }
        } else if (state == 1) {
            newText = LocaleController.getString("VoipTapToMute", R.string.VoipTapToMute);
            newSubtext = "";
            changed = this.bigMicDrawable.setCustomEndFrame(this.muteButtonState == 4 ? 99 : 69);
        } else if (state != 4) {
            TLRPC.TL_groupCallParticipant participant = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
            boolean z = participant != null && !participant.can_self_unmute && participant.muted && !ChatObject.canManageCalls(this.currentChat);
            mutedByAdmin = z;
            if (z) {
                int i3 = this.muteButtonState;
                if (i3 == 7) {
                    changed2 = this.bigMicDrawable.setCustomEndFrame(311);
                } else if (i3 == 6) {
                    changed2 = this.bigMicDrawable.setCustomEndFrame(274);
                } else if (i3 == 1) {
                    changed2 = this.bigMicDrawable.setCustomEndFrame(173);
                } else {
                    changed2 = this.bigMicDrawable.setCustomEndFrame(136);
                }
            } else {
                int i4 = this.muteButtonState;
                if (i4 == 5) {
                    changed2 = this.bigMicDrawable.setCustomEndFrame(WalletConstants.ERROR_CODE_INVALID_PARAMETERS);
                } else if (i4 == 7) {
                    changed2 = this.bigMicDrawable.setCustomEndFrame(376);
                } else if (i4 == 6) {
                    changed2 = this.bigMicDrawable.setCustomEndFrame(237);
                } else if (i4 == 2 || i4 == 4) {
                    changed2 = this.bigMicDrawable.setCustomEndFrame(36);
                } else {
                    changed2 = this.bigMicDrawable.setCustomEndFrame(99);
                }
            }
            if (state == 3) {
                changed = changed2;
                newText = LocaleController.getString("Connecting", R.string.Connecting);
                newSubtext = "";
            } else {
                newText = LocaleController.getString("VoipMutedByAdmin", R.string.VoipMutedByAdmin);
                changed = changed2;
                newSubtext = LocaleController.getString("VoipMutedTapForSpeak", R.string.VoipMutedTapForSpeak);
            }
        } else {
            newText = LocaleController.getString("VoipMutedTapedForSpeak", R.string.VoipMutedTapedForSpeak);
            newSubtext = LocaleController.getString("VoipMutedTapedForSpeakInfo", R.string.VoipMutedTapedForSpeakInfo);
            changed = this.bigMicDrawable.setCustomEndFrame(136);
        }
        if (isRtmpStream() && state != 3 && !this.call.isScheduled()) {
            newText = LocaleController.getString(fullscreen ? R.string.VoipGroupMinimizeStream : R.string.VoipGroupExpandStream);
            newSubtext = "";
            changed = this.animatingToFullscreenExpand != fullscreen;
            this.animatingToFullscreenExpand = fullscreen;
        }
        String contentDescription = !TextUtils.isEmpty(newSubtext) ? newText + " " + newSubtext : newText;
        this.muteButton.setContentDescription(contentDescription);
        if (!animated) {
            this.muteButtonState = state;
            RLottieDrawable rLottieDrawable = this.bigMicDrawable;
            boolean z2 = true;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            this.muteLabel[0].setText(newText);
            if (!isRtmpStream() || this.call.isScheduled()) {
                this.muteButton.setAlpha(1.0f);
                this.expandButton.setAlpha(0.0f);
                this.minimizeButton.setAlpha(0.0f);
            } else {
                this.muteButton.setAlpha(0.0f);
                GroupCallRenderersContainer groupCallRenderersContainer2 = this.renderersContainer;
                if (groupCallRenderersContainer2 == null || !groupCallRenderersContainer2.inFullscreenMode || (!AndroidUtilities.isTablet() && isLandscapeMode != isRtmpLandscapeMode())) {
                    z2 = false;
                }
                boolean isExpanded = z2;
                View hideView = isExpanded ? this.expandButton : this.minimizeButton;
                (isExpanded ? this.minimizeButton : this.expandButton).setAlpha(1.0f);
                hideView.setAlpha(0.0f);
            }
        } else {
            if (!changed) {
                i = 0;
            } else if (state == 5) {
                this.bigMicDrawable.setCurrentFrame(376);
                i = 0;
            } else if (state == 7) {
                this.bigMicDrawable.setCurrentFrame(173);
                i = 0;
            } else if (state == 6) {
                this.bigMicDrawable.setCurrentFrame(311);
                i = 0;
            } else if (state == 0) {
                int i5 = this.muteButtonState;
                if (i5 == 5) {
                    this.bigMicDrawable.setCurrentFrame(376);
                    i = 0;
                } else if (i5 == 7) {
                    this.bigMicDrawable.setCurrentFrame(344);
                    i = 0;
                } else if (i5 == 6) {
                    this.bigMicDrawable.setCurrentFrame(SearchViewPager.deleteItemId);
                    i = 0;
                } else if (i5 == 2) {
                    i = 0;
                    this.bigMicDrawable.setCurrentFrame(0);
                } else {
                    this.bigMicDrawable.setCurrentFrame(69);
                    i = 0;
                }
            } else if (state == 1) {
                this.bigMicDrawable.setCurrentFrame(this.muteButtonState == 4 ? 69 : 36);
                i = 0;
            } else if (state == 4) {
                this.bigMicDrawable.setCurrentFrame(99);
                i = 0;
            } else if (mutedByAdmin) {
                int i6 = this.muteButtonState;
                if (i6 == 7) {
                    this.bigMicDrawable.setCurrentFrame(274);
                    i = 0;
                } else if (i6 == 6) {
                    this.bigMicDrawable.setCurrentFrame(237);
                    i = 0;
                } else if (i6 == 1) {
                    this.bigMicDrawable.setCurrentFrame(136);
                    i = 0;
                } else {
                    this.bigMicDrawable.setCurrentFrame(99);
                    i = 0;
                }
            } else {
                int i7 = this.muteButtonState;
                if (i7 == 5) {
                    this.bigMicDrawable.setCurrentFrame(376);
                    i = 0;
                } else if (i7 == 7) {
                    this.bigMicDrawable.setCurrentFrame(344);
                    i = 0;
                } else if (i7 == 6) {
                    this.bigMicDrawable.setCurrentFrame(SearchViewPager.deleteItemId);
                    i = 0;
                } else if (i7 == 2 || i7 == 4) {
                    i = 0;
                    this.bigMicDrawable.setCurrentFrame(0);
                } else {
                    this.bigMicDrawable.setCurrentFrame(69);
                    i = 0;
                }
            }
            this.muteButton.playAnimation();
            this.muteLabel[1].setVisibility(i);
            this.muteLabel[1].setAlpha(0.0f);
            this.muteLabel[1].setTranslationY(-AndroidUtilities.dp(5.0f));
            this.muteLabel[1].setText(newText);
            if (isRtmpStream() && !this.call.isScheduled()) {
                this.muteButton.setAlpha(0.0f);
                boolean isExpanded2 = this.renderersContainer.inFullscreenMode && (AndroidUtilities.isTablet() || isLandscapeMode == isRtmpLandscapeMode());
                final View hideView2 = isExpanded2 ? this.expandButton : this.minimizeButton;
                final View showView = isExpanded2 ? this.minimizeButton : this.expandButton;
                final float muteButtonScale = AndroidUtilities.dp(52.0f) / (this.muteButton.getMeasuredWidth() - AndroidUtilities.dp(8.0f));
                boolean bigSize = !AndroidUtilities.isTablet() ? !(this.renderersContainer.inFullscreenMode || isLandscapeMode) : !this.renderersContainer.inFullscreenMode;
                Boolean bool = this.wasExpandBigSize;
                boolean changedSize = bool == null || bigSize != bool.booleanValue();
                this.wasExpandBigSize = Boolean.valueOf(bigSize);
                ValueAnimator valueAnimator3 = this.expandSizeAnimator;
                if (valueAnimator3 != null) {
                    valueAnimator3.cancel();
                    this.expandSizeAnimator = null;
                }
                if (changedSize) {
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                    this.expandSizeAnimator = ofFloat;
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda33
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                            GroupCallActivity.this.m3509lambda$updateMuteButton$49$orgtelegramuiGroupCallActivity(muteButtonScale, showView, valueAnimator4);
                        }
                    });
                    this.expandSizeAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.50
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            GroupCallActivity.this.expandSizeAnimator = null;
                        }
                    });
                    this.expandSizeAnimator.start();
                } else {
                    if (isLandscapeMode) {
                        multiplier2 = muteButtonScale;
                        f2 = 1.0f;
                    } else {
                        f2 = 1.0f;
                        multiplier2 = AndroidUtilities.lerp(1.0f, muteButtonScale, this.renderersContainer.progressToFullscreenMode);
                    }
                    showView.setAlpha(f2);
                    showView.setScaleX(multiplier2);
                    showView.setScaleY(multiplier2);
                    hideView2.setAlpha(0.0f);
                }
                if (changed) {
                    ValueAnimator ofFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
                    this.expandAnimator = ofFloat2;
                    ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda44
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                            GroupCallActivity.this.m3510lambda$updateMuteButton$50$orgtelegramuiGroupCallActivity(muteButtonScale, hideView2, showView, valueAnimator4);
                        }
                    });
                    this.expandAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.51
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            GroupCallActivity.this.expandAnimator = null;
                        }
                    });
                    this.expandAnimator.start();
                } else {
                    if (isLandscapeMode) {
                        multiplier = muteButtonScale;
                        f = 1.0f;
                    } else {
                        f = 1.0f;
                        multiplier = AndroidUtilities.lerp(1.0f, muteButtonScale, this.renderersContainer.progressToFullscreenMode);
                    }
                    showView.setAlpha(f);
                    showView.setScaleX(multiplier);
                    showView.setScaleY(multiplier);
                    hideView2.setAlpha(0.0f);
                }
            } else {
                this.muteButton.setAlpha(1.0f);
                this.expandButton.setAlpha(0.0f);
                this.minimizeButton.setAlpha(0.0f);
            }
            if (changed) {
                ValueAnimator ofFloat3 = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.muteButtonAnimator = ofFloat3;
                ofFloat3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda11
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                        GroupCallActivity.this.m3511lambda$updateMuteButton$51$orgtelegramuiGroupCallActivity(valueAnimator4);
                    }
                });
                this.muteButtonAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.52
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (GroupCallActivity.this.muteButtonAnimator != null) {
                            GroupCallActivity.this.muteButtonAnimator = null;
                            TextView temp = GroupCallActivity.this.muteLabel[0];
                            GroupCallActivity.this.muteLabel[0] = GroupCallActivity.this.muteLabel[1];
                            GroupCallActivity.this.muteLabel[1] = temp;
                            temp.setVisibility(4);
                            for (int a = 0; a < 2; a++) {
                                GroupCallActivity.this.muteLabel[a].setTranslationY(0.0f);
                            }
                        }
                    }
                });
                this.muteButtonAnimator.setDuration(180L);
                this.muteButtonAnimator.start();
            } else {
                this.muteLabel[0].setAlpha(0.0f);
                this.muteLabel[1].setAlpha(1.0f);
                TextView[] textViewArr = this.muteLabel;
                TextView temp = textViewArr[0];
                textViewArr[0] = textViewArr[1];
                textViewArr[1] = temp;
                temp.setVisibility(4);
                for (int a = 0; a < 2; a++) {
                    this.muteLabel[a].setTranslationY(0.0f);
                }
            }
            this.muteButtonState = state;
        }
        updateMuteButtonState(animated);
    }

    /* renamed from: lambda$updateMuteButton$49$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3509lambda$updateMuteButton$49$orgtelegramuiGroupCallActivity(float muteButtonScale, View showView, ValueAnimator animation) {
        float multiplier = isLandscapeMode ? muteButtonScale : AndroidUtilities.lerp(1.0f, muteButtonScale, this.renderersContainer.progressToFullscreenMode);
        showView.setScaleX(multiplier);
        showView.setScaleY(multiplier);
    }

    /* renamed from: lambda$updateMuteButton$50$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3510lambda$updateMuteButton$50$orgtelegramuiGroupCallActivity(float muteButtonScale, View hideView, View showView, ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        float multiplier = isLandscapeMode ? muteButtonScale : AndroidUtilities.lerp(1.0f, muteButtonScale, this.renderersContainer.progressToFullscreenMode);
        hideView.setAlpha(1.0f - val);
        float scale = (((1.0f - val) * 0.9f) + 0.1f) * multiplier;
        hideView.setScaleX(scale);
        hideView.setScaleY(scale);
        showView.setAlpha(val);
        float scale2 = ((0.9f * val) + 0.1f) * multiplier;
        showView.setScaleX(scale2);
        showView.setScaleY(scale2);
    }

    /* renamed from: lambda$updateMuteButton$51$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3511lambda$updateMuteButton$51$orgtelegramuiGroupCallActivity(ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        this.muteLabel[0].setAlpha(1.0f - v);
        this.muteLabel[0].setTranslationY(AndroidUtilities.dp(5.0f) * v);
        this.muteLabel[1].setAlpha(v);
        this.muteLabel[1].setTranslationY(AndroidUtilities.dp((5.0f * v) - 5.0f));
    }

    public void fillColors(int state, int[] colorsToSet) {
        if (state == 0) {
            colorsToSet[0] = Theme.getColor(Theme.key_voipgroup_unmuteButton2);
            colorsToSet[1] = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_soundButtonActive), Theme.getColor(Theme.key_voipgroup_soundButtonActiveScrolled), this.colorProgress, 1.0f);
            colorsToSet[2] = Theme.getColor(Theme.key_voipgroup_soundButton);
        } else if (state == 1) {
            colorsToSet[0] = Theme.getColor(Theme.key_voipgroup_muteButton2);
            colorsToSet[1] = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_soundButtonActive2), Theme.getColor(Theme.key_voipgroup_soundButtonActive2Scrolled), this.colorProgress, 1.0f);
            colorsToSet[2] = Theme.getColor(Theme.key_voipgroup_soundButton2);
        } else if (isGradientState(state)) {
            colorsToSet[0] = Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient3);
            colorsToSet[1] = Theme.getColor(Theme.key_voipgroup_mutedByAdminMuteButton);
            colorsToSet[2] = Theme.getColor(Theme.key_voipgroup_mutedByAdminMuteButtonDisabled);
        } else {
            colorsToSet[0] = Theme.getColor(Theme.key_voipgroup_disabledButton);
            colorsToSet[1] = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_disabledButtonActive), Theme.getColor(Theme.key_voipgroup_disabledButtonActiveScrolled), this.colorProgress, 1.0f);
            colorsToSet[2] = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_listViewBackgroundUnscrolled), Theme.getColor(Theme.key_voipgroup_disabledButton), this.colorProgress, 1.0f);
        }
    }

    public void showRecordHint(View view) {
        if (this.recordHintView == null) {
            HintView hintView = new HintView(getContext(), 8, true);
            this.recordHintView = hintView;
            hintView.setAlpha(0.0f);
            this.recordHintView.setVisibility(4);
            this.recordHintView.setShowingDuration(3000L);
            this.containerView.addView(this.recordHintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                this.recordHintView.setText(LocaleController.getString("VoipChannelRecording", R.string.VoipChannelRecording));
            } else {
                this.recordHintView.setText(LocaleController.getString("VoipGroupRecording", R.string.VoipGroupRecording));
            }
            this.recordHintView.setBackgroundColor(-366530760, -1);
        }
        this.recordHintView.setExtraTranslationY(-AndroidUtilities.statusBarHeight);
        this.recordHintView.showForView(view, true);
    }

    public void showReminderHint() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (!preferences.getBoolean("reminderhint", false)) {
            preferences.edit().putBoolean("reminderhint", true).commit();
            if (this.reminderHintView == null) {
                HintView hintView = new HintView(getContext(), 8);
                this.reminderHintView = hintView;
                hintView.setAlpha(0.0f);
                this.reminderHintView.setVisibility(4);
                this.reminderHintView.setShowingDuration(4000L);
                this.containerView.addView(this.reminderHintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
                this.reminderHintView.setText(LocaleController.getString("VoipChatReminderHint", R.string.VoipChatReminderHint));
                this.reminderHintView.setBackgroundColor(-366530760, -1);
            }
            this.reminderHintView.setExtraTranslationY(-AndroidUtilities.statusBarHeight);
            this.reminderHintView.showForView(this.muteButton, true);
        }
    }

    private void updateMuteButtonState(boolean animated) {
        this.muteButton.invalidate();
        WeavingState[] weavingStateArr = this.states;
        int i = this.muteButtonState;
        boolean z = false;
        if (weavingStateArr[i] == null) {
            weavingStateArr[i] = new WeavingState(this.muteButtonState);
            int i2 = this.muteButtonState;
            if (i2 == 3) {
                this.states[i2].shader = null;
            } else if (isGradientState(i2)) {
                this.states[this.muteButtonState].shader = new LinearGradient(0.0f, 400.0f, 400.0f, 0.0f, new int[]{Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient), Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient3), Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient2)}, (float[]) null, Shader.TileMode.CLAMP);
            } else {
                int i3 = this.muteButtonState;
                if (i3 == 1) {
                    this.states[i3].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor(Theme.key_voipgroup_muteButton), Theme.getColor(Theme.key_voipgroup_muteButton3)}, (float[]) null, Shader.TileMode.CLAMP);
                } else {
                    this.states[i3].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor(Theme.key_voipgroup_unmuteButton2), Theme.getColor(Theme.key_voipgroup_unmuteButton)}, (float[]) null, Shader.TileMode.CLAMP);
                }
            }
        }
        WeavingState[] weavingStateArr2 = this.states;
        int i4 = this.muteButtonState;
        WeavingState weavingState = weavingStateArr2[i4];
        WeavingState weavingState2 = this.currentState;
        float f = 0.0f;
        if (weavingState != weavingState2) {
            this.prevState = weavingState2;
            this.currentState = weavingStateArr2[i4];
            if (weavingState2 == null || !animated) {
                this.switchProgress = 1.0f;
                this.prevState = null;
            } else {
                this.switchProgress = 0.0f;
            }
        }
        if (!animated) {
            boolean showWaves = false;
            boolean showLighting = false;
            WeavingState weavingState3 = this.currentState;
            if (weavingState3 != null) {
                showWaves = weavingState3.currentState == 1 || this.currentState.currentState == 0;
                if (this.currentState.currentState != 3) {
                    z = true;
                }
                showLighting = z;
            }
            this.showWavesProgress = showWaves ? 1.0f : 0.0f;
            if (showLighting) {
                f = 1.0f;
            }
            this.showLightingProgress = f;
        }
        this.buttonsContainer.invalidate();
    }

    public static void processOnLeave(ChatObject.Call call, boolean discard, long selfId, Runnable onLeave) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp(discard ? 1 : 0);
        }
        if (call != null) {
            TLRPC.TL_groupCallParticipant participant = call.participants.get(selfId);
            if (participant != null) {
                call.participants.delete(selfId);
                call.sortedParticipants.remove(participant);
                call.visibleParticipants.remove(participant);
                int i = 0;
                while (i < call.visibleVideoParticipants.size()) {
                    ChatObject.VideoParticipant videoParticipant = call.visibleVideoParticipants.get(i);
                    if (MessageObject.getPeerId(videoParticipant.participant.peer) == MessageObject.getPeerId(participant.peer)) {
                        call.visibleVideoParticipants.remove(i);
                        i--;
                    }
                    i++;
                }
                TLRPC.GroupCall groupCall = call.call;
                groupCall.participants_count--;
            }
            for (int i2 = 0; i2 < call.sortedParticipants.size(); i2++) {
                TLRPC.TL_groupCallParticipant participant1 = call.sortedParticipants.get(i2);
                participant1.lastActiveDate = participant1.lastSpeakTime;
            }
        }
        if (onLeave != null) {
            onLeave.run();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }

    public static void onLeaveClick(Context context, final Runnable onLeave, boolean fromOverlayWindow) {
        VoIPService service = VoIPService.getSharedInstance();
        if (service == null) {
            return;
        }
        TLRPC.Chat currentChat = service.getChat();
        final ChatObject.Call call = service.groupCall;
        final long selfId = service.getSelfId();
        if (ChatObject.canManageCalls(currentChat)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            if (ChatObject.isChannelOrGiga(currentChat)) {
                builder.setTitle(LocaleController.getString("VoipChannelLeaveAlertTitle", R.string.VoipChannelLeaveAlertTitle));
                builder.setMessage(LocaleController.getString("VoipChannelLeaveAlertText", R.string.VoipChannelLeaveAlertText));
            } else {
                builder.setTitle(LocaleController.getString("VoipGroupLeaveAlertTitle", R.string.VoipGroupLeaveAlertTitle));
                builder.setMessage(LocaleController.getString("VoipGroupLeaveAlertText", R.string.VoipGroupLeaveAlertText));
            }
            service.getAccount();
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            final CheckBoxCell[] cells = {new CheckBoxCell(context, 1)};
            cells[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (fromOverlayWindow) {
                cells[0].setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            } else {
                cells[0].setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
                CheckBoxSquare checkBox = (CheckBoxSquare) cells[0].getCheckBoxView();
                checkBox.setColors(Theme.key_voipgroup_mutedIcon, Theme.key_voipgroup_listeningText, Theme.key_voipgroup_nameText);
            }
            cells[0].setTag(0);
            if (ChatObject.isChannelOrGiga(currentChat)) {
                cells[0].setText(LocaleController.getString("VoipChannelLeaveAlertEndChat", R.string.VoipChannelLeaveAlertEndChat), "", false, false);
            } else {
                cells[0].setText(LocaleController.getString("VoipGroupLeaveAlertEndChat", R.string.VoipGroupLeaveAlertEndChat), "", false, false);
            }
            cells[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
            linearLayout.addView(cells[0], LayoutHelper.createLinear(-1, -2));
            cells[0].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda18
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    GroupCallActivity.lambda$onLeaveClick$52(cells, view);
                }
            });
            builder.setCustomViewOffset(12);
            builder.setView(linearLayout);
            builder.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
            builder.setPositiveButton(LocaleController.getString("VoipGroupLeave", R.string.VoipGroupLeave), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda62
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    GroupCallActivity.processOnLeave(ChatObject.Call.this, cells[0].isChecked(), selfId, onLeave);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            if (fromOverlayWindow) {
                builder.setDimEnabled(false);
            }
            AlertDialog dialog = builder.create();
            if (fromOverlayWindow) {
                if (Build.VERSION.SDK_INT >= 26) {
                    dialog.getWindow().setType(2038);
                } else {
                    dialog.getWindow().setType(2003);
                }
                dialog.getWindow().clearFlags(2);
            }
            if (!fromOverlayWindow) {
                dialog.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
            }
            dialog.show();
            if (!fromOverlayWindow) {
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_voipgroup_leaveCallMenu));
                }
                dialog.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
                return;
            }
            return;
        }
        processOnLeave(call, false, selfId, onLeave);
    }

    public static /* synthetic */ void lambda$onLeaveClick$52(CheckBoxCell[] cells, View v) {
        Integer num = (Integer) v.getTag();
        cells[num.intValue()].setChecked(!cells[num.intValue()].isChecked(), true);
    }

    public void processSelectedOption(TLRPC.TL_groupCallParticipant participant, final long peerId, int option) {
        TLObject object;
        boolean z;
        final TLObject object2;
        String name;
        TextView button;
        int i;
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService == null) {
            return;
        }
        if (peerId > 0) {
            object = this.accountInstance.getMessagesController().getUser(Long.valueOf(peerId));
        } else {
            object = this.accountInstance.getMessagesController().getChat(Long.valueOf(-peerId));
        }
        boolean z2 = false;
        if (option == 0 || option == 2) {
            object2 = object;
            z = true;
        } else if (option != 3) {
            if (option == 6) {
                this.parentActivity.switchToAccount(this.currentAccount, true);
                Bundle args = new Bundle();
                if (peerId > 0) {
                    args.putLong("user_id", peerId);
                } else {
                    args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -peerId);
                }
                this.parentActivity.m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ChatActivity(args));
                dismiss();
                return;
            } else if (option == 8) {
                this.parentActivity.switchToAccount(this.currentAccount, true);
                BaseFragment fragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
                if ((fragment instanceof ChatActivity) && ((ChatActivity) fragment).getDialogId() == peerId) {
                    dismiss();
                    return;
                }
                Bundle args2 = new Bundle();
                if (peerId > 0) {
                    args2.putLong("user_id", peerId);
                } else {
                    args2.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -peerId);
                }
                this.parentActivity.m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ChatActivity(args2));
                dismiss();
                return;
            } else if (option == 7) {
                voIPService.editCallMember(object, true, null, null, false, null);
                updateMuteButton(2, true);
                return;
            } else if (option != 9) {
                if (option == 10) {
                    AlertsCreator.createChangeBioAlert(participant.about, peerId, getContext(), this.currentAccount);
                    return;
                } else if (option == 11) {
                    AlertsCreator.createChangeNameAlert(peerId, getContext(), this.currentAccount);
                    return;
                } else if (option == 5) {
                    voIPService.editCallMember(object, true, null, null, null, null);
                    getUndoView().showWithAction(0L, 35, object);
                    voIPService.setParticipantVolume(participant, 0);
                    return;
                } else {
                    if ((participant.flags & 128) == 0 || participant.volume != 0) {
                        i = 1;
                        voIPService.editCallMember(object, false, null, null, null, null);
                    } else {
                        participant.volume = 10000;
                        participant.volume_by_admin = false;
                        i = 1;
                        voIPService.editCallMember(object, false, null, Integer.valueOf(participant.volume), null, null);
                    }
                    voIPService.setParticipantVolume(participant, ChatObject.getParticipantVolume(participant));
                    getUndoView().showWithAction(0L, option == i ? 31 : 36, object, (Object) null, (Runnable) null, (Runnable) null);
                    return;
                }
            } else {
                ImageUpdater imageUpdater = this.currentAvatarUpdater;
                if (imageUpdater != null && imageUpdater.isUploadingImage()) {
                    return;
                }
                ImageUpdater imageUpdater2 = new ImageUpdater(true);
                this.currentAvatarUpdater = imageUpdater2;
                imageUpdater2.setOpenWithFrontfaceCamera(true);
                this.currentAvatarUpdater.setForceDarkTheme(true);
                this.currentAvatarUpdater.setSearchAvailable(true, true);
                this.currentAvatarUpdater.setShowingFromDialog(true);
                this.currentAvatarUpdater.parentFragment = this.parentActivity.getActionBarLayout().getLastFragment();
                ImageUpdater imageUpdater3 = this.currentAvatarUpdater;
                AvatarUpdaterDelegate avatarUpdaterDelegate = new AvatarUpdaterDelegate(peerId);
                this.avatarUpdaterDelegate = avatarUpdaterDelegate;
                imageUpdater3.setDelegate(avatarUpdaterDelegate);
                TLRPC.User user = this.accountInstance.getUserConfig().getCurrentUser();
                ImageUpdater imageUpdater4 = this.currentAvatarUpdater;
                if (user.photo != null && user.photo.photo_big != null && !(user.photo instanceof TLRPC.TL_userProfilePhotoEmpty)) {
                    z2 = true;
                }
                imageUpdater4.openMenu(z2, new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda28
                    @Override // java.lang.Runnable
                    public final void run() {
                        GroupCallActivity.this.m3503x6cf07c76();
                    }
                }, GroupCallActivity$$ExternalSyntheticLambda4.INSTANCE);
                return;
            }
        } else {
            object2 = object;
            z = true;
        }
        if (option == 0) {
            if (VoIPService.getSharedInstance() == null) {
                return;
            }
            VoIPService.getSharedInstance().editCallMember(object2, Boolean.valueOf(z), null, null, null, null);
            getUndoView().showWithAction(0L, 30, object2, (Object) null, (Runnable) null, (Runnable) null);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
        TextView messageTextView = new TextView(getContext());
        messageTextView.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
        int i2 = z ? 1 : 0;
        int i3 = z ? 1 : 0;
        messageTextView.setTextSize(i2, 16.0f);
        messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        FrameLayout frameLayout = new FrameLayout(getContext());
        builder.setView(frameLayout);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        BackupImageView imageView = new BackupImageView(getContext());
        imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        frameLayout.addView(imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
        avatarDrawable.setInfo(object2);
        if (object2 instanceof TLRPC.User) {
            TLRPC.User user2 = (TLRPC.User) object2;
            imageView.setForUserOrChat(user2, avatarDrawable);
            name = UserObject.getFirstName(user2);
        } else {
            TLRPC.Chat chat = (TLRPC.Chat) object2;
            imageView.setForUserOrChat(chat, avatarDrawable);
            name = chat.title;
        }
        TextView textView = new TextView(getContext());
        textView.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        if (option == 2) {
            textView.setText(LocaleController.getString("VoipGroupRemoveMemberAlertTitle2", R.string.VoipGroupRemoveMemberAlertTitle2));
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipChannelRemoveMemberAlertText2", R.string.VoipChannelRemoveMemberAlertText2, name, this.currentChat.title)));
            } else {
                messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupRemoveMemberAlertText2", R.string.VoipGroupRemoveMemberAlertText2, name, this.currentChat.title)));
            }
        } else {
            textView.setText(LocaleController.getString("VoipGroupAddMemberTitle", R.string.VoipGroupAddMemberTitle));
            messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupAddMemberText", R.string.VoipGroupAddMemberText, name, this.currentChat.title)));
        }
        int i4 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i5 = 21;
        float f = LocaleController.isRTL ? 21 : 76;
        if (LocaleController.isRTL) {
            i5 = 76;
        }
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, i4, f, 11.0f, i5, 0.0f));
        frameLayout.addView(messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
        if (option == 2) {
            builder.setPositiveButton(LocaleController.getString("VoipGroupUserRemove", R.string.VoipGroupUserRemove), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda63
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i6) {
                    GroupCallActivity.this.m3500x38663d33(object2, dialogInterface, i6);
                }
            });
        } else if (object2 instanceof TLRPC.User) {
            final TLRPC.User user3 = (TLRPC.User) object2;
            builder.setPositiveButton(LocaleController.getString("VoipGroupAdd", R.string.VoipGroupAdd), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda64
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i6) {
                    GroupCallActivity.this.m3502x617bcb5(user3, peerId, dialogInterface, i6);
                }
            });
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog dialog = builder.create();
        dialog.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
        dialog.show();
        if (option == 2 && (button = (TextView) dialog.getButton(-1)) != null) {
            button.setTextColor(Theme.getColor(Theme.key_voipgroup_leaveCallMenu));
        }
    }

    /* renamed from: lambda$processSelectedOption$54$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3500x38663d33(TLObject object, DialogInterface dialogInterface, int i) {
        if (object instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) object;
            this.accountInstance.getMessagesController().deleteParticipantFromChat(this.currentChat.id, user, null);
            getUndoView().showWithAction(0L, 32, user, (Object) null, (Runnable) null, (Runnable) null);
            return;
        }
        TLRPC.Chat chat = (TLRPC.Chat) object;
        this.accountInstance.getMessagesController().deleteParticipantFromChat(this.currentChat.id, null, chat, null, false, false);
        getUndoView().showWithAction(0L, 32, chat, (Object) null, (Runnable) null, (Runnable) null);
    }

    /* renamed from: lambda$processSelectedOption$56$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3502x617bcb5(TLRPC.User user, final long peerId, DialogInterface dialogInterface, int i) {
        BaseFragment fragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
        this.accountInstance.getMessagesController().addUserToChat(this.currentChat.id, user, 0, null, fragment, new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                GroupCallActivity.this.m3501x9f3efcf4(peerId);
            }
        });
    }

    /* renamed from: lambda$processSelectedOption$55$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3501x9f3efcf4(long peerId) {
        inviteUserToCall(peerId, false);
    }

    /* renamed from: lambda$processSelectedOption$57$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3503x6cf07c76() {
        this.accountInstance.getMessagesController().deleteUserPhoto(null);
    }

    public static /* synthetic */ void lambda$processSelectedOption$58(DialogInterface dialog) {
    }

    public boolean showMenuForCell(View rendererCell) {
        boolean z;
        GroupCallUserCell view;
        VolumeSlider volumeSlider;
        final LinearLayout linearLayout;
        LinearLayout buttonsLayout;
        ScrollView scrollView;
        TLRPC.TL_groupCallParticipant participant;
        boolean showWithAvatarPreview;
        ArrayList<Integer> options;
        ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
        VolumeSlider volumeSlider2;
        ScrollView scrollView2;
        boolean isAdmin;
        ScrollView scrollView3;
        final ArrayList<Integer> options2;
        final TLRPC.TL_groupCallParticipant participant2;
        ImageLocation imageLocation;
        ImageLocation thumbLocation;
        int popupY;
        int popupX;
        AvatarUpdaterDelegate avatarUpdaterDelegate;
        String str;
        int i;
        String str2;
        int i2;
        String str3;
        int i3;
        String str4;
        int i4;
        if (this.itemAnimator.isRunning()) {
            return false;
        }
        if (!this.avatarPriviewTransitionInProgress) {
            if (!this.avatarsPreviewShowed) {
                ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
                LinearLayout linearLayout2 = null;
                if (actionBarPopupWindow != null) {
                    actionBarPopupWindow.dismiss();
                    this.scrimPopupWindow = null;
                    return false;
                }
                clearScrimView();
                if (rendererCell instanceof GroupCallGridCell) {
                    GroupCallGridCell groupCallGridCell = (GroupCallGridCell) rendererCell;
                    if (groupCallGridCell.getParticipant() == this.call.videoNotAvailableParticipant) {
                        return false;
                    }
                    GroupCallUserCell view2 = new GroupCallUserCell(groupCallGridCell.getContext());
                    long selfPeerId = MessageObject.getPeerId(this.selfPeer);
                    view2.setData(this.accountInstance, groupCallGridCell.getParticipant().participant, this.call, selfPeerId, null, false);
                    this.hasScrimAnchorView = false;
                    this.scrimGridView = groupCallGridCell;
                    this.scrimRenderer = groupCallGridCell.getRenderer();
                    if (!isTabletMode && !isLandscapeMode) {
                        this.scrimViewAttached = true;
                        this.containerView.addView(view2, LayoutHelper.createFrame(-1, -2.0f, 0, 14.0f, 0.0f, 14.0f, 0.0f));
                    } else {
                        this.scrimViewAttached = false;
                    }
                    view = view2;
                } else if (rendererCell instanceof GroupCallFullscreenAdapter.GroupCallUserCell) {
                    GroupCallFullscreenAdapter.GroupCallUserCell groupCallFullscreenCell = (GroupCallFullscreenAdapter.GroupCallUserCell) rendererCell;
                    if (groupCallFullscreenCell.getParticipant() == this.call.videoNotAvailableParticipant.participant) {
                        return false;
                    }
                    GroupCallUserCell view3 = new GroupCallUserCell(groupCallFullscreenCell.getContext());
                    long selfPeerId2 = MessageObject.getPeerId(this.selfPeer);
                    view3.setData(this.accountInstance, groupCallFullscreenCell.getParticipant(), this.call, selfPeerId2, null, false);
                    this.hasScrimAnchorView = false;
                    this.scrimFullscreenView = groupCallFullscreenCell;
                    GroupCallMiniTextureView renderer = groupCallFullscreenCell.getRenderer();
                    this.scrimRenderer = renderer;
                    if (renderer != null && renderer.showingInFullscreen) {
                        this.scrimRenderer = null;
                    }
                    this.containerView.addView(view3, LayoutHelper.createFrame(-1, -2.0f, 0, 14.0f, 0.0f, 14.0f, 0.0f));
                    this.scrimViewAttached = true;
                    view = view3;
                } else {
                    this.hasScrimAnchorView = true;
                    this.scrimViewAttached = true;
                    view = (GroupCallUserCell) rendererCell;
                }
                if (view == null) {
                    return false;
                }
                boolean showWithAvatarPreview2 = !isLandscapeMode && !isTabletMode && !AndroidUtilities.isInMultiwindow;
                TLRPC.TL_groupCallParticipant participant3 = view.getParticipant();
                final Rect rect = new Rect();
                ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
                popupLayout2.setBackgroundDrawable(null);
                popupLayout2.setPadding(0, 0, 0, 0);
                popupLayout2.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.GroupCallActivity.53
                    private int[] pos = new int[2];

                    @Override // android.view.View.OnTouchListener
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getActionMasked() == 0) {
                            if (GroupCallActivity.this.scrimPopupWindow != null && GroupCallActivity.this.scrimPopupWindow.isShowing()) {
                                View contentView = GroupCallActivity.this.scrimPopupWindow.getContentView();
                                contentView.getLocationInWindow(this.pos);
                                Rect rect2 = rect;
                                int[] iArr = this.pos;
                                rect2.set(iArr[0], iArr[1], iArr[0] + contentView.getMeasuredWidth(), this.pos[1] + contentView.getMeasuredHeight());
                                if (!rect.contains((int) event.getX(), (int) event.getY())) {
                                    GroupCallActivity.this.scrimPopupWindow.dismiss();
                                }
                            }
                        } else if (event.getActionMasked() == 4 && GroupCallActivity.this.scrimPopupWindow != null && GroupCallActivity.this.scrimPopupWindow.isShowing()) {
                            GroupCallActivity.this.scrimPopupWindow.dismiss();
                        }
                        return false;
                    }
                });
                popupLayout2.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda51
                    @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        GroupCallActivity.this.m3505lambda$showMenuForCell$59$orgtelegramuiGroupCallActivity(keyEvent);
                    }
                });
                final LinearLayout buttonsLayout2 = new LinearLayout(getContext());
                if (!participant3.muted_by_you) {
                    linearLayout2 = new LinearLayout(getContext());
                }
                final LinearLayout volumeLayout = linearLayout2;
                this.currentOptionsLayout = buttonsLayout2;
                LinearLayout linearLayout3 = new LinearLayout(getContext()) { // from class: org.telegram.ui.GroupCallActivity.54
                    @Override // android.widget.LinearLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = View.MeasureSpec.getSize(widthMeasureSpec);
                        buttonsLayout2.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
                        LinearLayout linearLayout4 = volumeLayout;
                        if (linearLayout4 != null) {
                            linearLayout4.measure(View.MeasureSpec.makeMeasureSpec(buttonsLayout2.getMeasuredWidth(), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(0, 0));
                            setMeasuredDimension(buttonsLayout2.getMeasuredWidth(), buttonsLayout2.getMeasuredHeight() + volumeLayout.getMeasuredHeight());
                            return;
                        }
                        setMeasuredDimension(buttonsLayout2.getMeasuredWidth(), buttonsLayout2.getMeasuredHeight());
                    }
                };
                linearLayout3.setMinimumWidth(AndroidUtilities.dp(240.0f));
                linearLayout3.setOrientation(1);
                int color = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_listViewBackgroundUnscrolled), Theme.getColor(Theme.key_voipgroup_listViewBackground), this.colorProgress, 1.0f);
                if (volumeLayout != null && !view.isSelfUser() && !participant3.muted_by_you && (!participant3.muted || participant3.can_self_unmute)) {
                    Drawable shadowDrawable = getContext().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
                    shadowDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    volumeLayout.setBackgroundDrawable(shadowDrawable);
                    linearLayout3.addView(volumeLayout, LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 0.0f, 0.0f));
                    VolumeSlider volumeSlider3 = new VolumeSlider(getContext(), participant3);
                    volumeLayout.addView(volumeSlider3, -1, 48);
                    volumeSlider = volumeSlider3;
                } else {
                    volumeSlider = null;
                }
                buttonsLayout2.setMinimumWidth(AndroidUtilities.dp(240.0f));
                buttonsLayout2.setOrientation(1);
                Drawable shadowDrawable2 = getContext().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
                shadowDrawable2.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                buttonsLayout2.setBackgroundDrawable(shadowDrawable2);
                linearLayout3.addView(buttonsLayout2, LayoutHelper.createLinear(-2, -2, 0.0f, volumeSlider != null ? -8.0f : 0.0f, 0.0f, 0.0f));
                if (Build.VERSION.SDK_INT >= 21) {
                    linearLayout = linearLayout3;
                    buttonsLayout = buttonsLayout2;
                    scrollView = new ScrollView(getContext(), null, 0, R.style.scrollbarShapeStyle) { // from class: org.telegram.ui.GroupCallActivity.55
                        @Override // android.widget.ScrollView, android.widget.FrameLayout, android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                            setMeasuredDimension(linearLayout.getMeasuredWidth(), getMeasuredHeight());
                        }
                    };
                } else {
                    linearLayout = linearLayout3;
                    buttonsLayout = buttonsLayout2;
                    scrollView = new ScrollView(getContext());
                }
                scrollView.setClipToPadding(false);
                popupLayout2.addView(scrollView, LayoutHelper.createFrame(-2, -2.0f));
                long peerId = MessageObject.getPeerId(participant3.peer);
                ArrayList<String> items = new ArrayList<>(2);
                ArrayList<Integer> icons = new ArrayList<>(2);
                ArrayList<Integer> options3 = new ArrayList<>(2);
                boolean isAdmin2 = false;
                if (participant3.peer instanceof TLRPC.TL_peerUser) {
                    if (ChatObject.isChannel(this.currentChat)) {
                        volumeSlider2 = volumeSlider;
                        popupLayout = popupLayout2;
                        TLRPC.ChannelParticipant p = this.accountInstance.getMessagesController().getAdminInChannel(participant3.peer.user_id, this.currentChat.id);
                        isAdmin = p != null && ((p instanceof TLRPC.TL_channelParticipantCreator) || p.admin_rights.manage_call);
                        scrollView2 = scrollView;
                        options = options3;
                        showWithAvatarPreview = showWithAvatarPreview2;
                        participant = participant3;
                    } else {
                        volumeSlider2 = volumeSlider;
                        popupLayout = popupLayout2;
                        TLRPC.ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
                        if (chatFull == null || chatFull.participants == null) {
                            scrollView2 = scrollView;
                            options = options3;
                            showWithAvatarPreview = showWithAvatarPreview2;
                            participant = participant3;
                        } else {
                            int a = 0;
                            int N = chatFull.participants.participants.size();
                            while (true) {
                                if (a >= N) {
                                    scrollView2 = scrollView;
                                    options = options3;
                                    showWithAvatarPreview = showWithAvatarPreview2;
                                    participant = participant3;
                                    break;
                                }
                                TLRPC.ChatParticipant chatParticipant = chatFull.participants.participants.get(a);
                                scrollView2 = scrollView;
                                options = options3;
                                TLRPC.ChatFull chatFull2 = chatFull;
                                showWithAvatarPreview = showWithAvatarPreview2;
                                participant = participant3;
                                if (chatParticipant.user_id != participant3.peer.user_id) {
                                    a++;
                                    scrollView = scrollView2;
                                    options3 = options;
                                    chatFull = chatFull2;
                                    showWithAvatarPreview2 = showWithAvatarPreview;
                                    participant3 = participant;
                                } else {
                                    isAdmin2 = (chatParticipant instanceof TLRPC.TL_chatParticipantAdmin) || (chatParticipant instanceof TLRPC.TL_chatParticipantCreator);
                                }
                            }
                        }
                        isAdmin = isAdmin2;
                    }
                } else {
                    options = options3;
                    volumeSlider2 = volumeSlider;
                    showWithAvatarPreview = showWithAvatarPreview2;
                    participant = participant3;
                    popupLayout = popupLayout2;
                    scrollView2 = scrollView;
                    isAdmin = peerId == (-this.currentChat.id);
                }
                if (view.isSelfUser()) {
                    if (!view.isHandRaised()) {
                        options2 = options;
                    } else {
                        items.add(LocaleController.getString("VoipGroupCancelRaiseHand", R.string.VoipGroupCancelRaiseHand));
                        icons.add(Integer.valueOf((int) R.drawable.msg_handdown));
                        options2 = options;
                        options2.add(7);
                    }
                    if (view.hasAvatarSet()) {
                        i = R.string.VoipAddPhoto;
                        str = "VoipAddPhoto";
                    } else {
                        i = R.string.VoipSetNewPhoto;
                        str = "VoipSetNewPhoto";
                    }
                    items.add(LocaleController.getString(str, i));
                    icons.add(Integer.valueOf((int) R.drawable.msg_addphoto));
                    options2.add(9);
                    if (peerId > 0) {
                        participant2 = participant;
                        if (TextUtils.isEmpty(participant2.about)) {
                            i4 = R.string.VoipAddBio;
                            str4 = "VoipAddBio";
                        } else {
                            i4 = R.string.VoipEditBio;
                            str4 = "VoipEditBio";
                        }
                        items.add(LocaleController.getString(str4, i4));
                    } else {
                        participant2 = participant;
                        if (TextUtils.isEmpty(participant2.about)) {
                            i3 = R.string.VoipAddDescription;
                            str3 = "VoipAddDescription";
                        } else {
                            i3 = R.string.VoipEditDescription;
                            str3 = "VoipEditDescription";
                        }
                        items.add(LocaleController.getString(str3, i3));
                    }
                    icons.add(Integer.valueOf(TextUtils.isEmpty(participant2.about) ? R.drawable.msg_addbio : R.drawable.msg_info));
                    options2.add(10);
                    if (peerId > 0) {
                        i2 = R.string.VoipEditName;
                        str2 = "VoipEditName";
                    } else {
                        i2 = R.string.VoipEditTitle;
                        str2 = "VoipEditTitle";
                    }
                    items.add(LocaleController.getString(str2, i2));
                    icons.add(Integer.valueOf((int) R.drawable.msg_edit));
                    options2.add(11);
                    scrollView3 = scrollView2;
                } else {
                    options2 = options;
                    participant2 = participant;
                    if (ChatObject.canManageCalls(this.currentChat)) {
                        if (!isAdmin || !participant2.muted) {
                            if (!participant2.muted) {
                                scrollView3 = scrollView2;
                            } else if (participant2.can_self_unmute) {
                                scrollView3 = scrollView2;
                            } else {
                                items.add(LocaleController.getString("VoipGroupAllowToSpeak", R.string.VoipGroupAllowToSpeak));
                                scrollView3 = scrollView2;
                                if (participant2.raise_hand_rating != 0) {
                                    icons.add(Integer.valueOf((int) R.drawable.msg_allowspeak));
                                } else {
                                    icons.add(Integer.valueOf((int) R.drawable.msg_voice_unmuted));
                                }
                                options2.add(1);
                            }
                            items.add(LocaleController.getString("VoipGroupMute", R.string.VoipGroupMute));
                            icons.add(Integer.valueOf((int) R.drawable.msg_voice_muted));
                            options2.add(0);
                        } else {
                            scrollView3 = scrollView2;
                        }
                        if (participant2.peer.channel_id != 0 && !ChatObject.isMegagroup(this.currentAccount, participant2.peer.channel_id)) {
                            items.add(LocaleController.getString("VoipGroupOpenChannel", R.string.VoipGroupOpenChannel));
                            icons.add(Integer.valueOf((int) R.drawable.msg_channel));
                            options2.add(8);
                        } else {
                            items.add(LocaleController.getString("VoipGroupOpenProfile", R.string.VoipGroupOpenProfile));
                            icons.add(Integer.valueOf((int) R.drawable.msg_openprofile));
                            options2.add(6);
                        }
                        if (!isAdmin && ChatObject.canBlockUsers(this.currentChat)) {
                            items.add(LocaleController.getString("VoipGroupUserRemove", R.string.VoipGroupUserRemove));
                            icons.add(Integer.valueOf((int) R.drawable.msg_block2));
                            options2.add(2);
                        }
                    } else {
                        scrollView3 = scrollView2;
                        if (participant2.muted_by_you) {
                            items.add(LocaleController.getString("VoipGroupUnmuteForMe", R.string.VoipGroupUnmuteForMe));
                            icons.add(Integer.valueOf((int) R.drawable.msg_voice_unmuted));
                            options2.add(4);
                        } else {
                            items.add(LocaleController.getString("VoipGroupMuteForMe", R.string.VoipGroupMuteForMe));
                            icons.add(Integer.valueOf((int) R.drawable.msg_voice_muted));
                            options2.add(5);
                        }
                        if (participant2.peer.channel_id != 0 && !ChatObject.isMegagroup(this.currentAccount, participant2.peer.channel_id)) {
                            items.add(LocaleController.getString("VoipGroupOpenChannel", R.string.VoipGroupOpenChannel));
                            icons.add(Integer.valueOf((int) R.drawable.msg_msgbubble3));
                            options2.add(8);
                        } else {
                            items.add(LocaleController.getString("VoipGroupOpenChat", R.string.VoipGroupOpenChat));
                            icons.add(Integer.valueOf((int) R.drawable.msg_msgbubble3));
                            options2.add(6);
                        }
                    }
                }
                int a2 = 0;
                int N2 = items.size();
                while (a2 < N2) {
                    boolean isAdmin3 = isAdmin;
                    ActionBarMenuSubItem cell = new ActionBarMenuSubItem(getContext(), a2 == 0, a2 == N2 + (-1));
                    if (options2.get(a2).intValue() != 2) {
                        cell.setColors(Theme.getColor(Theme.key_voipgroup_actionBarItems), Theme.getColor(Theme.key_voipgroup_actionBarItems));
                    } else {
                        cell.setColors(Theme.getColor(Theme.key_voipgroup_leaveCallMenu), Theme.getColor(Theme.key_voipgroup_leaveCallMenu));
                    }
                    cell.setSelectorColor(Theme.getColor(Theme.key_voipgroup_listSelector));
                    cell.setTextAndIcon(items.get(a2), icons.get(a2).intValue());
                    buttonsLayout.addView(cell);
                    final int i5 = a2;
                    cell.setTag(options2.get(a2));
                    cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda13
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view4) {
                            GroupCallActivity.this.m3506lambda$showMenuForCell$60$orgtelegramuiGroupCallActivity(i5, options2, participant2, view4);
                        }
                    });
                    a2++;
                    isAdmin = isAdmin3;
                }
                scrollView3.addView(linearLayout, LayoutHelper.createScroll(-2, -2, 51));
                this.listView.stopScroll();
                this.layoutManager.setCanScrollVertically(false);
                this.scrimView = view;
                view.setAboutVisible(true);
                this.containerView.invalidate();
                this.listView.invalidate();
                AnimatorSet animatorSet = this.scrimAnimatorSet;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout3 = popupLayout;
                this.scrimPopupLayout = popupLayout3;
                if (peerId > 0) {
                    TLRPC.User currentUser = this.accountInstance.getMessagesController().getUser(Long.valueOf(peerId));
                    imageLocation = ImageLocation.getForUserOrChat(currentUser, 0);
                    thumbLocation = ImageLocation.getForUserOrChat(currentUser, 1);
                } else {
                    TLRPC.Chat currentChat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-peerId));
                    imageLocation = ImageLocation.getForUserOrChat(currentChat, 0);
                    thumbLocation = ImageLocation.getForUserOrChat(currentChat, 1);
                }
                GroupCallMiniTextureView groupCallMiniTextureView = this.scrimRenderer;
                boolean hasAttachedRenderer = groupCallMiniTextureView != null && groupCallMiniTextureView.isAttached();
                if (imageLocation == null && !hasAttachedRenderer) {
                    showWithAvatarPreview = false;
                } else if (showWithAvatarPreview) {
                    this.avatarsViewPager.setParentAvatarImage(this.scrimView.getAvatarImageView());
                    this.avatarsViewPager.setHasActiveVideo(hasAttachedRenderer);
                    this.avatarsViewPager.setData(peerId, true);
                    this.avatarsViewPager.setCreateThumbFromParent(true);
                    this.avatarsViewPager.initIfEmpty(imageLocation, thumbLocation, true);
                    GroupCallMiniTextureView groupCallMiniTextureView2 = this.scrimRenderer;
                    if (groupCallMiniTextureView2 != null) {
                        groupCallMiniTextureView2.setShowingAsScrimView(true, true);
                    }
                    if (MessageObject.getPeerId(this.selfPeer) == peerId && this.currentAvatarUpdater != null && (avatarUpdaterDelegate = this.avatarUpdaterDelegate) != null && avatarUpdaterDelegate.avatar != null) {
                        this.avatarsViewPager.addUploadingImage(this.avatarUpdaterDelegate.uploadingImageLocation, ImageLocation.getForLocal(this.avatarUpdaterDelegate.avatar));
                    }
                }
                if (showWithAvatarPreview) {
                    this.avatarsPreviewShowed = true;
                    popupLayout3.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                    this.containerView.addView(this.scrimPopupLayout, LayoutHelper.createFrame(-2, -2.0f));
                    this.useBlur = true;
                    prepareBlurBitmap();
                    this.avatarPriviewTransitionInProgress = true;
                    this.avatarPreviewContainer.setVisibility(0);
                    if (volumeSlider2 != null) {
                        volumeSlider2.invalidate();
                    }
                    runAvatarPreviewTransition(true, view);
                    GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.scrimFullscreenView;
                    if (groupCallUserCell != null) {
                        groupCallUserCell.getAvatarImageView().setAlpha(0.0f);
                        return true;
                    }
                    return true;
                }
                this.avatarsPreviewShowed = false;
                ActionBarPopupWindow actionBarPopupWindow2 = new ActionBarPopupWindow(popupLayout3, -2, -2) { // from class: org.telegram.ui.GroupCallActivity.56
                    @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow, android.widget.PopupWindow
                    public void dismiss() {
                        super.dismiss();
                        if (GroupCallActivity.this.scrimPopupWindow == this) {
                            GroupCallActivity.this.scrimPopupWindow = null;
                            if (GroupCallActivity.this.scrimAnimatorSet != null) {
                                GroupCallActivity.this.scrimAnimatorSet.cancel();
                                GroupCallActivity.this.scrimAnimatorSet = null;
                            }
                            GroupCallActivity.this.layoutManager.setCanScrollVertically(true);
                            GroupCallActivity.this.scrimAnimatorSet = new AnimatorSet();
                            ArrayList<Animator> animators = new ArrayList<>();
                            animators.add(ObjectAnimator.ofInt(GroupCallActivity.this.scrimPaint, AnimationProperties.PAINT_ALPHA, 0));
                            GroupCallActivity.this.scrimAnimatorSet.playTogether(animators);
                            GroupCallActivity.this.scrimAnimatorSet.setDuration(220L);
                            GroupCallActivity.this.scrimAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.56.1
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    GroupCallActivity.this.clearScrimView();
                                    GroupCallActivity.this.containerView.invalidate();
                                    GroupCallActivity.this.listView.invalidate();
                                    if (GroupCallActivity.this.delayedGroupCallUpdated) {
                                        GroupCallActivity.this.delayedGroupCallUpdated = false;
                                        GroupCallActivity.this.applyCallParticipantUpdates(true);
                                    }
                                }
                            });
                            GroupCallActivity.this.scrimAnimatorSet.start();
                        }
                    }
                };
                this.scrimPopupWindow = actionBarPopupWindow2;
                actionBarPopupWindow2.setPauseNotifications(true);
                this.scrimPopupWindow.setDismissAnimationDuration(220);
                this.scrimPopupWindow.setOutsideTouchable(true);
                this.scrimPopupWindow.setClippingEnabled(true);
                this.scrimPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
                this.scrimPopupWindow.setFocusable(true);
                popupLayout3.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                this.scrimPopupWindow.setInputMethodMode(2);
                this.scrimPopupWindow.setSoftInputMode(0);
                this.scrimPopupWindow.getContentView().setFocusableInTouchMode(true);
                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell2 = this.scrimFullscreenView;
                if (groupCallUserCell2 == null) {
                    popupX = (int) (((this.listView.getX() + this.listView.getMeasuredWidth()) + AndroidUtilities.dp(8.0f)) - popupLayout3.getMeasuredWidth());
                    popupY = this.hasScrimAnchorView ? (int) (this.listView.getY() + view.getY() + view.getClipHeight()) : this.scrimGridView != null ? (int) (this.listView.getY() + this.scrimGridView.getY() + this.scrimGridView.getMeasuredHeight()) : (int) this.listView.getY();
                } else if (isLandscapeMode) {
                    popupX = (((int) ((groupCallUserCell2.getX() + this.fullscreenUsersListView.getX()) + this.renderersContainer.getX())) - popupLayout3.getMeasuredWidth()) + AndroidUtilities.dp(32.0f);
                    popupY = ((int) ((this.scrimFullscreenView.getY() + this.fullscreenUsersListView.getY()) + this.renderersContainer.getY())) - AndroidUtilities.dp(6.0f);
                } else {
                    popupX = ((int) ((groupCallUserCell2.getX() + this.fullscreenUsersListView.getX()) + this.renderersContainer.getX())) - AndroidUtilities.dp(14.0f);
                    popupY = (int) (((this.scrimFullscreenView.getY() + this.fullscreenUsersListView.getY()) + this.renderersContainer.getY()) - popupLayout3.getMeasuredHeight());
                }
                this.scrimPopupWindow.showAtLocation(this.listView, 51, popupX, popupY);
                this.scrimAnimatorSet = new AnimatorSet();
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofInt(this.scrimPaint, AnimationProperties.PAINT_ALPHA, 0, 100));
                this.scrimAnimatorSet.playTogether(animators);
                this.scrimAnimatorSet.setDuration(150L);
                this.scrimAnimatorSet.start();
                return true;
            }
            z = true;
        } else {
            z = true;
        }
        dismissAvatarPreview(z);
        return false;
    }

    /* renamed from: lambda$showMenuForCell$59$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3505lambda$showMenuForCell$59$orgtelegramuiGroupCallActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.scrimPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.scrimPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$showMenuForCell$60$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3506lambda$showMenuForCell$60$orgtelegramuiGroupCallActivity(int i, ArrayList options, TLRPC.TL_groupCallParticipant participant, View v1) {
        if (i >= options.size()) {
            return;
        }
        TLRPC.TL_groupCallParticipant participant1 = this.call.participants.get(MessageObject.getPeerId(participant.peer));
        if (participant1 == null) {
            participant1 = participant;
        }
        processSelectedOption(participant1, MessageObject.getPeerId(participant1.peer), ((Integer) options.get(i)).intValue());
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        } else if (((Integer) options.get(i)).intValue() != 9 && ((Integer) options.get(i)).intValue() != 10 && ((Integer) options.get(i)).intValue() != 11) {
            dismissAvatarPreview(true);
        }
    }

    public void clearScrimView() {
        GroupCallMiniTextureView groupCallMiniTextureView = this.scrimRenderer;
        if (groupCallMiniTextureView != null) {
            groupCallMiniTextureView.textureView.setRoundCorners(AndroidUtilities.dp(8.0f));
            this.scrimRenderer.setShowingAsScrimView(false, false);
            this.scrimRenderer.invalidate();
            this.renderersContainer.invalidate();
        }
        GroupCallUserCell groupCallUserCell = this.scrimView;
        if (groupCallUserCell != null && !this.hasScrimAnchorView && groupCallUserCell.getParent() != null) {
            this.containerView.removeView(this.scrimView);
        }
        GroupCallUserCell groupCallUserCell2 = this.scrimView;
        if (groupCallUserCell2 != null) {
            groupCallUserCell2.setProgressToAvatarPreview(0.0f);
            this.scrimView.setAboutVisible(false);
            this.scrimView.getAvatarImageView().setAlpha(1.0f);
        }
        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell3 = this.scrimFullscreenView;
        if (groupCallUserCell3 != null) {
            groupCallUserCell3.getAvatarImageView().setAlpha(1.0f);
        }
        this.scrimView = null;
        this.scrimGridView = null;
        this.scrimFullscreenView = null;
        this.scrimRenderer = null;
    }

    private void startScreenCapture() {
        if (this.parentActivity == null || Build.VERSION.SDK_INT < 21) {
            return;
        }
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) this.parentActivity.getSystemService("media_projection");
        this.parentActivity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), LaunchActivity.SCREEN_CAPTURE_REQUEST_CODE);
    }

    private void runAvatarPreviewTransition(final boolean enter, GroupCallUserCell view) {
        float fromScale;
        float fromY;
        float fromX;
        final int fromRadius;
        GroupCallMiniTextureView groupCallMiniTextureView;
        int fromRadius2;
        float fromScale2;
        float fromY2;
        float fromX2;
        GroupCallMiniTextureView groupCallMiniTextureView2;
        boolean z;
        float left = AndroidUtilities.dp(14.0f) + this.containerView.getPaddingLeft();
        float top = AndroidUtilities.dp(14.0f) + this.containerView.getPaddingTop();
        if (this.hasScrimAnchorView) {
            float fromScale3 = view.getAvatarImageView().getMeasuredHeight() / this.listView.getMeasuredWidth();
            fromX = ((view.getAvatarImageView().getX() + view.getX()) + this.listView.getX()) - left;
            fromY = ((view.getAvatarImageView().getY() + view.getY()) + this.listView.getY()) - top;
            fromScale = fromScale3;
            fromRadius = (int) ((view.getAvatarImageView().getMeasuredHeight() >> 1) / fromScale3);
        } else {
            if (this.scrimRenderer == null) {
                this.previewTextureTransitionEnabled = true;
            } else {
                if (!enter) {
                    ProfileGalleryView profileGalleryView = this.avatarsViewPager;
                    if (profileGalleryView.getRealPosition(profileGalleryView.getCurrentItem()) != 0) {
                        z = false;
                        this.previewTextureTransitionEnabled = z;
                    }
                }
                z = true;
                this.previewTextureTransitionEnabled = z;
            }
            GroupCallGridCell groupCallGridCell = this.scrimGridView;
            if (groupCallGridCell != null && this.previewTextureTransitionEnabled) {
                fromX2 = (groupCallGridCell.getX() + this.listView.getX()) - left;
                fromY2 = ((this.scrimGridView.getY() + this.listView.getY()) + AndroidUtilities.dp(2.0f)) - top;
                fromScale2 = 1.0f;
                fromRadius2 = 0;
            } else {
                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.scrimFullscreenView;
                if (groupCallUserCell != null) {
                    if (this.scrimRenderer == null) {
                        fromX2 = (((groupCallUserCell.getAvatarImageView().getX() + this.scrimFullscreenView.getX()) + this.fullscreenUsersListView.getX()) + this.renderersContainer.getX()) - left;
                        fromY2 = (((this.scrimFullscreenView.getAvatarImageView().getY() + this.scrimFullscreenView.getY()) + this.fullscreenUsersListView.getY()) + this.renderersContainer.getY()) - top;
                        fromScale2 = this.scrimFullscreenView.getAvatarImageView().getMeasuredHeight() / this.listView.getMeasuredWidth();
                        fromRadius2 = (int) ((this.scrimFullscreenView.getAvatarImageView().getMeasuredHeight() >> 1) / fromScale2);
                    } else if (this.previewTextureTransitionEnabled) {
                        fromX2 = ((groupCallUserCell.getX() + this.fullscreenUsersListView.getX()) + this.renderersContainer.getX()) - left;
                        fromY2 = ((this.scrimFullscreenView.getY() + this.fullscreenUsersListView.getY()) + this.renderersContainer.getY()) - top;
                        fromScale2 = 1.0f;
                        fromRadius2 = 0;
                    } else {
                        fromX2 = 0.0f;
                        fromY2 = 0.0f;
                        fromScale2 = 0.96f;
                        fromRadius2 = 0;
                    }
                } else {
                    fromX2 = 0.0f;
                    fromY2 = 0.0f;
                    fromScale2 = 0.96f;
                    fromRadius2 = 0;
                }
            }
            if (!this.previewTextureTransitionEnabled && (groupCallMiniTextureView2 = this.scrimRenderer) != null) {
                groupCallMiniTextureView2.invalidate();
                this.renderersContainer.invalidate();
                this.scrimRenderer.setShowingAsScrimView(false, false);
                this.scrimRenderer = null;
            }
            fromX = fromX2;
            fromY = fromY2;
            fromScale = fromScale2;
            fromRadius = fromRadius2;
        }
        float fromX3 = 0.0f;
        if (enter) {
            this.avatarPreviewContainer.setScaleX(fromScale);
            this.avatarPreviewContainer.setScaleY(fromScale);
            this.avatarPreviewContainer.setTranslationX(fromX);
            this.avatarPreviewContainer.setTranslationY(fromY);
            this.avatarPagerIndicator.setAlpha(0.0f);
        }
        this.avatarsViewPager.setRoundRadius(fromRadius, fromRadius);
        if (this.useBlur) {
            if (enter) {
                this.blurredView.setAlpha(0.0f);
            }
            ViewPropertyAnimator animate = this.blurredView.animate();
            if (enter) {
                fromX3 = 1.0f;
            }
            animate.alpha(fromX3).setDuration(220L).start();
        }
        this.avatarPagerIndicator.animate().alpha(enter ? 1.0f : 0.0f).setDuration(220L).start();
        if (!enter && (groupCallMiniTextureView = this.scrimRenderer) != null) {
            groupCallMiniTextureView.setShowingAsScrimView(false, true);
            ProfileGalleryView profileGalleryView2 = this.avatarsViewPager;
            if (profileGalleryView2.getRealPosition(profileGalleryView2.getCurrentItem()) != 0) {
                this.scrimRenderer.textureView.cancelAnimation();
                this.scrimGridView = null;
            }
        }
        float[] fArr = new float[2];
        fArr[0] = enter ? 0.0f : 1.0f;
        fArr[1] = enter ? 1.0f : 0.0f;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fArr);
        final float f = fromScale;
        final float f2 = fromX;
        final float f3 = fromY;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda22
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                GroupCallActivity.this.m3504x7ab4ea1b(f, f2, f3, fromRadius, valueAnimator2);
            }
        });
        this.popupAnimationIndex = this.accountInstance.getNotificationCenter().setAnimationInProgress(this.popupAnimationIndex, new int[]{NotificationCenter.dialogPhotosLoaded, NotificationCenter.fileLoaded, NotificationCenter.messagesDidLoad});
        final GroupCallMiniTextureView videoRenderer = this.scrimGridView == null ? null : this.scrimRenderer;
        if (videoRenderer != null) {
            videoRenderer.animateToScrimView = true;
        }
        valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.57
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                GroupCallMiniTextureView groupCallMiniTextureView3 = videoRenderer;
                if (groupCallMiniTextureView3 != null) {
                    groupCallMiniTextureView3.animateToScrimView = false;
                }
                GroupCallActivity.this.accountInstance.getNotificationCenter().onAnimationFinish(GroupCallActivity.this.popupAnimationIndex);
                GroupCallActivity.this.avatarPriviewTransitionInProgress = false;
                GroupCallActivity.this.progressToAvatarPreview = enter ? 1.0f : 0.0f;
                GroupCallActivity.this.renderersContainer.progressToScrimView = GroupCallActivity.this.progressToAvatarPreview;
                if (!enter) {
                    GroupCallActivity.this.scrimPaint.setAlpha(0);
                    GroupCallActivity.this.clearScrimView();
                    if (GroupCallActivity.this.scrimPopupLayout.getParent() != null) {
                        GroupCallActivity.this.containerView.removeView(GroupCallActivity.this.scrimPopupLayout);
                    }
                    GroupCallActivity.this.scrimPopupLayout = null;
                    GroupCallActivity.this.avatarPreviewContainer.setVisibility(8);
                    GroupCallActivity.this.avatarsPreviewShowed = false;
                    GroupCallActivity.this.layoutManager.setCanScrollVertically(true);
                    GroupCallActivity.this.blurredView.setVisibility(8);
                    if (GroupCallActivity.this.delayedGroupCallUpdated) {
                        GroupCallActivity.this.delayedGroupCallUpdated = false;
                        GroupCallActivity.this.applyCallParticipantUpdates(true);
                    }
                    if (GroupCallActivity.this.scrimRenderer != null) {
                        GroupCallActivity.this.scrimRenderer.textureView.setRoundCorners(0.0f);
                    }
                } else {
                    GroupCallActivity.this.avatarPreviewContainer.setAlpha(1.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setScaleX(1.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setScaleY(1.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setTranslationX(0.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setTranslationY(0.0f);
                }
                GroupCallActivity.this.checkContentOverlayed();
                GroupCallActivity.this.containerView.invalidate();
                GroupCallActivity.this.avatarsViewPager.invalidate();
                GroupCallActivity.this.listView.invalidate();
            }
        });
        if (!this.hasScrimAnchorView && this.scrimRenderer != null) {
            valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            valueAnimator.setDuration(220L);
            this.scrimRenderer.textureView.setAnimateNextDuration(220L);
            this.scrimRenderer.textureView.synchOrRunAnimation(valueAnimator);
        } else {
            valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            valueAnimator.setDuration(220L);
            valueAnimator.start();
        }
        checkContentOverlayed();
    }

    /* renamed from: lambda$runAvatarPreviewTransition$61$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3504x7ab4ea1b(float fromScale, float fromX, float fromY, int fromRadius, ValueAnimator valueAnimator1) {
        float floatValue = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        this.progressToAvatarPreview = floatValue;
        this.renderersContainer.progressToScrimView = floatValue;
        float f = this.progressToAvatarPreview;
        float s = ((1.0f - f) * fromScale) + (f * 1.0f);
        this.avatarPreviewContainer.setScaleX(s);
        this.avatarPreviewContainer.setScaleY(s);
        this.avatarPreviewContainer.setTranslationX((1.0f - this.progressToAvatarPreview) * fromX);
        this.avatarPreviewContainer.setTranslationY((1.0f - this.progressToAvatarPreview) * fromY);
        if (!this.useBlur) {
            this.scrimPaint.setAlpha((int) (this.progressToAvatarPreview * 100.0f));
        }
        GroupCallMiniTextureView groupCallMiniTextureView = this.scrimRenderer;
        if (groupCallMiniTextureView != null) {
            groupCallMiniTextureView.textureView.setRoundCorners(AndroidUtilities.dp(8.0f) * (1.0f - this.progressToAvatarPreview));
        }
        this.avatarPreviewContainer.invalidate();
        this.containerView.invalidate();
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        float f2 = this.progressToAvatarPreview;
        profileGalleryView.setRoundRadius((int) (fromRadius * (1.0f - f2)), (int) (fromRadius * (1.0f - f2)));
    }

    public void dismissAvatarPreview(boolean animated) {
        if (this.avatarPriviewTransitionInProgress || !this.avatarsPreviewShowed) {
            return;
        }
        if (animated) {
            this.avatarPriviewTransitionInProgress = true;
            runAvatarPreviewTransition(false, this.scrimView);
            return;
        }
        clearScrimView();
        this.containerView.removeView(this.scrimPopupLayout);
        this.scrimPopupLayout = null;
        this.avatarPreviewContainer.setVisibility(8);
        this.containerView.invalidate();
        this.avatarsPreviewShowed = false;
        this.layoutManager.setCanScrollVertically(true);
        this.listView.invalidate();
        this.blurredView.setVisibility(8);
        if (this.delayedGroupCallUpdated) {
            this.delayedGroupCallUpdated = false;
            applyCallParticipantUpdates(true);
        }
        checkContentOverlayed();
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private static final int VIEW_TYPE_CALL_INVITED = 2;
        private static final int VIEW_TYPE_GRID = 4;
        private static final int VIEW_TYPE_INVITE_MEMBERS = 0;
        private static final int VIEW_TYPE_LAST_PADDING = 3;
        private static final int VIEW_TYPE_PARTICIPANT = 1;
        private static final int VIEW_TYPE_VIDEO_GRID_DIVIDER = 5;
        private static final int VIEW_TYPE_VIDEO_NOT_AVAILABLE = 6;
        private int addMemberRow;
        private boolean hasSelfUser;
        private int invitedEndRow;
        private int invitedStartRow;
        private int lastRow;
        private Context mContext;
        private int rowsCount;
        private int usersEndRow;
        private int usersStartRow;
        private int usersVideoGridEndRow;
        private int usersVideoGridStartRow;
        private int videoGridDividerRow;
        private int videoNotAvailableRow;

        public ListAdapter(Context context) {
            GroupCallActivity.this = r1;
            this.mContext = context;
        }

        public boolean addSelfToCounter() {
            if (!GroupCallActivity.this.isRtmpStream() && !this.hasSelfUser && VoIPService.getSharedInstance() != null) {
                return !VoIPService.getSharedInstance().isJoined();
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.rowsCount;
        }

        public void updateRows() {
            if (GroupCallActivity.this.call == null || GroupCallActivity.this.call.isScheduled() || GroupCallActivity.this.delayedGroupCallUpdated) {
                return;
            }
            boolean z = false;
            this.rowsCount = 0;
            if (GroupCallActivity.this.call.participants.indexOfKey(MessageObject.getPeerId(GroupCallActivity.this.selfPeer)) >= 0) {
                z = true;
            }
            this.hasSelfUser = z;
            int i = this.rowsCount;
            this.usersVideoGridStartRow = i;
            int size = i + GroupCallActivity.this.visibleVideoParticipants.size();
            this.rowsCount = size;
            this.usersVideoGridEndRow = size;
            int videoCount = GroupCallActivity.this.visibleVideoParticipants.size();
            if (videoCount > 0) {
                int i2 = this.rowsCount;
                this.rowsCount = i2 + 1;
                this.videoGridDividerRow = i2;
            } else {
                this.videoGridDividerRow = -1;
            }
            if (!GroupCallActivity.this.visibleVideoParticipants.isEmpty() && ChatObject.canManageCalls(GroupCallActivity.this.currentChat) && GroupCallActivity.this.call.call.participants_count > GroupCallActivity.this.accountInstance.getMessagesController().groupCallVideoMaxParticipants) {
                int i3 = this.rowsCount;
                this.rowsCount = i3 + 1;
                this.videoNotAvailableRow = i3;
            } else {
                this.videoNotAvailableRow = -1;
            }
            this.usersStartRow = this.rowsCount;
            if (!GroupCallActivity.this.isRtmpStream()) {
                this.rowsCount += GroupCallActivity.this.call.visibleParticipants.size();
            }
            this.usersEndRow = this.rowsCount;
            if (GroupCallActivity.this.call.invitedUsers.isEmpty() || GroupCallActivity.this.isRtmpStream()) {
                this.invitedStartRow = -1;
                this.invitedEndRow = -1;
            } else {
                int i4 = this.rowsCount;
                this.invitedStartRow = i4;
                int size2 = i4 + GroupCallActivity.this.call.invitedUsers.size();
                this.rowsCount = size2;
                this.invitedEndRow = size2;
            }
            if (!GroupCallActivity.this.isRtmpStream() && (((!ChatObject.isChannel(GroupCallActivity.this.currentChat) || GroupCallActivity.this.currentChat.megagroup) && ChatObject.canWriteToChat(GroupCallActivity.this.currentChat)) || (ChatObject.isChannel(GroupCallActivity.this.currentChat) && !GroupCallActivity.this.currentChat.megagroup && !TextUtils.isEmpty(GroupCallActivity.this.currentChat.username)))) {
                int i5 = this.rowsCount;
                this.rowsCount = i5 + 1;
                this.addMemberRow = i5;
            } else {
                this.addMemberRow = -1;
            }
            int i6 = this.rowsCount;
            this.rowsCount = i6 + 1;
            this.lastRow = i6;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int position) {
            updateRows();
            super.notifyItemChanged(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int position, Object payload) {
            updateRows();
            super.notifyItemChanged(position, payload);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemInserted(int position) {
            updateRows();
            super.notifyItemInserted(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            super.notifyItemMoved(fromPosition, toPosition);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRemoved(int position) {
            updateRows();
            super.notifyItemRemoved(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            boolean z = false;
            String key = Theme.key_voipgroup_mutedIcon;
            if (type == 1) {
                GroupCallUserCell cell = (GroupCallUserCell) holder.itemView;
                if (GroupCallActivity.this.actionBar.getTag() == null) {
                    key = Theme.key_voipgroup_mutedIconUnscrolled;
                }
                cell.setGrayIconColor(key, Theme.getColor(key));
                if (holder.getAdapterPosition() != getItemCount() - 2) {
                    z = true;
                }
                cell.setDrawDivider(z);
            } else if (type == 2) {
                GroupCallInvitedCell cell2 = (GroupCallInvitedCell) holder.itemView;
                if (GroupCallActivity.this.actionBar.getTag() == null) {
                    key = Theme.key_voipgroup_mutedIconUnscrolled;
                }
                cell2.setGrayIconColor(key, Theme.getColor(key));
                if (holder.getAdapterPosition() != getItemCount() - 2) {
                    z = true;
                }
                cell2.setDrawDivider(z);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.TL_groupCallParticipant participant;
            Long uid;
            ChatObject.VideoParticipant participant2;
            ChatObject.VideoParticipant participant3;
            TLRPC.FileLocation uploadingAvatar = null;
            float uploadingProgress = 1.0f;
            boolean animated = false;
            switch (holder.getItemViewType()) {
                case 0:
                    GroupCallTextCell textCell = (GroupCallTextCell) holder.itemView;
                    int color = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_lastSeenTextUnscrolled), Theme.getColor(Theme.key_voipgroup_lastSeenText), GroupCallActivity.this.actionBar.getTag() != null ? 1.0f : 0.0f, 1.0f);
                    textCell.setColors(color, color);
                    if (ChatObject.isChannel(GroupCallActivity.this.currentChat) && !GroupCallActivity.this.currentChat.megagroup && !TextUtils.isEmpty(GroupCallActivity.this.currentChat.username)) {
                        textCell.setTextAndIcon(LocaleController.getString("VoipGroupShareLink", R.string.VoipGroupShareLink), R.drawable.msg_link, false);
                        return;
                    } else {
                        textCell.setTextAndIcon(LocaleController.getString("VoipGroupInviteMember", R.string.VoipGroupInviteMember), R.drawable.msg_contact_add, false);
                        return;
                    }
                case 1:
                    GroupCallUserCell userCell = (GroupCallUserCell) holder.itemView;
                    int row = position - this.usersStartRow;
                    if (GroupCallActivity.this.delayedGroupCallUpdated) {
                        if (row >= 0 && row < GroupCallActivity.this.oldParticipants.size()) {
                            participant = (TLRPC.TL_groupCallParticipant) GroupCallActivity.this.oldParticipants.get(row);
                        } else {
                            participant = null;
                        }
                    } else if (row >= 0 && row < GroupCallActivity.this.call.visibleParticipants.size()) {
                        participant = GroupCallActivity.this.call.visibleParticipants.get(row);
                    } else {
                        participant = null;
                    }
                    if (participant != null) {
                        long peerId = MessageObject.getPeerId(participant.peer);
                        long selfPeerId = MessageObject.getPeerId(GroupCallActivity.this.selfPeer);
                        if (peerId == selfPeerId && GroupCallActivity.this.avatarUpdaterDelegate != null) {
                            uploadingAvatar = GroupCallActivity.this.avatarUpdaterDelegate.avatar;
                        }
                        if (uploadingAvatar != null) {
                            uploadingProgress = GroupCallActivity.this.avatarUpdaterDelegate.uploadingProgress;
                        }
                        if (userCell.getParticipant() != null && MessageObject.getPeerId(userCell.getParticipant().peer) == peerId) {
                            animated = true;
                        }
                        userCell.setData(GroupCallActivity.this.accountInstance, participant, GroupCallActivity.this.call, selfPeerId, uploadingAvatar, animated);
                        userCell.setUploadProgress(uploadingProgress, animated);
                        return;
                    }
                    return;
                case 2:
                    GroupCallInvitedCell invitedCell = (GroupCallInvitedCell) holder.itemView;
                    int row2 = position - this.invitedStartRow;
                    if (GroupCallActivity.this.delayedGroupCallUpdated) {
                        if (row2 >= 0 && row2 < GroupCallActivity.this.oldInvited.size()) {
                            uid = (Long) GroupCallActivity.this.oldInvited.get(row2);
                        } else {
                            uid = null;
                        }
                    } else if (row2 >= 0 && row2 < GroupCallActivity.this.call.invitedUsers.size()) {
                        uid = GroupCallActivity.this.call.invitedUsers.get(row2);
                    } else {
                        uid = null;
                    }
                    if (uid != null) {
                        invitedCell.setData(GroupCallActivity.this.currentAccount, uid);
                        return;
                    }
                    return;
                case 3:
                default:
                    return;
                case 4:
                    GroupCallGridCell userCell2 = (GroupCallGridCell) holder.itemView;
                    ChatObject.VideoParticipant oldParticipant = userCell2.getParticipant();
                    int row3 = position - this.usersVideoGridStartRow;
                    userCell2.spanCount = GroupCallActivity.this.spanSizeLookup.getSpanSize(position);
                    if (GroupCallActivity.this.delayedGroupCallUpdated) {
                        if (row3 >= 0 && row3 < GroupCallActivity.this.oldVideoParticipants.size()) {
                            participant2 = (ChatObject.VideoParticipant) GroupCallActivity.this.oldVideoParticipants.get(row3);
                        } else {
                            participant2 = null;
                        }
                    } else if (row3 >= 0 && row3 < GroupCallActivity.this.visibleVideoParticipants.size()) {
                        participant2 = GroupCallActivity.this.visibleVideoParticipants.get(row3);
                    } else {
                        participant2 = null;
                    }
                    if (participant2 == null) {
                        participant3 = participant2;
                    } else {
                        long peerId2 = MessageObject.getPeerId(participant2.participant.peer);
                        long selfPeerId2 = MessageObject.getPeerId(GroupCallActivity.this.selfPeer);
                        if (peerId2 == selfPeerId2 && GroupCallActivity.this.avatarUpdaterDelegate != null) {
                            uploadingAvatar = GroupCallActivity.this.avatarUpdaterDelegate.avatar;
                        }
                        if (uploadingAvatar != null) {
                            float f = GroupCallActivity.this.avatarUpdaterDelegate.uploadingProgress;
                        }
                        boolean z = userCell2.getParticipant() != null && userCell2.getParticipant().equals(participant2);
                        participant3 = participant2;
                        userCell2.setData(GroupCallActivity.this.accountInstance, participant2, GroupCallActivity.this.call, selfPeerId2);
                    }
                    if (oldParticipant != null && !oldParticipant.equals(participant3) && userCell2.attached && userCell2.getRenderer() != null) {
                        GroupCallActivity.this.attachRenderer(userCell2, false);
                        GroupCallActivity.this.attachRenderer(userCell2, true);
                        return;
                    }
                    return;
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return (type == 3 || type == 4 || type == 5 || type == 6) ? false : true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GroupCallTextCell(this.mContext) { // from class: org.telegram.ui.GroupCallActivity.ListAdapter.1
                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // org.telegram.ui.Cells.GroupCallTextCell, android.widget.FrameLayout, android.view.View
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            if (AndroidUtilities.isTablet()) {
                                int w = Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(widthMeasureSpec));
                                super.onMeasure(View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                                return;
                            }
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        }
                    };
                    break;
                case 1:
                    view = new GroupCallUserCell(this.mContext) { // from class: org.telegram.ui.GroupCallActivity.ListAdapter.2
                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // org.telegram.ui.Cells.GroupCallUserCell
                        public void onMuteClick(GroupCallUserCell cell) {
                            GroupCallActivity.this.showMenuForCell(cell);
                        }

                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // org.telegram.ui.Cells.GroupCallUserCell, android.widget.FrameLayout, android.view.View
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            if (AndroidUtilities.isTablet()) {
                                int w = Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(widthMeasureSpec));
                                super.onMeasure(View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                                return;
                            }
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        }
                    };
                    break;
                case 2:
                    view = new GroupCallInvitedCell(this.mContext) { // from class: org.telegram.ui.GroupCallActivity.ListAdapter.3
                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // org.telegram.ui.Cells.GroupCallInvitedCell, android.widget.FrameLayout, android.view.View
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            if (AndroidUtilities.isTablet()) {
                                int w = Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(widthMeasureSpec));
                                super.onMeasure(View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                                return;
                            }
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        }
                    };
                    break;
                case 3:
                default:
                    view = new View(this.mContext);
                    break;
                case 4:
                    view = new GroupCallGridCell(this.mContext, false) { // from class: org.telegram.ui.GroupCallActivity.ListAdapter.4
                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // org.telegram.ui.Components.voip.GroupCallGridCell, android.view.ViewGroup, android.view.View
                        public void onAttachedToWindow() {
                            super.onAttachedToWindow();
                            if (GroupCallActivity.this.listView.getVisibility() == 0 && GroupCallActivity.this.listViewVideoVisibility) {
                                GroupCallActivity.this.attachRenderer(this, true);
                            }
                        }

                        /* JADX INFO: Access modifiers changed from: protected */
                        @Override // org.telegram.ui.Components.voip.GroupCallGridCell, android.view.ViewGroup, android.view.View
                        public void onDetachedFromWindow() {
                            super.onDetachedFromWindow();
                            GroupCallActivity.this.attachRenderer(this, false);
                        }
                    };
                    break;
                case 5:
                    view = new View(this.mContext) { // from class: org.telegram.ui.GroupCallActivity.ListAdapter.5
                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(GroupCallActivity.isLandscapeMode ? 0.0f : 8.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    break;
                case 6:
                    TextView textView = new TextView(this.mContext);
                    textView.setTextColor(-8682615);
                    textView.setTextSize(1, 13.0f);
                    textView.setGravity(1);
                    textView.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                    if (ChatObject.isChannelOrGiga(GroupCallActivity.this.currentChat)) {
                        textView.setText(LocaleController.formatString("VoipChannelVideoNotAvailableAdmin", R.string.VoipChannelVideoNotAvailableAdmin, LocaleController.formatPluralString("Participants", GroupCallActivity.this.accountInstance.getMessagesController().groupCallVideoMaxParticipants, new Object[0])));
                    } else {
                        textView.setText(LocaleController.formatString("VoipVideoNotAvailableAdmin", R.string.VoipVideoNotAvailableAdmin, LocaleController.formatPluralString("Members", GroupCallActivity.this.accountInstance.getMessagesController().groupCallVideoMaxParticipants, new Object[0])));
                    }
                    view = textView;
                    break;
            }
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(-1, -2);
            view.setLayoutParams(params);
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == this.lastRow) {
                return 3;
            }
            if (position == this.addMemberRow) {
                return 0;
            }
            if (position == this.videoGridDividerRow) {
                return 5;
            }
            if (position >= this.usersStartRow && position < this.usersEndRow) {
                return 1;
            }
            if (position >= this.usersVideoGridStartRow && position < this.usersVideoGridEndRow) {
                return 4;
            }
            if (position == this.videoNotAvailableRow) {
                return 6;
            }
            return 2;
        }
    }

    public void attachRenderer(GroupCallGridCell cell, boolean attach) {
        if (isDismissed()) {
            return;
        }
        if (attach && cell.getRenderer() == null) {
            cell.setRenderer(GroupCallMiniTextureView.getOrCreate(this.attachedRenderers, this.renderersContainer, cell, null, null, cell.getParticipant(), this.call, this));
        } else if (!attach && cell.getRenderer() != null) {
            cell.getRenderer().setPrimaryView(null);
            cell.setRenderer(null);
        }
    }

    public void setOldRows(int addMemberRow, int usersStartRow, int usersEndRow, int invitedStartRow, int invitedEndRow, int usersVideoStartRow, int usersVideoEndRow, int videoDividerRow, int videoNotAvailableRow) {
        this.oldAddMemberRow = addMemberRow;
        this.oldUsersStartRow = usersStartRow;
        this.oldUsersEndRow = usersEndRow;
        this.oldInvitedStartRow = invitedStartRow;
        this.oldInvitedEndRow = invitedEndRow;
        this.oldUsersVideoStartRow = usersVideoStartRow;
        this.oldUsersVideoEndRow = usersVideoEndRow;
        this.oldVideoDividerRow = videoDividerRow;
        this.oldVideoNotAvailableRow = videoNotAvailableRow;
    }

    /* loaded from: classes4.dex */
    public static class UpdateCallback implements ListUpdateCallback {
        final RecyclerView.Adapter adapter;

        private UpdateCallback(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onInserted(int position, int count) {
            this.adapter.notifyItemRangeInserted(position, count);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onRemoved(int position, int count) {
            this.adapter.notifyItemRangeRemoved(position, count);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onMoved(int fromPosition, int toPosition) {
            this.adapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onChanged(int position, int count, Object payload) {
            this.adapter.notifyItemRangeChanged(position, count, payload);
        }
    }

    public void toggleAdminSpeak() {
        TLRPC.TL_phone_toggleGroupCallSettings req = new TLRPC.TL_phone_toggleGroupCallSettings();
        req.call = this.call.getInputGroupCall();
        req.join_muted = this.call.call.join_muted;
        req.flags |= 1;
        this.accountInstance.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda45
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupCallActivity.this.m3507lambda$toggleAdminSpeak$62$orgtelegramuiGroupCallActivity(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$toggleAdminSpeak$62$org-telegram-ui-GroupCallActivity */
    public /* synthetic */ void m3507lambda$toggleAdminSpeak$62$orgtelegramuiGroupCallActivity(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            this.accountInstance.getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>();
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog != null) {
            privateVideoPreviewDialog.dismiss(false, false);
        } else if (this.avatarsPreviewShowed) {
            dismissAvatarPreview(true);
        } else if (this.renderersContainer.inFullscreenMode) {
            fullscreenFor(null);
        } else {
            super.onBackPressed();
        }
    }

    /* loaded from: classes4.dex */
    public class AvatarUpdaterDelegate implements ImageUpdater.ImageUpdaterDelegate {
        private TLRPC.FileLocation avatar;
        private TLRPC.FileLocation avatarBig;
        private final long peerId;
        private ImageLocation uploadingImageLocation;
        public float uploadingProgress;

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public /* synthetic */ String getInitialSearchString() {
            return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
        }

        private AvatarUpdaterDelegate(long peerId) {
            GroupCallActivity.this = r1;
            this.peerId = peerId;
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public void didUploadPhoto(final TLRPC.InputFile photo, final TLRPC.InputFile video, final double videoStartTimestamp, final String videoPath, final TLRPC.PhotoSize bigSize, final TLRPC.PhotoSize smallSize) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCallActivity.AvatarUpdaterDelegate.this.m3530xfd6c80a1(photo, video, videoStartTimestamp, videoPath, smallSize, bigSize);
                }
            });
        }

        /* renamed from: lambda$didUploadPhoto$3$org-telegram-ui-GroupCallActivity$AvatarUpdaterDelegate */
        public /* synthetic */ void m3530xfd6c80a1(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, final String videoPath, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
            if (photo == null && video == null) {
                this.avatar = smallSize.location;
                TLRPC.FileLocation fileLocation = bigSize.location;
                this.avatarBig = fileLocation;
                this.uploadingImageLocation = ImageLocation.getForLocal(fileLocation);
                GroupCallActivity.this.avatarsViewPager.addUploadingImage(this.uploadingImageLocation, ImageLocation.getForLocal(this.avatar));
                AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
            } else if (this.peerId <= 0) {
                GroupCallActivity.this.accountInstance.getMessagesController().changeChatAvatar(-this.peerId, null, photo, video, videoStartTimestamp, videoPath, smallSize.location, bigSize.location, new Runnable() { // from class: org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        GroupCallActivity.AvatarUpdaterDelegate.this.m3529xd7d877a0();
                    }
                });
            } else {
                TLRPC.TL_photos_uploadProfilePhoto req = new TLRPC.TL_photos_uploadProfilePhoto();
                if (photo != null) {
                    req.file = photo;
                    req.flags |= 1;
                }
                if (video != null) {
                    req.video = video;
                    req.flags |= 2;
                    req.video_start_ts = videoStartTimestamp;
                    req.flags |= 4;
                }
                GroupCallActivity.this.accountInstance.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda3
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        GroupCallActivity.AvatarUpdaterDelegate.this.m3528xb2446e9f(videoPath, tLObject, tL_error);
                    }
                });
            }
        }

        /* renamed from: lambda$didUploadPhoto$1$org-telegram-ui-GroupCallActivity$AvatarUpdaterDelegate */
        public /* synthetic */ void m3528xb2446e9f(final String videoPath, final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCallActivity.AvatarUpdaterDelegate.this.m3527x8cb0659e(error, response, videoPath);
                }
            });
        }

        /* renamed from: lambda$didUploadPhoto$0$org-telegram-ui-GroupCallActivity$AvatarUpdaterDelegate */
        public /* synthetic */ void m3527x8cb0659e(TLRPC.TL_error error, TLObject response, String videoPath) {
            ImageLocation thumb;
            if (this.uploadingImageLocation != null) {
                GroupCallActivity.this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
                this.uploadingImageLocation = null;
            }
            if (error == null) {
                TLRPC.User user = GroupCallActivity.this.accountInstance.getMessagesController().getUser(Long.valueOf(GroupCallActivity.this.accountInstance.getUserConfig().getClientUserId()));
                if (user == null) {
                    user = GroupCallActivity.this.accountInstance.getUserConfig().getCurrentUser();
                    if (user != null) {
                        GroupCallActivity.this.accountInstance.getMessagesController().putUser(user, false);
                    } else {
                        return;
                    }
                } else {
                    GroupCallActivity.this.accountInstance.getUserConfig().setCurrentUser(user);
                }
                TLRPC.TL_photos_photo photos_photo = (TLRPC.TL_photos_photo) response;
                ArrayList<TLRPC.PhotoSize> sizes = photos_photo.photo.sizes;
                TLRPC.PhotoSize small = FileLoader.getClosestPhotoSizeWithSize(sizes, 150);
                TLRPC.PhotoSize big = FileLoader.getClosestPhotoSizeWithSize(sizes, 800);
                TLRPC.VideoSize videoSize = photos_photo.photo.video_sizes.isEmpty() ? null : photos_photo.photo.video_sizes.get(0);
                user.photo = new TLRPC.TL_userProfilePhoto();
                user.photo.photo_id = photos_photo.photo.id;
                if (small != null) {
                    user.photo.photo_small = small.location;
                }
                if (big != null) {
                    user.photo.photo_big = big.location;
                }
                if (small != null && this.avatar != null) {
                    File destFile = FileLoader.getInstance(GroupCallActivity.this.currentAccount).getPathToAttach(small, true);
                    File src = FileLoader.getInstance(GroupCallActivity.this.currentAccount).getPathToAttach(this.avatar, true);
                    src.renameTo(destFile);
                    String oldKey = this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50";
                    String newKey = small.location.volume_id + "_" + small.location.local_id + "@50_50";
                    user = user;
                    ImageLoader.getInstance().replaceImageInCache(oldKey, newKey, ImageLocation.getForUser(user, 1), false);
                }
                if (big != null && this.avatarBig != null) {
                    File destFile2 = FileLoader.getInstance(GroupCallActivity.this.currentAccount).getPathToAttach(big, true);
                    File src2 = FileLoader.getInstance(GroupCallActivity.this.currentAccount).getPathToAttach(this.avatarBig, true);
                    src2.renameTo(destFile2);
                }
                if (videoSize != null && videoPath != null) {
                    File destFile3 = FileLoader.getInstance(GroupCallActivity.this.currentAccount).getPathToAttach(videoSize, "mp4", true);
                    File src3 = new File(videoPath);
                    src3.renameTo(destFile3);
                }
                GroupCallActivity.this.accountInstance.getMessagesStorage().clearUserPhotos(user.id);
                ArrayList<TLRPC.User> users = new ArrayList<>();
                users.add(user);
                GroupCallActivity.this.accountInstance.getMessagesStorage().putUsersAndChats(users, null, false, true);
                TLRPC.User currentUser = GroupCallActivity.this.accountInstance.getMessagesController().getUser(Long.valueOf(this.peerId));
                ImageLocation imageLocation = ImageLocation.getForUser(currentUser, 0);
                ImageLocation thumbLocation = ImageLocation.getForUser(currentUser, 1);
                ImageLocation thumb2 = ImageLocation.getForLocal(this.avatarBig);
                if (thumb2 == null) {
                    thumb = ImageLocation.getForLocal(this.avatar);
                } else {
                    thumb = thumbLocation;
                }
                GroupCallActivity.this.avatarsViewPager.setCreateThumbFromParent(false);
                GroupCallActivity.this.avatarsViewPager.initIfEmpty(imageLocation, thumb, true);
                this.avatar = null;
                this.avatarBig = null;
                AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
                updateAvatarUploadingProgress(1.0f);
            }
            GroupCallActivity.this.accountInstance.getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
            GroupCallActivity.this.accountInstance.getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            GroupCallActivity.this.accountInstance.getUserConfig().saveConfig(true);
        }

        /* renamed from: lambda$didUploadPhoto$2$org-telegram-ui-GroupCallActivity$AvatarUpdaterDelegate */
        public /* synthetic */ void m3529xd7d877a0() {
            ImageLocation thumb;
            if (this.uploadingImageLocation != null) {
                GroupCallActivity.this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
                this.uploadingImageLocation = null;
            }
            TLRPC.Chat currentChat = GroupCallActivity.this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.peerId));
            ImageLocation imageLocation = ImageLocation.getForChat(currentChat, 0);
            ImageLocation thumbLocation = ImageLocation.getForChat(currentChat, 1);
            ImageLocation thumb2 = ImageLocation.getForLocal(this.avatarBig);
            if (thumb2 == null) {
                thumb = ImageLocation.getForLocal(this.avatar);
            } else {
                thumb = thumbLocation;
            }
            GroupCallActivity.this.avatarsViewPager.setCreateThumbFromParent(false);
            GroupCallActivity.this.avatarsViewPager.initIfEmpty(imageLocation, thumb, true);
            this.avatar = null;
            this.avatarBig = null;
            AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
            updateAvatarUploadingProgress(1.0f);
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public void didStartUpload(boolean isVideo) {
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public void onUploadProgressChanged(float progress) {
            GroupCallActivity.this.avatarsViewPager.setUploadProgress(this.uploadingImageLocation, progress);
            updateAvatarUploadingProgress(progress);
        }

        public void updateAvatarUploadingProgress(float progress) {
            this.uploadingProgress = progress;
            if (GroupCallActivity.this.listView == null) {
                return;
            }
            for (int i = 0; i < GroupCallActivity.this.listView.getChildCount(); i++) {
                View child = GroupCallActivity.this.listView.getChildAt(i);
                if (child instanceof GroupCallUserCell) {
                    GroupCallUserCell cell = (GroupCallUserCell) child;
                    if (cell.isSelfUser()) {
                        cell.setUploadProgress(progress, true);
                    }
                }
            }
        }
    }

    public View getScrimView() {
        return this.scrimView;
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onCameraSwitch(boolean isFrontFace) {
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            this.attachedRenderersTmp.get(i).updateAttachState(true);
        }
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog != null) {
            privateVideoPreviewDialog.update();
        }
    }

    /* loaded from: classes4.dex */
    public class GroupCallItemAnimator extends DefaultItemAnimator {
        HashSet<RecyclerView.ViewHolder> addingHolders;
        public float animationProgress;
        public ValueAnimator animator;
        float outMaxBottom;
        float outMinTop;
        HashSet<RecyclerView.ViewHolder> removingHolders;

        private GroupCallItemAnimator() {
            GroupCallActivity.this = r1;
            this.addingHolders = new HashSet<>();
            this.removingHolders = new HashSet<>();
        }

        @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public void endAnimations() {
            super.endAnimations();
            this.removingHolders.clear();
            this.addingHolders.clear();
            this.outMinTop = Float.MAX_VALUE;
            GroupCallActivity.this.listView.invalidate();
        }

        public void updateBackgroundBeforeAnimation() {
            if (this.animator != null) {
                return;
            }
            this.addingHolders.clear();
            this.addingHolders.addAll(this.mPendingAdditions);
            this.removingHolders.clear();
            this.removingHolders.addAll(this.mPendingRemovals);
            this.outMaxBottom = 0.0f;
            this.outMinTop = Float.MAX_VALUE;
            if (!this.addingHolders.isEmpty() || !this.removingHolders.isEmpty()) {
                int N = GroupCallActivity.this.listView.getChildCount();
                for (int a = 0; a < N; a++) {
                    View child = GroupCallActivity.this.listView.getChildAt(a);
                    RecyclerView.ViewHolder holder = GroupCallActivity.this.listView.findContainingViewHolder(child);
                    if (holder != null && holder.getItemViewType() != 3 && holder.getItemViewType() != 4 && holder.getItemViewType() != 5 && !this.addingHolders.contains(holder)) {
                        this.outMaxBottom = Math.max(this.outMaxBottom, child.getY() + child.getMeasuredHeight());
                        this.outMinTop = Math.min(this.outMinTop, Math.max(0.0f, child.getY()));
                    }
                }
                this.animationProgress = 0.0f;
                GroupCallActivity.this.listView.invalidate();
            }
        }

        @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public void runPendingAnimations() {
            boolean removalsPending = !this.mPendingRemovals.isEmpty();
            boolean movesPending = !this.mPendingMoves.isEmpty();
            boolean additionsPending = !this.mPendingAdditions.isEmpty();
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.animator = null;
            }
            if (removalsPending || movesPending || additionsPending) {
                this.animationProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.GroupCallActivity$GroupCallItemAnimator$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        GroupCallActivity.GroupCallItemAnimator.this.m3531x8b80169(valueAnimator2);
                    }
                });
                this.animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.GroupCallItemAnimator.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        GroupCallItemAnimator.this.animator = null;
                        GroupCallActivity.this.listView.invalidate();
                        GroupCallActivity.this.renderersContainer.invalidate();
                        GroupCallActivity.this.containerView.invalidate();
                        GroupCallActivity.this.updateLayout(true);
                        GroupCallItemAnimator.this.addingHolders.clear();
                        GroupCallItemAnimator.this.removingHolders.clear();
                    }
                });
                this.animator.setDuration(350L);
                this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animator.start();
                GroupCallActivity.this.listView.invalidate();
                GroupCallActivity.this.renderersContainer.invalidate();
            }
            super.runPendingAnimations();
        }

        /* renamed from: lambda$runPendingAnimations$0$org-telegram-ui-GroupCallActivity$GroupCallItemAnimator */
        public /* synthetic */ void m3531x8b80169(ValueAnimator valueAnimator) {
            this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallActivity.this.listView.invalidate();
            GroupCallActivity.this.renderersContainer.invalidate();
            GroupCallActivity.this.containerView.invalidate();
            GroupCallActivity.this.updateLayout(true);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithTouchOutside() {
        return !this.renderersContainer.inFullscreenMode;
    }

    public void onUserLeaveHint() {
        if (isRtmpStream() && AndroidUtilities.checkInlinePermissions(this.parentActivity) && !RTMPStreamPipOverlay.isVisible()) {
            dismiss();
            AndroidUtilities.runOnUIThread(GroupCallActivity$$ExternalSyntheticLambda41.INSTANCE, 100L);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.parentActivity.addOnUserLeaveHintListener(this.onUserLeaveHintListener);
    }

    public void onResume() {
        paused = false;
        this.listAdapter.notifyDataSetChanged();
        if (this.fullscreenUsersListView.getVisibility() == 0) {
            this.fullscreenAdapter.update(false, this.fullscreenUsersListView);
        }
        if (isTabletMode) {
            this.tabletGridAdapter.update(false, this.tabletVideoGridView);
        }
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            this.attachedRenderersTmp.get(i).updateAttachState(true);
        }
    }

    public void onPause() {
        paused = true;
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            this.attachedRenderersTmp.get(i).updateAttachState(false);
        }
    }

    public boolean isRtmpLandscapeMode() {
        if (!isRtmpStream() || this.call.visibleVideoParticipants.isEmpty()) {
            return false;
        }
        return this.call.visibleVideoParticipants.get(0).aspectRatio == 0.0f || this.call.visibleVideoParticipants.get(0).aspectRatio >= 1.0f;
    }

    public boolean isRtmpStream() {
        ChatObject.Call call = this.call;
        return call != null && call.call.rtmp_stream;
    }
}
