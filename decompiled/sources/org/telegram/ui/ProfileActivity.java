package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebStorage;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.SettingsSearchCell;
import org.telegram.ui.Cells.SettingsSuggestionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.AutoDeletePopupWrapper;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;
import org.telegram.ui.Components.Premium.ProfilePremiumCell;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TimerDrawable;
import org.telegram.ui.Components.TranslateAlert;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PinchToZoomHelper;
import org.telegram.ui.ProfileActivity;
/* loaded from: classes4.dex */
public class ProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, SharedMediaLayout.SharedMediaPreloaderDelegate, ImageUpdater.ImageUpdaterDelegate, SharedMediaLayout.Delegate {
    private static final int add_contact = 1;
    private static final int add_member = 18;
    private static final int add_photo = 36;
    private static final int add_shortcut = 14;
    private static final int block_contact = 2;
    private static final int call_item = 15;
    private static final int delete_avatar = 35;
    private static final int delete_contact = 5;
    private static final int edit_avatar = 34;
    private static final int edit_channel = 12;
    private static final int edit_contact = 4;
    private static final int edit_name = 30;
    private static final int gallery_menu_save = 21;
    private static final int invite_to_group = 9;
    private static final int leave_group = 7;
    private static final int logout = 31;
    private static final int qr_button = 37;
    private static final int search_button = 32;
    private static final int search_members = 17;
    private static final int set_as_main = 33;
    private static final int share = 10;
    private static final int share_contact = 3;
    private static final int start_secret_chat = 20;
    private static final int statistics = 19;
    private static final int video_call_item = 16;
    private static final int view_discussion = 22;
    private Property<ActionBar, Float> ACTIONBAR_HEADER_PROGRESS;
    private final Property<ProfileActivity, Float> HEADER_SHADOW;
    private AboutLinkCell aboutLinkCell;
    private int actionBarAnimationColorFrom;
    private Paint actionBarBackgroundPaint;
    private int addMemberRow;
    private int addToGroupButtonRow;
    private int addToGroupInfoRow;
    private int administratorsRow;
    private boolean allowProfileAnimation;
    private boolean allowPullingDown;
    private ActionBarMenuItem animatingItem;
    private float animationProgress;
    private ActionBarMenuSubItem autoDeleteItem;
    TimerDrawable autoDeleteItemDrawable;
    AutoDeletePopupWrapper autoDeletePopupWrapper;
    private TLRPC.FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private int avatarColor;
    private FrameLayout avatarContainer;
    private FrameLayout avatarContainer2;
    private AvatarDrawable avatarDrawable;
    private AvatarImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private float avatarScale;
    private float avatarX;
    private float avatarY;
    private ProfileGalleryView avatarsViewPager;
    private PagerIndicatorView avatarsViewPagerIndicatorView;
    private long banFromGroup;
    private int bioRow;
    private int blockedUsersRow;
    private TLRPC.BotInfo botInfo;
    private int bottomPaddingRow;
    private ActionBarMenuItem callItem;
    private boolean callItemVisible;
    private RLottieDrawable cameraDrawable;
    private boolean canSearchMembers;
    private RLottieDrawable cellCameraDrawable;
    private int channelInfoRow;
    private long chatId;
    private TLRPC.ChatFull chatInfo;
    private int chatRow;
    private int clearLogsRow;
    private NestedFrameLayout contentView;
    private boolean creatingChat;
    private CharSequence currentBio;
    private TLRPC.ChannelParticipant currentChannelParticipant;
    private TLRPC.Chat currentChat;
    private TLRPC.EncryptedChat currentEncryptedChat;
    private float currentExpanAnimatorFracture;
    private int dataRow;
    private int debugHeaderRow;
    private int devicesRow;
    private int devicesSectionRow;
    private long dialogId;
    private boolean disableProfileAnimation;
    private boolean doNotSetForeground;
    private ActionBarMenuItem editItem;
    private boolean editItemVisible;
    private int emptyRow;
    private StickerEmptyView emptyView;
    private ValueAnimator expandAnimator;
    private float[] expandAnimatorValues;
    private boolean expandPhoto;
    private float expandProgress;
    private float extraHeight;
    private int faqRow;
    private int filtersRow;
    private boolean firstLayout;
    private boolean fragmentOpened;
    private HintView fwdRestrictedHint;
    private boolean hasVoiceChatItem;
    private AnimatorSet headerAnimatorSet;
    protected float headerShadowAlpha;
    private AnimatorSet headerShadowAnimatorSet;
    private int helpHeaderRow;
    private int helpSectionCell;
    private ImageUpdater imageUpdater;
    private int infoHeaderRow;
    private int infoSectionRow;
    private float initialAnimationExtraHeight;
    private boolean invalidateScroll;
    private boolean isBot;
    public boolean isFragmentOpened;
    private boolean isInLandscapeMode;
    private boolean[] isOnline;
    private boolean isPulledDown;
    private boolean isQrItemVisible;
    private int joinRow;
    private int languageRow;
    private int lastMeasuredContentHeight;
    private int lastMeasuredContentWidth;
    private int lastSectionRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private int listContentHeight;
    private RecyclerListView listView;
    private float listViewVelocityY;
    private boolean loadingUsers;
    private int locationRow;
    private Drawable lockIconDrawable;
    private AudioPlayerAlert.ClippingTextViewSwitcher mediaCounterTextView;
    private float mediaHeaderAnimationProgress;
    private boolean mediaHeaderVisible;
    private int membersEndRow;
    private int membersHeaderRow;
    private int membersSectionRow;
    private int membersStartRow;
    private long mergeDialogId;
    private SimpleTextView[] nameTextView;
    private String nameTextViewRightDrawableContentDescription;
    private float nameX;
    private float nameY;
    private int navigationBarAnimationColorFrom;
    private boolean needSendMessage;
    private boolean needTimerImage;
    private int notificationRow;
    private int notificationsDividerRow;
    private int notificationsRow;
    private int numberRow;
    private int numberSectionRow;
    private int onlineCount;
    private SimpleTextView[] onlineTextView;
    private float onlineX;
    private float onlineY;
    private boolean openAnimationInProgress;
    private boolean openingAvatar;
    private ActionBarMenuItem otherItem;
    private int overlayCountVisible;
    private OverlaysView overlaysView;
    private LongSparseArray<TLRPC.ChatParticipant> participantsMap;
    private int passwordSuggestionRow;
    private int passwordSuggestionSectionRow;
    private int phoneRow;
    private int phoneSuggestionRow;
    private int phoneSuggestionSectionRow;
    PinchToZoomHelper pinchToZoomHelper;
    private int playProfileAnimation;
    private int policyRow;
    private HashMap<Integer, Integer> positionToOffset;
    private int premiumRow;
    private int premiumSectionsRow;
    private Drawable premiumStarDrawable;
    private CrossfadeDrawable premuimCrossfadeDrawable;
    private ImageLocation prevLoadedImageLocation;
    ChatActivity previousTransitionFragment;
    private int privacyRow;
    boolean profileTransitionInProgress;
    private PhotoViewer.PhotoViewerProvider provider;
    private ActionBarMenuItem qrItem;
    private AnimatorSet qrItemAnimation;
    private int questionRow;
    private boolean recreateMenuAfterAnimation;
    private Rect rect;
    private int reportRow;
    private boolean reportSpam;
    private Theme.ResourcesProvider resourcesProvider;
    private int rowCount;
    int savedScrollOffset;
    int savedScrollPosition;
    private ScamDrawable scamDrawable;
    private AnimatorSet scrimAnimatorSet;
    private Paint scrimPaint;
    private ActionBarPopupWindow scrimPopupWindow;
    private View scrimView;
    private boolean scrolling;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private RecyclerListView searchListView;
    private boolean searchMode;
    private int searchTransitionOffset;
    private float searchTransitionProgress;
    private Animator searchViewTransition;
    private int secretSettingsSectionRow;
    private long selectedUser;
    private int sendLastLogsRow;
    private int sendLogsRow;
    private int sendMessageRow;
    private TextCell setAvatarCell;
    private int setAvatarRow;
    private int setAvatarSectionRow;
    private int setUsernameRow;
    private int settingsKeyRow;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private int settingsTimerRow;
    private SharedMediaLayout sharedMediaLayout;
    private boolean sharedMediaLayoutAttached;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
    private int sharedMediaRow;
    private ArrayList<Integer> sortedUsers;
    private int stickersRow;
    private int subscribersRequestsRow;
    private int subscribersRow;
    private int switchBackendRow;
    private ImageView timeItem;
    private TimerDrawable timerDrawable;
    private TopView topView;
    private boolean transitionAnimationInProress;
    private int transitionIndex;
    private View transitionOnlineText;
    private ImageView ttlIconView;
    private int unblockRow;
    private UndoView undoView;
    private ImageLocation uploadingImageLocation;
    private boolean userBlocked;
    private long userId;
    private TLRPC.UserFull userInfo;
    private int userInfoRow;
    private int usernameRow;
    private boolean usersEndReached;
    private int usersForceShowingIn;
    private Drawable verifiedCheckDrawable;
    private CrossfadeDrawable verifiedCrossfadeDrawable;
    private Drawable verifiedDrawable;
    private int versionRow;
    private ActionBarMenuItem videoCallItem;
    private boolean videoCallItemVisible;
    private final ArrayList<TLRPC.ChatParticipant> visibleChatParticipants;
    private final ArrayList<Integer> visibleSortedUsers;
    private Paint whitePaint;
    private RLottieImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public /* synthetic */ String getInitialSearchString() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
    }

    static /* synthetic */ int access$7512(ProfileActivity x0, int x1) {
        int i = x0.listContentHeight + x1;
        x0.listContentHeight = i;
        return i;
    }

    /* loaded from: classes4.dex */
    public static class AvatarImageView extends BackupImageView {
        ProfileGalleryView avatarsViewPager;
        private ImageReceiver.BitmapHolder drawableHolder;
        private float foregroundAlpha;
        private final Paint placeholderPaint;
        private final RectF rect = new RectF();
        private ImageReceiver foregroundImageReceiver = new ImageReceiver(this);

        public void setAvatarsViewPager(ProfileGalleryView avatarsViewPager) {
            this.avatarsViewPager = avatarsViewPager;
        }

        public AvatarImageView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.placeholderPaint = paint;
            paint.setColor(-16777216);
        }

        public void setForegroundImage(ImageLocation imageLocation, String imageFilter, Drawable thumb) {
            this.foregroundImageReceiver.setImage(imageLocation, imageFilter, thumb, 0L, (String) null, (Object) null, 0);
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
        }

        public void setForegroundImageDrawable(ImageReceiver.BitmapHolder holder) {
            if (holder != null) {
                this.foregroundImageReceiver.setImageBitmap(holder.drawable);
            }
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
            this.drawableHolder = holder;
        }

        public float getForegroundAlpha() {
            return this.foregroundAlpha;
        }

        public void setForegroundAlpha(float value) {
            this.foregroundAlpha = value;
            invalidate();
        }

        public void clearForeground() {
            AnimatedFileDrawable drawable = this.foregroundImageReceiver.getAnimation();
            if (drawable != null) {
                drawable.removeSecondParentView(this);
            }
            this.foregroundImageReceiver.clearImage();
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
            this.foregroundAlpha = 0.0f;
            invalidate();
        }

        @Override // org.telegram.ui.Components.BackupImageView, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.foregroundImageReceiver.onDetachedFromWindow();
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
        }

        @Override // org.telegram.ui.Components.BackupImageView, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.foregroundImageReceiver.onAttachedToWindow();
        }

        @Override // org.telegram.ui.Components.BackupImageView
        public void setRoundRadius(int value) {
            super.setRoundRadius(value);
            this.foregroundImageReceiver.setRoundRadius(value);
        }

        @Override // org.telegram.ui.Components.BackupImageView, android.view.View
        public void onDraw(Canvas canvas) {
            if (this.foregroundAlpha < 1.0f) {
                this.imageReceiver.setImageCoords(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.imageReceiver.draw(canvas);
            }
            if (this.foregroundAlpha > 0.0f) {
                if (this.foregroundImageReceiver.getDrawable() != null) {
                    this.foregroundImageReceiver.setImageCoords(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                    this.foregroundImageReceiver.setAlpha(this.foregroundAlpha);
                    this.foregroundImageReceiver.draw(canvas);
                    return;
                }
                this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.placeholderPaint.setAlpha((int) (this.foregroundAlpha * 255.0f));
                int radius = this.foregroundImageReceiver.getRoundRadius()[0];
                canvas.drawRoundRect(this.rect, radius, radius, this.placeholderPaint);
            }
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            ProfileGalleryView profileGalleryView = this.avatarsViewPager;
            if (profileGalleryView != null) {
                profileGalleryView.invalidate();
            }
        }
    }

    /* loaded from: classes4.dex */
    public class TopView extends View {
        private int currentColor;
        private Paint paint = new Paint();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TopView(Context context) {
            super(context);
            ProfileActivity.this = r1;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(widthMeasureSpec) + AndroidUtilities.dp(3.0f));
        }

        @Override // android.view.View
        public void setBackgroundColor(int color) {
            if (color != this.currentColor) {
                this.currentColor = color;
                this.paint.setColor(color);
                invalidate();
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int height = ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
            float v = ProfileActivity.this.extraHeight + height + ProfileActivity.this.searchTransitionOffset;
            int y1 = (int) ((1.0f - ProfileActivity.this.mediaHeaderAnimationProgress) * v);
            if (y1 != 0) {
                if (ProfileActivity.this.previousTransitionFragment != null) {
                    AndroidUtilities.rectTmp2.set(0, 0, getMeasuredWidth(), y1);
                    ProfileActivity.this.previousTransitionFragment.contentView.drawBlurRect(canvas, getY(), AndroidUtilities.rectTmp2, ProfileActivity.this.previousTransitionFragment.getActionBar().blurScrimPaint, true);
                }
                this.paint.setColor(this.currentColor);
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), y1, this.paint);
            }
            if (y1 != v) {
                int color = ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite);
                this.paint.setColor(color);
                AndroidUtilities.rectTmp2.set(0, y1, getMeasuredWidth(), (int) v);
                ProfileActivity.this.contentView.drawBlurRect(canvas, getY(), AndroidUtilities.rectTmp2, this.paint, true);
            }
            if (ProfileActivity.this.parentLayout != null) {
                ProfileActivity.this.parentLayout.drawHeaderShadow(canvas, (int) (ProfileActivity.this.headerShadowAlpha * 255.0f), (int) v);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class OverlaysView extends View implements ProfileGalleryView.Callback {
        private float alpha;
        private float[] alphas;
        private final ValueAnimator animator;
        private final float[] animatorValues;
        private final Paint backgroundPaint;
        private final Paint barPaint;
        private final GradientDrawable bottomOverlayGradient;
        private final Rect bottomOverlayRect;
        private float currentAnimationValue;
        private int currentLoadingAnimationDirection;
        private float currentLoadingAnimationProgress;
        private float currentProgress;
        private boolean isOverlaysVisible;
        private long lastTime;
        private final float[] pressedOverlayAlpha;
        private final GradientDrawable[] pressedOverlayGradient;
        private final boolean[] pressedOverlayVisible;
        private int previousSelectedPotision;
        private float previousSelectedProgress;
        private final RectF rect;
        private final Paint selectedBarPaint;
        private int selectedPosition;
        private final int statusBarHeight;
        private final GradientDrawable topOverlayGradient;
        private final Rect topOverlayRect;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public OverlaysView(Context context) {
            super(context);
            ProfileActivity.this = r9;
            this.statusBarHeight = (!r9.actionBar.getOccupyStatusBar() || r9.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
            this.topOverlayRect = new Rect();
            this.bottomOverlayRect = new Rect();
            this.rect = new RectF();
            this.animatorValues = new float[]{0.0f, 1.0f};
            this.pressedOverlayGradient = new GradientDrawable[2];
            this.pressedOverlayVisible = new boolean[2];
            this.pressedOverlayAlpha = new float[2];
            this.alpha = 0.0f;
            this.alphas = null;
            this.previousSelectedPotision = -1;
            this.currentLoadingAnimationDirection = 1;
            setVisibility(8);
            Paint paint = new Paint(1);
            this.barPaint = paint;
            paint.setColor(1442840575);
            Paint paint2 = new Paint(1);
            this.selectedBarPaint = paint2;
            paint2.setColor(-1);
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{1107296256, 0});
            this.topOverlayGradient = gradientDrawable;
            gradientDrawable.setShape(0);
            GradientDrawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{1107296256, 0});
            this.bottomOverlayGradient = gradientDrawable2;
            gradientDrawable2.setShape(0);
            int i = 0;
            while (i < 2) {
                GradientDrawable.Orientation orientation = i == 0 ? GradientDrawable.Orientation.LEFT_RIGHT : GradientDrawable.Orientation.RIGHT_LEFT;
                this.pressedOverlayGradient[i] = new GradientDrawable(orientation, new int[]{838860800, 0});
                this.pressedOverlayGradient[i].setShape(0);
                i++;
            }
            Paint paint3 = new Paint(1);
            this.backgroundPaint = paint3;
            paint3.setColor(-16777216);
            paint3.setAlpha(66);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.setDuration(250L);
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$OverlaysView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.OverlaysView.this.m4408lambda$new$0$orgtelegramuiProfileActivity$OverlaysView(valueAnimator);
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.OverlaysView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (!OverlaysView.this.isOverlaysVisible) {
                        OverlaysView.this.setVisibility(8);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    OverlaysView.this.setVisibility(0);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ProfileActivity$OverlaysView */
        public /* synthetic */ void m4408lambda$new$0$orgtelegramuiProfileActivity$OverlaysView(ValueAnimator anim) {
            float[] fArr = this.animatorValues;
            float animatedFraction = anim.getAnimatedFraction();
            this.currentAnimationValue = animatedFraction;
            float value = AndroidUtilities.lerp(fArr, animatedFraction);
            setAlphaValue(value, true);
        }

        public void saveCurrentPageProgress() {
            this.previousSelectedProgress = this.currentProgress;
            this.previousSelectedPotision = this.selectedPosition;
            this.currentLoadingAnimationProgress = 0.0f;
            this.currentLoadingAnimationDirection = 1;
        }

        public void setAlphaValue(float value, boolean self) {
            if (Build.VERSION.SDK_INT > 18) {
                int alpha = (int) (255.0f * value);
                this.topOverlayGradient.setAlpha(alpha);
                this.bottomOverlayGradient.setAlpha(alpha);
                this.backgroundPaint.setAlpha((int) (66.0f * value));
                this.barPaint.setAlpha((int) (85.0f * value));
                this.selectedBarPaint.setAlpha(alpha);
                this.alpha = value;
            } else {
                setAlpha(value);
            }
            if (!self) {
                this.currentAnimationValue = value;
            }
            invalidate();
        }

        public boolean isOverlaysVisible() {
            return this.isOverlaysVisible;
        }

        public void setOverlaysVisible() {
            this.isOverlaysVisible = true;
            setVisibility(0);
        }

        public void setOverlaysVisible(boolean overlaysVisible, float durationFactor) {
            if (overlaysVisible != this.isOverlaysVisible) {
                this.isOverlaysVisible = overlaysVisible;
                this.animator.cancel();
                float value = AndroidUtilities.lerp(this.animatorValues, this.currentAnimationValue);
                float f = 1.0f;
                if (overlaysVisible) {
                    this.animator.setDuration(((1.0f - value) * 250.0f) / durationFactor);
                } else {
                    this.animator.setDuration((250.0f * value) / durationFactor);
                }
                float[] fArr = this.animatorValues;
                fArr[0] = value;
                if (!overlaysVisible) {
                    f = 0.0f;
                }
                fArr[1] = f;
                this.animator.start();
            }
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            int actionBarHeight = this.statusBarHeight + ActionBar.getCurrentActionBarHeight();
            this.topOverlayRect.set(0, 0, w, (int) (actionBarHeight * 0.5f));
            this.bottomOverlayRect.set(0, (int) (h - (AndroidUtilities.dp(72.0f) * 0.5f)), w, h);
            this.topOverlayGradient.setBounds(0, this.topOverlayRect.bottom, w, AndroidUtilities.dp(16.0f) + actionBarHeight);
            this.bottomOverlayGradient.setBounds(0, (h - AndroidUtilities.dp(72.0f)) - AndroidUtilities.dp(24.0f), w, this.bottomOverlayRect.top);
            this.pressedOverlayGradient[0].setBounds(0, 0, w / 5, h);
            this.pressedOverlayGradient[1].setBounds(w - (w / 5), 0, w, h);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int count;
            float progress;
            boolean invalidate;
            int baseAlpha;
            for (int i = 0; i < 2; i++) {
                float[] fArr = this.pressedOverlayAlpha;
                if (fArr[i] > 0.0f) {
                    this.pressedOverlayGradient[i].setAlpha((int) (fArr[i] * 255.0f));
                    this.pressedOverlayGradient[i].draw(canvas);
                }
            }
            this.topOverlayGradient.draw(canvas);
            this.bottomOverlayGradient.draw(canvas);
            canvas.drawRect(this.topOverlayRect, this.backgroundPaint);
            canvas.drawRect(this.bottomOverlayRect, this.backgroundPaint);
            int count2 = ProfileActivity.this.avatarsViewPager.getRealCount();
            this.selectedPosition = ProfileActivity.this.avatarsViewPager.getRealPosition();
            float[] fArr2 = this.alphas;
            if (fArr2 == null || fArr2.length != count2) {
                float[] fArr3 = new float[count2];
                this.alphas = fArr3;
                Arrays.fill(fArr3, 0.0f);
            }
            boolean invalidate2 = false;
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastTime;
            if (dt < 0 || dt > 20) {
                dt = 17;
            }
            this.lastTime = newTime;
            float f = 1.0f;
            if (count2 > 1 && count2 <= 20) {
                if (ProfileActivity.this.overlayCountVisible != 0) {
                    if (ProfileActivity.this.overlayCountVisible == 1) {
                        this.alpha = 0.0f;
                        ProfileActivity.this.overlayCountVisible = 2;
                    }
                } else {
                    this.alpha = 0.0f;
                    ProfileActivity.this.overlayCountVisible = 3;
                }
                if (ProfileActivity.this.overlayCountVisible == 2) {
                    this.barPaint.setAlpha((int) (this.alpha * 85.0f));
                    this.selectedBarPaint.setAlpha((int) (this.alpha * 255.0f));
                }
                int width = ((getMeasuredWidth() - AndroidUtilities.dp(10.0f)) - AndroidUtilities.dp((count2 - 1) * 2)) / count2;
                int y = AndroidUtilities.dp(4.0f) + ((Build.VERSION.SDK_INT < 21 || ProfileActivity.this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight);
                int a = 0;
                while (a < count2) {
                    int x = AndroidUtilities.dp((a * 2) + 5) + (width * a);
                    if (a != this.previousSelectedPotision || Math.abs(this.previousSelectedProgress - f) <= 1.0E-4f) {
                        count = count2;
                        boolean invalidate3 = invalidate2;
                        if (a == this.selectedPosition) {
                            if (ProfileActivity.this.avatarsViewPager.isCurrentItemVideo()) {
                                float currentItemProgress = ProfileActivity.this.avatarsViewPager.getCurrentItemProgress();
                                this.currentProgress = currentItemProgress;
                                progress = currentItemProgress;
                                if ((progress <= 0.0f && ProfileActivity.this.avatarsViewPager.isLoadingCurrentVideo()) || this.currentLoadingAnimationProgress > 0.0f) {
                                    float f2 = this.currentLoadingAnimationProgress;
                                    int i2 = this.currentLoadingAnimationDirection;
                                    float f3 = f2 + (((float) (i2 * dt)) / 500.0f);
                                    this.currentLoadingAnimationProgress = f3;
                                    if (f3 > 1.0f) {
                                        this.currentLoadingAnimationProgress = 1.0f;
                                        this.currentLoadingAnimationDirection = i2 * (-1);
                                    } else if (f3 <= 0.0f) {
                                        this.currentLoadingAnimationProgress = 0.0f;
                                        this.currentLoadingAnimationDirection = i2 * (-1);
                                    }
                                }
                                this.rect.set(x, y, x + width, AndroidUtilities.dp(2.0f) + y);
                                this.barPaint.setAlpha((int) (((this.currentLoadingAnimationProgress * 48.0f) + 85.0f) * this.alpha));
                                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), this.barPaint);
                                invalidate = true;
                                baseAlpha = 80;
                            } else {
                                this.currentProgress = 1.0f;
                                progress = 1.0f;
                                baseAlpha = 85;
                                invalidate = invalidate3;
                            }
                        } else {
                            progress = 1.0f;
                            baseAlpha = 85;
                            invalidate = invalidate3;
                        }
                    } else {
                        progress = this.previousSelectedProgress;
                        canvas.save();
                        count = count2;
                        canvas.clipRect(x + (width * progress), y, x + width, y + AndroidUtilities.dp(2.0f));
                        this.rect.set(x, y, x + width, y + AndroidUtilities.dp(2.0f));
                        this.barPaint.setAlpha((int) (this.alpha * 85.0f));
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), this.barPaint);
                        canvas.restore();
                        invalidate = true;
                        baseAlpha = 80;
                    }
                    boolean invalidate4 = invalidate;
                    long newTime2 = newTime;
                    this.rect.set(x, y, x + (width * progress), AndroidUtilities.dp(2.0f) + y);
                    if (a != this.selectedPosition) {
                        if (ProfileActivity.this.overlayCountVisible == 3) {
                            this.barPaint.setAlpha((int) (AndroidUtilities.lerp(baseAlpha, 255, CubicBezierInterpolator.EASE_BOTH.getInterpolation(this.alphas[a])) * this.alpha));
                        }
                    } else {
                        this.alphas[a] = 0.75f;
                    }
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), a == this.selectedPosition ? this.selectedBarPaint : this.barPaint);
                    a++;
                    invalidate2 = invalidate4;
                    count2 = count;
                    newTime = newTime2;
                    f = 1.0f;
                }
                boolean invalidate5 = invalidate2;
                if (ProfileActivity.this.overlayCountVisible == 2) {
                    float f4 = this.alpha;
                    if (f4 >= 1.0f) {
                        ProfileActivity.this.overlayCountVisible = 3;
                        invalidate2 = invalidate5;
                    } else {
                        float f5 = f4 + (((float) dt) / 180.0f);
                        this.alpha = f5;
                        if (f5 > 1.0f) {
                            this.alpha = 1.0f;
                        }
                        invalidate2 = true;
                    }
                } else {
                    if (ProfileActivity.this.overlayCountVisible == 3) {
                        int i3 = 0;
                        invalidate2 = invalidate5;
                        while (true) {
                            float[] fArr4 = this.alphas;
                            if (i3 >= fArr4.length) {
                                break;
                            }
                            if (i3 != this.selectedPosition && fArr4[i3] > 0.0f) {
                                fArr4[i3] = fArr4[i3] - (((float) dt) / 500.0f);
                                if (fArr4[i3] <= 0.0f) {
                                    fArr4[i3] = 0.0f;
                                    if (i3 == this.previousSelectedPotision) {
                                        this.previousSelectedPotision = -1;
                                    }
                                }
                                invalidate2 = true;
                            } else if (i3 == this.previousSelectedPotision) {
                                this.previousSelectedPotision = -1;
                            }
                            i3++;
                        }
                    }
                    invalidate2 = invalidate5;
                }
            }
            for (int i4 = 0; i4 < 2; i4++) {
                if (!this.pressedOverlayVisible[i4]) {
                    float[] fArr5 = this.pressedOverlayAlpha;
                    if (fArr5[i4] > 0.0f) {
                        fArr5[i4] = fArr5[i4] - (((float) dt) / 180.0f);
                        if (fArr5[i4] < 0.0f) {
                            fArr5[i4] = 0.0f;
                        }
                        invalidate2 = true;
                    }
                } else {
                    float[] fArr6 = this.pressedOverlayAlpha;
                    if (fArr6[i4] < 1.0f) {
                        fArr6[i4] = fArr6[i4] + (((float) dt) / 180.0f);
                        if (fArr6[i4] > 1.0f) {
                            fArr6[i4] = 1.0f;
                        }
                        invalidate2 = true;
                    }
                }
            }
            if (invalidate2) {
                postInvalidateOnAnimation();
            }
        }

        @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
        public void onDown(boolean left) {
            this.pressedOverlayVisible[!left ? 1 : 0] = true;
            postInvalidateOnAnimation();
        }

        @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
        public void onRelease() {
            Arrays.fill(this.pressedOverlayVisible, false);
            postInvalidateOnAnimation();
        }

        @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
        public void onPhotosLoaded() {
            ProfileActivity.this.updateProfileData(false);
        }

        @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
        public void onVideoSet() {
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class NestedFrameLayout extends SizeNotifierFrameLayout implements NestedScrollingParent3 {
        private NestedScrollingParentHelper nestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public NestedFrameLayout(Context context) {
            super(context);
            ProfileActivity.this = r1;
        }

        @Override // androidx.core.view.NestedScrollingParent3
        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int[] consumed) {
            if (target == ProfileActivity.this.listView && ProfileActivity.this.sharedMediaLayoutAttached) {
                RecyclerListView innerListView = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                int top = ProfileActivity.this.sharedMediaLayout.getTop();
                if (top == 0) {
                    consumed[1] = dyUnconsumed;
                    innerListView.scrollBy(0, dyUnconsumed);
                }
            }
        }

        @Override // androidx.core.view.NestedScrollingParent2
        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        }

        @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
        public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
            return super.onNestedPreFling(target, velocityX, velocityY);
        }

        @Override // androidx.core.view.NestedScrollingParent2
        public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
            if (target == ProfileActivity.this.listView) {
                int top = -1;
                if (ProfileActivity.this.sharedMediaRow != -1 && ProfileActivity.this.sharedMediaLayoutAttached) {
                    boolean searchVisible = ProfileActivity.this.actionBar.isSearchFieldVisible();
                    int t = ProfileActivity.this.sharedMediaLayout.getTop();
                    if (dy < 0) {
                        boolean scrolledInner = false;
                        if (t <= 0) {
                            RecyclerListView innerListView = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) innerListView.getLayoutManager();
                            int pos = linearLayoutManager.findFirstVisibleItemPosition();
                            if (pos != -1) {
                                RecyclerView.ViewHolder holder = innerListView.findViewHolderForAdapterPosition(pos);
                                if (holder != null) {
                                    top = holder.itemView.getTop();
                                }
                                int paddingTop = innerListView.getPaddingTop();
                                if (top != paddingTop || pos != 0) {
                                    consumed[1] = pos != 0 ? dy : Math.max(dy, top - paddingTop);
                                    innerListView.scrollBy(0, dy);
                                    scrolledInner = true;
                                }
                            }
                        }
                        if (searchVisible) {
                            if (!scrolledInner && t < 0) {
                                consumed[1] = dy - Math.max(t, dy);
                            } else {
                                consumed[1] = dy;
                            }
                        }
                    } else if (searchVisible) {
                        RecyclerListView innerListView2 = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                        consumed[1] = dy;
                        if (t > 0) {
                            consumed[1] = consumed[1] - dy;
                        }
                        if (consumed[1] > 0) {
                            innerListView2.scrollBy(0, consumed[1]);
                        }
                    }
                }
            }
        }

        @Override // androidx.core.view.NestedScrollingParent2
        public boolean onStartNestedScroll(View child, View target, int axes, int type) {
            return ProfileActivity.this.sharedMediaRow != -1 && axes == 2;
        }

        @Override // androidx.core.view.NestedScrollingParent2
        public void onNestedScrollAccepted(View child, View target, int axes, int type) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        }

        @Override // androidx.core.view.NestedScrollingParent2
        public void onStopNestedScroll(View target, int type) {
            this.nestedScrollingParentHelper.onStopNestedScroll(target);
        }

        @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
        public void onStopNestedScroll(View child) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
        public void drawList(Canvas blurCanvas, boolean top) {
            super.drawList(blurCanvas, top);
            blurCanvas.save();
            blurCanvas.translate(0.0f, ProfileActivity.this.listView.getY());
            ProfileActivity.this.sharedMediaLayout.drawListForBlur(blurCanvas);
            blurCanvas.restore();
        }
    }

    /* loaded from: classes4.dex */
    public class PagerIndicatorView extends View {
        private final PagerAdapter adapter;
        private final ValueAnimator animator;
        private final Paint backgroundPaint;
        private boolean isIndicatorVisible;
        private final TextPaint textPaint;
        private final RectF indicatorRect = new RectF();
        private final float[] animatorValues = {0.0f, 1.0f};

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public PagerIndicatorView(Context context) {
            super(context);
            ProfileActivity.this = r6;
            PagerAdapter adapter = r6.avatarsViewPager.getAdapter();
            this.adapter = adapter;
            setVisibility(8);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setColor(-1);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(AndroidUtilities.dpf2(15.0f));
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setColor(637534208);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$PagerIndicatorView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.PagerIndicatorView.this.m4409lambda$new$0$orgtelegramuiProfileActivity$PagerIndicatorView(valueAnimator);
                }
            });
            final boolean expanded = r6.expandPhoto;
            ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.PagerIndicatorView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (PagerIndicatorView.this.isIndicatorVisible) {
                        if (ProfileActivity.this.searchItem != null) {
                            ProfileActivity.this.searchItem.setClickable(false);
                        }
                        if (ProfileActivity.this.editItemVisible) {
                            ProfileActivity.this.editItem.setVisibility(8);
                        }
                        if (ProfileActivity.this.callItemVisible) {
                            ProfileActivity.this.callItem.setVisibility(8);
                        }
                        if (ProfileActivity.this.videoCallItemVisible) {
                            ProfileActivity.this.videoCallItem.setVisibility(8);
                            return;
                        }
                        return;
                    }
                    PagerIndicatorView.this.setVisibility(8);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    if (ProfileActivity.this.searchItem != null && !expanded) {
                        ProfileActivity.this.searchItem.setClickable(true);
                    }
                    if (ProfileActivity.this.editItemVisible) {
                        ProfileActivity.this.editItem.setVisibility(0);
                    }
                    if (ProfileActivity.this.callItemVisible) {
                        ProfileActivity.this.callItem.setVisibility(0);
                    }
                    if (ProfileActivity.this.videoCallItemVisible) {
                        ProfileActivity.this.videoCallItem.setVisibility(0);
                    }
                    PagerIndicatorView.this.setVisibility(0);
                }
            });
            r6.avatarsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.ProfileActivity.PagerIndicatorView.2
                private int prevPage;

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageSelected(int position) {
                    int realPosition = ProfileActivity.this.avatarsViewPager.getRealPosition(position);
                    PagerIndicatorView.this.invalidateIndicatorRect(this.prevPage != realPosition);
                    this.prevPage = realPosition;
                    PagerIndicatorView.this.updateAvatarItems();
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrollStateChanged(int state) {
                }
            });
            adapter.registerDataSetObserver(new DataSetObserver() { // from class: org.telegram.ui.ProfileActivity.PagerIndicatorView.3
                @Override // android.database.DataSetObserver
                public void onChanged() {
                    int count = ProfileActivity.this.avatarsViewPager.getRealCount();
                    if (ProfileActivity.this.overlayCountVisible == 0 && count > 1 && count <= 20 && ProfileActivity.this.overlaysView.isOverlaysVisible()) {
                        ProfileActivity.this.overlayCountVisible = 1;
                    }
                    PagerIndicatorView.this.invalidateIndicatorRect(false);
                    PagerIndicatorView.this.refreshVisibility(1.0f);
                    PagerIndicatorView.this.updateAvatarItems();
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ProfileActivity$PagerIndicatorView */
        public /* synthetic */ void m4409lambda$new$0$orgtelegramuiProfileActivity$PagerIndicatorView(ValueAnimator a) {
            float value = AndroidUtilities.lerp(this.animatorValues, a.getAnimatedFraction());
            if (ProfileActivity.this.searchItem != null && !ProfileActivity.this.isPulledDown) {
                ProfileActivity.this.searchItem.setScaleX(1.0f - value);
                ProfileActivity.this.searchItem.setScaleY(1.0f - value);
                ProfileActivity.this.searchItem.setAlpha(1.0f - value);
            }
            if (ProfileActivity.this.editItemVisible) {
                ProfileActivity.this.editItem.setScaleX(1.0f - value);
                ProfileActivity.this.editItem.setScaleY(1.0f - value);
                ProfileActivity.this.editItem.setAlpha(1.0f - value);
            }
            if (ProfileActivity.this.callItemVisible) {
                ProfileActivity.this.callItem.setScaleX(1.0f - value);
                ProfileActivity.this.callItem.setScaleY(1.0f - value);
                ProfileActivity.this.callItem.setAlpha(1.0f - value);
            }
            if (ProfileActivity.this.videoCallItemVisible) {
                ProfileActivity.this.videoCallItem.setScaleX(1.0f - value);
                ProfileActivity.this.videoCallItem.setScaleY(1.0f - value);
                ProfileActivity.this.videoCallItem.setAlpha(1.0f - value);
            }
            setScaleX(value);
            setScaleY(value);
            setAlpha(value);
        }

        public void updateAvatarItemsInternal() {
            if (ProfileActivity.this.otherItem != null && ProfileActivity.this.avatarsViewPager != null && ProfileActivity.this.isPulledDown) {
                int position = ProfileActivity.this.avatarsViewPager.getRealPosition();
                if (position == 0) {
                    ProfileActivity.this.otherItem.hideSubItem(33);
                    ProfileActivity.this.otherItem.showSubItem(36);
                    return;
                }
                ProfileActivity.this.otherItem.showSubItem(33);
                ProfileActivity.this.otherItem.hideSubItem(36);
            }
        }

        public void updateAvatarItems() {
            if (ProfileActivity.this.imageUpdater != null) {
                if (ProfileActivity.this.otherItem.isSubMenuShowing()) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$PagerIndicatorView$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProfileActivity.PagerIndicatorView.this.updateAvatarItemsInternal();
                        }
                    }, 500L);
                } else {
                    updateAvatarItemsInternal();
                }
            }
        }

        public boolean isIndicatorVisible() {
            return this.isIndicatorVisible;
        }

        public boolean isIndicatorFullyVisible() {
            return this.isIndicatorVisible && !this.animator.isRunning();
        }

        public void setIndicatorVisible(boolean indicatorVisible, float durationFactor) {
            if (indicatorVisible != this.isIndicatorVisible) {
                this.isIndicatorVisible = indicatorVisible;
                this.animator.cancel();
                float value = AndroidUtilities.lerp(this.animatorValues, this.animator.getAnimatedFraction());
                float f = 1.0f;
                if (durationFactor <= 0.0f) {
                    this.animator.setDuration(0L);
                } else if (indicatorVisible) {
                    this.animator.setDuration(((1.0f - value) * 250.0f) / durationFactor);
                } else {
                    this.animator.setDuration((250.0f * value) / durationFactor);
                }
                float[] fArr = this.animatorValues;
                fArr[0] = value;
                if (!indicatorVisible) {
                    f = 0.0f;
                }
                fArr[1] = f;
                this.animator.start();
            }
        }

        public void refreshVisibility(float durationFactor) {
            setIndicatorVisible(ProfileActivity.this.isPulledDown && ProfileActivity.this.avatarsViewPager.getRealCount() > 20, durationFactor);
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            invalidateIndicatorRect(false);
        }

        public void invalidateIndicatorRect(boolean pageChanged) {
            if (pageChanged) {
                ProfileActivity.this.overlaysView.saveCurrentPageProgress();
            }
            ProfileActivity.this.overlaysView.invalidate();
            float textWidth = this.textPaint.measureText(getCurrentTitle());
            int i = 0;
            this.indicatorRect.right = (getMeasuredWidth() - AndroidUtilities.dp(54.0f)) - (ProfileActivity.this.qrItem != null ? AndroidUtilities.dp(48.0f) : 0);
            RectF rectF = this.indicatorRect;
            rectF.left = rectF.right - (AndroidUtilities.dpf2(16.0f) + textWidth);
            RectF rectF2 = this.indicatorRect;
            if (ProfileActivity.this.actionBar.getOccupyStatusBar()) {
                i = AndroidUtilities.statusBarHeight;
            }
            rectF2.top = i + AndroidUtilities.dp(15.0f);
            RectF rectF3 = this.indicatorRect;
            rectF3.bottom = rectF3.top + AndroidUtilities.dp(26.0f);
            setPivotX(this.indicatorRect.centerX());
            setPivotY(this.indicatorRect.centerY());
            invalidate();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float radius = AndroidUtilities.dpf2(12.0f);
            canvas.drawRoundRect(this.indicatorRect, radius, radius, this.backgroundPaint);
            canvas.drawText(getCurrentTitle(), this.indicatorRect.centerX(), this.indicatorRect.top + AndroidUtilities.dpf2(18.5f), this.textPaint);
        }

        private String getCurrentTitle() {
            return this.adapter.getPageTitle(ProfileActivity.this.avatarsViewPager.getCurrentItem()).toString();
        }

        public ActionBarMenuItem getSecondaryMenuItem() {
            if (ProfileActivity.this.callItemVisible) {
                return ProfileActivity.this.callItem;
            }
            if (ProfileActivity.this.editItemVisible) {
                return ProfileActivity.this.editItem;
            }
            if (ProfileActivity.this.searchItem != null) {
                return ProfileActivity.this.searchItem;
            }
            return null;
        }
    }

    public ProfileActivity(Bundle args) {
        this(args, null);
    }

    public ProfileActivity(Bundle args, SharedMediaLayout.SharedMediaPreloader preloader) {
        super(args);
        this.nameTextView = new SimpleTextView[2];
        this.nameTextViewRightDrawableContentDescription = null;
        this.onlineTextView = new SimpleTextView[2];
        this.scrimView = null;
        this.scrimPaint = new Paint(1) { // from class: org.telegram.ui.ProfileActivity.1
            @Override // android.graphics.Paint
            public void setAlpha(int a) {
                super.setAlpha(a);
                ProfileActivity.this.fragmentView.invalidate();
            }
        };
        this.actionBarBackgroundPaint = new Paint(1);
        this.isOnline = new boolean[1];
        this.headerShadowAlpha = 1.0f;
        this.participantsMap = new LongSparseArray<>();
        this.allowProfileAnimation = true;
        this.disableProfileAnimation = false;
        this.positionToOffset = new HashMap<>();
        this.expandAnimatorValues = new float[]{0.0f, 1.0f};
        this.whitePaint = new Paint();
        this.onlineCount = -1;
        this.rect = new Rect();
        this.visibleChatParticipants = new ArrayList<>();
        this.visibleSortedUsers = new ArrayList<>();
        this.usersForceShowingIn = 0;
        this.firstLayout = true;
        this.invalidateScroll = true;
        this.isQrItemVisible = true;
        this.actionBarAnimationColorFrom = 0;
        this.navigationBarAnimationColorFrom = 0;
        this.HEADER_SHADOW = new AnimationProperties.FloatProperty<ProfileActivity>("headerShadow") { // from class: org.telegram.ui.ProfileActivity.2
            public void setValue(ProfileActivity object, float value) {
                ProfileActivity.this.headerShadowAlpha = value;
                ProfileActivity.this.topView.invalidate();
            }

            public Float get(ProfileActivity object) {
                return Float.valueOf(ProfileActivity.this.headerShadowAlpha);
            }
        };
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.ProfileActivity.3
            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
                TLRPC.Chat chat;
                if (fileLocation == null) {
                    return null;
                }
                TLRPC.FileLocation photoBig = null;
                if (ProfileActivity.this.userId != 0) {
                    TLRPC.User user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                    if (user != null && user.photo != null && user.photo.photo_big != null) {
                        photoBig = user.photo.photo_big;
                    }
                } else if (ProfileActivity.this.chatId != 0 && (chat = ProfileActivity.this.getMessagesController().getChat(Long.valueOf(ProfileActivity.this.chatId))) != null && chat.photo != null && chat.photo.photo_big != null) {
                    photoBig = chat.photo.photo_big;
                }
                if (photoBig == null || photoBig.local_id != fileLocation.local_id || photoBig.volume_id != fileLocation.volume_id || photoBig.dc_id != fileLocation.dc_id) {
                    return null;
                }
                int[] coords = new int[2];
                ProfileActivity.this.avatarImage.getLocationInWindow(coords);
                PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                boolean z = false;
                object.viewX = coords[0];
                object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                object.parentView = ProfileActivity.this.avatarImage;
                object.imageReceiver = ProfileActivity.this.avatarImage.getImageReceiver();
                if (ProfileActivity.this.userId != 0) {
                    object.dialogId = ProfileActivity.this.userId;
                } else if (ProfileActivity.this.chatId != 0) {
                    object.dialogId = -ProfileActivity.this.chatId;
                }
                object.thumb = object.imageReceiver.getBitmapSafe();
                object.size = -1L;
                object.radius = ProfileActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                object.scale = ProfileActivity.this.avatarContainer.getScaleX();
                if (ProfileActivity.this.userId == ProfileActivity.this.getUserConfig().clientUserId) {
                    z = true;
                }
                object.canEdit = z;
                return object;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void willHidePhotoViewer() {
                ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void openPhotoForEdit(String file, String thumb, boolean isVideo) {
                ProfileActivity.this.imageUpdater.openPhotoForEdit(file, thumb, 0, isVideo);
            }
        };
        this.ACTIONBAR_HEADER_PROGRESS = new AnimationProperties.FloatProperty<ActionBar>("animationProgress") { // from class: org.telegram.ui.ProfileActivity.28
            public void setValue(ActionBar object, float value) {
                ProfileActivity.this.mediaHeaderAnimationProgress = value;
                ProfileActivity.this.topView.invalidate();
                int color1 = ProfileActivity.this.getThemedColor(Theme.key_profile_title);
                int color2 = ProfileActivity.this.getThemedColor(Theme.key_player_actionBarTitle);
                int c = AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f);
                ProfileActivity.this.nameTextView[1].setTextColor(c);
                if (ProfileActivity.this.lockIconDrawable != null) {
                    ProfileActivity.this.lockIconDrawable.setColorFilter(c, PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.scamDrawable != null) {
                    int color12 = ProfileActivity.this.getThemedColor(Theme.key_avatar_subtitleInProfileBlue);
                    ProfileActivity.this.scamDrawable.setColor(AndroidUtilities.getOffsetColor(color12, color2, value, 1.0f));
                }
                int color13 = ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon);
                ProfileActivity.this.actionBar.setItemsColor(AndroidUtilities.getOffsetColor(color13, ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText2), value, 1.0f), false);
                int color14 = ProfileActivity.this.getThemedColor(Theme.key_avatar_actionBarSelectorBlue);
                ProfileActivity.this.actionBar.setItemsBackgroundColor(AndroidUtilities.getOffsetColor(color14, ProfileActivity.this.getThemedColor(Theme.key_actionBarActionModeDefaultSelector), value, 1.0f), false);
                ProfileActivity.this.topView.invalidate();
                ProfileActivity.this.otherItem.setIconColor(ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
                ProfileActivity.this.callItem.setIconColor(ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
                ProfileActivity.this.videoCallItem.setIconColor(ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
                ProfileActivity.this.editItem.setIconColor(ProfileActivity.this.getThemedColor(Theme.key_actionBarDefaultIcon));
                if (ProfileActivity.this.verifiedDrawable != null) {
                    int color15 = ProfileActivity.this.getThemedColor(Theme.key_profile_verifiedBackground);
                    ProfileActivity.this.verifiedDrawable.setColorFilter(AndroidUtilities.getOffsetColor(color15, ProfileActivity.this.getThemedColor(Theme.key_player_actionBarTitle), value, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.verifiedCheckDrawable != null) {
                    int color16 = ProfileActivity.this.getThemedColor(Theme.key_profile_verifiedCheck);
                    ProfileActivity.this.verifiedCheckDrawable.setColorFilter(AndroidUtilities.getOffsetColor(color16, ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite), value, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.premiumStarDrawable != null) {
                    int color17 = ProfileActivity.this.getThemedColor(Theme.key_profile_verifiedBackground);
                    ProfileActivity.this.premiumStarDrawable.setColorFilter(AndroidUtilities.getOffsetColor(color17, ProfileActivity.this.getThemedColor(Theme.key_player_actionBarTitle), value, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.avatarsViewPagerIndicatorView.getSecondaryMenuItem() != null) {
                    if (ProfileActivity.this.videoCallItemVisible || ProfileActivity.this.editItemVisible || ProfileActivity.this.callItemVisible) {
                        ProfileActivity profileActivity = ProfileActivity.this;
                        profileActivity.needLayoutText(Math.min(1.0f, profileActivity.extraHeight / AndroidUtilities.dp(88.0f)));
                    }
                }
            }

            public Float get(ActionBar object) {
                return Float.valueOf(ProfileActivity.this.mediaHeaderAnimationProgress);
            }
        };
        this.scrimAnimatorSet = null;
        this.savedScrollPosition = -1;
        this.sharedMediaPreloader = preloader;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        this.userId = this.arguments.getLong("user_id", 0L);
        this.chatId = this.arguments.getLong(ChatReactionsEditActivity.KEY_CHAT_ID, 0L);
        this.banFromGroup = this.arguments.getLong("ban_chat_id", 0L);
        this.reportSpam = this.arguments.getBoolean("reportSpam", false);
        if (!this.expandPhoto) {
            boolean z = this.arguments.getBoolean("expandPhoto", false);
            this.expandPhoto = z;
            if (z) {
                this.needSendMessage = true;
            }
        }
        if (this.userId != 0) {
            long j = this.arguments.getLong("dialog_id", 0L);
            this.dialogId = j;
            if (j != 0) {
                this.currentEncryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(this.dialogId)));
            }
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user == null) {
                return false;
            }
            getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatCreated);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.blockedUsersDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.botInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.userInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reloadInterface);
            this.userBlocked = getMessagesController().blockePeers.indexOfKey(this.userId) >= 0;
            if (user.bot) {
                this.isBot = true;
                getMediaDataController().loadBotInfo(user.id, user.id, true, this.classGuid);
            }
            this.userInfo = getMessagesController().getUserFull(this.userId);
            getMessagesController().loadFullUser(getMessagesController().getUser(Long.valueOf(this.userId)), this.classGuid, true);
            this.participantsMap = null;
            if (UserObject.isUserSelf(user)) {
                ImageUpdater imageUpdater = new ImageUpdater(true);
                this.imageUpdater = imageUpdater;
                imageUpdater.setOpenWithFrontfaceCamera(true);
                this.imageUpdater.parentFragment = this;
                this.imageUpdater.setDelegate(this);
                getMediaDataController().checkFeaturedStickers();
                getMessagesController().loadSuggestedFilters();
                getMessagesController().loadUserInfo(getUserConfig().getCurrentUser(), true, this.classGuid);
            }
            this.actionBarAnimationColorFrom = this.arguments.getInt("actionBarColor", 0);
        } else if (this.chatId == 0) {
            return false;
        } else {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
            this.currentChat = chat;
            if (chat == null) {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda12
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProfileActivity.this.m4375lambda$onFragmentCreate$0$orgtelegramuiProfileActivity(countDownLatch);
                    }
                });
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (this.currentChat == null) {
                    return false;
                }
                getMessagesController().putChat(this.currentChat, true);
            }
            if (this.currentChat.megagroup) {
                getChannelParticipants(true);
            } else {
                this.participantsMap = null;
            }
            getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.chatOnlineCountDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);
            this.sortedUsers = new ArrayList<>();
            updateOnlineCount(true);
            if (this.chatInfo == null) {
                this.chatInfo = getMessagesController().getChatFull(this.chatId);
            }
            if (ChatObject.isChannel(this.currentChat)) {
                getMessagesController().loadFullChat(this.chatId, this.classGuid, true);
            } else if (this.chatInfo == null) {
                this.chatInfo = getMessagesStorage().loadChatInfo(this.chatId, false, null, false, false);
            }
        }
        if (this.sharedMediaPreloader == null) {
            this.sharedMediaPreloader = new SharedMediaLayout.SharedMediaPreloader(this);
        }
        this.sharedMediaPreloader.addDelegate(this);
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        updateRowsIds();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (this.arguments.containsKey("preload_messages")) {
            getMessagesController().ensureMessagesLoaded(this.userId, 0, null);
        }
        return true;
    }

    /* renamed from: lambda$onFragmentCreate$0$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4375lambda$onFragmentCreate$0$orgtelegramuiProfileActivity(CountDownLatch countDownLatch) {
        this.currentChat = getMessagesStorage().getChat(this.chatId);
        countDownLatch.countDown();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onDestroy();
        }
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader = this.sharedMediaPreloader;
        if (sharedMediaPreloader != null) {
            sharedMediaPreloader.onDestroy(this);
        }
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2 = this.sharedMediaPreloader;
        if (sharedMediaPreloader2 != null) {
            sharedMediaPreloader2.removeDelegate(this);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.onDestroy();
        }
        if (this.userId != 0) {
            getNotificationCenter().removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatCreated);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.blockedUsersDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.botInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
            getMessagesController().cancelLoadFullUser(this.userId);
        } else if (this.chatId != 0) {
            getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.chatOnlineCountDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
        }
        AvatarImageView avatarImageView = this.avatarImage;
        if (avatarImageView != null) {
            avatarImageView.setImageDrawable(null);
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.clear();
        }
        PinchToZoomHelper pinchToZoomHelper = this.pinchToZoomHelper;
        if (pinchToZoomHelper != null) {
            pinchToZoomHelper.clear();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ActionBar createActionBar(Context context) {
        BaseFragment lastFragment = this.parentLayout.getLastFragment();
        if ((lastFragment instanceof ChatActivity) && ((ChatActivity) lastFragment).themeDelegate != null && ((ChatActivity) lastFragment).themeDelegate.getCurrentTheme() != null) {
            this.resourcesProvider = lastFragment.getResourceProvider();
        }
        ActionBar actionBar = new ActionBar(context, this.resourcesProvider) { // from class: org.telegram.ui.ProfileActivity.4
            @Override // org.telegram.ui.ActionBar.ActionBar, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                ProfileActivity.this.avatarContainer.getHitRect(ProfileActivity.this.rect);
                if (ProfileActivity.this.rect.contains((int) event.getX(), (int) event.getY())) {
                    return false;
                }
                return super.onTouchEvent(event);
            }

            @Override // org.telegram.ui.ActionBar.ActionBar
            public void setItemsColor(int color, boolean isActionMode) {
                super.setItemsColor(color, isActionMode);
                if (!isActionMode && ProfileActivity.this.ttlIconView != null) {
                    ProfileActivity.this.ttlIconView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            }
        };
        boolean z = true;
        actionBar.setForceSkipTouches(true);
        actionBar.setBackgroundColor(0);
        actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_avatar_actionBarSelectorBlue), false);
        actionBar.setItemsColor(getThemedColor(Theme.key_actionBarDefaultIcon), false);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        actionBar.setClipContent(true);
        if (Build.VERSION.SDK_INT < 21 || AndroidUtilities.isTablet() || this.inBubbleMode) {
            z = false;
        }
        actionBar.setOccupyStatusBar(z);
        final ImageView backButton = actionBar.getBackButton();
        backButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return ProfileActivity.this.m4352lambda$createActionBar$2$orgtelegramuiProfileActivity(backButton, view);
            }
        });
        return actionBar;
    }

    /* renamed from: lambda$createActionBar$2$org-telegram-ui-ProfileActivity */
    public /* synthetic */ boolean m4352lambda$createActionBar$2$orgtelegramuiProfileActivity(ImageView backButton, View e) {
        ActionBarPopupWindow menu = BackButtonMenu.show(this, backButton, getDialogId(), this.resourcesProvider);
        if (menu != null) {
            menu.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda8
                @Override // android.widget.PopupWindow.OnDismissListener
                public final void onDismiss() {
                    ProfileActivity.this.m4351lambda$createActionBar$1$orgtelegramuiProfileActivity();
                }
            });
            dimBehindView(backButton, 0.3f);
            UndoView undoView = this.undoView;
            if (undoView != null) {
                undoView.hide(true, 1);
            }
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createActionBar$1$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4351lambda$createActionBar$1$orgtelegramuiProfileActivity() {
        dimBehindView(false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v12 */
    /* JADX WARN: Type inference failed for: r11v13 */
    /* JADX WARN: Type inference failed for: r11v14 */
    /* JADX WARN: Type inference failed for: r11v2, types: [int, boolean] */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        long did;
        ?? r11;
        Object writeButtonTag;
        int scrollTo;
        String str;
        ViewGroup decorView;
        ChatAvatarContainer avatarContainer;
        Theme.createProfileResources(context);
        Theme.createChatResources(context, false);
        BaseFragment lastFragment = this.parentLayout.getLastFragment();
        if ((lastFragment instanceof ChatActivity) && ((ChatActivity) lastFragment).themeDelegate != null && ((ChatActivity) lastFragment).themeDelegate.getCurrentTheme() != null) {
            this.resourcesProvider = lastFragment.getResourceProvider();
        }
        this.searchTransitionOffset = 0;
        this.searchTransitionProgress = 1.0f;
        this.searchMode = false;
        this.hasOwnBackground = true;
        this.extraHeight = AndroidUtilities.dp(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass5());
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onDestroy();
        }
        if (this.dialogId != 0) {
            did = this.dialogId;
        } else if (this.userId != 0) {
            did = this.userId;
        } else {
            did = -this.chatId;
        }
        this.fragmentView = new AnonymousClass6(context);
        TLRPC.ChatFull chatFull = this.chatInfo;
        ArrayList<Integer> users = (chatFull == null || chatFull.participants == null || this.chatInfo.participants.participants.size() <= 5) ? null : this.sortedUsers;
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader = this.sharedMediaPreloader;
        TLRPC.UserFull userFull = this.userInfo;
        final long did2 = did;
        SharedMediaLayout sharedMediaLayout2 = new SharedMediaLayout(context, did, sharedMediaPreloader, userFull != null ? userFull.common_chats_count : 0, this.sortedUsers, this.chatInfo, users != null, this, this, 1, this.resourcesProvider) { // from class: org.telegram.ui.ProfileActivity.7
            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected void onSelectedTabChanged() {
                ProfileActivity.this.updateSelectedMediaTabText();
            }

            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected boolean canShowSearchItem() {
                return ProfileActivity.this.mediaHeaderVisible;
            }

            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected void onSearchStateChanged(boolean expanded) {
                if (SharedConfig.smoothKeyboard) {
                    AndroidUtilities.removeAdjustResize(ProfileActivity.this.getParentActivity(), ProfileActivity.this.classGuid);
                }
                ProfileActivity.this.listView.stopScroll();
                ProfileActivity.this.avatarContainer2.setPivotY(ProfileActivity.this.avatarContainer.getPivotY() + (ProfileActivity.this.avatarContainer.getMeasuredHeight() / 2.0f));
                ProfileActivity.this.avatarContainer2.setPivotX(ProfileActivity.this.avatarContainer2.getMeasuredWidth() / 2.0f);
                AndroidUtilities.updateViewVisibilityAnimated(ProfileActivity.this.avatarContainer2, !expanded, 0.95f, true);
                int i = 4;
                ProfileActivity.this.callItem.setVisibility((expanded || !ProfileActivity.this.callItemVisible) ? 8 : 4);
                ProfileActivity.this.videoCallItem.setVisibility((expanded || !ProfileActivity.this.videoCallItemVisible) ? 8 : 4);
                ProfileActivity.this.editItem.setVisibility((expanded || !ProfileActivity.this.editItemVisible) ? 8 : 4);
                ProfileActivity.this.otherItem.setVisibility(expanded ? 8 : 4);
                if (ProfileActivity.this.qrItem != null) {
                    ActionBarMenuItem actionBarMenuItem = ProfileActivity.this.qrItem;
                    if (expanded) {
                        i = 8;
                    }
                    actionBarMenuItem.setVisibility(i);
                }
            }

            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected boolean onMemberClick(TLRPC.ChatParticipant participant, boolean isLong) {
                return ProfileActivity.this.onMemberClick(participant, isLong);
            }

            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected void drawBackgroundWithBlur(Canvas canvas, float y, Rect rectTmp2, Paint backgroundPaint) {
                ProfileActivity.this.contentView.drawBlurRect(canvas, ProfileActivity.this.listView.getY() + getY() + y, rectTmp2, backgroundPaint, true);
            }

            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected void invalidateBlur() {
                ProfileActivity.this.contentView.invalidateBlur();
            }
        };
        this.sharedMediaLayout = sharedMediaLayout2;
        sharedMediaLayout2.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.userId == getUserConfig().clientUserId) {
            ActionBarMenuItem addItem = menu.addItem(37, R.drawable.msg_qr_mini, getResourceProvider());
            this.qrItem = addItem;
            addItem.setContentDescription(LocaleController.getString("GetQRCode", R.string.GetQRCode));
            updateQrItemVisibility(false);
            if (ContactsController.getInstance(this.currentAccount).getPrivacyRules(7) == null) {
                ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
            }
        }
        if (this.imageUpdater != null) {
            r11 = 1;
            r11 = 1;
            ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(32, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.ProfileActivity.8
                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public Animator getCustomToggleTransition() {
                    ProfileActivity profileActivity = ProfileActivity.this;
                    profileActivity.searchMode = !profileActivity.searchMode;
                    if (!ProfileActivity.this.searchMode) {
                        ProfileActivity.this.searchItem.clearFocusOnSearchView();
                    }
                    if (ProfileActivity.this.searchMode) {
                        ProfileActivity.this.searchItem.getSearchField().setText("");
                    }
                    ProfileActivity profileActivity2 = ProfileActivity.this;
                    return profileActivity2.searchExpandTransition(profileActivity2.searchMode);
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onTextChanged(EditText editText) {
                    ProfileActivity.this.searchAdapter.search(editText.getText().toString().toLowerCase());
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            actionBarMenuItemSearchListener.setContentDescription(LocaleController.getString("SearchInSettings", R.string.SearchInSettings));
            this.searchItem.setSearchFieldHint(LocaleController.getString("SearchInSettings", R.string.SearchInSettings));
            this.sharedMediaLayout.getSearchItem().setVisibility(8);
            if (this.expandPhoto) {
                this.searchItem.setVisibility(8);
            }
        } else {
            r11 = 1;
        }
        ActionBarMenuItem addItem2 = menu.addItem(16, R.drawable.profile_video);
        this.videoCallItem = addItem2;
        addItem2.setContentDescription(LocaleController.getString("VideoCall", R.string.VideoCall));
        if (this.chatId != 0) {
            this.callItem = menu.addItem(15, R.drawable.msg_voicechat2);
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                this.callItem.setContentDescription(LocaleController.getString("VoipChannelVoiceChat", R.string.VoipChannelVoiceChat));
            } else {
                this.callItem.setContentDescription(LocaleController.getString("VoipGroupVoiceChat", R.string.VoipGroupVoiceChat));
            }
        } else {
            ActionBarMenuItem addItem3 = menu.addItem(15, R.drawable.ic_call);
            this.callItem = addItem3;
            addItem3.setContentDescription(LocaleController.getString("Call", R.string.Call));
        }
        ActionBarMenuItem addItem4 = menu.addItem(12, R.drawable.group_edit_profile);
        this.editItem = addItem4;
        addItem4.setContentDescription(LocaleController.getString("Edit", R.string.Edit));
        this.otherItem = menu.addItem(10, R.drawable.ic_ab_other, this.resourcesProvider);
        ImageView imageView = new ImageView(context);
        this.ttlIconView = imageView;
        imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
        AndroidUtilities.updateViewVisibilityAnimated(this.ttlIconView, false, 0.8f, false);
        this.ttlIconView.setImageResource(R.drawable.msg_mini_autodelete_timer);
        this.otherItem.addView(this.ttlIconView, LayoutHelper.createFrame(12, 12.0f, 19, 8.0f, 2.0f, 0.0f, 0.0f));
        this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        int scrollToPosition = 0;
        if (this.listView == null || this.imageUpdater == null) {
            writeButtonTag = null;
            scrollTo = -1;
        } else {
            int scrollTo2 = this.layoutManager.findFirstVisibleItemPosition();
            View topView = this.layoutManager.findViewByPosition(scrollTo2);
            if (topView != null) {
                scrollToPosition = topView.getTop() - this.listView.getPaddingTop();
            } else {
                scrollTo2 = -1;
            }
            Object writeButtonTag2 = this.writeButton.getTag();
            writeButtonTag = writeButtonTag2;
            scrollTo = scrollTo2;
        }
        createActionBarMenu(false);
        this.listAdapter = new ListAdapter(context);
        this.searchAdapter = new SearchAdapter(context);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable;
        avatarDrawable.setProfile(r11);
        this.fragmentView.setWillNotDraw(false);
        NestedFrameLayout nestedFrameLayout = (NestedFrameLayout) this.fragmentView;
        this.contentView = nestedFrameLayout;
        nestedFrameLayout.needBlur = r11;
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.ProfileActivity.9
            private VelocityTracker velocityTracker;

            @Override // org.telegram.ui.Components.RecyclerListView
            public boolean canHighlightChildAt(View child, float x, float y) {
                return !(child instanceof AboutLinkCell);
            }

            @Override // org.telegram.ui.Components.RecyclerListView
            public boolean allowSelectChildAtPosition(View child) {
                return child != ProfileActivity.this.sharedMediaLayout;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
            public boolean hasOverlappingRendering() {
                return false;
            }

            @Override // androidx.recyclerview.widget.RecyclerView
            public void requestChildOnScreen(View child, View focused) {
            }

            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                if (ProfileActivity.this.fragmentView != null) {
                    ProfileActivity.this.fragmentView.invalidate();
                }
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                View view;
                VelocityTracker velocityTracker;
                int action = e.getAction();
                if (action == 0) {
                    VelocityTracker velocityTracker2 = this.velocityTracker;
                    if (velocityTracker2 == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    } else {
                        velocityTracker2.clear();
                    }
                    this.velocityTracker.addMovement(e);
                } else if (action == 2) {
                    VelocityTracker velocityTracker3 = this.velocityTracker;
                    if (velocityTracker3 != null) {
                        velocityTracker3.addMovement(e);
                        this.velocityTracker.computeCurrentVelocity(1000);
                        ProfileActivity.this.listViewVelocityY = this.velocityTracker.getYVelocity(e.getPointerId(e.getActionIndex()));
                    }
                } else if ((action == 1 || action == 3) && (velocityTracker = this.velocityTracker) != null) {
                    velocityTracker.recycle();
                    this.velocityTracker = null;
                }
                boolean result = super.onTouchEvent(e);
                if ((action == 1 || action == 3) && ProfileActivity.this.allowPullingDown && (view = ProfileActivity.this.layoutManager.findViewByPosition(0)) != null) {
                    if (ProfileActivity.this.isPulledDown) {
                        int actionBarHeight = ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                        ProfileActivity.this.listView.smoothScrollBy(0, (view.getTop() - ProfileActivity.this.listView.getMeasuredWidth()) + actionBarHeight, CubicBezierInterpolator.EASE_OUT_QUINT);
                    } else {
                        ProfileActivity.this.listView.smoothScrollBy(0, view.getTop() - AndroidUtilities.dp(88.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
                    }
                }
                return result;
            }

            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (getItemAnimator().isRunning() && child.getBackground() == null && child.getTranslationY() != 0.0f) {
                    boolean useAlpha = ProfileActivity.this.listView.getChildAdapterPosition(child) == ProfileActivity.this.sharedMediaRow && child.getAlpha() != 1.0f;
                    if (useAlpha) {
                        ProfileActivity.this.whitePaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f * child.getAlpha()));
                    }
                    canvas.drawRect(ProfileActivity.this.listView.getX(), child.getY(), ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), child.getY() + child.getHeight(), ProfileActivity.this.whitePaint);
                    if (useAlpha) {
                        ProfileActivity.this.whitePaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f));
                    }
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        DefaultItemAnimator defaultItemAnimator = new AnonymousClass10();
        this.listView.setItemAnimator(defaultItemAnimator);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDelayAnimations(false);
        this.listView.setClipToPadding(false);
        this.listView.setHideIfEmpty(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) { // from class: org.telegram.ui.ProfileActivity.11
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return ProfileActivity.this.imageUpdater != null;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                boolean z = false;
                View view = ProfileActivity.this.layoutManager.findViewByPosition(0);
                if (view != null && !ProfileActivity.this.openingAvatar) {
                    int canScroll = view.getTop() - AndroidUtilities.dp(88.0f);
                    if (ProfileActivity.this.allowPullingDown || canScroll <= dy) {
                        if (ProfileActivity.this.allowPullingDown) {
                            if (dy < canScroll) {
                                if (ProfileActivity.this.listView.getScrollState() == 1 && !ProfileActivity.this.isPulledDown) {
                                    dy /= 2;
                                }
                            } else {
                                dy = canScroll;
                                ProfileActivity.this.allowPullingDown = false;
                            }
                        }
                    } else {
                        dy = canScroll;
                        if (ProfileActivity.this.avatarsViewPager.hasImages() && ProfileActivity.this.avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled() && !ProfileActivity.this.isInLandscapeMode && !AndroidUtilities.isTablet()) {
                            ProfileActivity profileActivity = ProfileActivity.this;
                            if (profileActivity.avatarBig == null) {
                                z = true;
                            }
                            profileActivity.allowPullingDown = z;
                        }
                    }
                }
                return super.scrollVerticallyBy(dy, recycler, state);
            }
        };
        this.layoutManager = linearLayoutManager;
        int i = r11 == true ? 1 : 0;
        int i2 = r11 == true ? 1 : 0;
        int i3 = r11 == true ? 1 : 0;
        linearLayoutManager.setOrientation(i);
        this.layoutManager.mIgnoreTopPadding = false;
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setGlowColor(0);
        this.listView.setAdapter(this.listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda31
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view, int i4) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i4);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view, int i4, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i4, f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i4, float f, float f2) {
                ProfileActivity.this.m4359lambda$createView$4$orgtelegramuiProfileActivity(did2, context, view, i4, f, f2);
            }
        });
        this.listView.setOnItemLongClickListener(new AnonymousClass13());
        if (this.searchItem != null) {
            RecyclerListView recyclerListView2 = new RecyclerListView(context);
            this.searchListView = recyclerListView2;
            recyclerListView2.setVerticalScrollBarEnabled(false);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context, r11, false));
            this.searchListView.setGlowColor(getThemedColor(Theme.key_avatar_backgroundActionBarBlue));
            this.searchListView.setAdapter(this.searchAdapter);
            this.searchListView.setItemAnimator(null);
            this.searchListView.setVisibility(8);
            this.searchListView.setLayoutAnimation(null);
            this.searchListView.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda30
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view, int i4) {
                    ProfileActivity.this.m4360lambda$createView$5$orgtelegramuiProfileActivity(view, i4);
                }
            });
            this.searchListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda32
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
                public final boolean onItemClick(View view, int i4) {
                    return ProfileActivity.this.m4362lambda$createView$7$orgtelegramuiProfileActivity(view, i4);
                }
            });
            this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ProfileActivity.14
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1) {
                        AndroidUtilities.hideKeyboard(ProfileActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setAnimateEmptyView(r11, r11);
            StickerEmptyView stickerEmptyView = new StickerEmptyView(context, null, r11);
            this.emptyView = stickerEmptyView;
            stickerEmptyView.setAnimateLayoutChange(r11);
            this.emptyView.subtitle.setVisibility(8);
            this.emptyView.setVisibility(8);
            frameLayout.addView(this.emptyView);
            this.searchAdapter.loadFaqWebPage();
        }
        if (this.banFromGroup != 0) {
            MessagesController messagesController = getMessagesController();
            str = Theme.key_avatar_backgroundActionBarBlue;
            final TLRPC.Chat chat = messagesController.getChat(Long.valueOf(this.banFromGroup));
            if (this.currentChannelParticipant == null) {
                TLRPC.TL_channels_getParticipant req = new TLRPC.TL_channels_getParticipant();
                req.channel = MessagesController.getInputChannel(chat);
                req.participant = getMessagesController().getInputPeer(this.userId);
                getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda26
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ProfileActivity.this.m4364lambda$createView$9$orgtelegramuiProfileActivity(tLObject, tL_error);
                    }
                });
            }
            FrameLayout frameLayout1 = new FrameLayout(context) { // from class: org.telegram.ui.ProfileActivity.15
                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                    Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                    Theme.chat_composeShadowDrawable.draw(canvas);
                    canvas.drawRect(0.0f, bottom, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                }
            };
            frameLayout1.setWillNotDraw(false);
            frameLayout.addView(frameLayout1, LayoutHelper.createFrame(-1, 51, 83));
            frameLayout1.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ProfileActivity.this.m4353lambda$createView$10$orgtelegramuiProfileActivity(chat, view);
                }
            });
            TextView textView = new TextView(context);
            textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteRedText));
            textView.setTextSize(r11, 15.0f);
            textView.setGravity(17);
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView.setText(LocaleController.getString("BanFromTheGroup", R.string.BanFromTheGroup));
            frameLayout1.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, AndroidUtilities.dp(48.0f));
            this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
        } else {
            str = Theme.key_avatar_backgroundActionBarBlue;
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
        }
        TopView topView2 = new TopView(context);
        this.topView = topView2;
        topView2.setBackgroundColor(getThemedColor(str));
        frameLayout.addView(this.topView);
        this.contentView.blurBehindViews.add(this.topView);
        this.avatarContainer = new FrameLayout(context);
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.ProfileActivity.17
            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (ProfileActivity.this.transitionOnlineText != null) {
                    canvas.save();
                    canvas.translate(ProfileActivity.this.onlineTextView[0].getX(), ProfileActivity.this.onlineTextView[0].getY());
                    canvas.saveLayerAlpha(0.0f, 0.0f, ProfileActivity.this.transitionOnlineText.getMeasuredWidth(), ProfileActivity.this.transitionOnlineText.getMeasuredHeight(), (int) ((1.0f - ProfileActivity.this.animationProgress) * 255.0f), 31);
                    ProfileActivity.this.transitionOnlineText.draw(canvas);
                    canvas.restore();
                    canvas.restore();
                    invalidate();
                }
            }
        };
        this.avatarContainer2 = frameLayout2;
        AndroidUtilities.updateViewVisibilityAnimated(frameLayout2, true, 1.0f, false);
        frameLayout.addView(this.avatarContainer2, LayoutHelper.createFrame(-1, -1.0f, GravityCompat.START, 0.0f, 0.0f, 0.0f, 0.0f));
        this.avatarContainer.setPivotX(0.0f);
        this.avatarContainer.setPivotY(0.0f);
        this.avatarContainer2.addView(this.avatarContainer, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        AvatarImageView avatarImageView = new AvatarImageView(context) { // from class: org.telegram.ui.ProfileActivity.18
            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                if (getImageReceiver().hasNotThumb()) {
                    info.setText(LocaleController.getString("AccDescrProfilePicture", R.string.AccDescrProfilePicture));
                    if (Build.VERSION.SDK_INT >= 21) {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("Open", R.string.Open)));
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccDescrOpenInPhotoViewer", R.string.AccDescrOpenInPhotoViewer)));
                        return;
                    }
                    return;
                }
                info.setVisibleToUser(false);
            }
        };
        this.avatarImage = avatarImageView;
        avatarImageView.getImageReceiver().setAllowDecodeSingleFrame(true);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        this.avatarContainer.addView(this.avatarImage, LayoutHelper.createFrame(-1, -1.0f));
        this.avatarImage.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ProfileActivity.this.m4354lambda$createView$11$orgtelegramuiProfileActivity(view);
            }
        });
        this.avatarImage.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return ProfileActivity.this.m4355lambda$createView$12$orgtelegramuiProfileActivity(view);
            }
        });
        RadialProgressView radialProgressView = new RadialProgressView(context) { // from class: org.telegram.ui.ProfileActivity.19
            private Paint paint;

            {
                ProfileActivity.this = this;
                Paint paint = new Paint(1);
                this.paint = paint;
                paint.setColor(1426063360);
            }

            @Override // org.telegram.ui.Components.RadialProgressView, android.view.View
            public void onDraw(Canvas canvas) {
                if (ProfileActivity.this.avatarImage != null && ProfileActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                    this.paint.setAlpha((int) (ProfileActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                    canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f, this.paint);
                }
                super.onDraw(canvas);
            }
        };
        this.avatarProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(26.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarProgressView.setNoProgress(false);
        this.avatarContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(-1, -1.0f));
        ImageView imageView2 = new ImageView(context);
        this.timeItem = imageView2;
        imageView2.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
        this.timeItem.setScaleType(ImageView.ScaleType.CENTER);
        this.timeItem.setAlpha(0.0f);
        ImageView imageView3 = this.timeItem;
        TimerDrawable timerDrawable = new TimerDrawable(context, null);
        this.timerDrawable = timerDrawable;
        imageView3.setImageDrawable(timerDrawable);
        frameLayout.addView(this.timeItem, LayoutHelper.createFrame(34, 34, 51));
        showAvatarProgress(false, false);
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.onDestroy();
        }
        this.overlaysView = new OverlaysView(context);
        long did3 = this.userId;
        if (did3 == 0) {
            did3 = -this.chatId;
        }
        int scrollTo3 = scrollTo;
        ProfileGalleryView profileGalleryView2 = new ProfileGalleryView(context, did3, this.actionBar, this.listView, this.avatarImage, getClassGuid(), this.overlaysView);
        this.avatarsViewPager = profileGalleryView2;
        profileGalleryView2.setChatInfo(this.chatInfo);
        this.avatarContainer2.addView(this.avatarsViewPager);
        this.avatarContainer2.addView(this.overlaysView);
        this.avatarImage.setAvatarsViewPager(this.avatarsViewPager);
        PagerIndicatorView pagerIndicatorView = new PagerIndicatorView(context);
        this.avatarsViewPagerIndicatorView = pagerIndicatorView;
        this.avatarContainer2.addView(pagerIndicatorView, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout.addView(this.actionBar);
        float rightMargin = 54 + ((!this.callItemVisible || this.userId == 0) ? 0 : 54);
        int initialTitleWidth = -2;
        if (this.parentLayout != null && (this.parentLayout.getLastFragment() instanceof ChatActivity) && (avatarContainer = ((ChatActivity) this.parentLayout.getLastFragment()).getAvatarContainer()) != null && avatarContainer.getLayoutParams() != null && avatarContainer.getTitleTextView() != null) {
            rightMargin = (((ViewGroup.MarginLayoutParams) avatarContainer.getLayoutParams()).rightMargin + (avatarContainer.getWidth() - avatarContainer.getTitleTextView().getRight())) / AndroidUtilities.density;
            initialTitleWidth = (int) (avatarContainer.getTitleTextView().getWidth() / AndroidUtilities.density);
        }
        int a = 0;
        while (true) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (a >= simpleTextViewArr.length) {
                break;
            }
            if (this.playProfileAnimation != 0 || a != 0) {
                simpleTextViewArr[a] = new SimpleTextView(context) { // from class: org.telegram.ui.ProfileActivity.20
                    @Override // org.telegram.ui.ActionBar.SimpleTextView, android.view.View
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                        super.onInitializeAccessibilityNodeInfo(info);
                        if (isFocusable() && ProfileActivity.this.nameTextViewRightDrawableContentDescription != null) {
                            info.setText(((Object) getText()) + ", " + ProfileActivity.this.nameTextViewRightDrawableContentDescription);
                        }
                    }
                };
                if (a == 1) {
                    this.nameTextView[a].setTextColor(getThemedColor(Theme.key_profile_title));
                } else {
                    this.nameTextView[a].setTextColor(getThemedColor(Theme.key_actionBarDefaultTitle));
                }
                this.nameTextView[a].setTextSize(18);
                this.nameTextView[a].setGravity(3);
                this.nameTextView[a].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                this.nameTextView[a].setPivotX(0.0f);
                this.nameTextView[a].setPivotY(0.0f);
                this.nameTextView[a].setAlpha(a == 0 ? 0.0f : 1.0f);
                if (a == 1) {
                    this.nameTextView[a].setScrollNonFitText(true);
                    this.nameTextView[a].setImportantForAccessibility(2);
                }
                this.nameTextView[a].setFocusable(a == 0);
                this.avatarContainer2.addView(this.nameTextView[a], LayoutHelper.createFrame(a == 0 ? initialTitleWidth : -2, -2.0f, 51, 118.0f, 0.0f, a == 0 ? rightMargin : 0.0f, 0.0f));
            }
            a++;
        }
        int a2 = 0;
        while (true) {
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (a2 >= simpleTextViewArr2.length) {
                break;
            }
            simpleTextViewArr2[a2] = new SimpleTextView(context);
            this.onlineTextView[a2].setTextColor(getThemedColor(Theme.key_avatar_subtitleInProfileBlue));
            this.onlineTextView[a2].setTextSize(14);
            this.onlineTextView[a2].setGravity(3);
            this.onlineTextView[a2].setAlpha((a2 == 0 || a2 == 2) ? 0.0f : 1.0f);
            if (a2 > 0) {
                this.onlineTextView[a2].setImportantForAccessibility(2);
            }
            this.onlineTextView[a2].setFocusable(a2 == 0);
            this.avatarContainer2.addView(this.onlineTextView[a2], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a2 == 0 ? rightMargin : 8.0f, 0.0f));
            a2++;
        }
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = new AudioPlayerAlert.ClippingTextViewSwitcher(context) { // from class: org.telegram.ui.ProfileActivity.21
            @Override // org.telegram.ui.Components.AudioPlayerAlert.ClippingTextViewSwitcher
            protected TextView createTextView() {
                TextView textView2 = new TextView(context);
                textView2.setTextColor(ProfileActivity.this.getThemedColor(Theme.key_player_actionBarSubtitle));
                textView2.setTextSize(1, 14.0f);
                textView2.setSingleLine(true);
                textView2.setEllipsize(TextUtils.TruncateAt.END);
                textView2.setGravity(3);
                return textView2;
            }
        };
        this.mediaCounterTextView = clippingTextViewSwitcher;
        clippingTextViewSwitcher.setAlpha(0.0f);
        this.avatarContainer2.addView(this.mediaCounterTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 8.0f, 0.0f));
        updateProfileData(true);
        this.writeButton = new RLottieImageView(context);
        Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor(Theme.key_profile_actionBackground), getThemedColor(Theme.key_profile_actionPressedBackground)), 0, 0);
        combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        this.writeButton.setBackground(combinedDrawable);
        if (this.userId != 0) {
            if (this.imageUpdater != null) {
                this.cameraDrawable = new RLottieDrawable(R.raw.camera_outline, String.valueOf((int) R.raw.camera_outline), AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f), false, null);
                this.cellCameraDrawable = new RLottieDrawable(R.raw.camera_outline, "2131558415_cell", AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), false, null);
                this.writeButton.setAnimation(this.cameraDrawable);
                this.writeButton.setContentDescription(LocaleController.getString("AccDescrChangeProfilePicture", R.string.AccDescrChangeProfilePicture));
                this.writeButton.setPadding(AndroidUtilities.dp(2.0f), 0, 0, AndroidUtilities.dp(2.0f));
            } else {
                this.writeButton.setImageResource(R.drawable.profile_newmsg);
                this.writeButton.setContentDescription(LocaleController.getString("AccDescrOpenChat", R.string.AccDescrOpenChat));
            }
        } else {
            this.writeButton.setImageResource(R.drawable.profile_discuss);
            this.writeButton.setContentDescription(LocaleController.getString("ViewDiscussion", R.string.ViewDiscussion));
        }
        this.writeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_profile_actionIcon), PorterDuff.Mode.MULTIPLY));
        this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
        frameLayout.addView(this.writeButton, LayoutHelper.createFrame(60, 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ProfileActivity.this.m4356lambda$createView$13$orgtelegramuiProfileActivity(view);
            }
        });
        needLayout(false);
        if (scrollTo3 != -1 && writeButtonTag != null) {
            this.writeButton.setTag(0);
            this.writeButton.setScaleX(0.2f);
            this.writeButton.setScaleY(0.2f);
            this.writeButton.setAlpha(0.0f);
        }
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ProfileActivity.22
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean z = true;
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(ProfileActivity.this.getParentActivity().getCurrentFocus());
                }
                if (ProfileActivity.this.openingAvatar && newState != 2) {
                    ProfileActivity.this.openingAvatar = false;
                }
                if (ProfileActivity.this.searchItem != null) {
                    ProfileActivity.this.scrolling = newState != 0;
                    ActionBarMenuItem actionBarMenuItem = ProfileActivity.this.searchItem;
                    if (ProfileActivity.this.scrolling || ProfileActivity.this.isPulledDown) {
                        z = false;
                    }
                    actionBarMenuItem.setEnabled(z);
                }
                ProfileActivity.this.sharedMediaLayout.scrollingByUser = ProfileActivity.this.listView.scrollingByUser;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (ProfileActivity.this.fwdRestrictedHint != null) {
                    ProfileActivity.this.fwdRestrictedHint.hide();
                }
                ProfileActivity.this.checkListViewScroll();
                boolean z = false;
                if (ProfileActivity.this.participantsMap != null && !ProfileActivity.this.usersEndReached && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.membersEndRow - 8) {
                    ProfileActivity.this.getChannelParticipants(false);
                }
                SharedMediaLayout sharedMediaLayout3 = ProfileActivity.this.sharedMediaLayout;
                if (ProfileActivity.this.sharedMediaLayout.getY() == 0.0f) {
                    z = true;
                }
                sharedMediaLayout3.setPinnedToTop(z);
            }
        });
        UndoView undoView = new UndoView(context, null, false, this.resourcesProvider);
        this.undoView = undoView;
        frameLayout.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.expandAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProfileActivity.this.m4357lambda$createView$14$orgtelegramuiProfileActivity(valueAnimator);
            }
        });
        this.expandAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        this.expandAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.23
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ProfileActivity.this.actionBar.setItemsBackgroundColor(ProfileActivity.this.isPulledDown ? Theme.ACTION_BAR_WHITE_SELECTOR_COLOR : ProfileActivity.this.getThemedColor(Theme.key_avatar_actionBarSelectorBlue), false);
                ProfileActivity.this.avatarImage.clearForeground();
                ProfileActivity.this.doNotSetForeground = false;
            }
        });
        updateRowsIds();
        updateSelectedMediaTabText();
        HintView hintView = new HintView(getParentActivity(), 9);
        this.fwdRestrictedHint = hintView;
        hintView.setAlpha(0.0f);
        frameLayout.addView(this.fwdRestrictedHint, LayoutHelper.createFrame(-2, -2.0f, 51, 12.0f, 0.0f, 12.0f, 0.0f));
        this.sharedMediaLayout.setForwardRestrictedHint(this.fwdRestrictedHint);
        if (Build.VERSION.SDK_INT >= 21) {
            decorView = (ViewGroup) getParentActivity().getWindow().getDecorView();
        } else {
            decorView = frameLayout;
        }
        PinchToZoomHelper pinchToZoomHelper = new PinchToZoomHelper(decorView, frameLayout) { // from class: org.telegram.ui.ProfileActivity.24
            Paint statusBarPaint;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.PinchToZoomHelper
            public void invalidateViews() {
                super.invalidateViews();
                ProfileActivity.this.fragmentView.invalidate();
                for (int i4 = 0; i4 < ProfileActivity.this.avatarsViewPager.getChildCount(); i4++) {
                    ProfileActivity.this.avatarsViewPager.getChildAt(i4).invalidate();
                }
                if (ProfileActivity.this.writeButton != null) {
                    ProfileActivity.this.writeButton.invalidate();
                }
            }

            @Override // org.telegram.ui.PinchToZoomHelper
            protected void drawOverlays(Canvas canvas, float alpha, float parentOffsetX, float parentOffsetY, float clipTop, float clipBottom) {
                if (alpha > 0.0f) {
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, ProfileActivity.this.avatarsViewPager.getMeasuredWidth(), ProfileActivity.this.avatarsViewPager.getMeasuredHeight() + AndroidUtilities.dp(30.0f));
                    canvas.saveLayerAlpha(AndroidUtilities.rectTmp, (int) (255.0f * alpha), 31);
                    ProfileActivity.this.avatarContainer2.draw(canvas);
                    if (ProfileActivity.this.actionBar.getOccupyStatusBar() && !SharedConfig.noStatusBar) {
                        if (this.statusBarPaint == null) {
                            Paint paint = new Paint();
                            this.statusBarPaint = paint;
                            paint.setColor(ColorUtils.setAlphaComponent(-16777216, 51));
                        }
                        canvas.drawRect(ProfileActivity.this.actionBar.getX(), ProfileActivity.this.actionBar.getY(), ProfileActivity.this.actionBar.getX() + ProfileActivity.this.actionBar.getMeasuredWidth(), ProfileActivity.this.actionBar.getY() + AndroidUtilities.statusBarHeight, this.statusBarPaint);
                    }
                    canvas.save();
                    canvas.translate(ProfileActivity.this.actionBar.getX(), ProfileActivity.this.actionBar.getY());
                    ProfileActivity.this.actionBar.draw(canvas);
                    canvas.restore();
                    if (ProfileActivity.this.writeButton != null && ProfileActivity.this.writeButton.getVisibility() == 0 && ProfileActivity.this.writeButton.getAlpha() > 0.0f) {
                        canvas.save();
                        float s = (alpha * 0.5f) + 0.5f;
                        canvas.scale(s, s, ProfileActivity.this.writeButton.getX() + (ProfileActivity.this.writeButton.getMeasuredWidth() / 2.0f), ProfileActivity.this.writeButton.getY() + (ProfileActivity.this.writeButton.getMeasuredHeight() / 2.0f));
                        canvas.translate(ProfileActivity.this.writeButton.getX(), ProfileActivity.this.writeButton.getY());
                        ProfileActivity.this.writeButton.draw(canvas);
                        canvas.restore();
                    }
                    canvas.restore();
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.PinchToZoomHelper
            public boolean zoomEnabled(View child, ImageReceiver receiver) {
                return super.zoomEnabled(child, receiver) && ProfileActivity.this.listView.getScrollState() != 1;
            }
        };
        this.pinchToZoomHelper = pinchToZoomHelper;
        pinchToZoomHelper.setCallback(new PinchToZoomHelper.Callback() { // from class: org.telegram.ui.ProfileActivity.25
            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public /* synthetic */ TextureView getCurrentTextureView() {
                return PinchToZoomHelper.Callback.CC.$default$getCurrentTextureView(this);
            }

            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public /* synthetic */ void onZoomFinished(MessageObject messageObject) {
                PinchToZoomHelper.Callback.CC.$default$onZoomFinished(this, messageObject);
            }

            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public void onZoomStarted(MessageObject messageObject) {
                ProfileActivity.this.listView.cancelClickRunnables(true);
                if (ProfileActivity.this.sharedMediaLayout != null && ProfileActivity.this.sharedMediaLayout.getCurrentListView() != null) {
                    ProfileActivity.this.sharedMediaLayout.getCurrentListView().cancelClickRunnables(true);
                }
                Bitmap bitmap = ProfileActivity.this.pinchToZoomHelper.getPhotoImage() == null ? null : ProfileActivity.this.pinchToZoomHelper.getPhotoImage().getBitmap();
                if (bitmap != null) {
                    ProfileActivity.this.topView.setBackgroundColor(ColorUtils.blendARGB(AndroidUtilities.calcBitmapColor(bitmap), ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite), 0.1f));
                }
            }
        });
        this.avatarsViewPager.setPinchToZoomHelper(this.pinchToZoomHelper);
        this.scrimPaint.setAlpha(0);
        this.actionBarBackgroundPaint.setColor(getThemedColor(Theme.key_listSelector));
        this.contentView.blurBehindViews.add(this.sharedMediaLayout);
        updateTtlIcon();
        return this.fragmentView;
    }

    /* renamed from: org.telegram.ui.ProfileActivity$5 */
    /* loaded from: classes4.dex */
    public class AnonymousClass5 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass5() {
            ProfileActivity.this = this$0;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int id) {
            long did;
            String thumb;
            if (ProfileActivity.this.getParentActivity() == null) {
                return;
            }
            if (id == -1) {
                ProfileActivity.this.finishFragment();
                return;
            }
            Integer num = null;
            String str = null;
            boolean z = false;
            if (id == 2) {
                TLRPC.User user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                if (user != null) {
                    if (!ProfileActivity.this.isBot || MessagesController.isSupportUser(user)) {
                        if (ProfileActivity.this.userBlocked) {
                            ProfileActivity.this.getMessagesController().unblockPeer(ProfileActivity.this.userId);
                            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                BulletinFactory.createBanBulletin(ProfileActivity.this, false).show();
                            }
                        } else if (ProfileActivity.this.reportSpam) {
                            ProfileActivity profileActivity = ProfileActivity.this;
                            AlertsCreator.showBlockReportSpamAlert(profileActivity, profileActivity.userId, user, null, ProfileActivity.this.currentEncryptedChat, false, null, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda11
                                @Override // org.telegram.messenger.MessagesStorage.IntCallback
                                public final void run(int i) {
                                    ProfileActivity.AnonymousClass5.this.m4394lambda$onItemClick$0$orgtelegramuiProfileActivity$5(i);
                                }
                            }, ProfileActivity.this.resourcesProvider);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                            builder.setTitle(LocaleController.getString("BlockUser", R.string.BlockUser));
                            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", R.string.AreYouSureBlockContact2, ContactsController.formatName(user.first_name, user.last_name))));
                            builder.setPositiveButton(LocaleController.getString("BlockContact", R.string.BlockContact), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda0
                                @Override // android.content.DialogInterface.OnClickListener
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    ProfileActivity.AnonymousClass5.this.m4395lambda$onItemClick$1$orgtelegramuiProfileActivity$5(dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            AlertDialog dialog = builder.create();
                            ProfileActivity.this.showDialog(dialog);
                            TextView button = (TextView) dialog.getButton(-1);
                            if (button != null) {
                                button.setTextColor(ProfileActivity.this.getThemedColor(Theme.key_dialogTextRed2));
                            }
                        }
                    } else if (!ProfileActivity.this.userBlocked) {
                        ProfileActivity.this.getMessagesController().blockPeer(ProfileActivity.this.userId);
                    } else {
                        ProfileActivity.this.getMessagesController().unblockPeer(ProfileActivity.this.userId);
                        ProfileActivity.this.getSendMessagesHelper().sendMessage("/start", ProfileActivity.this.userId, null, null, null, false, null, null, null, true, 0, null);
                        ProfileActivity.this.finishFragment();
                    }
                }
            } else if (id == 1) {
                TLRPC.User user2 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                Bundle args = new Bundle();
                args.putLong("user_id", user2.id);
                args.putBoolean("addContact", true);
                ProfileActivity.this.presentFragment(new ContactAddActivity(args, ProfileActivity.this.resourcesProvider));
            } else if (id == 3) {
                Bundle args2 = new Bundle();
                args2.putBoolean("onlySelect", true);
                args2.putInt("dialogsType", 3);
                args2.putString("selectAlertString", LocaleController.getString("SendContactToText", R.string.SendContactToText));
                args2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroupText", R.string.SendContactToGroupText));
                DialogsActivity fragment = new DialogsActivity(args2);
                fragment.setDelegate(ProfileActivity.this);
                ProfileActivity.this.presentFragment(fragment);
            } else if (id == 4) {
                Bundle args3 = new Bundle();
                args3.putLong("user_id", ProfileActivity.this.userId);
                ProfileActivity.this.presentFragment(new ContactAddActivity(args3, ProfileActivity.this.resourcesProvider));
            } else if (id == 5) {
                final TLRPC.User user3 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                if (user3 == null || ProfileActivity.this.getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder2 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                builder2.setTitle(LocaleController.getString("DeleteContact", R.string.DeleteContact));
                builder2.setMessage(LocaleController.getString("AreYouSureDeleteContact", R.string.AreYouSureDeleteContact));
                builder2.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda6
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ProfileActivity.AnonymousClass5.this.m4398lambda$onItemClick$2$orgtelegramuiProfileActivity$5(user3, dialogInterface, i);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog dialog2 = builder2.create();
                ProfileActivity.this.showDialog(dialog2);
                TextView button2 = (TextView) dialog2.getButton(-1);
                if (button2 != null) {
                    button2.setTextColor(ProfileActivity.this.getThemedColor(Theme.key_dialogTextRed2));
                }
            } else if (id == 7) {
                ProfileActivity.this.leaveChatPressed();
            } else if (id == 12) {
                Bundle args4 = new Bundle();
                args4.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, ProfileActivity.this.chatId);
                ChatEditActivity fragment2 = new ChatEditActivity(args4);
                fragment2.setInfo(ProfileActivity.this.chatInfo);
                ProfileActivity.this.presentFragment(fragment2);
            } else if (id == 9) {
                final TLRPC.User user4 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                if (user4 == null) {
                    return;
                }
                Bundle args5 = new Bundle();
                args5.putBoolean("onlySelect", true);
                args5.putInt("dialogsType", 2);
                args5.putBoolean("resetDelegate", false);
                args5.putBoolean("closeFragment", false);
                final DialogsActivity fragment3 = new DialogsActivity(args5);
                fragment3.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda2
                    @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
                    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z2) {
                        ProfileActivity.AnonymousClass5.this.m4402lambda$onItemClick$6$orgtelegramuiProfileActivity$5(user4, fragment3, dialogsActivity, arrayList, charSequence, z2);
                    }
                });
                ProfileActivity.this.presentFragment(fragment3);
            } else if (id == 10) {
                String text = null;
                try {
                    if (ProfileActivity.this.userId != 0) {
                        TLRPC.User user5 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                        if (user5 != null) {
                            if (ProfileActivity.this.botInfo != null && ProfileActivity.this.userInfo != null && !TextUtils.isEmpty(ProfileActivity.this.userInfo.about)) {
                                text = String.format("%s https://" + ProfileActivity.this.getMessagesController().linkPrefix + "/%s", ProfileActivity.this.userInfo.about, user5.username);
                            } else {
                                text = String.format("https://" + ProfileActivity.this.getMessagesController().linkPrefix + "/%s", user5.username);
                            }
                        } else {
                            return;
                        }
                    } else if (ProfileActivity.this.chatId != 0) {
                        TLRPC.Chat chat = ProfileActivity.this.getMessagesController().getChat(Long.valueOf(ProfileActivity.this.chatId));
                        if (chat != null) {
                            if (ProfileActivity.this.chatInfo != null && !TextUtils.isEmpty(ProfileActivity.this.chatInfo.about)) {
                                text = String.format("%s\nhttps://" + ProfileActivity.this.getMessagesController().linkPrefix + "/%s", ProfileActivity.this.chatInfo.about, chat.username);
                            } else {
                                text = String.format("https://" + ProfileActivity.this.getMessagesController().linkPrefix + "/%s", chat.username);
                            }
                        } else {
                            return;
                        }
                    }
                    if (TextUtils.isEmpty(text)) {
                        return;
                    }
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType(ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
                    intent.putExtra("android.intent.extra.TEXT", text);
                    ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else if (id == 14) {
                try {
                    if (ProfileActivity.this.currentEncryptedChat != null) {
                        did = DialogObject.makeEncryptedDialogId(ProfileActivity.this.currentEncryptedChat.id);
                    } else if (ProfileActivity.this.userId != 0) {
                        did = ProfileActivity.this.userId;
                    } else if (ProfileActivity.this.chatId != 0) {
                        did = -ProfileActivity.this.chatId;
                    } else {
                        return;
                    }
                    ProfileActivity.this.getMediaDataController().installShortcut(did);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            } else if (id == 15 || id == 16) {
                if (ProfileActivity.this.userId != 0) {
                    TLRPC.User user6 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                    if (user6 != null) {
                        VoIPHelper.startCall(user6, id == 16, ProfileActivity.this.userInfo != null && ProfileActivity.this.userInfo.video_calls_available, ProfileActivity.this.getParentActivity(), ProfileActivity.this.userInfo, ProfileActivity.this.getAccountInstance());
                    }
                } else if (ProfileActivity.this.chatId != 0) {
                    ChatObject.Call call = ProfileActivity.this.getMessagesController().getGroupCall(ProfileActivity.this.chatId, false);
                    if (call != null) {
                        TLRPC.Chat chat2 = ProfileActivity.this.currentChat;
                        Activity parentActivity = ProfileActivity.this.getParentActivity();
                        ProfileActivity profileActivity2 = ProfileActivity.this;
                        VoIPHelper.startCall(chat2, null, null, false, parentActivity, profileActivity2, profileActivity2.getAccountInstance());
                        return;
                    }
                    ProfileActivity profileActivity3 = ProfileActivity.this;
                    VoIPHelper.showGroupCallAlert(profileActivity3, profileActivity3.currentChat, null, false, ProfileActivity.this.getAccountInstance());
                }
            } else if (id == 17) {
                Bundle args6 = new Bundle();
                args6.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, ProfileActivity.this.chatId);
                args6.putInt(CommonProperties.TYPE, 2);
                args6.putBoolean("open_search", true);
                ChatUsersActivity fragment4 = new ChatUsersActivity(args6);
                fragment4.setInfo(ProfileActivity.this.chatInfo);
                ProfileActivity.this.presentFragment(fragment4);
            } else if (id == 18) {
                ProfileActivity.this.openAddMember();
            } else if (id == 19) {
                TLRPC.Chat chat3 = ProfileActivity.this.getMessagesController().getChat(Long.valueOf(ProfileActivity.this.chatId));
                Bundle args7 = new Bundle();
                args7.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, ProfileActivity.this.chatId);
                args7.putBoolean("is_megagroup", chat3.megagroup);
                ProfileActivity.this.presentFragment(new StatisticActivity(args7));
            } else if (id == 22) {
                ProfileActivity.this.openDiscussion();
            } else if (id == 20) {
                AlertDialog.Builder builder3 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                builder3.setTitle(LocaleController.getString("AreYouSureSecretChatTitle", R.string.AreYouSureSecretChatTitle));
                builder3.setMessage(LocaleController.getString("AreYouSureSecretChat", R.string.AreYouSureSecretChat));
                builder3.setPositiveButton(LocaleController.getString("Start", R.string.Start), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda4
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ProfileActivity.AnonymousClass5.this.m4403lambda$onItemClick$7$orgtelegramuiProfileActivity$5(dialogInterface, i);
                    }
                });
                builder3.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                ProfileActivity.this.showDialog(builder3.create());
            } else if (id == 21) {
                if (ProfileActivity.this.getParentActivity() == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT < 23 || ((Build.VERSION.SDK_INT > 28 && !BuildVars.NO_SCOPED_STORAGE) || ProfileActivity.this.getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0)) {
                    ImageLocation location = ProfileActivity.this.avatarsViewPager.getImageLocation(ProfileActivity.this.avatarsViewPager.getRealPosition());
                    if (location == null) {
                        return;
                    }
                    if (location.imageType == 2) {
                        z = true;
                    }
                    final boolean isVideo = z;
                    FileLoader fileLoader = FileLoader.getInstance(ProfileActivity.this.currentAccount);
                    TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = location.location;
                    if (isVideo) {
                        str = "mp4";
                    }
                    File f = fileLoader.getPathToAttach(tL_fileLocationToBeDeprecated, str, true);
                    if (f.exists()) {
                        MediaController.saveFile(f.toString(), ProfileActivity.this.getParentActivity(), 0, null, null, new Runnable() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda9
                            @Override // java.lang.Runnable
                            public final void run() {
                                ProfileActivity.AnonymousClass5.this.m4404lambda$onItemClick$8$orgtelegramuiProfileActivity$5(isVideo);
                            }
                        });
                        return;
                    }
                    return;
                }
                ProfileActivity.this.getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
            } else if (id == 30) {
                ProfileActivity.this.presentFragment(new ChangeNameActivity(ProfileActivity.this.resourcesProvider));
            } else if (id == 31) {
                ProfileActivity.this.presentFragment(new LogoutActivity());
            } else if (id == 33) {
                int position = ProfileActivity.this.avatarsViewPager.getRealPosition();
                final TLRPC.Photo photo = ProfileActivity.this.avatarsViewPager.getPhoto(position);
                if (photo != null) {
                    ProfileActivity.this.avatarsViewPager.startMovePhotoToBegin(position);
                    TLRPC.TL_photos_updateProfilePhoto req = new TLRPC.TL_photos_updateProfilePhoto();
                    req.id = new TLRPC.TL_inputPhoto();
                    req.id.id = photo.id;
                    req.id.access_hash = photo.access_hash;
                    req.id.file_reference = photo.file_reference;
                    final UserConfig userConfig = ProfileActivity.this.getUserConfig();
                    ProfileActivity.this.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda1
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            ProfileActivity.AnonymousClass5.this.m4396lambda$onItemClick$10$orgtelegramuiProfileActivity$5(userConfig, photo, tLObject, tL_error);
                        }
                    });
                    UndoView undoView = ProfileActivity.this.undoView;
                    long j = ProfileActivity.this.userId;
                    if (!photo.video_sizes.isEmpty()) {
                        num = 1;
                    }
                    undoView.showWithAction(j, 22, num);
                    TLRPC.User user7 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(userConfig.clientUserId));
                    TLRPC.PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 800);
                    if (user7 != null) {
                        TLRPC.PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 90);
                        user7.photo.photo_id = photo.id;
                        user7.photo.photo_small = smallSize.location;
                        user7.photo.photo_big = bigSize.location;
                        userConfig.setCurrentUser(user7);
                        userConfig.saveConfig(true);
                        NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                        ProfileActivity.this.updateProfileData(true);
                    }
                    ProfileActivity.this.avatarsViewPager.commitMoveToBegin();
                }
            } else if (id == 34) {
                int position2 = ProfileActivity.this.avatarsViewPager.getRealPosition();
                ImageLocation location2 = ProfileActivity.this.avatarsViewPager.getImageLocation(position2);
                if (location2 != null) {
                    File f2 = FileLoader.getInstance(ProfileActivity.this.currentAccount).getPathToAttach(PhotoViewer.getFileLocation(location2), PhotoViewer.getFileLocationExt(location2), true);
                    boolean isVideo2 = location2.imageType == 2;
                    if (isVideo2) {
                        ImageLocation imageLocation = ProfileActivity.this.avatarsViewPager.getRealImageLocation(position2);
                        thumb = FileLoader.getInstance(ProfileActivity.this.currentAccount).getPathToAttach(PhotoViewer.getFileLocation(imageLocation), PhotoViewer.getFileLocationExt(imageLocation), true).getAbsolutePath();
                    } else {
                        thumb = null;
                    }
                    ProfileActivity.this.imageUpdater.openPhotoForEdit(f2.getAbsolutePath(), thumb, 0, isVideo2);
                }
            } else if (id != 35) {
                if (id == 36) {
                    ProfileActivity.this.onWriteButtonClick();
                } else if (id == 37 && ProfileActivity.this.qrItem != null && ProfileActivity.this.qrItem.getAlpha() > 0.0f) {
                    Bundle args8 = new Bundle();
                    args8.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, ProfileActivity.this.chatId);
                    args8.putLong("user_id", ProfileActivity.this.userId);
                    ProfileActivity.this.presentFragment(new QrActivity(args8));
                }
            } else {
                AlertDialog.Builder builder4 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                ImageLocation location3 = ProfileActivity.this.avatarsViewPager.getImageLocation(ProfileActivity.this.avatarsViewPager.getRealPosition());
                if (location3 == null) {
                    return;
                }
                if (location3.imageType == 2) {
                    builder4.setTitle(LocaleController.getString("AreYouSureDeleteVideoTitle", R.string.AreYouSureDeleteVideoTitle));
                    builder4.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", R.string.AreYouSureDeleteVideo, new Object[0]));
                } else {
                    builder4.setTitle(LocaleController.getString("AreYouSureDeletePhotoTitle", R.string.AreYouSureDeletePhotoTitle));
                    builder4.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", R.string.AreYouSureDeletePhoto, new Object[0]));
                }
                builder4.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda3
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ProfileActivity.AnonymousClass5.this.m4397lambda$onItemClick$11$orgtelegramuiProfileActivity$5(dialogInterface, i);
                    }
                });
                builder4.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder4.create();
                ProfileActivity.this.showDialog(alertDialog);
                TextView button3 = (TextView) alertDialog.getButton(-1);
                if (button3 != null) {
                    button3.setTextColor(ProfileActivity.this.getThemedColor(Theme.key_dialogTextRed2));
                }
            }
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4394lambda$onItemClick$0$orgtelegramuiProfileActivity$5(int param) {
            if (param == 1) {
                ProfileActivity.this.getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                ProfileActivity.this.playProfileAnimation = 0;
                ProfileActivity.this.finishFragment();
                return;
            }
            ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf(ProfileActivity.this.userId));
        }

        /* renamed from: lambda$onItemClick$1$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4395lambda$onItemClick$1$orgtelegramuiProfileActivity$5(DialogInterface dialogInterface, int i) {
            ProfileActivity.this.getMessagesController().blockPeer(ProfileActivity.this.userId);
            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                BulletinFactory.createBanBulletin(ProfileActivity.this, true).show();
            }
        }

        /* renamed from: lambda$onItemClick$2$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4398lambda$onItemClick$2$orgtelegramuiProfileActivity$5(TLRPC.User user, DialogInterface dialogInterface, int i) {
            ArrayList<TLRPC.User> arrayList = new ArrayList<>();
            arrayList.add(user);
            ProfileActivity.this.getContactsController().deleteContact(arrayList, true);
        }

        /* renamed from: lambda$onItemClick$6$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4402lambda$onItemClick$6$orgtelegramuiProfileActivity$5(final TLRPC.User user, final DialogsActivity fragment, final DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
            final long did = ((Long) dids.get(0)).longValue();
            TLRPC.Chat chat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Long.valueOf(-did));
            if (chat != null) {
                if (chat.creator || (chat.admin_rights != null && chat.admin_rights.add_admins)) {
                    ProfileActivity.this.getMessagesController().checkIsInChat(chat, user, new MessagesController.IsInChatCheckedCallback() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda10
                        @Override // org.telegram.messenger.MessagesController.IsInChatCheckedCallback
                        public final void run(boolean z, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str) {
                            ProfileActivity.AnonymousClass5.this.m4400lambda$onItemClick$4$orgtelegramuiProfileActivity$5(did, fragment, z, tL_chatAdminRights, str);
                        }
                    });
                    return;
                }
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
            builder.setTitle(LocaleController.getString("AddBot", R.string.AddBot));
            String chatName = chat == null ? "" : chat.title;
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, UserObject.getUserName(user), chatName)));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.setPositiveButton(LocaleController.getString("AddBot", R.string.AddBot), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda5
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.AnonymousClass5.this.m4401lambda$onItemClick$5$orgtelegramuiProfileActivity$5(did, fragment1, user, dialogInterface, i);
                }
            });
            ProfileActivity.this.showDialog(builder.create());
        }

        /* renamed from: lambda$onItemClick$4$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4400lambda$onItemClick$4$orgtelegramuiProfileActivity$5(final long did, final DialogsActivity fragment, final boolean isInChatAlready, final TLRPC.TL_chatAdminRights rightsAdmin, final String currentRank) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.AnonymousClass5.this.m4399lambda$onItemClick$3$orgtelegramuiProfileActivity$5(did, rightsAdmin, currentRank, isInChatAlready, fragment);
                }
            });
        }

        /* renamed from: lambda$onItemClick$3$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4399lambda$onItemClick$3$orgtelegramuiProfileActivity$5(long did, TLRPC.TL_chatAdminRights rightsAdmin, String currentRank, boolean isInChatAlready, final DialogsActivity fragment) {
            ChatRightsEditActivity editRightsActivity = new ChatRightsEditActivity(ProfileActivity.this.userId, -did, rightsAdmin, null, null, currentRank, 2, true, !isInChatAlready, null);
            editRightsActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ProfileActivity.5.1
                @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin2, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                    ProfileActivity.this.disableProfileAnimation = true;
                    fragment.removeSelfFromStack();
                    ProfileActivity.this.getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }

                @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                public void didChangeOwner(TLRPC.User user) {
                }
            });
            ProfileActivity.this.presentFragment(editRightsActivity);
        }

        /* renamed from: lambda$onItemClick$5$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4401lambda$onItemClick$5$orgtelegramuiProfileActivity$5(long did, DialogsActivity fragment1, TLRPC.User user, DialogInterface di, int i) {
            ProfileActivity.this.disableProfileAnimation = true;
            Bundle args1 = new Bundle();
            args1.putBoolean("scrollToTopOnResume", true);
            args1.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did);
            if (!ProfileActivity.this.getMessagesController().checkCanOpenChat(args1, fragment1)) {
                return;
            }
            ChatActivity chatActivity = new ChatActivity(args1);
            ProfileActivity.this.getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
            ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            ProfileActivity.this.getMessagesController().addUserToChat(-did, user, 0, null, chatActivity, true, null, null);
            ProfileActivity.this.presentFragment(chatActivity, true);
        }

        /* renamed from: lambda$onItemClick$7$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4403lambda$onItemClick$7$orgtelegramuiProfileActivity$5(DialogInterface dialogInterface, int i) {
            ProfileActivity.this.creatingChat = true;
            ProfileActivity.this.getSecretChatHelper().startSecretChat(ProfileActivity.this.getParentActivity(), ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId)));
        }

        /* renamed from: lambda$onItemClick$8$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4404lambda$onItemClick$8$orgtelegramuiProfileActivity$5(boolean isVideo) {
            if (ProfileActivity.this.getParentActivity() == null) {
                return;
            }
            BulletinFactory.createSaveToGalleryBulletin(ProfileActivity.this, isVideo, null).show();
        }

        /* renamed from: lambda$onItemClick$10$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4396lambda$onItemClick$10$orgtelegramuiProfileActivity$5(final UserConfig userConfig, final TLRPC.Photo photo, final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.AnonymousClass5.this.m4405lambda$onItemClick$9$orgtelegramuiProfileActivity$5(response, userConfig, photo);
                }
            });
        }

        /* renamed from: lambda$onItemClick$9$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4405lambda$onItemClick$9$orgtelegramuiProfileActivity$5(TLObject response, UserConfig userConfig, TLRPC.Photo photo) {
            ProfileActivity.this.avatarsViewPager.finishSettingMainPhoto();
            if (response instanceof TLRPC.TL_photos_photo) {
                TLRPC.TL_photos_photo photos_photo = (TLRPC.TL_photos_photo) response;
                ProfileActivity.this.getMessagesController().putUsers(photos_photo.users, false);
                TLRPC.User user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(userConfig.clientUserId));
                if (photos_photo.photo instanceof TLRPC.TL_photo) {
                    ProfileActivity.this.avatarsViewPager.replaceFirstPhoto(photo, photos_photo.photo);
                    if (user != null) {
                        user.photo.photo_id = photos_photo.photo.id;
                        userConfig.setCurrentUser(user);
                        userConfig.saveConfig(true);
                    }
                }
            }
        }

        /* renamed from: lambda$onItemClick$11$org-telegram-ui-ProfileActivity$5 */
        public /* synthetic */ void m4397lambda$onItemClick$11$orgtelegramuiProfileActivity$5(DialogInterface dialogInterface, int i) {
            int position = ProfileActivity.this.avatarsViewPager.getRealPosition();
            TLRPC.Photo photo = ProfileActivity.this.avatarsViewPager.getPhoto(position);
            if (ProfileActivity.this.avatarsViewPager.getRealCount() == 1) {
                ProfileActivity.this.setForegroundImage(true);
            }
            if (photo == null || ProfileActivity.this.avatarsViewPager.getRealPosition() == 0) {
                ProfileActivity.this.getMessagesController().deleteUserPhoto(null);
            } else {
                TLRPC.TL_inputPhoto inputPhoto = new TLRPC.TL_inputPhoto();
                inputPhoto.id = photo.id;
                inputPhoto.access_hash = photo.access_hash;
                inputPhoto.file_reference = photo.file_reference;
                if (inputPhoto.file_reference == null) {
                    inputPhoto.file_reference = new byte[0];
                }
                ProfileActivity.this.getMessagesController().deleteUserPhoto(inputPhoto);
                ProfileActivity.this.getMessagesStorage().clearUserPhoto(ProfileActivity.this.userId, photo.id);
            }
            if (ProfileActivity.this.avatarsViewPager.removePhotoAtIndex(position)) {
                ProfileActivity.this.avatarsViewPager.setVisibility(8);
                ProfileActivity.this.avatarImage.setForegroundAlpha(1.0f);
                ProfileActivity.this.avatarContainer.setVisibility(0);
                ProfileActivity.this.doNotSetForeground = true;
                View view = ProfileActivity.this.layoutManager.findViewByPosition(0);
                if (view != null) {
                    ProfileActivity.this.listView.smoothScrollBy(0, view.getTop() - AndroidUtilities.dp(88.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$6 */
    /* loaded from: classes4.dex */
    class AnonymousClass6 extends NestedFrameLayout {
        private boolean ignoreLayout;
        private Paint grayPaint = new Paint();
        private final ArrayList<View> sortedChildren = new ArrayList<>();
        private final Comparator<View> viewComparator = ProfileActivity$6$$ExternalSyntheticLambda0.INSTANCE;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass6(Context context) {
            super(context);
            ProfileActivity.this = this$0;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (!ProfileActivity.this.pinchToZoomHelper.isInOverlayMode()) {
                if (ProfileActivity.this.sharedMediaLayout == null || !ProfileActivity.this.sharedMediaLayout.isInFastScroll() || !ProfileActivity.this.sharedMediaLayout.isPinnedToTop()) {
                    if (ProfileActivity.this.sharedMediaLayout != null && ProfileActivity.this.sharedMediaLayout.checkPinchToZoom(ev)) {
                        return true;
                    }
                    return super.dispatchTouchEvent(ev);
                }
                return ProfileActivity.this.sharedMediaLayout.dispatchFastScrollEvent(ev);
            }
            return ProfileActivity.this.pinchToZoomHelper.onTouchEvent(ev);
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            boolean changed;
            int paddingTop;
            int paddingBottom;
            int paddingBottom2;
            View view;
            int pos;
            boolean layout;
            int paddingBottom3;
            int paddingTop2;
            int paddingBottom4;
            int actionBarHeight = ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
            if (ProfileActivity.this.listView != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ProfileActivity.this.listView.getLayoutParams();
                if (layoutParams.topMargin != actionBarHeight) {
                    layoutParams.topMargin = actionBarHeight;
                }
            }
            if (ProfileActivity.this.searchListView != null) {
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) ProfileActivity.this.searchListView.getLayoutParams();
                if (layoutParams2.topMargin != actionBarHeight) {
                    layoutParams2.topMargin = actionBarHeight;
                }
            }
            int height = View.MeasureSpec.getSize(heightMeasureSpec);
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
            if (ProfileActivity.this.lastMeasuredContentWidth != getMeasuredWidth() || ProfileActivity.this.lastMeasuredContentHeight != getMeasuredHeight()) {
                boolean changed2 = (ProfileActivity.this.lastMeasuredContentWidth == 0 || ProfileActivity.this.lastMeasuredContentWidth == getMeasuredWidth()) ? false : true;
                ProfileActivity.this.listContentHeight = 0;
                int count = ProfileActivity.this.listAdapter.getItemCount();
                ProfileActivity.this.lastMeasuredContentWidth = getMeasuredWidth();
                ProfileActivity.this.lastMeasuredContentHeight = getMeasuredHeight();
                int ws = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), C.BUFFER_FLAG_ENCRYPTED);
                int hs = View.MeasureSpec.makeMeasureSpec(ProfileActivity.this.listView.getMeasuredHeight(), 0);
                ProfileActivity.this.positionToOffset.clear();
                for (int i = 0; i < count; i++) {
                    int type = ProfileActivity.this.listAdapter.getItemViewType(i);
                    ProfileActivity.this.positionToOffset.put(Integer.valueOf(i), Integer.valueOf(ProfileActivity.this.listContentHeight));
                    if (type != 13) {
                        RecyclerView.ViewHolder holder = ProfileActivity.this.listAdapter.createViewHolder(null, type);
                        ProfileActivity.this.listAdapter.onBindViewHolder(holder, i);
                        holder.itemView.measure(ws, hs);
                        ProfileActivity.access$7512(ProfileActivity.this, holder.itemView.getMeasuredHeight());
                    } else {
                        ProfileActivity profileActivity = ProfileActivity.this;
                        ProfileActivity.access$7512(profileActivity, profileActivity.listView.getMeasuredHeight());
                    }
                }
                if (ProfileActivity.this.emptyView != null) {
                    ((FrameLayout.LayoutParams) ProfileActivity.this.emptyView.getLayoutParams()).topMargin = AndroidUtilities.dp(88.0f) + AndroidUtilities.statusBarHeight;
                }
                changed = changed2;
            } else {
                changed = false;
            }
            if (ProfileActivity.this.fragmentOpened || (!ProfileActivity.this.expandPhoto && (!ProfileActivity.this.openAnimationInProgress || ProfileActivity.this.playProfileAnimation != 2))) {
                if (ProfileActivity.this.fragmentOpened && !ProfileActivity.this.openAnimationInProgress && !ProfileActivity.this.firstLayout) {
                    this.ignoreLayout = true;
                    if (!ProfileActivity.this.isInLandscapeMode && !AndroidUtilities.isTablet()) {
                        int paddingTop3 = ProfileActivity.this.listView.getMeasuredWidth();
                        paddingBottom = Math.max(0, getMeasuredHeight() - ((ProfileActivity.this.listContentHeight + AndroidUtilities.dp(88.0f)) + actionBarHeight));
                        paddingTop = paddingTop3;
                    } else {
                        paddingBottom = 0;
                        paddingTop = AndroidUtilities.dp(88.0f);
                    }
                    if (ProfileActivity.this.banFromGroup == 0) {
                        ProfileActivity.this.listView.setBottomGlowOffset(0);
                        paddingBottom2 = paddingBottom;
                    } else {
                        int paddingBottom5 = paddingBottom + AndroidUtilities.dp(48.0f);
                        ProfileActivity.this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
                        paddingBottom2 = paddingBottom5;
                    }
                    int currentPaddingTop = ProfileActivity.this.listView.getPaddingTop();
                    View view2 = null;
                    int pos2 = -1;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= ProfileActivity.this.listView.getChildCount()) {
                            break;
                        }
                        int p = ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i2));
                        if (p != -1) {
                            view2 = ProfileActivity.this.listView.getChildAt(i2);
                            pos2 = p;
                            break;
                        }
                        i2++;
                    }
                    if (view2 == null) {
                        View view3 = ProfileActivity.this.listView.getChildAt(0);
                        if (view3 != null) {
                            RecyclerView.ViewHolder holder2 = ProfileActivity.this.listView.findContainingViewHolder(view3);
                            int pos3 = holder2.getAdapterPosition();
                            if (pos3 != -1) {
                                view = view3;
                                pos = pos3;
                            } else {
                                int pos4 = holder2.getPosition();
                                view = view3;
                                pos = pos4;
                            }
                        } else {
                            view = view3;
                            pos = pos2;
                        }
                    } else {
                        view = view2;
                        pos = pos2;
                    }
                    int top = paddingTop;
                    if (view != null) {
                        top = view.getTop();
                    }
                    boolean layout2 = false;
                    if (!ProfileActivity.this.actionBar.isSearchFieldVisible() || ProfileActivity.this.sharedMediaRow < 0) {
                        if (ProfileActivity.this.invalidateScroll || currentPaddingTop != paddingTop) {
                            if (ProfileActivity.this.savedScrollPosition >= 0) {
                                ProfileActivity.this.layoutManager.scrollToPositionWithOffset(ProfileActivity.this.savedScrollPosition, ProfileActivity.this.savedScrollOffset - paddingTop);
                            } else if ((changed && ProfileActivity.this.allowPullingDown) || view == null) {
                                ProfileActivity.this.layoutManager.scrollToPositionWithOffset(0, AndroidUtilities.dp(88.0f) - paddingTop);
                            } else {
                                if (pos == 0 && !ProfileActivity.this.allowPullingDown && top > AndroidUtilities.dp(88.0f)) {
                                    top = AndroidUtilities.dp(88.0f);
                                }
                                ProfileActivity.this.layoutManager.scrollToPositionWithOffset(pos, top - paddingTop);
                                layout2 = true;
                            }
                        }
                    } else {
                        ProfileActivity.this.layoutManager.scrollToPositionWithOffset(ProfileActivity.this.sharedMediaRow, -paddingTop);
                        layout2 = true;
                    }
                    if (currentPaddingTop == paddingTop && ProfileActivity.this.listView.getPaddingBottom() == paddingBottom2) {
                        layout = layout2;
                    } else {
                        ProfileActivity.this.listView.setPadding(0, paddingTop, 0, paddingBottom2);
                        layout = true;
                    }
                    if (layout) {
                        measureChildWithMargins(ProfileActivity.this.listView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        try {
                            ProfileActivity.this.listView.layout(0, actionBarHeight, ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getMeasuredHeight() + actionBarHeight);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    this.ignoreLayout = false;
                    return;
                }
                return;
            }
            this.ignoreLayout = true;
            if (ProfileActivity.this.expandPhoto) {
                if (ProfileActivity.this.searchItem != null) {
                    ProfileActivity.this.searchItem.setAlpha(0.0f);
                    ProfileActivity.this.searchItem.setEnabled(false);
                    ProfileActivity.this.searchItem.setVisibility(8);
                }
                ProfileActivity.this.nameTextView[1].setTextColor(-1);
                ProfileActivity.this.onlineTextView[1].setTextColor(Color.argb(179, 255, 255, 255));
                ProfileActivity.this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
                ProfileActivity.this.actionBar.setItemsColor(-1, false);
                ProfileActivity.this.overlaysView.setOverlaysVisible();
                ProfileActivity.this.overlaysView.setAlphaValue(1.0f, false);
                ProfileActivity.this.avatarImage.setForegroundAlpha(1.0f);
                ProfileActivity.this.avatarContainer.setVisibility(8);
                ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                ProfileActivity.this.avatarsViewPager.setVisibility(0);
                ProfileActivity.this.expandPhoto = false;
            }
            ProfileActivity.this.allowPullingDown = true;
            ProfileActivity.this.isPulledDown = true;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, true);
            if (ProfileActivity.this.otherItem != null) {
                if (!ProfileActivity.this.getMessagesController().isChatNoForwards(ProfileActivity.this.currentChat)) {
                    ProfileActivity.this.otherItem.showSubItem(21);
                } else {
                    ProfileActivity.this.otherItem.hideSubItem(21);
                }
                if (ProfileActivity.this.imageUpdater != null) {
                    ProfileActivity.this.otherItem.showSubItem(34);
                    ProfileActivity.this.otherItem.showSubItem(35);
                    ProfileActivity.this.otherItem.hideSubItem(31);
                }
            }
            ProfileActivity.this.currentExpanAnimatorFracture = 1.0f;
            if (!ProfileActivity.this.isInLandscapeMode) {
                paddingTop2 = ProfileActivity.this.listView.getMeasuredWidth();
                paddingBottom3 = Math.max(0, getMeasuredHeight() - ((ProfileActivity.this.listContentHeight + AndroidUtilities.dp(88.0f)) + actionBarHeight));
            } else {
                paddingTop2 = AndroidUtilities.dp(88.0f);
                paddingBottom3 = 0;
            }
            if (ProfileActivity.this.banFromGroup == 0) {
                ProfileActivity.this.listView.setBottomGlowOffset(0);
                paddingBottom4 = paddingBottom3;
            } else {
                int paddingBottom6 = paddingBottom3 + AndroidUtilities.dp(48.0f);
                ProfileActivity.this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
                paddingBottom4 = paddingBottom6;
            }
            ProfileActivity.this.initialAnimationExtraHeight = paddingTop2 - actionBarHeight;
            ProfileActivity.this.layoutManager.scrollToPositionWithOffset(0, -actionBarHeight);
            ProfileActivity.this.listView.setPadding(0, paddingTop2, 0, paddingBottom4);
            measureChildWithMargins(ProfileActivity.this.listView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            ProfileActivity.this.listView.layout(0, actionBarHeight, ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getMeasuredHeight() + actionBarHeight);
            this.ignoreLayout = false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            ProfileActivity.this.savedScrollPosition = -1;
            ProfileActivity.this.firstLayout = false;
            ProfileActivity.this.invalidateScroll = false;
            ProfileActivity.this.checkListViewScroll();
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        public static /* synthetic */ int lambda$$0(View view, View view2) {
            return (int) (view.getY() - view2.getY());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            int i;
            boolean currentHasBackground;
            int currentY;
            ProfileActivity.this.whitePaint.setColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
            float f = 1.0f;
            if (ProfileActivity.this.listView.getVisibility() != 0) {
                int top = ProfileActivity.this.searchListView.getTop();
                canvas.drawRect(0.0f, ProfileActivity.this.searchTransitionOffset + top + ProfileActivity.this.extraHeight, getMeasuredWidth(), getMeasuredHeight() + top, ProfileActivity.this.whitePaint);
            } else {
                this.grayPaint.setColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundGray));
                if (ProfileActivity.this.transitionAnimationInProress) {
                    ProfileActivity.this.whitePaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f));
                }
                if (ProfileActivity.this.transitionAnimationInProress) {
                    this.grayPaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f));
                }
                int count = ProfileActivity.this.listView.getChildCount();
                this.sortedChildren.clear();
                boolean hasRemovingItems = false;
                for (int i2 = 0; i2 < count; i2++) {
                    if (ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i2)) != -1) {
                        this.sortedChildren.add(ProfileActivity.this.listView.getChildAt(i2));
                    } else {
                        hasRemovingItems = true;
                    }
                }
                Collections.sort(this.sortedChildren, this.viewComparator);
                float lastY = ProfileActivity.this.listView.getY();
                int count2 = this.sortedChildren.size();
                if (!ProfileActivity.this.openAnimationInProgress && count2 > 0 && !hasRemovingItems) {
                    lastY += this.sortedChildren.get(0).getY();
                }
                float alpha = 1.0f;
                boolean hasBackground = false;
                float lastY2 = lastY;
                int i3 = 0;
                while (i3 < count2) {
                    View child = this.sortedChildren.get(i3);
                    boolean currentHasBackground2 = child.getBackground() != null;
                    int currentY2 = (int) (ProfileActivity.this.listView.getY() + child.getY());
                    if (hasBackground == currentHasBackground2) {
                        if (child.getAlpha() != f) {
                            i = i3;
                        } else {
                            alpha = 1.0f;
                            i = i3;
                        }
                    } else {
                        if (hasBackground) {
                            currentY = currentY2;
                            currentHasBackground = currentHasBackground2;
                            i = i3;
                            canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getMeasuredWidth() + ProfileActivity.this.listView.getX(), currentY2, this.grayPaint);
                        } else {
                            currentY = currentY2;
                            currentHasBackground = currentHasBackground2;
                            i = i3;
                            if (alpha == f) {
                                canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), currentY, ProfileActivity.this.whitePaint);
                            } else {
                                canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), currentY, this.grayPaint);
                                ProfileActivity.this.whitePaint.setAlpha((int) (alpha * 255.0f));
                                canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), currentY, ProfileActivity.this.whitePaint);
                                ProfileActivity.this.whitePaint.setAlpha(255);
                            }
                        }
                        boolean hasBackground2 = currentHasBackground;
                        hasBackground = hasBackground2;
                        lastY2 = currentY;
                        alpha = child.getAlpha();
                    }
                    i3 = i + 1;
                    f = 1.0f;
                }
                if (hasBackground) {
                    canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getBottom(), this.grayPaint);
                } else if (alpha == 1.0f) {
                    canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getBottom(), ProfileActivity.this.whitePaint);
                } else {
                    canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getBottom(), this.grayPaint);
                    ProfileActivity.this.whitePaint.setAlpha((int) (alpha * 255.0f));
                    canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getBottom(), ProfileActivity.this.whitePaint);
                    ProfileActivity.this.whitePaint.setAlpha(255);
                }
            }
            super.dispatchDraw(canvas);
            if (ProfileActivity.this.profileTransitionInProgress && ProfileActivity.this.parentLayout.fragmentsStack.size() > 1) {
                BaseFragment fragment = ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                if (fragment instanceof ChatActivity) {
                    ChatActivity chatActivity = (ChatActivity) fragment;
                    FragmentContextView fragmentContextView = chatActivity.getFragmentContextView();
                    if (fragmentContextView != null && fragmentContextView.isCallStyle()) {
                        float progress = ProfileActivity.this.extraHeight / AndroidUtilities.dpf2(fragmentContextView.getStyleHeight());
                        if (progress > 1.0f) {
                            progress = 1.0f;
                        }
                        canvas.save();
                        canvas.translate(fragmentContextView.getX(), fragmentContextView.getY());
                        fragmentContextView.setDrawOverlay(true);
                        fragmentContextView.setCollapseTransition(true, ProfileActivity.this.extraHeight, progress);
                        fragmentContextView.draw(canvas);
                        fragmentContextView.setCollapseTransition(false, ProfileActivity.this.extraHeight, progress);
                        fragmentContextView.setDrawOverlay(false);
                        canvas.restore();
                    }
                }
            }
            if (ProfileActivity.this.scrimPaint.getAlpha() > 0) {
                canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), ProfileActivity.this.scrimPaint);
            }
            if (ProfileActivity.this.scrimView != null) {
                int c = canvas.save();
                canvas.translate(ProfileActivity.this.scrimView.getLeft(), ProfileActivity.this.scrimView.getTop());
                if (ProfileActivity.this.scrimView == ProfileActivity.this.actionBar.getBackButton()) {
                    int r = Math.max(ProfileActivity.this.scrimView.getMeasuredWidth(), ProfileActivity.this.scrimView.getMeasuredHeight()) / 2;
                    int wasAlpha = ProfileActivity.this.actionBarBackgroundPaint.getAlpha();
                    ProfileActivity.this.actionBarBackgroundPaint.setAlpha((int) ((wasAlpha * (ProfileActivity.this.scrimPaint.getAlpha() / 255.0f)) / 0.3f));
                    canvas.drawCircle(r, r, r * 0.7f, ProfileActivity.this.actionBarBackgroundPaint);
                    ProfileActivity.this.actionBarBackgroundPaint.setAlpha(wasAlpha);
                }
                ProfileActivity.this.scrimView.draw(canvas);
                canvas.restoreToCount(c);
            }
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (ProfileActivity.this.pinchToZoomHelper.isInOverlayMode() && (child == ProfileActivity.this.avatarContainer2 || child == ProfileActivity.this.actionBar || child == ProfileActivity.this.writeButton)) {
                return true;
            }
            return super.drawChild(canvas, child, drawingTime);
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$10 */
    /* loaded from: classes4.dex */
    public class AnonymousClass10 extends DefaultItemAnimator {
        int animationIndex = -1;

        AnonymousClass10() {
            ProfileActivity.this = this$0;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.recyclerview.widget.DefaultItemAnimator
        public void onAllAnimationsDone() {
            super.onAllAnimationsDone();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$10$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.AnonymousClass10.this.m4391lambda$onAllAnimationsDone$0$orgtelegramuiProfileActivity$10();
                }
            });
        }

        /* renamed from: lambda$onAllAnimationsDone$0$org-telegram-ui-ProfileActivity$10 */
        public /* synthetic */ void m4391lambda$onAllAnimationsDone$0$orgtelegramuiProfileActivity$10() {
            ProfileActivity.this.getNotificationCenter().onAnimationFinish(this.animationIndex);
        }

        @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public void runPendingAnimations() {
            boolean removalsPending = !this.mPendingRemovals.isEmpty();
            boolean movesPending = !this.mPendingMoves.isEmpty();
            boolean changesPending = !this.mPendingChanges.isEmpty();
            boolean additionsPending = !this.mPendingAdditions.isEmpty();
            if (removalsPending || movesPending || additionsPending || changesPending) {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$10$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ProfileActivity.AnonymousClass10.this.m4392lambda$runPendingAnimations$1$orgtelegramuiProfileActivity$10(valueAnimator2);
                    }
                });
                valueAnimator.setDuration(getMoveDuration());
                valueAnimator.start();
                this.animationIndex = ProfileActivity.this.getNotificationCenter().setAnimationInProgress(this.animationIndex, null);
            }
            super.runPendingAnimations();
        }

        /* renamed from: lambda$runPendingAnimations$1$org-telegram-ui-ProfileActivity$10 */
        public /* synthetic */ void m4392lambda$runPendingAnimations$1$orgtelegramuiProfileActivity$10(ValueAnimator valueAnimator1) {
            ProfileActivity.this.listView.invalidate();
        }

        @Override // androidx.recyclerview.widget.DefaultItemAnimator
        protected long getAddAnimationDelay(long removeDuration, long moveDuration, long changeDuration) {
            return 0L;
        }

        @Override // androidx.recyclerview.widget.DefaultItemAnimator
        protected long getMoveAnimationDelay() {
            return 0L;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public long getMoveDuration() {
            return 220L;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public long getRemoveDuration() {
            return 220L;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public long getAddDuration() {
            return 220L;
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4359lambda$createView$4$orgtelegramuiProfileActivity(final long did, Context context, View view, int position, float x, float y) {
        long flags;
        if (getParentActivity() == null) {
            return;
        }
        this.listView.stopScroll();
        if (position != this.settingsKeyRow) {
            if (position == this.settingsTimerRow) {
                showDialog(AlertsCreator.createTTLAlert(getParentActivity(), this.currentEncryptedChat, this.resourcesProvider).create());
                return;
            } else if (position != this.notificationsRow) {
                if (position == this.unblockRow) {
                    getMessagesController().unblockPeer(this.userId);
                    if (BulletinFactory.canShowBulletin(this)) {
                        BulletinFactory.createBanBulletin(this, false).show();
                        return;
                    }
                    return;
                } else if (position == this.addToGroupButtonRow) {
                    try {
                        this.actionBar.getActionBarMenuOnItemClick().onItemClick(9);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    return;
                } else if (position == this.sendMessageRow) {
                    onWriteButtonClick();
                    return;
                } else if (position == this.reportRow) {
                    AlertsCreator.createReportAlert(getParentActivity(), getDialogId(), 0, this, this.resourcesProvider, null);
                    return;
                } else if (position < this.membersStartRow || position >= this.membersEndRow) {
                    if (position == this.addMemberRow) {
                        openAddMember();
                        return;
                    } else if (position == this.usernameRow) {
                        processOnClickOrPress(position, view);
                        return;
                    } else if (position == this.locationRow) {
                        if (this.chatInfo.location instanceof TLRPC.TL_channelLocation) {
                            LocationActivity fragment = new LocationActivity(5);
                            fragment.setChatLocation(this.chatId, (TLRPC.TL_channelLocation) this.chatInfo.location);
                            presentFragment(fragment);
                            return;
                        }
                        return;
                    } else if (position == this.joinRow) {
                        getMessagesController().addUserToChat(this.currentChat.id, getUserConfig().getCurrentUser(), 0, null, this, null);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
                        return;
                    } else if (position != this.subscribersRow) {
                        if (position != this.subscribersRequestsRow) {
                            if (position != this.administratorsRow) {
                                if (position != this.blockedUsersRow) {
                                    if (position == this.notificationRow) {
                                        presentFragment(new NotificationsSettingsActivity());
                                        return;
                                    } else if (position == this.privacyRow) {
                                        presentFragment(new PrivacySettingsActivity());
                                        return;
                                    } else if (position == this.dataRow) {
                                        presentFragment(new DataSettingsActivity());
                                        return;
                                    } else if (position == this.chatRow) {
                                        presentFragment(new ThemeActivity(0));
                                        return;
                                    } else if (position == this.filtersRow) {
                                        presentFragment(new FiltersSetupActivity());
                                        return;
                                    } else if (position == this.stickersRow) {
                                        presentFragment(new StickersActivity(0));
                                        return;
                                    } else if (position == this.devicesRow) {
                                        presentFragment(new SessionsActivity(0));
                                        return;
                                    } else if (position == this.questionRow) {
                                        showDialog(AlertsCreator.createSupportAlert(this, this.resourcesProvider));
                                        return;
                                    } else if (position == this.faqRow) {
                                        Browser.openUrl(getParentActivity(), LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
                                        return;
                                    } else if (position == this.policyRow) {
                                        Browser.openUrl(getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl));
                                        return;
                                    } else if (position == this.sendLogsRow) {
                                        sendLogs(false);
                                        return;
                                    } else if (position == this.sendLastLogsRow) {
                                        sendLogs(true);
                                        return;
                                    } else if (position == this.clearLogsRow) {
                                        FileLog.cleanupLogs();
                                        return;
                                    } else if (position != this.switchBackendRow) {
                                        if (position == this.languageRow) {
                                            presentFragment(new LanguageSelectActivity());
                                            return;
                                        } else if (position == this.setUsernameRow) {
                                            presentFragment(new ChangeUsernameActivity());
                                            return;
                                        } else if (position == this.bioRow) {
                                            if (this.userInfo != null) {
                                                presentFragment(new ChangeBioActivity());
                                                return;
                                            }
                                            return;
                                        } else if (position == this.numberRow) {
                                            presentFragment(new ActionIntroActivity(3));
                                            return;
                                        } else if (position == this.setAvatarRow) {
                                            onWriteButtonClick();
                                            return;
                                        } else if (position == this.premiumRow) {
                                            presentFragment(new PremiumPreviewFragment("settings"));
                                            return;
                                        } else {
                                            processOnClickOrPress(position, view);
                                            return;
                                        }
                                    } else if (getParentActivity() == null) {
                                        return;
                                    } else {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
                                        builder1.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                                        builder1.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                        builder1.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda36
                                            @Override // android.content.DialogInterface.OnClickListener
                                            public final void onClick(DialogInterface dialogInterface, int i) {
                                                ProfileActivity.this.m4358lambda$createView$3$orgtelegramuiProfileActivity(dialogInterface, i);
                                            }
                                        });
                                        builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                        showDialog(builder1.create());
                                        return;
                                    }
                                }
                                Bundle args = new Bundle();
                                args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
                                args.putInt(CommonProperties.TYPE, 0);
                                ChatUsersActivity fragment2 = new ChatUsersActivity(args);
                                fragment2.setInfo(this.chatInfo);
                                presentFragment(fragment2);
                                return;
                            }
                            Bundle args2 = new Bundle();
                            args2.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
                            args2.putInt(CommonProperties.TYPE, 1);
                            ChatUsersActivity fragment3 = new ChatUsersActivity(args2);
                            fragment3.setInfo(this.chatInfo);
                            presentFragment(fragment3);
                            return;
                        }
                        MemberRequestsActivity activity = new MemberRequestsActivity(this.chatId);
                        presentFragment(activity);
                        return;
                    } else {
                        Bundle args3 = new Bundle();
                        args3.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
                        args3.putInt(CommonProperties.TYPE, 2);
                        ChatUsersActivity fragment4 = new ChatUsersActivity(args3);
                        fragment4.setInfo(this.chatInfo);
                        presentFragment(fragment4);
                        return;
                    }
                } else {
                    TLRPC.ChatParticipant participant = !this.sortedUsers.isEmpty() ? this.chatInfo.participants.participants.get(this.sortedUsers.get(position - this.membersStartRow).intValue()) : this.chatInfo.participants.participants.get(position - this.membersStartRow);
                    onMemberClick(participant, false);
                    return;
                }
            } else if ((LocaleController.isRTL && x <= AndroidUtilities.dp(76.0f)) || (!LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))) {
                NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                boolean checked = !checkCell.isChecked();
                boolean defaultEnabled = getNotificationsController().isGlobalNotificationsEnabled(did);
                if (checked) {
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                    SharedPreferences.Editor editor = preferences.edit();
                    if (defaultEnabled) {
                        editor.remove("notify2_" + did);
                    } else {
                        editor.putInt("notify2_" + did, 0);
                    }
                    getMessagesStorage().setDialogFlags(did, 0L);
                    editor.commit();
                    TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(did);
                    if (dialog != null) {
                        dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                    }
                } else {
                    SharedPreferences preferences2 = MessagesController.getNotificationsSettings(this.currentAccount);
                    SharedPreferences.Editor editor2 = preferences2.edit();
                    if (!defaultEnabled) {
                        editor2.remove("notify2_" + did);
                        flags = 0L;
                    } else {
                        editor2.putInt("notify2_" + did, 2);
                        flags = 1L;
                    }
                    getNotificationsController().removeNotificationsForDialog(did);
                    getMessagesStorage().setDialogFlags(did, flags);
                    editor2.commit();
                    TLRPC.Dialog dialog2 = getMessagesController().dialogs_dict.get(did);
                    if (dialog2 != null) {
                        dialog2.notify_settings = new TLRPC.TL_peerNotifySettings();
                        if (defaultEnabled) {
                            dialog2.notify_settings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                }
                getNotificationsController().updateServerNotificationsSettings(did);
                checkCell.setChecked(checked);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.notificationsRow);
                if (holder != null) {
                    this.listAdapter.onBindViewHolder(holder, this.notificationsRow);
                    return;
                }
                return;
            } else {
                ChatNotificationsPopupWrapper chatNotificationsPopupWrapper = new ChatNotificationsPopupWrapper(context, this.currentAccount, null, true, true, new ChatNotificationsPopupWrapper.Callback() { // from class: org.telegram.ui.ProfileActivity.12
                    @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                    public /* synthetic */ void dismiss() {
                        ChatNotificationsPopupWrapper.Callback.CC.$default$dismiss(this);
                    }

                    @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                    public void toggleSound() {
                        SharedPreferences preferences3 = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount);
                        int i = 1;
                        boolean enabled = !preferences3.getBoolean("sound_enabled_" + did, true);
                        preferences3.edit().putBoolean("sound_enabled_" + did, enabled).apply();
                        if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                            ProfileActivity profileActivity = ProfileActivity.this;
                            if (enabled) {
                                i = 0;
                            }
                            BulletinFactory.createSoundEnabledBulletin(profileActivity, i, profileActivity.getResourceProvider()).show();
                        }
                    }

                    @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                    public void muteFor(int timeInSeconds) {
                        if (timeInSeconds != 0) {
                            ProfileActivity.this.getNotificationsController().muteUntil(did, timeInSeconds);
                            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                ProfileActivity profileActivity = ProfileActivity.this;
                                BulletinFactory.createMuteBulletin(profileActivity, 5, timeInSeconds, profileActivity.getResourceProvider()).show();
                            }
                            if (ProfileActivity.this.notificationsRow >= 0) {
                                ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.notificationsRow);
                                return;
                            }
                            return;
                        }
                        if (ProfileActivity.this.getMessagesController().isDialogMuted(did)) {
                            toggleMute();
                        }
                        if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                            ProfileActivity profileActivity2 = ProfileActivity.this;
                            BulletinFactory.createMuteBulletin(profileActivity2, 4, timeInSeconds, profileActivity2.getResourceProvider()).show();
                        }
                    }

                    @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                    public void showCustomize() {
                        if (did != 0) {
                            Bundle args4 = new Bundle();
                            args4.putLong("dialog_id", did);
                            ProfileActivity.this.presentFragment(new ProfileNotificationsActivity(args4, ProfileActivity.this.resourcesProvider));
                        }
                    }

                    @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                    public void toggleMute() {
                        boolean muted = ProfileActivity.this.getMessagesController().isDialogMuted(did);
                        ProfileActivity.this.getNotificationsController().muteDialog(did, !muted);
                        BulletinFactory.createMuteBulletin(ProfileActivity.this, !muted, null).show();
                        if (ProfileActivity.this.notificationsRow >= 0) {
                            ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.notificationsRow);
                        }
                    }
                }, getResourceProvider());
                chatNotificationsPopupWrapper.m2522x80790d7d(did);
                chatNotificationsPopupWrapper.showAsOptions(this, view, x, y);
                return;
            }
        }
        Bundle args4 = new Bundle();
        args4.putInt(ChatReactionsEditActivity.KEY_CHAT_ID, DialogObject.getEncryptedChatId(this.dialogId));
        presentFragment(new IdenticonActivity(args4));
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4358lambda$createView$3$orgtelegramuiProfileActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.pushAuthKey = null;
        SharedConfig.pushAuthKeyId = null;
        SharedConfig.saveConfig();
        getConnectionsManager().switchBackend(true);
    }

    /* renamed from: org.telegram.ui.ProfileActivity$13 */
    /* loaded from: classes4.dex */
    public class AnonymousClass13 implements RecyclerListView.OnItemLongClickListener {
        private int pressCount = 0;

        AnonymousClass13() {
            ProfileActivity.this = this$0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
        public boolean onItemClick(View view, int position) {
            String str;
            String str2;
            int i;
            String str3;
            int i2;
            String str4;
            String str5;
            String str6;
            String str7;
            int i3;
            String str8;
            int i4;
            if (position != ProfileActivity.this.versionRow) {
                if (position < ProfileActivity.this.membersStartRow || position >= ProfileActivity.this.membersEndRow) {
                    return ProfileActivity.this.processOnClickOrPress(position, view);
                }
                TLRPC.ChatParticipant participant = !ProfileActivity.this.sortedUsers.isEmpty() ? (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(((Integer) ProfileActivity.this.sortedUsers.get(position - ProfileActivity.this.membersStartRow)).intValue()) : (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(position - ProfileActivity.this.membersStartRow);
                return ProfileActivity.this.onMemberClick(participant, true);
            }
            int i5 = this.pressCount + 1;
            this.pressCount = i5;
            if (i5 < 2 && !BuildVars.DEBUG_PRIVATE_VERSION) {
                try {
                    Toast.makeText(ProfileActivity.this.getParentActivity(), "¯\\_(ツ)_/¯", 0).show();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                builder.setTitle(LocaleController.getString("DebugMenu", R.string.DebugMenu));
                CharSequence[] items = new CharSequence[20];
                items[0] = LocaleController.getString("DebugMenuImportContacts", R.string.DebugMenuImportContacts);
                items[1] = LocaleController.getString("DebugMenuReloadContacts", R.string.DebugMenuReloadContacts);
                items[2] = LocaleController.getString("DebugMenuResetContacts", R.string.DebugMenuResetContacts);
                items[3] = LocaleController.getString("DebugMenuResetDialogs", R.string.DebugMenuResetDialogs);
                if (BuildVars.DEBUG_VERSION) {
                    str = null;
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        i4 = R.string.DebugMenuDisableLogs;
                        str8 = "DebugMenuDisableLogs";
                    } else {
                        i4 = R.string.DebugMenuEnableLogs;
                        str8 = "DebugMenuEnableLogs";
                    }
                    str = LocaleController.getString(str8, i4);
                }
                items[4] = str;
                if (SharedConfig.inappCamera) {
                    i = R.string.DebugMenuDisableCamera;
                    str2 = "DebugMenuDisableCamera";
                } else {
                    i = R.string.DebugMenuEnableCamera;
                    str2 = "DebugMenuEnableCamera";
                }
                items[5] = LocaleController.getString(str2, i);
                items[6] = LocaleController.getString("DebugMenuClearMediaCache", R.string.DebugMenuClearMediaCache);
                items[7] = LocaleController.getString("DebugMenuCallSettings", R.string.DebugMenuCallSettings);
                items[8] = null;
                items[9] = (BuildVars.DEBUG_PRIVATE_VERSION || BuildVars.isStandaloneApp()) ? LocaleController.getString("DebugMenuCheckAppUpdate", R.string.DebugMenuCheckAppUpdate) : null;
                items[10] = LocaleController.getString("DebugMenuReadAllDialogs", R.string.DebugMenuReadAllDialogs);
                if (SharedConfig.pauseMusicOnRecord) {
                    i2 = R.string.DebugMenuDisablePauseMusic;
                    str3 = "DebugMenuDisablePauseMusic";
                } else {
                    i2 = R.string.DebugMenuEnablePauseMusic;
                    str3 = "DebugMenuEnablePauseMusic";
                }
                items[11] = LocaleController.getString(str3, i2);
                if (!BuildVars.DEBUG_VERSION || AndroidUtilities.isTablet() || Build.VERSION.SDK_INT < 23) {
                    str4 = null;
                } else {
                    if (SharedConfig.smoothKeyboard) {
                        i3 = R.string.DebugMenuDisableSmoothKeyboard;
                        str7 = "DebugMenuDisableSmoothKeyboard";
                    } else {
                        i3 = R.string.DebugMenuEnableSmoothKeyboard;
                        str7 = "DebugMenuEnableSmoothKeyboard";
                    }
                    str4 = LocaleController.getString(str7, i3);
                }
                items[12] = str4;
                items[13] = BuildVars.DEBUG_PRIVATE_VERSION ? SharedConfig.disableVoiceAudioEffects ? "Enable voip audio effects" : "Disable voip audio effects" : null;
                items[14] = Build.VERSION.SDK_INT >= 21 ? SharedConfig.noStatusBar ? "Show status bar background" : "Hide status bar background" : null;
                items[15] = BuildVars.DEBUG_PRIVATE_VERSION ? "Clean app update" : null;
                items[16] = BuildVars.DEBUG_PRIVATE_VERSION ? "Reset suggestions" : null;
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    str5 = LocaleController.getString(SharedConfig.forceRtmpStream ? R.string.DebugMenuDisableForceRtmpStreamFlag : R.string.DebugMenuEnableForceRtmpStreamFlag);
                } else {
                    str5 = null;
                }
                items[17] = str5;
                items[18] = BuildVars.DEBUG_PRIVATE_VERSION ? LocaleController.getString((int) R.string.DebugMenuClearWebViewCache) : null;
                if (Build.VERSION.SDK_INT >= 19) {
                    str6 = LocaleController.getString(SharedConfig.debugWebView ? R.string.DebugMenuDisableWebViewDebug : R.string.DebugMenuEnableWebViewDebug);
                } else {
                    str6 = null;
                }
                items[19] = str6;
                builder.setItems(items, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$13$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i6) {
                        ProfileActivity.AnonymousClass13.this.m4393lambda$onItemClick$0$orgtelegramuiProfileActivity$13(dialogInterface, i6);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                ProfileActivity.this.showDialog(builder.create());
            }
            return true;
        }

        /* renamed from: lambda$onItemClick$0$org-telegram-ui-ProfileActivity$13 */
        public /* synthetic */ void m4393lambda$onItemClick$0$orgtelegramuiProfileActivity$13(DialogInterface dialog, int which) {
            if (which == 0) {
                ProfileActivity.this.getUserConfig().syncContacts = true;
                ProfileActivity.this.getUserConfig().saveConfig(false);
                ProfileActivity.this.getContactsController().forceImportContacts();
            } else if (which == 1) {
                ProfileActivity.this.getContactsController().loadContacts(false, 0L);
            } else if (which == 2) {
                ProfileActivity.this.getContactsController().resetImportedContacts();
            } else if (which == 3) {
                ProfileActivity.this.getMessagesController().forceResetDialogs();
            } else if (which == 4) {
                BuildVars.LOGS_ENABLED = true ^ BuildVars.LOGS_ENABLED;
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0);
                sharedPreferences.edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                ProfileActivity.this.updateRowsIds();
                ProfileActivity.this.listAdapter.notifyDataSetChanged();
            } else if (which == 5) {
                SharedConfig.toggleInappCamera();
            } else if (which == 6) {
                ProfileActivity.this.getMessagesStorage().clearSentMedia();
                SharedConfig.setNoSoundHintShowed(false);
                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.remove("archivehint").remove("proximityhint").remove("archivehint_l").remove("gifhint").remove("reminderhint").remove("soundHint").remove("themehint").remove("bganimationhint").remove("filterhint").commit();
                MessagesController.getEmojiSettings(ProfileActivity.this.currentAccount).edit().remove("featured_hidden").commit();
                SharedConfig.textSelectionHintShows = 0;
                SharedConfig.lockRecordAudioVideoHint = 0;
                SharedConfig.stickersReorderingHintUsed = false;
                SharedConfig.forwardingOptionsHintShown = false;
                SharedConfig.messageSeenHintCount = 3;
                SharedConfig.emojiInteractionsHintCount = 3;
                SharedConfig.dayNightThemeSwitchHintCount = 3;
                SharedConfig.fastScrollHintCount = 3;
                ChatThemeController.getInstance(ProfileActivity.this.currentAccount).clearCache();
            } else if (which == 7) {
                VoIPHelper.showCallDebugSettings(ProfileActivity.this.getParentActivity());
            } else if (which == 8) {
                SharedConfig.toggleRoundCamera16to9();
            } else if (which == 9) {
                ((LaunchActivity) ProfileActivity.this.getParentActivity()).checkAppUpdate(true);
            } else if (which == 10) {
                ProfileActivity.this.getMessagesStorage().readAllDialogs(-1);
            } else if (which == 11) {
                SharedConfig.togglePauseMusicOnRecord();
            } else if (which == 12) {
                SharedConfig.toggleSmoothKeyboard();
                if (SharedConfig.smoothKeyboard && ProfileActivity.this.getParentActivity() != null) {
                    ProfileActivity.this.getParentActivity().getWindow().setSoftInputMode(16);
                }
            } else if (which == 13) {
                SharedConfig.toggleDisableVoiceAudioEffects();
            } else if (which == 14) {
                SharedConfig.toggleNoStatusBar();
                if (ProfileActivity.this.getParentActivity() != null && Build.VERSION.SDK_INT >= 21) {
                    if (SharedConfig.noStatusBar) {
                        ProfileActivity.this.getParentActivity().getWindow().setStatusBarColor(0);
                    } else {
                        ProfileActivity.this.getParentActivity().getWindow().setStatusBarColor(AndroidUtilities.DARK_STATUS_BAR_OVERLAY);
                    }
                }
            } else if (which == 15) {
                SharedConfig.pendingAppUpdate = null;
                SharedConfig.saveConfig();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.appUpdateAvailable, new Object[0]);
            } else if (which == 16) {
                Set<String> suggestions = ProfileActivity.this.getMessagesController().pendingSuggestions;
                suggestions.add("VALIDATE_PHONE_NUMBER");
                suggestions.add("VALIDATE_PASSWORD");
                ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.newSuggestionsAvailable, new Object[0]);
            } else if (which == 17) {
                SharedConfig.toggleForceRTMPStream();
            } else if (which == 18) {
                ApplicationLoader.applicationContext.deleteDatabase("webview.db");
                ApplicationLoader.applicationContext.deleteDatabase("webviewCache.db");
                WebStorage.getInstance().deleteAllData();
            } else if (which == 19) {
                SharedConfig.toggleDebugWebView();
                Toast.makeText(ProfileActivity.this.getParentActivity(), LocaleController.getString(SharedConfig.debugWebView ? R.string.DebugMenuWebViewDebugEnabled : R.string.DebugMenuWebViewDebugDisabled), 0).show();
            }
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4360lambda$createView$5$orgtelegramuiProfileActivity(View view, int position) {
        if (position < 0) {
            return;
        }
        Object object = Integer.valueOf(this.numberRow);
        boolean add = true;
        if (this.searchAdapter.searchWas) {
            if (position < this.searchAdapter.searchResults.size()) {
                object = this.searchAdapter.searchResults.get(position);
            } else {
                int position2 = position - (this.searchAdapter.searchResults.size() + 1);
                if (position2 >= 0 && position2 < this.searchAdapter.faqSearchResults.size()) {
                    object = this.searchAdapter.faqSearchResults.get(position2);
                }
            }
        } else {
            if (!this.searchAdapter.recentSearches.isEmpty()) {
                position--;
            }
            if (position < 0 || position >= this.searchAdapter.recentSearches.size()) {
                int position3 = position - (this.searchAdapter.recentSearches.size() + 1);
                if (position3 >= 0 && position3 < this.searchAdapter.faqSearchArray.size()) {
                    object = this.searchAdapter.faqSearchArray.get(position3);
                    add = false;
                }
            } else {
                object = this.searchAdapter.recentSearches.get(position);
            }
        }
        if (object instanceof SearchAdapter.SearchResult) {
            SearchAdapter.SearchResult result = (SearchAdapter.SearchResult) object;
            result.open();
        } else if (object instanceof MessagesController.FaqSearchResult) {
            MessagesController.FaqSearchResult result2 = (MessagesController.FaqSearchResult) object;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.openArticle, this.searchAdapter.faqWebPage, result2.url);
        }
        if (add && object != null) {
            this.searchAdapter.addRecent(object);
        }
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ProfileActivity */
    public /* synthetic */ boolean m4362lambda$createView$7$orgtelegramuiProfileActivity(View view, int position) {
        if (this.searchAdapter.isSearchWas() || this.searchAdapter.recentSearches.isEmpty()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
        builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda37
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.m4361lambda$createView$6$orgtelegramuiProfileActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
        return true;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4361lambda$createView$6$orgtelegramuiProfileActivity(DialogInterface dialogInterface, int i) {
        this.searchAdapter.clearRecent();
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4364lambda$createView$9$orgtelegramuiProfileActivity(final TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.this.m4363lambda$createView$8$orgtelegramuiProfileActivity(response);
                }
            });
        }
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4363lambda$createView$8$orgtelegramuiProfileActivity(TLObject response) {
        this.currentChannelParticipant = ((TLRPC.TL_channels_channelParticipant) response).participant;
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4353lambda$createView$10$orgtelegramuiProfileActivity(TLRPC.Chat chat, View v) {
        long j = this.userId;
        long j2 = this.banFromGroup;
        TLRPC.TL_chatBannedRights tL_chatBannedRights = chat.default_banned_rights;
        TLRPC.ChannelParticipant channelParticipant = this.currentChannelParticipant;
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(j, j2, null, tL_chatBannedRights, channelParticipant != null ? channelParticipant.banned_rights : null, "", 1, true, false, null);
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ProfileActivity.16
            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                ProfileActivity.this.removeSelfFromStack();
            }

            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didChangeOwner(TLRPC.User user) {
                ProfileActivity.this.undoView.showWithAction(-ProfileActivity.this.chatId, ProfileActivity.this.currentChat.megagroup ? 10 : 9, user);
            }
        });
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4354lambda$createView$11$orgtelegramuiProfileActivity(View v) {
        RecyclerView.ViewHolder holder;
        Integer offset;
        if (this.avatarBig != null) {
            return;
        }
        if (!AndroidUtilities.isTablet() && !this.isInLandscapeMode && this.avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
            this.openingAvatar = true;
            this.allowPullingDown = true;
            View child = null;
            int i = 0;
            while (true) {
                if (i >= this.listView.getChildCount()) {
                    break;
                }
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i)) != 0) {
                    i++;
                } else {
                    child = this.listView.getChildAt(i);
                    break;
                }
            }
            if (child != null && (holder = this.listView.findContainingViewHolder(child)) != null && (offset = this.positionToOffset.get(Integer.valueOf(holder.getAdapterPosition()))) != null) {
                this.listView.smoothScrollBy(0, -(offset.intValue() + ((this.listView.getPaddingTop() - child.getTop()) - this.actionBar.getMeasuredHeight())), CubicBezierInterpolator.EASE_OUT_QUINT);
                return;
            }
        }
        openAvatar();
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-ProfileActivity */
    public /* synthetic */ boolean m4355lambda$createView$12$orgtelegramuiProfileActivity(View v) {
        if (this.avatarBig != null) {
            return false;
        }
        openAvatar();
        return false;
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4356lambda$createView$13$orgtelegramuiProfileActivity(View v) {
        if (this.writeButton.getTag() != null) {
            return;
        }
        onWriteButtonClick();
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4357lambda$createView$14$orgtelegramuiProfileActivity(ValueAnimator anim) {
        int statusColor;
        int newTop = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        float[] fArr = this.expandAnimatorValues;
        float animatedFraction = anim.getAnimatedFraction();
        this.currentExpanAnimatorFracture = animatedFraction;
        float value = AndroidUtilities.lerp(fArr, animatedFraction);
        this.avatarContainer.setScaleX(this.avatarScale);
        this.avatarContainer.setScaleY(this.avatarScale);
        this.avatarContainer.setTranslationX(AndroidUtilities.lerp(this.avatarX, 0.0f, value));
        this.avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil(this.avatarY), 0.0f, value));
        this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(AndroidUtilities.dpf2(21.0f), 0.0f, value));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setAlpha(1.0f - value);
            this.searchItem.setScaleY(1.0f - value);
            this.searchItem.setVisibility(0);
            ActionBarMenuItem actionBarMenuItem2 = this.searchItem;
            actionBarMenuItem2.setClickable(actionBarMenuItem2.getAlpha() > 0.5f);
            if (this.qrItem != null) {
                float translation = AndroidUtilities.dp(48.0f) * value;
                this.qrItem.setTranslationX(translation);
                this.avatarsViewPagerIndicatorView.setTranslationX(translation - AndroidUtilities.dp(48.0f));
            }
        }
        if (this.extraHeight > AndroidUtilities.dp(88.0f) && this.expandProgress < 0.33f) {
            refreshNameAndOnlineXY();
        }
        ScamDrawable scamDrawable = this.scamDrawable;
        if (scamDrawable != null) {
            scamDrawable.setColor(ColorUtils.blendARGB(getThemedColor(Theme.key_avatar_subtitleInProfileBlue), Color.argb(179, 255, 255, 255), value));
        }
        Drawable drawable = this.lockIconDrawable;
        if (drawable != null) {
            drawable.setColorFilter(ColorUtils.blendARGB(getThemedColor(Theme.key_chat_lockIcon), -1, value), PorterDuff.Mode.MULTIPLY);
        }
        CrossfadeDrawable crossfadeDrawable = this.verifiedCrossfadeDrawable;
        if (crossfadeDrawable != null) {
            crossfadeDrawable.setProgress(value);
        }
        CrossfadeDrawable crossfadeDrawable2 = this.premuimCrossfadeDrawable;
        if (crossfadeDrawable2 != null) {
            crossfadeDrawable2.setProgress(value);
        }
        float k = AndroidUtilities.dpf2(8.0f);
        float nameTextViewXEnd = AndroidUtilities.dpf2(16.0f) - this.nameTextView[1].getLeft();
        float nameTextViewYEnd = ((newTop + this.extraHeight) - AndroidUtilities.dpf2(38.0f)) - this.nameTextView[1].getBottom();
        float f = this.nameX;
        float nameTextViewCx = k + f + ((nameTextViewXEnd - f) / 2.0f);
        float f2 = this.nameY;
        float nameTextViewCy = k + f2 + ((nameTextViewYEnd - f2) / 2.0f);
        float nameTextViewX = ((1.0f - value) * (1.0f - value) * f) + ((1.0f - value) * 2.0f * value * nameTextViewCx) + (value * value * nameTextViewXEnd);
        float nameTextViewY = ((1.0f - value) * (1.0f - value) * f2) + ((1.0f - value) * 2.0f * value * nameTextViewCy) + (value * value * nameTextViewYEnd);
        float onlineTextViewXEnd = AndroidUtilities.dpf2(16.0f) - this.onlineTextView[1].getLeft();
        float onlineTextViewYEnd = ((newTop + this.extraHeight) - AndroidUtilities.dpf2(18.0f)) - this.onlineTextView[1].getBottom();
        float f3 = this.onlineX;
        float onlineTextViewCx = k + f3 + ((onlineTextViewXEnd - f3) / 2.0f);
        float f4 = this.onlineY;
        float onlineTextViewCy = k + f4 + ((onlineTextViewYEnd - f4) / 2.0f);
        float onlineTextViewX = ((1.0f - value) * (1.0f - value) * f3) + ((1.0f - value) * 2.0f * value * onlineTextViewCx) + (value * value * onlineTextViewXEnd);
        float onlineTextViewY = ((1.0f - value) * (1.0f - value) * f4) + ((1.0f - value) * 2.0f * value * onlineTextViewCy) + (value * value * onlineTextViewYEnd);
        this.nameTextView[1].setTranslationX(nameTextViewX);
        this.nameTextView[1].setTranslationY(nameTextViewY);
        this.onlineTextView[1].setTranslationX(onlineTextViewX);
        this.onlineTextView[1].setTranslationY(onlineTextViewY);
        this.mediaCounterTextView.setTranslationX(onlineTextViewX);
        this.mediaCounterTextView.setTranslationY(onlineTextViewY);
        Object onlineTextViewTag = this.onlineTextView[1].getTag();
        if (onlineTextViewTag instanceof String) {
            statusColor = getThemedColor((String) onlineTextViewTag);
        } else {
            statusColor = getThemedColor(Theme.key_avatar_subtitleInProfileBlue);
        }
        this.onlineTextView[1].setTextColor(ColorUtils.blendARGB(statusColor, Color.argb(179, 255, 255, 255), value));
        if (this.extraHeight > AndroidUtilities.dp(88.0f)) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            simpleTextViewArr[1].setPivotY(AndroidUtilities.lerp(0, simpleTextViewArr[1].getMeasuredHeight(), value));
            this.nameTextView[1].setScaleX(AndroidUtilities.lerp(1.12f, 1.67f, value));
            this.nameTextView[1].setScaleY(AndroidUtilities.lerp(1.12f, 1.67f, value));
        }
        needLayoutText(Math.min(1.0f, this.extraHeight / AndroidUtilities.dp(88.0f)));
        this.nameTextView[1].setTextColor(ColorUtils.blendARGB(getThemedColor(Theme.key_profile_title), -1, value));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor(Theme.key_actionBarDefaultIcon), -1, value), false);
        this.avatarImage.setForegroundAlpha(value);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
        params.width = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), this.listView.getMeasuredWidth() / this.avatarScale, value);
        params.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + newTop) / this.avatarScale, value);
        params.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, value);
        this.avatarContainer.requestLayout();
    }

    private void updateTtlIcon() {
        if (this.ttlIconView == null) {
            return;
        }
        boolean visible = false;
        if (this.currentEncryptedChat == null) {
            TLRPC.UserFull userFull = this.userInfo;
            if (userFull != null && userFull.ttl_period > 0) {
                visible = true;
            } else if (this.chatInfo != null && ChatObject.canUserDoAdminAction(this.currentChat, 13) && this.chatInfo.ttl_period > 0) {
                visible = true;
            }
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.ttlIconView, visible, 0.8f, this.fragmentOpened);
    }

    public long getDialogId() {
        long j = this.dialogId;
        if (j != 0) {
            return j;
        }
        long j2 = this.userId;
        if (j2 != 0) {
            return j2;
        }
        return -this.chatId;
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public TLRPC.Chat getCurrentChat() {
        return this.currentChat;
    }

    public TLRPC.UserFull getUserInfo() {
        return this.userInfo;
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public boolean isFragmentOpened() {
        return this.isFragmentOpened;
    }

    private void openAvatar() {
        ImageLocation videoLocation;
        if (this.listView.getScrollState() == 1) {
            return;
        }
        if (this.userId != 0) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user.photo != null && user.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                if (user.photo.dc_id != 0) {
                    user.photo.photo_big.dc_id = user.photo.dc_id;
                }
                PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
            }
        } else if (this.chatId != 0) {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
            if (chat.photo != null && chat.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                if (chat.photo.dc_id != 0) {
                    chat.photo.photo_big.dc_id = chat.photo.dc_id;
                }
                TLRPC.ChatFull chatFull = this.chatInfo;
                if (chatFull != null && (chatFull.chat_photo instanceof TLRPC.TL_photo) && !this.chatInfo.chat_photo.video_sizes.isEmpty()) {
                    videoLocation = ImageLocation.getForPhoto(this.chatInfo.chat_photo.video_sizes.get(0), this.chatInfo.chat_photo);
                } else {
                    videoLocation = null;
                }
                PhotoViewer.getInstance().openPhotoWithVideo(chat.photo.photo_big, videoLocation, this.provider);
            }
        }
    }

    public void onWriteButtonClick() {
        if (this.userId != 0) {
            boolean z = true;
            if (this.imageUpdater != null) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                if (user == null) {
                    user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                }
                if (user == null) {
                    return;
                }
                ImageUpdater imageUpdater = this.imageUpdater;
                if (user.photo == null || user.photo.photo_big == null || (user.photo instanceof TLRPC.TL_userProfilePhotoEmpty)) {
                    z = false;
                }
                imageUpdater.openMenu(z, new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda10
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProfileActivity.this.m4378lambda$onWriteButtonClick$15$orgtelegramuiProfileActivity();
                    }
                }, new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        ProfileActivity.this.m4379lambda$onWriteButtonClick$16$orgtelegramuiProfileActivity(dialogInterface);
                    }
                });
                this.cameraDrawable.setCurrentFrame(0);
                this.cameraDrawable.setCustomEndFrame(43);
                this.cellCameraDrawable.setCurrentFrame(0);
                this.cellCameraDrawable.setCustomEndFrame(43);
                this.writeButton.playAnimation();
                TextCell textCell = this.setAvatarCell;
                if (textCell != null) {
                    textCell.getImageView().playAnimation();
                    return;
                }
                return;
            } else if (this.playProfileAnimation != 0 && (this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)) {
                finishFragment();
                return;
            } else {
                TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
                if (user2 == null || (user2 instanceof TLRPC.TL_userEmpty)) {
                    return;
                }
                Bundle args = new Bundle();
                args.putLong("user_id", this.userId);
                if (!getMessagesController().checkCanOpenChat(args, this)) {
                    return;
                }
                boolean removeFragment = this.arguments.getBoolean("removeFragmentOnChatOpen", true);
                if (!AndroidUtilities.isTablet() && removeFragment) {
                    getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
                    getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
                int distance = getArguments().getInt("nearby_distance", -1);
                if (distance >= 0) {
                    args.putInt("nearby_distance", distance);
                }
                ChatActivity chatActivity = new ChatActivity(args);
                chatActivity.setPreloadedSticker(getMediaDataController().getGreetingsSticker(), false);
                presentFragment(chatActivity, removeFragment);
                if (AndroidUtilities.isTablet()) {
                    finishFragment();
                    return;
                }
                return;
            }
        }
        openDiscussion();
    }

    /* renamed from: lambda$onWriteButtonClick$15$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4378lambda$onWriteButtonClick$15$orgtelegramuiProfileActivity() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto(null);
        this.cameraDrawable.setCurrentFrame(0);
        this.cellCameraDrawable.setCurrentFrame(0);
    }

    /* renamed from: lambda$onWriteButtonClick$16$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4379lambda$onWriteButtonClick$16$orgtelegramuiProfileActivity(DialogInterface dialog) {
        if (!this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCustomEndFrame(86);
            this.cellCameraDrawable.setCustomEndFrame(86);
            this.writeButton.playAnimation();
            TextCell textCell = this.setAvatarCell;
            if (textCell != null) {
                textCell.getImageView().playAnimation();
                return;
            }
            return;
        }
        this.cameraDrawable.setCurrentFrame(0, false);
        this.cellCameraDrawable.setCurrentFrame(0, false);
    }

    public void openDiscussion() {
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (chatFull == null || chatFull.linked_chat_id == 0) {
            return;
        }
        Bundle args = new Bundle();
        args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatInfo.linked_chat_id);
        if (!getMessagesController().checkCanOpenChat(args, this)) {
            return;
        }
        presentFragment(new ChatActivity(args));
    }

    public boolean onMemberClick(TLRPC.ChatParticipant participant, boolean isLong) {
        return onMemberClick(participant, isLong, false);
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public boolean onMemberClick(final TLRPC.ChatParticipant participant, boolean isLong, boolean resultOnly) {
        boolean canRestrict;
        boolean allowKick;
        boolean canEditAdmin;
        boolean editingAdmin;
        TLRPC.ChannelParticipant channelParticipant;
        boolean hasRemove;
        String str;
        int i;
        if (getParentActivity() == null) {
            return false;
        }
        if (isLong) {
            final TLRPC.User user = getMessagesController().getUser(Long.valueOf(participant.user_id));
            if (user == null || participant.user_id == getUserConfig().getClientUserId()) {
                return false;
            }
            this.selectedUser = participant.user_id;
            if (ChatObject.isChannel(this.currentChat)) {
                TLRPC.ChannelParticipant channelParticipant2 = ((TLRPC.TL_chatChannelParticipant) participant).channelParticipant;
                getMessagesController().getUser(Long.valueOf(participant.user_id));
                boolean canEditAdmin2 = ChatObject.canAddAdmins(this.currentChat);
                if (canEditAdmin2 && ((channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator) || ((channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) && !channelParticipant2.can_edit))) {
                    canEditAdmin2 = false;
                }
                boolean allowKick2 = ChatObject.canBlockUsers(this.currentChat) && ((!(channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) && !(channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator)) || channelParticipant2.can_edit);
                boolean canRestrict2 = allowKick2;
                if (this.currentChat.gigagroup) {
                    canRestrict2 = false;
                }
                channelParticipant = channelParticipant2;
                editingAdmin = channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin;
                canEditAdmin = canEditAdmin2;
                allowKick = allowKick2;
                canRestrict = canRestrict2;
            } else {
                boolean allowKick3 = this.currentChat.creator || ((participant instanceof TLRPC.TL_chatParticipant) && (ChatObject.canBlockUsers(this.currentChat) || participant.inviter_id == getUserConfig().getClientUserId()));
                boolean canEditAdmin3 = this.currentChat.creator;
                boolean canRestrict3 = this.currentChat.creator;
                channelParticipant = null;
                editingAdmin = participant instanceof TLRPC.TL_chatParticipantAdmin;
                canEditAdmin = canEditAdmin3;
                allowKick = allowKick3;
                canRestrict = canRestrict3;
            }
            ArrayList<Integer> arrayList = null;
            ArrayList<String> items = resultOnly ? null : new ArrayList<>();
            ArrayList<Integer> icons = resultOnly ? null : new ArrayList<>();
            if (!resultOnly) {
                arrayList = new ArrayList<>();
            }
            final ArrayList<Integer> actions = arrayList;
            if (canEditAdmin) {
                if (resultOnly) {
                    return true;
                }
                if (editingAdmin) {
                    i = R.string.EditAdminRights;
                    str = "EditAdminRights";
                } else {
                    i = R.string.SetAsAdmin;
                    str = "SetAsAdmin";
                }
                items.add(LocaleController.getString(str, i));
                icons.add(Integer.valueOf((int) R.drawable.msg_admins));
                actions.add(0);
            }
            if (canRestrict) {
                if (resultOnly) {
                    return true;
                }
                items.add(LocaleController.getString("ChangePermissions", R.string.ChangePermissions));
                icons.add(Integer.valueOf((int) R.drawable.msg_permissions));
                actions.add(1);
            }
            if (!allowKick) {
                hasRemove = false;
            } else if (resultOnly) {
                return true;
            } else {
                items.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
                icons.add(Integer.valueOf((int) R.drawable.msg_remove));
                actions.add(2);
                hasRemove = true;
            }
            if (resultOnly || items.isEmpty()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
            final TLRPC.ChannelParticipant channelParticipant3 = channelParticipant;
            final boolean z = editingAdmin;
            builder.setItems((CharSequence[]) items.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(icons), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda39
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    ProfileActivity.this.m4377lambda$onMemberClick$18$orgtelegramuiProfileActivity(actions, participant, channelParticipant3, user, z, dialogInterface, i2);
                }
            });
            AlertDialog alertDialog = builder.create();
            showDialog(alertDialog);
            if (hasRemove) {
                alertDialog.setItemColor(items.size() - 1, getThemedColor(Theme.key_dialogTextRed2), getThemedColor(Theme.key_dialogRedIcon));
            }
            return true;
        } else if (participant.user_id == getUserConfig().getClientUserId()) {
            return false;
        } else {
            Bundle args = new Bundle();
            args.putLong("user_id", participant.user_id);
            args.putBoolean("preload_messages", true);
            presentFragment(new ProfileActivity(args));
            return true;
        }
    }

    /* renamed from: lambda$onMemberClick$18$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4377lambda$onMemberClick$18$orgtelegramuiProfileActivity(ArrayList actions, final TLRPC.ChatParticipant participant, final TLRPC.ChannelParticipant channelParticipant, final TLRPC.User user, final boolean editingAdmin, DialogInterface dialogInterface, int i) {
        if (((Integer) actions.get(i)).intValue() == 2) {
            kickUser(this.selectedUser, participant);
            return;
        }
        final int action = ((Integer) actions.get(i)).intValue();
        if (action == 1 && ((channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) || (participant instanceof TLRPC.TL_chatParticipantAdmin))) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder2.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, ContactsController.formatName(user.first_name, user.last_name)));
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda41
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface2, int i2) {
                    ProfileActivity.this.m4376lambda$onMemberClick$17$orgtelegramuiProfileActivity(channelParticipant, action, user, participant, editingAdmin, dialogInterface2, i2);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder2.create());
        } else if (channelParticipant != null) {
            openRightsEdit(action, user, participant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank, editingAdmin);
        } else {
            openRightsEdit(action, user, participant, null, null, "", editingAdmin);
        }
    }

    /* renamed from: lambda$onMemberClick$17$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4376lambda$onMemberClick$17$orgtelegramuiProfileActivity(TLRPC.ChannelParticipant channelParticipant, int action, TLRPC.User user, TLRPC.ChatParticipant participant, boolean editingAdmin, DialogInterface dialog, int which) {
        if (channelParticipant != null) {
            openRightsEdit(action, user, participant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank, editingAdmin);
        } else {
            openRightsEdit(action, user, participant, null, null, "", editingAdmin);
        }
    }

    private void openRightsEdit(final int action, final TLRPC.User user, final TLRPC.ChatParticipant participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, final boolean editingAdmin) {
        final boolean[] needShowBulletin = new boolean[1];
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(user.id, this.chatId, adminRights, this.currentChat.default_banned_rights, bannedRights, rank, action, true, false, null) { // from class: org.telegram.ui.ProfileActivity.26
            @Override // org.telegram.ui.ActionBar.BaseFragment
            public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
                if (!isOpen && backward && needShowBulletin[0] && BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                    BulletinFactory.createPromoteToAdminBulletin(ProfileActivity.this, user.first_name).show();
                }
            }
        };
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ProfileActivity.27
            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank2) {
                TLRPC.ChatParticipant newParticipant;
                int i = action;
                if (i == 0) {
                    TLRPC.ChatParticipant chatParticipant = participant;
                    if (chatParticipant instanceof TLRPC.TL_chatChannelParticipant) {
                        TLRPC.TL_chatChannelParticipant channelParticipant1 = (TLRPC.TL_chatChannelParticipant) chatParticipant;
                        if (rights == 1) {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipantAdmin();
                            channelParticipant1.channelParticipant.flags |= 4;
                        } else {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipant();
                        }
                        channelParticipant1.channelParticipant.inviter_id = ProfileActivity.this.getUserConfig().getClientUserId();
                        channelParticipant1.channelParticipant.peer = new TLRPC.TL_peerUser();
                        channelParticipant1.channelParticipant.peer.user_id = participant.user_id;
                        channelParticipant1.channelParticipant.date = participant.date;
                        channelParticipant1.channelParticipant.banned_rights = rightsBanned;
                        channelParticipant1.channelParticipant.admin_rights = rightsAdmin;
                        channelParticipant1.channelParticipant.rank = rank2;
                    } else if (chatParticipant != null) {
                        if (rights == 1) {
                            newParticipant = new TLRPC.TL_chatParticipantAdmin();
                        } else {
                            newParticipant = new TLRPC.TL_chatParticipant();
                        }
                        newParticipant.user_id = participant.user_id;
                        newParticipant.date = participant.date;
                        newParticipant.inviter_id = participant.inviter_id;
                        int index = ProfileActivity.this.chatInfo.participants.participants.indexOf(participant);
                        if (index >= 0) {
                            ProfileActivity.this.chatInfo.participants.participants.set(index, newParticipant);
                        }
                    }
                    if (rights == 1 && !editingAdmin) {
                        needShowBulletin[0] = true;
                    }
                } else if (i == 1 && rights == 0 && ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                    boolean changed = false;
                    int a = 0;
                    while (true) {
                        if (a >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                            break;
                        }
                        TLRPC.ChannelParticipant p = ((TLRPC.TL_chatChannelParticipant) ProfileActivity.this.chatInfo.participants.participants.get(a)).channelParticipant;
                        if (MessageObject.getPeerId(p.peer) == participant.user_id) {
                            ProfileActivity.this.chatInfo.participants_count--;
                            ProfileActivity.this.chatInfo.participants.participants.remove(a);
                            changed = true;
                            break;
                        }
                        a++;
                    }
                    if (ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                        int a2 = 0;
                        while (true) {
                            if (a2 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                                break;
                            }
                            TLRPC.ChatParticipant p2 = ProfileActivity.this.chatInfo.participants.participants.get(a2);
                            if (p2.user_id == participant.user_id) {
                                ProfileActivity.this.chatInfo.participants.participants.remove(a2);
                                changed = true;
                                break;
                            }
                            a2++;
                        }
                    }
                    if (changed) {
                        ProfileActivity.this.updateOnlineCount(true);
                        ProfileActivity.this.updateRowsIds();
                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didChangeOwner(TLRPC.User user2) {
                ProfileActivity.this.undoView.showWithAction(-ProfileActivity.this.chatId, ProfileActivity.this.currentChat.megagroup ? 10 : 9, user2);
            }
        });
        presentFragment(fragment);
    }

    public boolean processOnClickOrPress(final int position, View view) {
        String username;
        String text;
        TLRPC.Chat chat;
        String text2;
        TLRPC.UserFull userFull;
        if (position == this.usernameRow || position == this.setUsernameRow) {
            if (this.userId != 0) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
                if (user == null || user.username == null) {
                    return false;
                }
                username = user.username;
            } else if (this.chatId == 0 || (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) == null || chat.username == null) {
                return false;
            } else {
                username = chat.username;
            }
            try {
                ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                if (this.userId != 0) {
                    text = "@" + username;
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("UsernameCopied", R.string.UsernameCopied), this.resourcesProvider).show();
                } else {
                    text = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/" + username;
                    BulletinFactory.of(this).createCopyLinkBulletin(LocaleController.getString("LinkCopied", R.string.LinkCopied), this.resourcesProvider).show();
                }
                ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, text);
                clipboard.setPrimaryClip(clip);
            } catch (Exception e) {
                FileLog.e(e);
            }
            return true;
        } else if (position == this.phoneRow || position == this.numberRow) {
            final TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user2 == null || user2.phone == null || user2.phone.length() == 0 || getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
            ArrayList<CharSequence> items = new ArrayList<>();
            final ArrayList<Integer> actions = new ArrayList<>();
            if (position == this.phoneRow) {
                TLRPC.UserFull userFull2 = this.userInfo;
                if (userFull2 != null && userFull2.phone_calls_available) {
                    items.add(LocaleController.getString("CallViaTelegram", R.string.CallViaTelegram));
                    actions.add(2);
                    if (Build.VERSION.SDK_INT >= 18 && this.userInfo.video_calls_available) {
                        items.add(LocaleController.getString("VideoCallViaTelegram", R.string.VideoCallViaTelegram));
                        actions.add(3);
                    }
                }
                items.add(LocaleController.getString("Call", R.string.Call));
                actions.add(0);
            }
            items.add(LocaleController.getString("Copy", R.string.Copy));
            actions.add(1);
            builder.setItems((CharSequence[]) items.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda40
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.m4381lambda$processOnClickOrPress$19$orgtelegramuiProfileActivity(actions, user2, dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return true;
        } else if (position != this.channelInfoRow && position != this.userInfoRow && position != this.locationRow && position != this.bioRow) {
            return false;
        } else {
            if (position == this.bioRow && ((userFull = this.userInfo) == null || TextUtils.isEmpty(userFull.about))) {
                return false;
            }
            if ((view instanceof AboutLinkCell) && ((AboutLinkCell) view).onClick()) {
                return false;
            }
            String str = null;
            if (position == this.locationRow) {
                TLRPC.ChatFull chatFull = this.chatInfo;
                if (chatFull != null && (chatFull.location instanceof TLRPC.TL_channelLocation)) {
                    str = ((TLRPC.TL_channelLocation) this.chatInfo.location).address;
                }
                text2 = str;
            } else if (position == this.channelInfoRow) {
                TLRPC.ChatFull chatFull2 = this.chatInfo;
                if (chatFull2 != null) {
                    str = chatFull2.about;
                }
                text2 = str;
            } else {
                TLRPC.UserFull userFull3 = this.userInfo;
                if (userFull3 != null) {
                    str = userFull3.about;
                }
                text2 = str;
            }
            final String finalText = text2;
            if (TextUtils.isEmpty(finalText)) {
                return false;
            }
            final String[] fromLanguage = {"und"};
            final boolean translateButtonEnabled = MessagesController.getGlobalMainSettings().getBoolean("translate_button", false);
            final boolean[] withTranslate = new boolean[1];
            withTranslate[0] = position == this.bioRow || position == this.channelInfoRow || position == this.userInfoRow;
            final String toLang = LocaleController.getInstance().getCurrentLocale().getLanguage();
            final Runnable showMenu = new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.this.m4384lambda$processOnClickOrPress$22$orgtelegramuiProfileActivity(withTranslate, finalText, position, fromLanguage, toLang);
                }
            };
            if (withTranslate[0]) {
                if (LanguageDetector.hasSupport()) {
                    LanguageDetector.detectLanguage(finalText, new LanguageDetector.StringCallback() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda24
                        @Override // org.telegram.messenger.LanguageDetector.StringCallback
                        public final void run(String str2) {
                            ProfileActivity.this.m4385lambda$processOnClickOrPress$23$orgtelegramuiProfileActivity(fromLanguage, withTranslate, toLang, translateButtonEnabled, showMenu, str2);
                        }
                    }, new LanguageDetector.ExceptionCallback() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda23
                        @Override // org.telegram.messenger.LanguageDetector.ExceptionCallback
                        public final void run(Exception exc) {
                            ProfileActivity.lambda$processOnClickOrPress$24(showMenu, exc);
                        }
                    });
                } else {
                    showMenu.run();
                }
            } else {
                showMenu.run();
            }
            return !(view instanceof AboutLinkCell);
        }
    }

    /* renamed from: lambda$processOnClickOrPress$19$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4381lambda$processOnClickOrPress$19$orgtelegramuiProfileActivity(ArrayList actions, TLRPC.User user, DialogInterface dialogInterface, int i) {
        int i2 = ((Integer) actions.get(i)).intValue();
        if (i2 == 0) {
            try {
                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + user.phone));
                intent.addFlags(268435456);
                getParentActivity().startActivityForResult(intent, 500);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (i2 == 1) {
            try {
                ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, "+" + user.phone);
                clipboard.setPrimaryClip(clip);
                if (Build.VERSION.SDK_INT < 31) {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("PhoneCopied", R.string.PhoneCopied)).show();
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        } else if (i2 == 2 || i2 == 3) {
            boolean z = i2 == 3;
            TLRPC.UserFull userFull = this.userInfo;
            VoIPHelper.startCall(user, z, userFull != null && userFull.video_calls_available, getParentActivity(), this.userInfo, getAccountInstance());
        }
    }

    /* renamed from: lambda$processOnClickOrPress$22$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4384lambda$processOnClickOrPress$22$orgtelegramuiProfileActivity(boolean[] withTranslate, final String finalText, final int position, final String[] fromLanguage, final String toLang) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
        builder.setItems(withTranslate[0] ? new CharSequence[]{LocaleController.getString("Copy", R.string.Copy), LocaleController.getString("TranslateMessage", R.string.TranslateMessage)} : new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda38
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.m4383lambda$processOnClickOrPress$21$orgtelegramuiProfileActivity(finalText, position, fromLanguage, toLang, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    /* renamed from: lambda$processOnClickOrPress$21$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4383lambda$processOnClickOrPress$21$orgtelegramuiProfileActivity(String finalText, int position, String[] fromLanguage, String toLang, DialogInterface dialogInterface, int i) {
        try {
            if (i == 0) {
                AndroidUtilities.addToClipboard(finalText);
                if (position == this.bioRow) {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("BioCopied", R.string.BioCopied)).show();
                } else {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("TextCopied", R.string.TextCopied)).show();
                }
            } else if (i == 1) {
                TranslateAlert.showAlert(this.fragmentView.getContext(), this, fromLanguage[0], toLang, finalText, false, new TranslateAlert.OnLinkPress() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda34
                    @Override // org.telegram.ui.Components.TranslateAlert.OnLinkPress
                    public final boolean run(URLSpan uRLSpan) {
                        return ProfileActivity.this.m4382lambda$processOnClickOrPress$20$orgtelegramuiProfileActivity(uRLSpan);
                    }
                }, null);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$processOnClickOrPress$20$org-telegram-ui-ProfileActivity */
    public /* synthetic */ boolean m4382lambda$processOnClickOrPress$20$orgtelegramuiProfileActivity(URLSpan span) {
        if (span != null) {
            openUrl(span.getURL());
            return true;
        }
        return false;
    }

    /* renamed from: lambda$processOnClickOrPress$23$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4385lambda$processOnClickOrPress$23$orgtelegramuiProfileActivity(String[] fromLanguage, boolean[] withTranslate, String toLang, boolean translateButtonEnabled, Runnable showMenu, String fromLang) {
        TLRPC.Chat chat;
        fromLanguage[0] = fromLang;
        withTranslate[0] = fromLang != null && (!fromLang.equals(toLang) || fromLang.equals("und")) && ((translateButtonEnabled && !RestrictedLanguagesSelectActivity.getRestrictedLanguages().contains(fromLang)) || ((chat = this.currentChat) != null && ((chat.has_link || this.currentChat.username != null) && ("uk".equals(fromLang) || "ru".equals(fromLang)))));
        showMenu.run();
    }

    public static /* synthetic */ void lambda$processOnClickOrPress$24(Runnable showMenu, Exception error) {
        FileLog.e("mlkit: failed to detect language in selection", error);
        showMenu.run();
    }

    public void leaveChatPressed() {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, this.currentChat, null, false, false, true, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda25
            @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
            public final void run(boolean z) {
                ProfileActivity.this.m4373lambda$leaveChatPressed$25$orgtelegramuiProfileActivity(z);
            }
        }, this.resourcesProvider);
    }

    /* renamed from: lambda$leaveChatPressed$25$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4373lambda$leaveChatPressed$25$orgtelegramuiProfileActivity(boolean param) {
        this.playProfileAnimation = 0;
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        finishFragment();
        getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf(-this.currentChat.id), null, this.currentChat, Boolean.valueOf(param));
    }

    public void getChannelParticipants(boolean reload) {
        LongSparseArray<TLRPC.ChatParticipant> longSparseArray;
        if (this.loadingUsers || (longSparseArray = this.participantsMap) == null || this.chatInfo == null) {
            return;
        }
        this.loadingUsers = true;
        int i = 0;
        final int delay = (longSparseArray.size() == 0 || !reload) ? 0 : 300;
        final TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        req.channel = getMessagesController().getInputChannel(this.chatId);
        req.filter = new TLRPC.TL_channelParticipantsRecent();
        if (!reload) {
            i = this.participantsMap.size();
        }
        req.offset = i;
        req.limit = 200;
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda28
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ProfileActivity.this.m4371lambda$getChannelParticipants$27$orgtelegramuiProfileActivity(req, delay, tLObject, tL_error);
            }
        });
        getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
    }

    /* renamed from: lambda$getChannelParticipants$27$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4371lambda$getChannelParticipants$27$orgtelegramuiProfileActivity(final TLRPC.TL_channels_getParticipants req, int delay, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                ProfileActivity.this.m4370lambda$getChannelParticipants$26$orgtelegramuiProfileActivity(error, response, req);
            }
        }, delay);
    }

    /* renamed from: lambda$getChannelParticipants$26$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4370lambda$getChannelParticipants$26$orgtelegramuiProfileActivity(TLRPC.TL_error error, TLObject response, TLRPC.TL_channels_getParticipants req) {
        if (error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            getMessagesController().putUsers(res.users, false);
            getMessagesController().putChats(res.chats, false);
            if (res.users.size() < 200) {
                this.usersEndReached = true;
            }
            if (req.offset == 0) {
                this.participantsMap.clear();
                this.chatInfo.participants = new TLRPC.TL_chatParticipants();
                getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
                getMessagesStorage().updateChannelUsers(this.chatId, res.participants);
            }
            for (int a = 0; a < res.participants.size(); a++) {
                TLRPC.TL_chatChannelParticipant participant = new TLRPC.TL_chatChannelParticipant();
                participant.channelParticipant = res.participants.get(a);
                participant.inviter_id = participant.channelParticipant.inviter_id;
                participant.user_id = MessageObject.getPeerId(participant.channelParticipant.peer);
                participant.date = participant.channelParticipant.date;
                if (this.participantsMap.indexOfKey(participant.user_id) < 0) {
                    if (this.chatInfo.participants == null) {
                        this.chatInfo.participants = new TLRPC.TL_chatParticipants();
                    }
                    this.chatInfo.participants.participants.add(participant);
                    this.participantsMap.put(participant.user_id, participant);
                }
            }
        }
        this.loadingUsers = false;
        updateListAnimated(true);
    }

    private void setMediaHeaderVisible(boolean visible) {
        if (this.mediaHeaderVisible == visible) {
            return;
        }
        this.mediaHeaderVisible = visible;
        AnimatorSet animatorSet = this.headerAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = this.headerShadowAnimatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        final ActionBarMenuItem mediaSearchItem = this.sharedMediaLayout.getSearchItem();
        if (!this.mediaHeaderVisible) {
            if (this.callItemVisible) {
                this.callItem.setVisibility(0);
            }
            if (this.videoCallItemVisible) {
                this.videoCallItem.setVisibility(0);
            }
            if (this.editItemVisible) {
                this.editItem.setVisibility(0);
            }
            this.otherItem.setVisibility(0);
        } else {
            if (this.sharedMediaLayout.isSearchItemVisible()) {
                mediaSearchItem.setVisibility(0);
            }
            if (this.sharedMediaLayout.isCalendarItemVisible()) {
                this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(0);
            } else {
                this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(4);
            }
        }
        if (this.actionBar != null) {
            this.actionBar.createMenu().requestLayout();
        }
        ArrayList<Animator> animators = new ArrayList<>();
        ActionBarMenuItem actionBarMenuItem = this.callItem;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = visible ? 0.0f : 1.0f;
        animators.add(ObjectAnimator.ofFloat(actionBarMenuItem, property, fArr));
        ActionBarMenuItem actionBarMenuItem2 = this.videoCallItem;
        Property property2 = View.ALPHA;
        float[] fArr2 = new float[1];
        fArr2[0] = visible ? 0.0f : 1.0f;
        animators.add(ObjectAnimator.ofFloat(actionBarMenuItem2, property2, fArr2));
        ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
        Property property3 = View.ALPHA;
        float[] fArr3 = new float[1];
        fArr3[0] = visible ? 0.0f : 1.0f;
        animators.add(ObjectAnimator.ofFloat(actionBarMenuItem3, property3, fArr3));
        ActionBarMenuItem actionBarMenuItem4 = this.editItem;
        Property property4 = View.ALPHA;
        float[] fArr4 = new float[1];
        fArr4[0] = visible ? 0.0f : 1.0f;
        animators.add(ObjectAnimator.ofFloat(actionBarMenuItem4, property4, fArr4));
        ActionBarMenuItem actionBarMenuItem5 = this.callItem;
        Property property5 = View.TRANSLATION_Y;
        float[] fArr5 = new float[1];
        fArr5[0] = visible ? -AndroidUtilities.dp(10.0f) : 0.0f;
        animators.add(ObjectAnimator.ofFloat(actionBarMenuItem5, property5, fArr5));
        ActionBarMenuItem actionBarMenuItem6 = this.videoCallItem;
        Property property6 = View.TRANSLATION_Y;
        float[] fArr6 = new float[1];
        fArr6[0] = visible ? -AndroidUtilities.dp(10.0f) : 0.0f;
        animators.add(ObjectAnimator.ofFloat(actionBarMenuItem6, property6, fArr6));
        ActionBarMenuItem actionBarMenuItem7 = this.otherItem;
        Property property7 = View.TRANSLATION_Y;
        float[] fArr7 = new float[1];
        fArr7[0] = visible ? -AndroidUtilities.dp(10.0f) : 0.0f;
        animators.add(ObjectAnimator.ofFloat(actionBarMenuItem7, property7, fArr7));
        ActionBarMenuItem actionBarMenuItem8 = this.editItem;
        Property property8 = View.TRANSLATION_Y;
        float[] fArr8 = new float[1];
        fArr8[0] = visible ? -AndroidUtilities.dp(10.0f) : 0.0f;
        animators.add(ObjectAnimator.ofFloat(actionBarMenuItem8, property8, fArr8));
        Property property9 = View.ALPHA;
        float[] fArr9 = new float[1];
        fArr9[0] = visible ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(mediaSearchItem, property9, fArr9));
        Property property10 = View.TRANSLATION_Y;
        float[] fArr10 = new float[1];
        fArr10[0] = visible ? 0.0f : AndroidUtilities.dp(10.0f);
        animators.add(ObjectAnimator.ofFloat(mediaSearchItem, property10, fArr10));
        ImageView imageView = this.sharedMediaLayout.photoVideoOptionsItem;
        Property property11 = View.ALPHA;
        float[] fArr11 = new float[1];
        fArr11[0] = visible ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(imageView, property11, fArr11));
        ImageView imageView2 = this.sharedMediaLayout.photoVideoOptionsItem;
        Property property12 = View.TRANSLATION_Y;
        float[] fArr12 = new float[1];
        fArr12[0] = visible ? 0.0f : AndroidUtilities.dp(10.0f);
        animators.add(ObjectAnimator.ofFloat(imageView2, property12, fArr12));
        ActionBar actionBar = this.actionBar;
        Property<ActionBar, Float> property13 = this.ACTIONBAR_HEADER_PROGRESS;
        float[] fArr13 = new float[1];
        fArr13[0] = visible ? 1.0f : 0.0f;
        animators.add(ObjectAnimator.ofFloat(actionBar, property13, fArr13));
        SimpleTextView simpleTextView = this.onlineTextView[1];
        Property property14 = View.ALPHA;
        float[] fArr14 = new float[1];
        fArr14[0] = visible ? 0.0f : 1.0f;
        animators.add(ObjectAnimator.ofFloat(simpleTextView, property14, fArr14));
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.mediaCounterTextView;
        Property property15 = View.ALPHA;
        float[] fArr15 = new float[1];
        if (!visible) {
            f = 0.0f;
        }
        fArr15[0] = f;
        animators.add(ObjectAnimator.ofFloat(clippingTextViewSwitcher, property15, fArr15));
        if (visible) {
            animators.add(ObjectAnimator.ofFloat(this, this.HEADER_SHADOW, 0.0f));
        }
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.headerAnimatorSet = animatorSet3;
        animatorSet3.playTogether(animators);
        this.headerAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.headerAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.29
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ProfileActivity.this.headerAnimatorSet != null) {
                    if (ProfileActivity.this.mediaHeaderVisible) {
                        if (ProfileActivity.this.callItemVisible) {
                            ProfileActivity.this.callItem.setVisibility(8);
                        }
                        if (ProfileActivity.this.videoCallItemVisible) {
                            ProfileActivity.this.videoCallItem.setVisibility(8);
                        }
                        if (ProfileActivity.this.editItemVisible) {
                            ProfileActivity.this.editItem.setVisibility(8);
                        }
                        ProfileActivity.this.otherItem.setVisibility(8);
                    } else {
                        if (ProfileActivity.this.sharedMediaLayout.isSearchItemVisible()) {
                            mediaSearchItem.setVisibility(0);
                        }
                        ProfileActivity.this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(4);
                        ProfileActivity.this.headerShadowAnimatorSet = new AnimatorSet();
                        AnimatorSet animatorSet4 = ProfileActivity.this.headerShadowAnimatorSet;
                        ProfileActivity profileActivity = ProfileActivity.this;
                        animatorSet4.playTogether(ObjectAnimator.ofFloat(profileActivity, profileActivity.HEADER_SHADOW, 1.0f));
                        ProfileActivity.this.headerShadowAnimatorSet.setDuration(100L);
                        ProfileActivity.this.headerShadowAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.29.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation2) {
                                ProfileActivity.this.headerShadowAnimatorSet = null;
                            }
                        });
                        ProfileActivity.this.headerShadowAnimatorSet.start();
                    }
                }
                ProfileActivity.this.headerAnimatorSet = null;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                ProfileActivity.this.headerAnimatorSet = null;
            }
        });
        this.headerAnimatorSet.setDuration(150L);
        this.headerAnimatorSet.start();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, true);
    }

    public void openAddMember() {
        Bundle args = new Bundle();
        args.putBoolean("addToGroup", true);
        args.putLong("chatId", this.currentChat.id);
        GroupCreateActivity fragment = new GroupCreateActivity(args);
        fragment.setInfo(this.chatInfo);
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (chatFull != null && chatFull.participants != null) {
            LongSparseArray<TLObject> users = new LongSparseArray<>();
            for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                users.put(this.chatInfo.participants.participants.get(a).user_id, null);
            }
            fragment.setIgnoreUsers(users);
        }
        fragment.setDelegate(new GroupCreateActivity.ContactsAddActivityDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda35
            @Override // org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate
            public final void didSelectUsers(ArrayList arrayList, int i) {
                ProfileActivity.this.m4380lambda$openAddMember$28$orgtelegramuiProfileActivity(arrayList, i);
            }

            @Override // org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate
            public /* synthetic */ void needAddBot(TLRPC.User user) {
                GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, user);
            }
        });
        presentFragment(fragment);
    }

    /* renamed from: lambda$openAddMember$28$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4380lambda$openAddMember$28$orgtelegramuiProfileActivity(ArrayList users, int fwdCount) {
        HashSet<Long> currentParticipants = new HashSet<>();
        if (this.chatInfo.participants.participants != null) {
            for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                currentParticipants.add(Long.valueOf(this.chatInfo.participants.participants.get(i).user_id));
            }
        }
        int N = users.size();
        for (int a = 0; a < N; a++) {
            TLRPC.User user = (TLRPC.User) users.get(a);
            getMessagesController().addUserToChat(this.chatId, user, fwdCount, null, this, null);
            if (!currentParticipants.contains(Long.valueOf(user.id))) {
                if (this.chatInfo.participants == null) {
                    this.chatInfo.participants = new TLRPC.TL_chatParticipants();
                }
                if (ChatObject.isChannel(this.currentChat)) {
                    TLRPC.TL_chatChannelParticipant channelParticipant1 = new TLRPC.TL_chatChannelParticipant();
                    channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipant();
                    channelParticipant1.channelParticipant.inviter_id = getUserConfig().getClientUserId();
                    channelParticipant1.channelParticipant.peer = new TLRPC.TL_peerUser();
                    channelParticipant1.channelParticipant.peer.user_id = user.id;
                    channelParticipant1.channelParticipant.date = getConnectionsManager().getCurrentTime();
                    channelParticipant1.user_id = user.id;
                    this.chatInfo.participants.participants.add(channelParticipant1);
                } else {
                    TLRPC.ChatParticipant participant = new TLRPC.TL_chatParticipant();
                    participant.user_id = user.id;
                    participant.inviter_id = getAccountInstance().getUserConfig().clientUserId;
                    this.chatInfo.participants.participants.add(participant);
                }
                this.chatInfo.participants_count++;
                getMessagesController().putUser(user, false);
            }
        }
        updateListAnimated(true);
    }

    public void checkListViewScroll() {
        boolean mediaHeaderVisible;
        if (this.listView.getVisibility() != 0) {
            return;
        }
        if (this.sharedMediaLayoutAttached) {
            this.sharedMediaLayout.setVisibleHeight(this.listView.getMeasuredHeight() - this.sharedMediaLayout.getTop());
        }
        if (this.listView.getChildCount() <= 0 || this.openAnimationInProgress) {
            return;
        }
        int newOffset = 0;
        View child = null;
        int i = 0;
        while (true) {
            if (i >= this.listView.getChildCount()) {
                break;
            }
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i)) != 0) {
                i++;
            } else {
                child = this.listView.getChildAt(i);
                break;
            }
        }
        RecyclerListView.Holder holder = child == null ? null : (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        boolean z = false;
        int top = child == null ? 0 : child.getTop();
        int adapterPosition = holder != null ? holder.getAdapterPosition() : -1;
        if (top >= 0 && adapterPosition == 0) {
            newOffset = top;
        }
        boolean searchVisible = this.imageUpdater == null && this.actionBar.isSearchFieldVisible();
        int i2 = this.sharedMediaRow;
        if (i2 != -1 && !searchVisible) {
            RecyclerListView.Holder holder2 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(i2);
            mediaHeaderVisible = holder2 != null && holder2.itemView.getTop() <= 0;
        } else {
            mediaHeaderVisible = searchVisible;
        }
        setMediaHeaderVisible(mediaHeaderVisible);
        if (this.extraHeight != newOffset) {
            this.extraHeight = newOffset;
            this.topView.invalidate();
            if (this.playProfileAnimation != 0) {
                if (this.extraHeight != 0.0f) {
                    z = true;
                }
                this.allowProfileAnimation = z;
            }
            needLayout(true);
        }
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public void updateSelectedMediaTabText() {
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout == null || this.mediaCounterTextView == null) {
            return;
        }
        int id = sharedMediaLayout.getClosestTab();
        int[] mediaCount = this.sharedMediaPreloader.getLastMediaCount();
        if (id == 0) {
            if (mediaCount[7] == 0 && mediaCount[6] == 0) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Media", mediaCount[0], new Object[0]));
            } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() != 1 && mediaCount[7] != 0) {
                if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 2 || mediaCount[6] == 0) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Videos", mediaCount[7], new Object[0]));
                    return;
                }
                String str = String.format("%s, %s", LocaleController.formatPluralString("Photos", mediaCount[6], new Object[0]), LocaleController.formatPluralString("Videos", mediaCount[7], new Object[0]));
                this.mediaCounterTextView.setText(str);
            } else {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Photos", mediaCount[6], new Object[0]));
            }
        } else if (id == 1) {
            this.mediaCounterTextView.setText(LocaleController.formatPluralString("Files", mediaCount[1], new Object[0]));
        } else if (id == 2) {
            this.mediaCounterTextView.setText(LocaleController.formatPluralString("Voice", mediaCount[2], new Object[0]));
        } else if (id == 3) {
            this.mediaCounterTextView.setText(LocaleController.formatPluralString("Links", mediaCount[3], new Object[0]));
        } else if (id == 4) {
            this.mediaCounterTextView.setText(LocaleController.formatPluralString("MusicFiles", mediaCount[4], new Object[0]));
        } else if (id == 5) {
            this.mediaCounterTextView.setText(LocaleController.formatPluralString("GIFs", mediaCount[5], new Object[0]));
        } else if (id == 6) {
            this.mediaCounterTextView.setText(LocaleController.formatPluralString("CommonGroups", this.userInfo.common_chats_count, new Object[0]));
        } else if (id == 7) {
            this.mediaCounterTextView.setText(this.onlineTextView[1].getText());
        }
    }

    public void needLayout(boolean animated) {
        OverlaysView overlaysView;
        ValueAnimator valueAnimator;
        BackupImageView imageView;
        TLRPC.ChatFull chatFull;
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && !this.openAnimationInProgress) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarContainer != null) {
            float diff = Math.min(1.0f, this.extraHeight / AndroidUtilities.dp(88.0f));
            this.listView.setTopGlowOffset((int) this.extraHeight);
            this.listView.setOverScrollMode((this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || this.extraHeight >= ((float) (this.listView.getMeasuredWidth() - newTop))) ? 0 : 2);
            RLottieImageView rLottieImageView = this.writeButton;
            if (rLottieImageView != null) {
                rLottieImageView.setTranslationY(((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) + this.searchTransitionOffset) - AndroidUtilities.dp(29.5f));
                if (!this.openAnimationInProgress) {
                    float f = 0.2f;
                    boolean setVisible = diff > 0.2f && !this.searchMode && (this.imageUpdater == null || this.setAvatarRow == -1);
                    if (setVisible && this.chatId != 0) {
                        setVisible = (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup || (chatFull = this.chatInfo) == null || chatFull.linked_chat_id == 0 || this.infoHeaderRow == -1) ? false : true;
                    }
                    boolean currentVisible = this.writeButton.getTag() == null;
                    if (setVisible != currentVisible) {
                        if (setVisible) {
                            this.writeButton.setTag(null);
                        } else {
                            this.writeButton.setTag(0);
                        }
                        if (this.writeButtonAnimation != null) {
                            AnimatorSet old = this.writeButtonAnimation;
                            this.writeButtonAnimation = null;
                            old.cancel();
                        }
                        if (animated) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            this.writeButtonAnimation = animatorSet;
                            if (setVisible) {
                                animatorSet.setInterpolator(new DecelerateInterpolator());
                                this.writeButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, 1.0f));
                            } else {
                                animatorSet.setInterpolator(new AccelerateInterpolator());
                                this.writeButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, 0.2f), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, 0.2f), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, 0.0f));
                            }
                            this.writeButtonAnimation.setDuration(150L);
                            this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.30
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    if (ProfileActivity.this.writeButtonAnimation != null && ProfileActivity.this.writeButtonAnimation.equals(animation)) {
                                        ProfileActivity.this.writeButtonAnimation = null;
                                    }
                                }
                            });
                            this.writeButtonAnimation.start();
                        } else {
                            this.writeButton.setScaleX(setVisible ? 1.0f : 0.2f);
                            RLottieImageView rLottieImageView2 = this.writeButton;
                            if (setVisible) {
                                f = 1.0f;
                            }
                            rLottieImageView2.setScaleY(f);
                            this.writeButton.setAlpha(setVisible ? 1.0f : 0.0f);
                        }
                    }
                    if (this.qrItem != null) {
                        updateQrItemVisibility(animated);
                        if (!animated) {
                            float translation = AndroidUtilities.dp(48.0f) * this.qrItem.getAlpha();
                            this.qrItem.setTranslationX(translation);
                            PagerIndicatorView pagerIndicatorView = this.avatarsViewPagerIndicatorView;
                            if (pagerIndicatorView != null) {
                                pagerIndicatorView.setTranslationX(translation - AndroidUtilities.dp(48.0f));
                            }
                        }
                    }
                }
            }
            this.avatarX = (-AndroidUtilities.dpf2(47.0f)) * diff;
            this.avatarY = (((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ((ActionBar.getCurrentActionBarHeight() / 2.0f) * (diff + 1.0f))) - (AndroidUtilities.density * 21.0f)) + (AndroidUtilities.density * 27.0f * diff) + this.actionBar.getTranslationY();
            float h = this.openAnimationInProgress ? this.initialAnimationExtraHeight : this.extraHeight;
            if (h > AndroidUtilities.dp(88.0f) || this.isPulledDown) {
                float max = Math.max(0.0f, Math.min(1.0f, (h - AndroidUtilities.dp(88.0f)) / ((this.listView.getMeasuredWidth() - newTop) - AndroidUtilities.dp(88.0f))));
                this.expandProgress = max;
                this.avatarScale = AndroidUtilities.lerp(1.4285715f, 2.4285715f, Math.min(1.0f, max * 3.0f));
                float durationFactor = Math.min(AndroidUtilities.dpf2(2000.0f), Math.max(AndroidUtilities.dpf2(1100.0f), Math.abs(this.listViewVelocityY))) / AndroidUtilities.dpf2(1100.0f);
                if (this.allowPullingDown && (this.openingAvatar || this.expandProgress >= 0.33f)) {
                    if (!this.isPulledDown) {
                        if (this.otherItem != null) {
                            if (!getMessagesController().isChatNoForwards(this.currentChat)) {
                                this.otherItem.showSubItem(21);
                            } else {
                                this.otherItem.hideSubItem(21);
                            }
                            if (this.imageUpdater != null) {
                                this.otherItem.showSubItem(36);
                                this.otherItem.showSubItem(34);
                                this.otherItem.showSubItem(35);
                                this.otherItem.hideSubItem(33);
                                this.otherItem.hideSubItem(31);
                            }
                        }
                        ActionBarMenuItem actionBarMenuItem = this.searchItem;
                        if (actionBarMenuItem != null) {
                            actionBarMenuItem.setEnabled(false);
                        }
                        this.isPulledDown = true;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, true);
                        this.overlaysView.setOverlaysVisible(true, durationFactor);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(durationFactor);
                        this.avatarsViewPager.setCreateThumbFromParent(true);
                        this.avatarsViewPager.getAdapter().notifyDataSetChanged();
                        this.expandAnimator.cancel();
                        float value = AndroidUtilities.lerp(this.expandAnimatorValues, this.currentExpanAnimatorFracture);
                        float[] fArr = this.expandAnimatorValues;
                        fArr[0] = value;
                        fArr[1] = 1.0f;
                        this.expandAnimator.setDuration(((1.0f - value) * 250.0f) / durationFactor);
                        this.expandAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.31
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationStart(Animator animation) {
                                ProfileActivity.this.setForegroundImage(false);
                                ProfileActivity.this.avatarsViewPager.setAnimatedFileMaybe(ProfileActivity.this.avatarImage.getImageReceiver().getAnimation());
                                ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                ProfileActivity.this.expandAnimator.removeListener(this);
                                ProfileActivity.this.topView.setBackgroundColor(-16777216);
                                ProfileActivity.this.avatarContainer.setVisibility(8);
                                ProfileActivity.this.avatarsViewPager.setVisibility(0);
                            }
                        });
                        this.expandAnimator.start();
                    }
                    ViewGroup.LayoutParams params = this.avatarsViewPager.getLayoutParams();
                    params.width = this.listView.getMeasuredWidth();
                    params.height = (int) (newTop + h);
                    this.avatarsViewPager.requestLayout();
                    if (!this.expandAnimator.isRunning()) {
                        float additionalTranslationY = 0.0f;
                        if (this.openAnimationInProgress && this.playProfileAnimation == 2) {
                            additionalTranslationY = (-(1.0f - this.animationProgress)) * AndroidUtilities.dp(50.0f);
                        }
                        this.nameTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - this.nameTextView[1].getLeft());
                        this.nameTextView[1].setTranslationY((((newTop + h) - AndroidUtilities.dpf2(38.0f)) - this.nameTextView[1].getBottom()) + additionalTranslationY);
                        this.onlineTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - this.onlineTextView[1].getLeft());
                        this.onlineTextView[1].setTranslationY((((newTop + h) - AndroidUtilities.dpf2(18.0f)) - this.onlineTextView[1].getBottom()) + additionalTranslationY);
                        this.mediaCounterTextView.setTranslationX(this.onlineTextView[1].getTranslationX());
                        this.mediaCounterTextView.setTranslationY(this.onlineTextView[1].getTranslationY());
                    }
                } else {
                    if (this.isPulledDown) {
                        this.isPulledDown = false;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, true);
                        ActionBarMenuItem actionBarMenuItem2 = this.otherItem;
                        if (actionBarMenuItem2 != null) {
                            actionBarMenuItem2.hideSubItem(21);
                            if (this.imageUpdater != null) {
                                this.otherItem.hideSubItem(33);
                                this.otherItem.hideSubItem(34);
                                this.otherItem.hideSubItem(35);
                                this.otherItem.showSubItem(36);
                                this.otherItem.showSubItem(31);
                                this.otherItem.showSubItem(30);
                            }
                        }
                        ActionBarMenuItem actionBarMenuItem3 = this.searchItem;
                        if (actionBarMenuItem3 != null) {
                            actionBarMenuItem3.setEnabled(!this.scrolling);
                        }
                        this.overlaysView.setOverlaysVisible(false, durationFactor);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(durationFactor);
                        this.expandAnimator.cancel();
                        this.avatarImage.getImageReceiver().setAllowStartAnimation(true);
                        this.avatarImage.getImageReceiver().startAnimation();
                        float value2 = AndroidUtilities.lerp(this.expandAnimatorValues, this.currentExpanAnimatorFracture);
                        float[] fArr2 = this.expandAnimatorValues;
                        fArr2[0] = value2;
                        fArr2[1] = 0.0f;
                        if (!this.isInLandscapeMode) {
                            this.expandAnimator.setDuration((250.0f * value2) / durationFactor);
                        } else {
                            this.expandAnimator.setDuration(0L);
                        }
                        this.topView.setBackgroundColor(getThemedColor(Theme.key_avatar_backgroundActionBarBlue));
                        if (!this.doNotSetForeground && (imageView = this.avatarsViewPager.getCurrentItemView()) != null) {
                            this.avatarImage.setForegroundImageDrawable(imageView.getImageReceiver().getDrawableSafe());
                        }
                        this.avatarImage.setForegroundAlpha(1.0f);
                        this.avatarContainer.setVisibility(0);
                        this.avatarsViewPager.setVisibility(8);
                        this.expandAnimator.start();
                    }
                    this.avatarContainer.setScaleX(this.avatarScale);
                    this.avatarContainer.setScaleY(this.avatarScale);
                    ValueAnimator valueAnimator2 = this.expandAnimator;
                    if (valueAnimator2 == null || !valueAnimator2.isRunning()) {
                        refreshNameAndOnlineXY();
                        this.nameTextView[1].setTranslationX(this.nameX);
                        this.nameTextView[1].setTranslationY(this.nameY);
                        this.onlineTextView[1].setTranslationX(this.onlineX);
                        this.onlineTextView[1].setTranslationY(this.onlineY);
                        this.mediaCounterTextView.setTranslationX(this.onlineX);
                        this.mediaCounterTextView.setTranslationY(this.onlineY);
                    }
                }
            }
            if (this.openAnimationInProgress && this.playProfileAnimation == 2) {
                float avY = (((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + (ActionBar.getCurrentActionBarHeight() / 2.0f)) - (AndroidUtilities.density * 21.0f)) + this.actionBar.getTranslationY();
                this.nameTextView[0].setTranslationX(0.0f);
                this.nameTextView[0].setTranslationY(((float) Math.floor(avY)) + AndroidUtilities.dp(1.3f));
                this.onlineTextView[0].setTranslationX(0.0f);
                this.onlineTextView[0].setTranslationY(((float) Math.floor(avY)) + AndroidUtilities.dp(24.0f));
                this.nameTextView[0].setScaleX(1.0f);
                this.nameTextView[0].setScaleY(1.0f);
                SimpleTextView[] simpleTextViewArr = this.nameTextView;
                simpleTextViewArr[1].setPivotY(simpleTextViewArr[1].getMeasuredHeight());
                this.nameTextView[1].setScaleX(1.67f);
                this.nameTextView[1].setScaleY(1.67f);
                this.avatarScale = AndroidUtilities.lerp(1.0f, 2.4285715f, this.animationProgress);
                this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(AndroidUtilities.dpf2(21.0f), 0.0f, this.animationProgress));
                this.avatarContainer.setTranslationX(AndroidUtilities.lerp(0.0f, 0.0f, this.animationProgress));
                this.avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil(avY), 0.0f, this.animationProgress));
                float extra = (this.avatarContainer.getMeasuredWidth() - AndroidUtilities.dp(42.0f)) * this.avatarScale;
                this.timeItem.setTranslationX(this.avatarContainer.getX() + AndroidUtilities.dp(16.0f) + extra);
                this.timeItem.setTranslationY(this.avatarContainer.getY() + AndroidUtilities.dp(15.0f) + extra);
                this.avatarContainer.setScaleX(this.avatarScale);
                this.avatarContainer.setScaleY(this.avatarScale);
                this.overlaysView.setAlphaValue(this.animationProgress, false);
                this.actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor(Theme.key_actionBarDefaultIcon), -1, this.animationProgress), false);
                ScamDrawable scamDrawable = this.scamDrawable;
                if (scamDrawable != null) {
                    scamDrawable.setColor(ColorUtils.blendARGB(getThemedColor(Theme.key_avatar_subtitleInProfileBlue), Color.argb(179, 255, 255, 255), this.animationProgress));
                }
                Drawable drawable = this.lockIconDrawable;
                if (drawable != null) {
                    drawable.setColorFilter(ColorUtils.blendARGB(getThemedColor(Theme.key_chat_lockIcon), -1, this.animationProgress), PorterDuff.Mode.MULTIPLY);
                }
                CrossfadeDrawable crossfadeDrawable = this.verifiedCrossfadeDrawable;
                if (crossfadeDrawable != null) {
                    crossfadeDrawable.setProgress(this.animationProgress);
                    this.nameTextView[1].invalidate();
                }
                CrossfadeDrawable crossfadeDrawable2 = this.premuimCrossfadeDrawable;
                if (crossfadeDrawable2 != null) {
                    crossfadeDrawable2.setProgress(this.animationProgress);
                    this.nameTextView[1].invalidate();
                }
                FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
                int lerp = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + newTop) / this.avatarScale, this.animationProgress);
                params2.height = lerp;
                params2.width = lerp;
                params2.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, this.animationProgress);
                this.avatarContainer.requestLayout();
            } else if (this.extraHeight <= AndroidUtilities.dp(88.0f)) {
                this.avatarScale = ((diff * 18.0f) + 42.0f) / 42.0f;
                float nameScale = (0.12f * diff) + 1.0f;
                ValueAnimator valueAnimator3 = this.expandAnimator;
                if (valueAnimator3 == null || !valueAnimator3.isRunning()) {
                    this.avatarContainer.setScaleX(this.avatarScale);
                    this.avatarContainer.setScaleY(this.avatarScale);
                    this.avatarContainer.setTranslationX(this.avatarX);
                    this.avatarContainer.setTranslationY((float) Math.ceil(this.avatarY));
                    float extra2 = (AndroidUtilities.dp(42.0f) * this.avatarScale) - AndroidUtilities.dp(42.0f);
                    this.timeItem.setTranslationX(this.avatarContainer.getX() + AndroidUtilities.dp(16.0f) + extra2);
                    this.timeItem.setTranslationY(this.avatarContainer.getY() + AndroidUtilities.dp(15.0f) + extra2);
                }
                this.nameX = AndroidUtilities.density * (-21.0f) * diff;
                this.nameY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(1.3f) + (AndroidUtilities.dp(7.0f) * diff);
                this.onlineX = AndroidUtilities.density * (-21.0f) * diff;
                this.onlineY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(24.0f) + (((float) Math.floor(AndroidUtilities.density * 11.0f)) * diff);
                int a = 0;
                while (true) {
                    SimpleTextView[] simpleTextViewArr2 = this.nameTextView;
                    if (a >= simpleTextViewArr2.length) {
                        break;
                    }
                    if (simpleTextViewArr2[a] != null) {
                        ValueAnimator valueAnimator4 = this.expandAnimator;
                        if (valueAnimator4 == null || !valueAnimator4.isRunning()) {
                            this.nameTextView[a].setTranslationX(this.nameX);
                            this.nameTextView[a].setTranslationY(this.nameY);
                            this.onlineTextView[a].setTranslationX(this.onlineX);
                            this.onlineTextView[a].setTranslationY(this.onlineY);
                            if (a == 1) {
                                this.mediaCounterTextView.setTranslationX(this.onlineX);
                                this.mediaCounterTextView.setTranslationY(this.onlineY);
                            }
                        }
                        this.nameTextView[a].setScaleX(nameScale);
                        this.nameTextView[a].setScaleY(nameScale);
                    }
                    a++;
                }
            }
            if (!this.openAnimationInProgress && ((valueAnimator = this.expandAnimator) == null || !valueAnimator.isRunning())) {
                needLayoutText(diff);
            }
        }
        if (this.isPulledDown || ((overlaysView = this.overlaysView) != null && overlaysView.animator != null && this.overlaysView.animator.isRunning())) {
            ViewGroup.LayoutParams overlaysLp = this.overlaysView.getLayoutParams();
            overlaysLp.width = this.listView.getMeasuredWidth();
            overlaysLp.height = (int) (this.extraHeight + newTop);
            this.overlaysView.requestLayout();
        }
    }

    public void updateQrItemVisibility(boolean animated) {
        if (this.qrItem == null) {
            return;
        }
        float f = 1.0f;
        int i = 0;
        boolean setQrVisible = isQrNeedVisible() && Math.min(1.0f, this.extraHeight / ((float) AndroidUtilities.dp(88.0f))) > 0.5f && this.searchTransitionProgress > 0.5f;
        if (animated) {
            if (setQrVisible != this.isQrItemVisible) {
                this.isQrItemVisible = setQrVisible;
                AnimatorSet animatorSet = this.qrItemAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.qrItemAnimation = null;
                }
                this.qrItemAnimation = new AnimatorSet();
                if (this.qrItem.getVisibility() != 8 || setQrVisible) {
                    this.qrItem.setVisibility(0);
                }
                if (setQrVisible) {
                    this.qrItemAnimation.setInterpolator(new DecelerateInterpolator());
                    this.qrItemAnimation.playTogether(ObjectAnimator.ofFloat(this.qrItem, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.qrItem, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.avatarsViewPagerIndicatorView, View.TRANSLATION_X, -AndroidUtilities.dp(48.0f)));
                } else {
                    this.qrItemAnimation.setInterpolator(new AccelerateInterpolator());
                    this.qrItemAnimation.playTogether(ObjectAnimator.ofFloat(this.qrItem, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.qrItem, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.avatarsViewPagerIndicatorView, View.TRANSLATION_X, 0.0f));
                }
                this.qrItemAnimation.setDuration(150L);
                this.qrItemAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.32
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        ProfileActivity.this.qrItemAnimation = null;
                    }
                });
                this.qrItemAnimation.start();
                return;
            }
            return;
        }
        AnimatorSet animatorSet2 = this.qrItemAnimation;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.qrItemAnimation = null;
        }
        this.isQrItemVisible = setQrVisible;
        ActionBarMenuItem actionBarMenuItem = this.qrItem;
        if (!setQrVisible) {
            f = 0.0f;
        }
        actionBarMenuItem.setAlpha(f);
        ActionBarMenuItem actionBarMenuItem2 = this.qrItem;
        if (!setQrVisible) {
            i = 8;
        }
        actionBarMenuItem2.setVisibility(i);
    }

    public void setForegroundImage(boolean secondParent) {
        String filter;
        Drawable drawable = this.avatarImage.getImageReceiver().getDrawable();
        if (drawable instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) drawable;
            this.avatarImage.setForegroundImage(null, null, fileDrawable);
            if (secondParent) {
                fileDrawable.addSecondParentView(this.avatarImage);
                return;
            }
            return;
        }
        ImageLocation location = this.avatarsViewPager.getImageLocation(0);
        if (location != null && location.imageType == 2) {
            filter = "avatar";
        } else {
            filter = null;
        }
        this.avatarImage.setForegroundImage(location, filter, drawable);
    }

    private void refreshNameAndOnlineXY() {
        this.nameX = AndroidUtilities.dp(-21.0f) + (this.avatarContainer.getMeasuredWidth() * (this.avatarScale - 1.4285715f));
        this.nameY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(1.3f) + AndroidUtilities.dp(7.0f) + ((this.avatarContainer.getMeasuredHeight() * (this.avatarScale - 1.4285715f)) / 2.0f);
        this.onlineX = AndroidUtilities.dp(-21.0f) + (this.avatarContainer.getMeasuredWidth() * (this.avatarScale - 1.4285715f));
        this.onlineY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(24.0f) + ((float) Math.floor(AndroidUtilities.density * 11.0f)) + ((this.avatarContainer.getMeasuredHeight() * (this.avatarScale - 1.4285715f)) / 2.0f);
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public RecyclerListView getListView() {
        return this.listView;
    }

    public void needLayoutText(float diff) {
        int width;
        float scale = this.nameTextView[1].getScaleX();
        float maxScale = this.extraHeight > ((float) AndroidUtilities.dp(88.0f)) ? 1.67f : 1.12f;
        if (this.extraHeight > AndroidUtilities.dp(88.0f) && scale != maxScale) {
            return;
        }
        int viewWidth = AndroidUtilities.isTablet() ? AndroidUtilities.dp(490.0f) : AndroidUtilities.displaySize.x;
        this.avatarsViewPagerIndicatorView.getSecondaryMenuItem();
        int extra = 0;
        if (this.editItemVisible) {
            extra = 0 + 48;
        }
        if (this.callItemVisible) {
            extra += 48;
        }
        if (this.videoCallItemVisible) {
            extra += 48;
        }
        if (this.searchItem != null) {
            extra += 48;
        }
        int buttonsWidth = AndroidUtilities.dp((extra * (1.0f - this.mediaHeaderAnimationProgress)) + 40.0f + 126.0f);
        int minWidth = viewWidth - buttonsWidth;
        int width2 = (int) ((viewWidth - (buttonsWidth * Math.max(0.0f, 1.0f - (diff != 1.0f ? (0.15f * diff) / (1.0f - diff) : 1.0f)))) - this.nameTextView[1].getTranslationX());
        float width22 = (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * scale) + this.nameTextView[1].getSideDrawablesSize();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
        int prevWidth = layoutParams.width;
        if (width2 < width22) {
            width = width2;
            layoutParams.width = Math.max(minWidth, (int) Math.ceil((width2 - AndroidUtilities.dp(24.0f)) / (scale + ((maxScale - scale) * 7.0f))));
        } else {
            width = width2;
            layoutParams.width = (int) Math.ceil(width22);
        }
        layoutParams.width = (int) Math.min(((viewWidth - this.nameTextView[1].getX()) / scale) - AndroidUtilities.dp(8.0f), layoutParams.width);
        if (layoutParams.width != prevWidth) {
            this.nameTextView[1].requestLayout();
        }
        float width23 = this.onlineTextView[1].getPaint().measureText(this.onlineTextView[1].getText().toString());
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
        FrameLayout.LayoutParams layoutParams22 = (FrameLayout.LayoutParams) this.mediaCounterTextView.getLayoutParams();
        int prevWidth2 = layoutParams2.width;
        int ceil = (int) Math.ceil(this.onlineTextView[1].getTranslationX() + AndroidUtilities.dp(8.0f) + (AndroidUtilities.dp(40.0f) * (1.0f - diff)));
        layoutParams2.rightMargin = ceil;
        layoutParams22.rightMargin = ceil;
        int width3 = width;
        if (width3 < width23) {
            int ceil2 = (int) Math.ceil(width3);
            layoutParams2.width = ceil2;
            layoutParams22.width = ceil2;
        } else {
            layoutParams2.width = -2;
            layoutParams22.width = -2;
        }
        if (prevWidth2 != layoutParams2.width) {
            this.onlineTextView[1].requestLayout();
            this.mediaCounterTextView.requestLayout();
        }
    }

    private void fixLayout() {
        if (this.fragmentView == null) {
            return;
        }
        this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ProfileActivity.33
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                if (ProfileActivity.this.fragmentView != null) {
                    ProfileActivity.this.checkListViewScroll();
                    ProfileActivity.this.needLayout(true);
                    ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration newConfig) {
        View view;
        super.onConfigurationChanged(newConfig);
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onConfigurationChanged(newConfig);
        }
        invalidateIsInLandscapeMode();
        if (this.isInLandscapeMode && this.isPulledDown && (view = this.layoutManager.findViewByPosition(0)) != null) {
            this.listView.scrollBy(0, view.getTop() - AndroidUtilities.dp(88.0f));
        }
        fixLayout();
    }

    private void invalidateIsInLandscapeMode() {
        Point size = new Point();
        Display display = getParentActivity().getWindowManager().getDefaultDisplay();
        display.getSize(size);
        this.isInLandscapeMode = size.x > size.y;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, final Object... args) {
        TLRPC.Chat chat;
        RecyclerListView recyclerListView;
        RecyclerListView recyclerListView2;
        RecyclerListView.Holder holder;
        boolean infoChanged = false;
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 || (MessagesController.UPDATE_MASK_NAME & mask) != 0 || (MessagesController.UPDATE_MASK_STATUS & mask) != 0) {
                infoChanged = true;
            }
            if (this.userId != 0) {
                if (infoChanged) {
                    updateProfileData(true);
                }
                if ((MessagesController.UPDATE_MASK_PHONE & mask) != 0 && (recyclerListView2 = this.listView) != null && (holder = (RecyclerListView.Holder) recyclerListView2.findViewHolderForPosition(this.phoneRow)) != null) {
                    this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                }
            } else if (this.chatId != 0) {
                if ((MessagesController.UPDATE_MASK_CHAT & mask) != 0 || (MessagesController.UPDATE_MASK_CHAT_AVATAR & mask) != 0 || (MessagesController.UPDATE_MASK_CHAT_NAME & mask) != 0 || (MessagesController.UPDATE_MASK_CHAT_MEMBERS & mask) != 0 || (MessagesController.UPDATE_MASK_STATUS & mask) != 0) {
                    if ((MessagesController.UPDATE_MASK_CHAT & mask) != 0) {
                        updateListAnimated(true);
                    } else {
                        updateOnlineCount(true);
                    }
                    updateProfileData(true);
                }
                if (infoChanged && (recyclerListView = this.listView) != null) {
                    int count = recyclerListView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof UserCell) {
                            ((UserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.chatOnlineCountDidLoad) {
            Long chatId = (Long) args[0];
            if (this.chatInfo == null || (chat = this.currentChat) == null || chat.id != chatId.longValue()) {
                return;
            }
            this.chatInfo.online_count = ((Integer) args[1]).intValue();
            updateOnlineCount(true);
            updateProfileData(false);
        } else if (id == NotificationCenter.contactsDidLoad) {
            createActionBarMenu(true);
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda19
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProfileActivity.this.m4365xcb9826bb(args);
                    }
                });
            }
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            TLRPC.EncryptedChat chat2 = (TLRPC.EncryptedChat) args[0];
            if (this.currentEncryptedChat != null && chat2.id == this.currentEncryptedChat.id) {
                this.currentEncryptedChat = chat2;
                updateListAnimated(false);
            }
        } else if (id == NotificationCenter.blockedUsersDidLoad) {
            boolean oldValue = this.userBlocked;
            boolean z = getMessagesController().blockePeers.indexOfKey(this.userId) >= 0;
            this.userBlocked = z;
            if (oldValue != z) {
                createActionBarMenu(true);
                updateListAnimated(false);
            }
        } else if (id == NotificationCenter.groupCallUpdated) {
            Long chatId2 = (Long) args[0];
            if (this.currentChat != null && chatId2.longValue() == this.currentChat.id && ChatObject.canManageCalls(this.currentChat)) {
                TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(chatId2.longValue());
                if (chatFull != null) {
                    TLRPC.ChatFull chatFull2 = this.chatInfo;
                    if (chatFull2 != null) {
                        chatFull.participants = chatFull2.participants;
                    }
                    this.chatInfo = chatFull;
                }
                TLRPC.ChatFull chatFull3 = this.chatInfo;
                if (chatFull3 == null) {
                    return;
                }
                if ((chatFull3.call == null && !this.hasVoiceChatItem) || (this.chatInfo.call != null && this.hasVoiceChatItem)) {
                    createActionBarMenu(false);
                }
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull4 = (TLRPC.ChatFull) args[0];
            if (chatFull4.id == this.chatId) {
                boolean byChannelUsers = ((Boolean) args[2]).booleanValue();
                if ((this.chatInfo instanceof TLRPC.TL_channelFull) && chatFull4.participants == null) {
                    chatFull4.participants = this.chatInfo.participants;
                }
                if (this.chatInfo == null && (chatFull4 instanceof TLRPC.TL_channelFull)) {
                    infoChanged = true;
                }
                this.chatInfo = chatFull4;
                if (this.mergeDialogId == 0 && chatFull4.migrated_from_chat_id != 0) {
                    this.mergeDialogId = -this.chatInfo.migrated_from_chat_id;
                    getMediaDataController().getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                }
                fetchUsersFromChannelInfo();
                ProfileGalleryView profileGalleryView = this.avatarsViewPager;
                if (profileGalleryView != null) {
                    profileGalleryView.setChatInfo(this.chatInfo);
                }
                updateListAnimated(true);
                TLRPC.Chat newChat = getMessagesController().getChat(Long.valueOf(this.chatId));
                if (newChat != null) {
                    this.currentChat = newChat;
                    createActionBarMenu(true);
                }
                if (this.currentChat.megagroup && (infoChanged || !byChannelUsers)) {
                    getChannelParticipants(true);
                }
                updateAutoDeleteItem();
                updateTtlIcon();
            }
        } else if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.botInfoDidLoad) {
            TLRPC.BotInfo info = (TLRPC.BotInfo) args[0];
            if (info.user_id == this.userId) {
                this.botInfo = info;
                updateListAnimated(false);
            }
        } else if (id == NotificationCenter.userInfoDidLoad) {
            long uid = ((Long) args[0]).longValue();
            if (uid == this.userId) {
                TLRPC.UserFull userFull = (TLRPC.UserFull) args[1];
                this.userInfo = userFull;
                if (this.imageUpdater != null) {
                    if (!TextUtils.equals(userFull.about, this.currentBio)) {
                        this.listAdapter.notifyItemChanged(this.bioRow);
                    }
                } else {
                    if (!this.openAnimationInProgress && !this.callItemVisible) {
                        createActionBarMenu(true);
                    } else {
                        this.recreateMenuAfterAnimation = true;
                    }
                    updateListAnimated(false);
                    this.sharedMediaLayout.setCommonGroupsCount(this.userInfo.common_chats_count);
                    updateSelectedMediaTabText();
                    SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader = this.sharedMediaPreloader;
                    if (sharedMediaPreloader == null || sharedMediaPreloader.isMediaWasLoaded()) {
                        resumeDelayedFragmentAnimation();
                        needLayout(true);
                    }
                }
                updateAutoDeleteItem();
                updateTtlIcon();
            }
        } else if (id == NotificationCenter.privacyRulesUpdated) {
            if (this.qrItem != null) {
                updateQrItemVisibility(true);
            }
        } else if (id == NotificationCenter.didReceiveNewMessages) {
            boolean scheduled = ((Boolean) args[2]).booleanValue();
            if (scheduled) {
                return;
            }
            long did = getDialogId();
            if (did == ((Long) args[0]).longValue()) {
                DialogObject.isEncryptedDialog(did);
                ArrayList<MessageObject> arr = (ArrayList) args[1];
                for (int a2 = 0; a2 < arr.size(); a2++) {
                    MessageObject obj = arr.get(a2);
                    if (this.currentEncryptedChat != null && (obj.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction) && (obj.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
                        TLRPC.TL_decryptedMessageActionSetMessageTTL tL_decryptedMessageActionSetMessageTTL = (TLRPC.TL_decryptedMessageActionSetMessageTTL) obj.messageOwner.action.encryptedAction;
                        ListAdapter listAdapter = this.listAdapter;
                        if (listAdapter != null) {
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView3 = this.listView;
            if (recyclerListView3 != null) {
                recyclerListView3.invalidateViews();
            }
        } else if (id == NotificationCenter.reloadInterface) {
            int i = this.emptyRow;
            updateListAnimated(false);
        } else if (id == NotificationCenter.newSuggestionsAvailable) {
            int prevRow1 = this.passwordSuggestionRow;
            int prevRow2 = this.phoneSuggestionRow;
            updateRowsIds();
            if (prevRow1 != this.passwordSuggestionRow || prevRow2 != this.phoneSuggestionRow) {
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: lambda$didReceivedNotification$29$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4365xcb9826bb(Object[] args) {
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) args[0];
        Bundle args2 = new Bundle();
        args2.putInt("enc_id", encryptedChat.id);
        presentFragment(new ChatActivity(args2), true);
    }

    private void updateAutoDeleteItem() {
        if (this.autoDeleteItem == null || this.autoDeletePopupWrapper == null) {
            return;
        }
        int ttl = 0;
        TLRPC.UserFull userFull = this.userInfo;
        if (userFull != null || this.chatInfo != null) {
            ttl = userFull != null ? userFull.ttl_period : this.chatInfo.ttl_period;
        }
        this.autoDeleteItemDrawable.setTime(ttl);
        this.autoDeletePopupWrapper.m2212xf8eeadab(ttl);
    }

    private void updateTimeItem() {
        TimerDrawable timerDrawable = this.timerDrawable;
        if (timerDrawable == null) {
            return;
        }
        TLRPC.EncryptedChat encryptedChat = this.currentEncryptedChat;
        if (encryptedChat != null) {
            timerDrawable.setTime(encryptedChat.ttl);
            this.timeItem.setTag(1);
            this.timeItem.setVisibility(0);
            return;
        }
        TLRPC.UserFull userFull = this.userInfo;
        if (userFull != null) {
            timerDrawable.setTime(userFull.ttl_period);
            if (this.needTimerImage && this.userInfo.ttl_period != 0) {
                this.timeItem.setTag(1);
                this.timeItem.setVisibility(0);
                return;
            }
            this.timeItem.setTag(null);
            this.timeItem.setVisibility(8);
            return;
        }
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (chatFull != null) {
            timerDrawable.setTime(chatFull.ttl_period);
            if (this.needTimerImage && this.chatInfo.ttl_period != 0) {
                this.timeItem.setTag(1);
                this.timeItem.setVisibility(0);
                return;
            }
            this.timeItem.setTag(null);
            this.timeItem.setVisibility(8);
            return;
        }
        this.timeItem.setTag(null);
        this.timeItem.setVisibility(8);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean needDelayOpenAnimation() {
        if (this.playProfileAnimation == 0) {
            return true;
        }
        return false;
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate
    public void mediaCountUpdated() {
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null && (sharedMediaPreloader = this.sharedMediaPreloader) != null) {
            sharedMediaLayout.setNewMediaCounts(sharedMediaPreloader.getLastMediaCount());
        }
        updateSharedMediaRows();
        updateSelectedMediaTabText();
        if (this.userInfo != null) {
            resumeDelayedFragmentAnimation();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onResume();
        }
        invalidateIsInLandscapeMode();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            this.firstLayout = true;
            listAdapter.notifyDataSetChanged();
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onResume();
            setParentActivityTitle(LocaleController.getString("Settings", R.string.Settings));
        }
        updateProfileData(true);
        fixLayout();
        SimpleTextView[] simpleTextViewArr = this.nameTextView;
        if (simpleTextViewArr[1] != null) {
            setParentActivityTitle(simpleTextViewArr[1].getText());
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onPause();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent event) {
        SharedMediaLayout sharedMediaLayout;
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null && profileGalleryView.getVisibility() == 0 && this.avatarsViewPager.getRealCount() > 1) {
            this.avatarsViewPager.getHitRect(this.rect);
            if (this.rect.contains((int) event.getX(), ((int) event.getY()) - this.actionBar.getMeasuredHeight())) {
                return false;
            }
        }
        if (this.sharedMediaRow == -1 || (sharedMediaLayout = this.sharedMediaLayout) == null) {
            return true;
        }
        if (!sharedMediaLayout.isSwipeBackEnabled()) {
            return false;
        }
        this.sharedMediaLayout.getHitRect(this.rect);
        if (this.rect.contains((int) event.getX(), ((int) event.getY()) - this.actionBar.getMeasuredHeight())) {
            return this.sharedMediaLayout.isCurrentTabFirst();
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean canBeginSlide() {
        if (!this.sharedMediaLayout.isSwipeBackEnabled()) {
            return false;
        }
        return super.canBeginSlide();
    }

    public UndoView getUndoView() {
        return this.undoView;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        SharedMediaLayout sharedMediaLayout;
        return this.actionBar.isEnabled() && (this.sharedMediaRow == -1 || (sharedMediaLayout = this.sharedMediaLayout) == null || !sharedMediaLayout.closeActionMode());
    }

    public boolean isSettings() {
        return this.imageUpdater != null;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    public void setPlayProfileAnimation(int type) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (!AndroidUtilities.isTablet()) {
            this.needTimerImage = type != 0;
            if (preferences.getBoolean("view_animations", true)) {
                this.playProfileAnimation = type;
            } else if (type == 2) {
                this.expandPhoto = true;
            }
        }
    }

    private void updateSharedMediaRows() {
        if (this.listAdapter == null) {
            return;
        }
        updateListAnimated(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        this.isFragmentOpened = isOpen;
        if (((!isOpen && backward) || (isOpen && !backward)) && this.playProfileAnimation != 0 && this.allowProfileAnimation && !this.isPulledDown) {
            this.openAnimationInProgress = true;
        }
        if (isOpen) {
            if (this.imageUpdater != null) {
                this.transitionIndex = getNotificationCenter().setAnimationInProgress(this.transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad, NotificationCenter.userInfoDidLoad});
            } else {
                this.transitionIndex = getNotificationCenter().setAnimationInProgress(this.transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad});
            }
            if (Build.VERSION.SDK_INT >= 21 && !backward && getParentActivity() != null) {
                this.navigationBarAnimationColorFrom = getParentActivity().getWindow().getNavigationBarColor();
            }
        }
        this.transitionAnimationInProress = true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            if (!backward) {
                if (this.playProfileAnimation != 0 && this.allowProfileAnimation) {
                    this.openAnimationInProgress = false;
                    checkListViewScroll();
                    if (this.recreateMenuAfterAnimation) {
                        createActionBarMenu(true);
                    }
                }
                if (!this.fragmentOpened) {
                    this.fragmentOpened = true;
                    this.invalidateScroll = true;
                    this.fragmentView.requestLayout();
                }
            }
            getNotificationCenter().onAnimationFinish(this.transitionIndex);
        }
        this.transitionAnimationInProress = false;
    }

    public float getAnimationProgress() {
        return this.animationProgress;
    }

    public void setAnimationProgress(float progress) {
        int color;
        this.animationProgress = progress;
        this.listView.setAlpha(progress);
        this.listView.setTranslationX(AndroidUtilities.dp(48.0f) - (AndroidUtilities.dp(48.0f) * progress));
        if (this.playProfileAnimation != 2 || this.avatarColor == 0) {
            color = AvatarDrawable.getProfileBackColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5L : this.chatId, this.resourcesProvider);
        } else {
            color = this.avatarColor;
        }
        int actionBarColor = this.actionBarAnimationColorFrom;
        if (actionBarColor == 0) {
            actionBarColor = getThemedColor(Theme.key_actionBarDefault);
        }
        int actionBarColor2 = actionBarColor;
        if (SharedConfig.chatBlurEnabled()) {
            actionBarColor = ColorUtils.setAlphaComponent(actionBarColor, 0);
        }
        this.topView.setBackgroundColor(ColorUtils.blendARGB(actionBarColor, color, progress));
        this.timerDrawable.setBackgroundColor(ColorUtils.blendARGB(actionBarColor2, color, progress));
        int color2 = AvatarDrawable.getIconColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5L : this.chatId, this.resourcesProvider);
        int iconColor = getThemedColor(Theme.key_actionBarDefaultIcon);
        this.actionBar.setItemsColor(ColorUtils.blendARGB(iconColor, color2, progress), false);
        int color3 = getThemedColor(Theme.key_profile_title);
        int titleColor = getThemedColor(Theme.key_actionBarDefaultTitle);
        for (int i = 0; i < 2; i++) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (simpleTextViewArr[i] != null && (i != 1 || this.playProfileAnimation != 2)) {
                simpleTextViewArr[i].setTextColor(ColorUtils.blendARGB(titleColor, color3, progress));
            }
        }
        int color4 = this.isOnline[0] ? getThemedColor(Theme.key_profile_status) : AvatarDrawable.getProfileTextColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5L : this.chatId, this.resourcesProvider);
        int subtitleColor = getThemedColor(this.isOnline[0] ? Theme.key_chat_status : Theme.key_actionBarDefaultSubtitle);
        for (int i2 = 0; i2 < 2; i2++) {
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (simpleTextViewArr2[i2] != null && (i2 != 1 || this.playProfileAnimation != 2)) {
                simpleTextViewArr2[i2].setTextColor(ColorUtils.blendARGB(subtitleColor, color4, progress));
            }
        }
        this.extraHeight = this.initialAnimationExtraHeight * progress;
        long j = this.userId;
        if (j == 0) {
            j = this.chatId;
        }
        int color5 = AvatarDrawable.getProfileColorForId(j, this.resourcesProvider);
        long j2 = this.userId;
        if (j2 == 0) {
            j2 = this.chatId;
        }
        int color22 = AvatarDrawable.getColorForId(j2);
        if (color5 != color22) {
            this.avatarDrawable.setColor(ColorUtils.blendARGB(color22, color5, progress));
            this.avatarImage.invalidate();
        }
        int i3 = this.navigationBarAnimationColorFrom;
        if (i3 != 0) {
            setNavigationBarColor(ColorUtils.blendARGB(i3, getNavigationBarColor(), progress));
        }
        this.topView.invalidate();
        needLayout(true);
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
        AboutLinkCell aboutLinkCell = this.aboutLinkCell;
        if (aboutLinkCell != null) {
            aboutLinkCell.invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public int getNavigationBarColor() {
        return Theme.getColor(Theme.key_windowBackgroundGray, this.resourcesProvider);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, final Runnable callback) {
        if (this.playProfileAnimation != 0 && this.allowProfileAnimation && !this.isPulledDown && !this.disableProfileAnimation) {
            ImageView imageView = this.timeItem;
            if (imageView != null) {
                imageView.setAlpha(1.0f);
            }
            if (this.parentLayout != null && this.parentLayout.fragmentsStack.size() >= 2) {
                BaseFragment fragment = this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2);
                if (fragment instanceof ChatActivity) {
                    this.previousTransitionFragment = (ChatActivity) fragment;
                }
            }
            if (this.previousTransitionFragment != null) {
                updateTimeItem();
            }
            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(this.playProfileAnimation == 2 ? 250L : 180L);
            this.listView.setLayerType(2, null);
            ActionBarMenu menu = this.actionBar.createMenu();
            if (menu.getItem(10) == null && this.animatingItem == null) {
                this.animatingItem = menu.addItem(10, R.drawable.ic_ab_other);
            }
            if (!isOpen) {
                this.initialAnimationExtraHeight = this.extraHeight;
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, "animationProgress", 1.0f, 0.0f));
                RLottieImageView rLottieImageView = this.writeButton;
                if (rLottieImageView != null) {
                    animators.add(ObjectAnimator.ofFloat(rLottieImageView, View.SCALE_X, 0.2f));
                    animators.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, 0.2f));
                    animators.add(ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, 0.0f));
                }
                int a = 0;
                while (a < 2) {
                    SimpleTextView simpleTextView = this.nameTextView[a];
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = a == 0 ? 1.0f : 0.0f;
                    animators.add(ObjectAnimator.ofFloat(simpleTextView, property, fArr));
                    a++;
                }
                if (this.timeItem.getTag() != null) {
                    this.timeItem.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(this.timeItem, View.ALPHA, 0.0f, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(this.timeItem, View.SCALE_X, 0.0f, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(this.timeItem, View.SCALE_Y, 0.0f, 1.0f));
                }
                ActionBarMenuItem actionBarMenuItem = this.animatingItem;
                if (actionBarMenuItem != null) {
                    actionBarMenuItem.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, 1.0f));
                }
                if (this.callItemVisible && this.chatId != 0) {
                    this.callItem.setAlpha(1.0f);
                    animators.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, 0.0f));
                }
                if (this.videoCallItemVisible) {
                    this.videoCallItem.setAlpha(1.0f);
                    animators.add(ObjectAnimator.ofFloat(this.videoCallItem, View.ALPHA, 0.0f));
                }
                if (this.editItemVisible) {
                    this.editItem.setAlpha(1.0f);
                    animators.add(ObjectAnimator.ofFloat(this.editItem, View.ALPHA, 0.0f));
                }
                ImageView imageView2 = this.ttlIconView;
                if (imageView2 != null) {
                    animators.add(ObjectAnimator.ofFloat(imageView2, View.ALPHA, this.ttlIconView.getAlpha(), 0.0f));
                }
                boolean crossfadeOnlineText = false;
                BaseFragment previousFragment = this.parentLayout.fragmentsStack.size() > 1 ? this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2) : null;
                if (previousFragment instanceof ChatActivity) {
                    ChatAvatarContainer avatarContainer = ((ChatActivity) previousFragment).getAvatarContainer();
                    if (avatarContainer.getSubtitleTextView().getLeftDrawable() != null || avatarContainer.statusMadeShorter[0]) {
                        this.transitionOnlineText = avatarContainer.getSubtitleTextView();
                        this.avatarContainer2.invalidate();
                        crossfadeOnlineText = true;
                        animators.add(ObjectAnimator.ofFloat(this.onlineTextView[0], View.ALPHA, 0.0f));
                        animators.add(ObjectAnimator.ofFloat(this.onlineTextView[1], View.ALPHA, 0.0f));
                    }
                }
                if (!crossfadeOnlineText) {
                    int a2 = 0;
                    while (a2 < 2) {
                        SimpleTextView simpleTextView2 = this.onlineTextView[a2];
                        Property property2 = View.ALPHA;
                        float[] fArr2 = new float[1];
                        fArr2[0] = a2 == 0 ? 1.0f : 0.0f;
                        animators.add(ObjectAnimator.ofFloat(simpleTextView2, property2, fArr2));
                        a2++;
                    }
                }
                animatorSet.playTogether(animators);
            } else {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
                layoutParams.rightMargin = (int) ((AndroidUtilities.density * (-21.0f)) + AndroidUtilities.dp(8.0f));
                this.onlineTextView[1].setLayoutParams(layoutParams);
                if (this.playProfileAnimation != 2) {
                    int width = (int) Math.ceil((AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0f)) + (AndroidUtilities.density * 21.0f));
                    float width2 = (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * 1.12f) + this.nameTextView[1].getSideDrawablesSize();
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
                    if (width < width2) {
                        layoutParams2.width = (int) Math.ceil(width / 1.12f);
                    } else {
                        layoutParams2.width = -2;
                    }
                    this.nameTextView[1].setLayoutParams(layoutParams2);
                    this.initialAnimationExtraHeight = AndroidUtilities.dp(88.0f);
                } else {
                    FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
                    layoutParams3.width = (int) ((AndroidUtilities.displaySize.x - AndroidUtilities.dp(32.0f)) / 1.67f);
                    this.nameTextView[1].setLayoutParams(layoutParams3);
                }
                this.fragmentView.setBackgroundColor(0);
                setAnimationProgress(0.0f);
                ArrayList<Animator> animators2 = new ArrayList<>();
                animators2.add(ObjectAnimator.ofFloat(this, "animationProgress", 0.0f, 1.0f));
                RLottieImageView rLottieImageView2 = this.writeButton;
                if (rLottieImageView2 != null && rLottieImageView2.getTag() == null) {
                    this.writeButton.setScaleX(0.2f);
                    this.writeButton.setScaleY(0.2f);
                    this.writeButton.setAlpha(0.0f);
                    animators2.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, 1.0f));
                    animators2.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, 1.0f));
                    animators2.add(ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, 1.0f));
                }
                if (this.playProfileAnimation == 2) {
                    this.avatarColor = AndroidUtilities.calcBitmapColor(this.avatarImage.getImageReceiver().getBitmap());
                    this.nameTextView[1].setTextColor(-1);
                    this.onlineTextView[1].setTextColor(Color.argb(179, 255, 255, 255));
                    this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
                    this.overlaysView.setOverlaysVisible();
                }
                int a3 = 0;
                while (a3 < 2) {
                    this.nameTextView[a3].setAlpha(a3 == 0 ? 1.0f : 0.0f);
                    SimpleTextView simpleTextView3 = this.nameTextView[a3];
                    Property property3 = View.ALPHA;
                    float[] fArr3 = new float[1];
                    fArr3[0] = a3 == 0 ? 0.0f : 1.0f;
                    animators2.add(ObjectAnimator.ofFloat(simpleTextView3, property3, fArr3));
                    a3++;
                }
                if (this.timeItem.getTag() != null) {
                    animators2.add(ObjectAnimator.ofFloat(this.timeItem, View.ALPHA, 1.0f, 0.0f));
                    animators2.add(ObjectAnimator.ofFloat(this.timeItem, View.SCALE_X, 1.0f, 0.0f));
                    animators2.add(ObjectAnimator.ofFloat(this.timeItem, View.SCALE_Y, 1.0f, 0.0f));
                }
                ActionBarMenuItem actionBarMenuItem2 = this.animatingItem;
                if (actionBarMenuItem2 != null) {
                    actionBarMenuItem2.setAlpha(1.0f);
                    animators2.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, 0.0f));
                }
                if (this.callItemVisible && this.chatId != 0) {
                    this.callItem.setAlpha(0.0f);
                    animators2.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, 1.0f));
                }
                if (this.videoCallItemVisible) {
                    this.videoCallItem.setAlpha(0.0f);
                    animators2.add(ObjectAnimator.ofFloat(this.videoCallItem, View.ALPHA, 1.0f));
                }
                if (this.editItemVisible) {
                    this.editItem.setAlpha(0.0f);
                    animators2.add(ObjectAnimator.ofFloat(this.editItem, View.ALPHA, 1.0f));
                }
                if (this.ttlIconView.getTag() != null) {
                    this.ttlIconView.setAlpha(0.0f);
                    animators2.add(ObjectAnimator.ofFloat(this.ttlIconView, View.ALPHA, 1.0f));
                }
                boolean onlineTextCrosafade = false;
                BaseFragment previousFragment2 = this.parentLayout.fragmentsStack.size() > 1 ? this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2) : null;
                if (previousFragment2 instanceof ChatActivity) {
                    ChatAvatarContainer avatarContainer2 = ((ChatActivity) previousFragment2).getAvatarContainer();
                    if (avatarContainer2.getSubtitleTextView().getLeftDrawable() != null || avatarContainer2.statusMadeShorter[0]) {
                        this.transitionOnlineText = avatarContainer2.getSubtitleTextView();
                        this.avatarContainer2.invalidate();
                        onlineTextCrosafade = true;
                        this.onlineTextView[0].setAlpha(0.0f);
                        this.onlineTextView[1].setAlpha(0.0f);
                        animators2.add(ObjectAnimator.ofFloat(this.onlineTextView[1], View.ALPHA, 1.0f));
                    }
                }
                if (!onlineTextCrosafade) {
                    int a4 = 0;
                    while (a4 < 2) {
                        this.onlineTextView[a4].setAlpha(a4 == 0 ? 1.0f : 0.0f);
                        SimpleTextView simpleTextView4 = this.onlineTextView[a4];
                        Property property4 = View.ALPHA;
                        float[] fArr4 = new float[1];
                        fArr4[0] = a4 == 0 ? 0.0f : 1.0f;
                        animators2.add(ObjectAnimator.ofFloat(simpleTextView4, property4, fArr4));
                        a4++;
                    }
                }
                animatorSet.playTogether(animators2);
            }
            this.profileTransitionInProgress = true;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda22
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ProfileActivity.this.m4374x9575e2fd(valueAnimator2);
                }
            });
            animatorSet.playTogether(valueAnimator);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.34
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ProfileActivity.this.listView.setLayerType(0, null);
                    if (ProfileActivity.this.animatingItem != null) {
                        ActionBarMenu menu2 = ProfileActivity.this.actionBar.createMenu();
                        menu2.clearItems();
                        ProfileActivity.this.animatingItem = null;
                    }
                    callback.run();
                    if (ProfileActivity.this.playProfileAnimation == 2) {
                        ProfileActivity.this.playProfileAnimation = 1;
                        ProfileActivity.this.avatarImage.setForegroundAlpha(1.0f);
                        ProfileActivity.this.avatarContainer.setVisibility(8);
                        ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                        ProfileActivity.this.avatarsViewPager.setVisibility(0);
                    }
                    ProfileActivity.this.transitionOnlineText = null;
                    ProfileActivity.this.avatarContainer2.invalidate();
                    ProfileActivity.this.profileTransitionInProgress = false;
                    ProfileActivity.this.previousTransitionFragment = null;
                    ProfileActivity.this.fragmentView.invalidate();
                }
            });
            animatorSet.setInterpolator(this.playProfileAnimation == 2 ? CubicBezierInterpolator.DEFAULT : new DecelerateInterpolator());
            animatorSet.getClass();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    animatorSet.start();
                }
            }, 50L);
            return animatorSet;
        }
        return null;
    }

    /* renamed from: lambda$onCustomTransitionAnimation$30$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4374x9575e2fd(ValueAnimator valueAnimator1) {
        this.fragmentView.invalidate();
    }

    public void updateOnlineCount(boolean notify) {
        this.onlineCount = 0;
        final int currentTime = getConnectionsManager().getCurrentTime();
        this.sortedUsers.clear();
        TLRPC.ChatFull chatFull = this.chatInfo;
        if ((chatFull instanceof TLRPC.TL_chatFull) || ((chatFull instanceof TLRPC.TL_channelFull) && chatFull.participants_count <= 200 && this.chatInfo.participants != null)) {
            for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                TLRPC.ChatParticipant participant = this.chatInfo.participants.participants.get(a);
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(participant.user_id));
                if (user != null && user.status != null && ((user.status.expires > currentTime || user.id == getUserConfig().getClientUserId()) && user.status.expires > 10000)) {
                    this.onlineCount++;
                }
                this.sortedUsers.add(Integer.valueOf(a));
            }
            try {
                Collections.sort(this.sortedUsers, new Comparator() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda21
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        return ProfileActivity.this.m4389lambda$updateOnlineCount$31$orgtelegramuiProfileActivity(currentTime, (Integer) obj, (Integer) obj2);
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (notify && this.listAdapter != null && this.membersStartRow > 0) {
                AndroidUtilities.updateVisibleRows(this.listView);
            }
            if (this.sharedMediaLayout == null || this.sharedMediaRow == -1) {
                return;
            }
            if ((this.sortedUsers.size() > 5 || this.usersForceShowingIn == 2) && this.usersForceShowingIn != 1) {
                this.sharedMediaLayout.setChatUsers(this.sortedUsers, this.chatInfo);
                return;
            }
            return;
        }
        TLRPC.ChatFull chatFull2 = this.chatInfo;
        if ((chatFull2 instanceof TLRPC.TL_channelFull) && chatFull2.participants_count > 200) {
            this.onlineCount = this.chatInfo.online_count;
        }
    }

    /* renamed from: lambda$updateOnlineCount$31$org-telegram-ui-ProfileActivity */
    public /* synthetic */ int m4389lambda$updateOnlineCount$31$orgtelegramuiProfileActivity(int currentTime, Integer lhs, Integer rhs) {
        TLRPC.User user1 = getMessagesController().getUser(Long.valueOf(this.chatInfo.participants.participants.get(rhs.intValue()).user_id));
        TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.chatInfo.participants.participants.get(lhs.intValue()).user_id));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null) {
            if (user1.bot) {
                status1 = -110;
            } else if (user1.self) {
                status1 = currentTime + 50000;
            } else if (user1.status != null) {
                status1 = user1.status.expires;
            }
        }
        if (user2 != null) {
            if (user2.bot) {
                status2 = -110;
            } else if (user2.self) {
                status2 = currentTime + 50000;
            } else if (user2.status != null) {
                status2 = user2.status.expires;
            }
        }
        if (status1 > 0 && status2 > 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if (status1 < 0 && status2 < 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
            return -1;
        } else {
            return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
        }
    }

    public void setChatInfo(TLRPC.ChatFull value) {
        this.chatInfo = value;
        if (value != null && value.migrated_from_chat_id != 0 && this.mergeDialogId == 0) {
            this.mergeDialogId = -this.chatInfo.migrated_from_chat_id;
            getMediaDataController().getMediaCounts(this.mergeDialogId, this.classGuid);
        }
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.setChatInfo(this.chatInfo);
        }
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.setChatInfo(this.chatInfo);
        }
        fetchUsersFromChannelInfo();
    }

    public void setUserInfo(TLRPC.UserFull value) {
        this.userInfo = value;
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public boolean canSearchMembers() {
        return this.canSearchMembers;
    }

    private void fetchUsersFromChannelInfo() {
        TLRPC.Chat chat = this.currentChat;
        if (chat == null || !chat.megagroup) {
            return;
        }
        TLRPC.ChatFull chatFull = this.chatInfo;
        if ((chatFull instanceof TLRPC.TL_channelFull) && chatFull.participants != null) {
            for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                TLRPC.ChatParticipant chatParticipant = this.chatInfo.participants.participants.get(a);
                this.participantsMap.put(chatParticipant.user_id, chatParticipant);
            }
        }
    }

    private void kickUser(long uid, TLRPC.ChatParticipant participant) {
        if (uid != 0) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(uid));
            getMessagesController().deleteParticipantFromChat(this.chatId, user, this.chatInfo);
            if (this.currentChat != null && user != null && BulletinFactory.canShowBulletin(this)) {
                BulletinFactory.createRemoveFromChatBulletin(this, user, this.currentChat.title).show();
            }
            if (this.chatInfo.participants.participants.remove(participant)) {
                updateListAnimated(true);
                return;
            }
            return;
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(-this.chatId));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        getMessagesController().deleteParticipantFromChat(this.chatId, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), this.chatInfo);
        this.playProfileAnimation = 0;
        finishFragment();
    }

    public boolean isChat() {
        return this.chatId != 0;
    }

    public void updateRowsIds() {
        int actionBarHeight;
        int i;
        TLRPC.UserFull userFull;
        ProfileGalleryView profileGalleryView;
        int prevRowsCount = this.rowCount;
        this.rowCount = 0;
        this.setAvatarRow = -1;
        this.setAvatarSectionRow = -1;
        this.numberSectionRow = -1;
        this.numberRow = -1;
        this.setUsernameRow = -1;
        this.bioRow = -1;
        this.phoneSuggestionSectionRow = -1;
        this.phoneSuggestionRow = -1;
        this.passwordSuggestionSectionRow = -1;
        this.passwordSuggestionRow = -1;
        this.settingsSectionRow = -1;
        this.settingsSectionRow2 = -1;
        this.notificationRow = -1;
        this.languageRow = -1;
        this.premiumRow = -1;
        this.premiumSectionsRow = -1;
        this.privacyRow = -1;
        this.dataRow = -1;
        this.chatRow = -1;
        this.filtersRow = -1;
        this.stickersRow = -1;
        this.devicesRow = -1;
        this.devicesSectionRow = -1;
        this.helpHeaderRow = -1;
        this.questionRow = -1;
        this.faqRow = -1;
        this.policyRow = -1;
        this.helpSectionCell = -1;
        this.debugHeaderRow = -1;
        this.sendLogsRow = -1;
        this.sendLastLogsRow = -1;
        this.clearLogsRow = -1;
        this.switchBackendRow = -1;
        this.versionRow = -1;
        this.sendMessageRow = -1;
        this.reportRow = -1;
        this.emptyRow = -1;
        this.infoHeaderRow = -1;
        this.phoneRow = -1;
        this.userInfoRow = -1;
        this.locationRow = -1;
        this.channelInfoRow = -1;
        this.usernameRow = -1;
        this.settingsTimerRow = -1;
        this.settingsKeyRow = -1;
        this.notificationsDividerRow = -1;
        this.notificationsRow = -1;
        this.infoSectionRow = -1;
        this.secretSettingsSectionRow = -1;
        this.bottomPaddingRow = -1;
        this.addToGroupButtonRow = -1;
        this.addToGroupInfoRow = -1;
        this.membersHeaderRow = -1;
        this.membersStartRow = -1;
        this.membersEndRow = -1;
        this.addMemberRow = -1;
        this.subscribersRow = -1;
        this.subscribersRequestsRow = -1;
        this.administratorsRow = -1;
        this.blockedUsersRow = -1;
        this.membersSectionRow = -1;
        this.sharedMediaRow = -1;
        this.unblockRow = -1;
        this.joinRow = -1;
        this.lastSectionRow = -1;
        this.visibleChatParticipants.clear();
        this.visibleSortedUsers.clear();
        boolean hasMedia = false;
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader = this.sharedMediaPreloader;
        if (sharedMediaPreloader != null) {
            int[] lastMediaCount = sharedMediaPreloader.getLastMediaCount();
            int a = 0;
            while (true) {
                if (a >= lastMediaCount.length) {
                    break;
                } else if (lastMediaCount[a] <= 0) {
                    a++;
                } else {
                    hasMedia = true;
                    break;
                }
            }
        }
        boolean z = true;
        if (this.userId != 0) {
            if (LocaleController.isRTL) {
                int i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.emptyRow = i2;
            }
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (UserObject.isUserSelf(user)) {
                if (this.avatarBig == null && ((user.photo == null || (!(user.photo.photo_big instanceof TLRPC.TL_fileLocation_layer97) && !(user.photo.photo_big instanceof TLRPC.TL_fileLocationToBeDeprecated))) && ((profileGalleryView = this.avatarsViewPager) == null || profileGalleryView.getRealCount() == 0))) {
                    int i3 = this.rowCount;
                    int i4 = i3 + 1;
                    this.rowCount = i4;
                    this.setAvatarRow = i3;
                    this.rowCount = i4 + 1;
                    this.setAvatarSectionRow = i4;
                }
                int i5 = this.rowCount;
                int i6 = i5 + 1;
                this.rowCount = i6;
                this.numberSectionRow = i5;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.numberRow = i6;
                int i8 = i7 + 1;
                this.rowCount = i8;
                this.setUsernameRow = i7;
                int i9 = i8 + 1;
                this.rowCount = i9;
                this.bioRow = i8;
                this.rowCount = i9 + 1;
                this.settingsSectionRow = i9;
                Set<String> suggestions = getMessagesController().pendingSuggestions;
                if (suggestions.contains("VALIDATE_PHONE_NUMBER")) {
                    int i10 = this.rowCount;
                    int i11 = i10 + 1;
                    this.rowCount = i11;
                    this.phoneSuggestionRow = i10;
                    this.rowCount = i11 + 1;
                    this.phoneSuggestionSectionRow = i11;
                }
                if (suggestions.contains("VALIDATE_PASSWORD")) {
                    int i12 = this.rowCount;
                    int i13 = i12 + 1;
                    this.rowCount = i13;
                    this.passwordSuggestionRow = i12;
                    this.rowCount = i13 + 1;
                    this.passwordSuggestionSectionRow = i13;
                }
                int i14 = this.rowCount;
                int i15 = i14 + 1;
                this.rowCount = i15;
                this.settingsSectionRow2 = i14;
                int i16 = i15 + 1;
                this.rowCount = i16;
                this.notificationRow = i15;
                int i17 = i16 + 1;
                this.rowCount = i17;
                this.privacyRow = i16;
                int i18 = i17 + 1;
                this.rowCount = i18;
                this.dataRow = i17;
                int i19 = i18 + 1;
                this.rowCount = i19;
                this.chatRow = i18;
                this.rowCount = i19 + 1;
                this.stickersRow = i19;
                if (getMessagesController().filtersEnabled || !getMessagesController().dialogFilters.isEmpty()) {
                    int i20 = this.rowCount;
                    this.rowCount = i20 + 1;
                    this.filtersRow = i20;
                }
                int i21 = this.rowCount;
                int i22 = i21 + 1;
                this.rowCount = i22;
                this.devicesRow = i21;
                int i23 = i22 + 1;
                this.rowCount = i23;
                this.languageRow = i22;
                this.rowCount = i23 + 1;
                this.devicesSectionRow = i23;
                if (!getMessagesController().premiumLocked) {
                    int i24 = this.rowCount;
                    int i25 = i24 + 1;
                    this.rowCount = i25;
                    this.premiumRow = i24;
                    this.rowCount = i25 + 1;
                    this.premiumSectionsRow = i25;
                }
                int i26 = this.rowCount;
                int i27 = i26 + 1;
                this.rowCount = i27;
                this.helpHeaderRow = i26;
                int i28 = i27 + 1;
                this.rowCount = i28;
                this.questionRow = i27;
                int i29 = i28 + 1;
                this.rowCount = i29;
                this.faqRow = i28;
                this.rowCount = i29 + 1;
                this.policyRow = i29;
                if (BuildVars.LOGS_ENABLED || BuildVars.DEBUG_PRIVATE_VERSION) {
                    int i30 = this.rowCount;
                    int i31 = i30 + 1;
                    this.rowCount = i31;
                    this.helpSectionCell = i30;
                    this.rowCount = i31 + 1;
                    this.debugHeaderRow = i31;
                }
                if (BuildVars.LOGS_ENABLED) {
                    int i32 = this.rowCount;
                    int i33 = i32 + 1;
                    this.rowCount = i33;
                    this.sendLogsRow = i32;
                    int i34 = i33 + 1;
                    this.rowCount = i34;
                    this.sendLastLogsRow = i33;
                    this.rowCount = i34 + 1;
                    this.clearLogsRow = i34;
                }
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    int i35 = this.rowCount;
                    this.rowCount = i35 + 1;
                    this.switchBackendRow = i35;
                }
                int i36 = this.rowCount;
                this.rowCount = i36 + 1;
                this.versionRow = i36;
            } else {
                TLRPC.UserFull userFull2 = this.userInfo;
                boolean hasInfo = (userFull2 != null && !TextUtils.isEmpty(userFull2.about)) || (user != null && !TextUtils.isEmpty(user.username));
                if (user == null || TextUtils.isEmpty(user.phone)) {
                    z = false;
                }
                boolean hasPhone = z;
                int i37 = this.rowCount;
                int i38 = i37 + 1;
                this.rowCount = i38;
                this.infoHeaderRow = i37;
                if (!this.isBot && (hasPhone || !hasInfo)) {
                    this.rowCount = i38 + 1;
                    this.phoneRow = i38;
                }
                TLRPC.UserFull userFull3 = this.userInfo;
                if (userFull3 != null && !TextUtils.isEmpty(userFull3.about)) {
                    int i39 = this.rowCount;
                    this.rowCount = i39 + 1;
                    this.userInfoRow = i39;
                }
                if (user != null && !TextUtils.isEmpty(user.username)) {
                    int i40 = this.rowCount;
                    this.rowCount = i40 + 1;
                    this.usernameRow = i40;
                }
                if (this.phoneRow != -1 || this.userInfoRow != -1 || this.usernameRow != -1) {
                    int i41 = this.rowCount;
                    this.rowCount = i41 + 1;
                    this.notificationsDividerRow = i41;
                }
                if (this.userId != getUserConfig().getClientUserId()) {
                    int i42 = this.rowCount;
                    this.rowCount = i42 + 1;
                    this.notificationsRow = i42;
                }
                int i43 = this.rowCount;
                int i44 = i43 + 1;
                this.rowCount = i44;
                this.infoSectionRow = i43;
                TLRPC.EncryptedChat encryptedChat = this.currentEncryptedChat;
                if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
                    int i45 = i44 + 1;
                    this.rowCount = i45;
                    this.settingsTimerRow = i44;
                    int i46 = i45 + 1;
                    this.rowCount = i46;
                    this.settingsKeyRow = i45;
                    this.rowCount = i46 + 1;
                    this.secretSettingsSectionRow = i46;
                }
                if (user != null && !this.isBot && encryptedChat == null && user.id != getUserConfig().getClientUserId() && this.userBlocked) {
                    int i47 = this.rowCount;
                    int i48 = i47 + 1;
                    this.rowCount = i48;
                    this.unblockRow = i47;
                    this.rowCount = i48 + 1;
                    this.lastSectionRow = i48;
                }
                if (user != null && this.isBot && !user.bot_nochats) {
                    int i49 = this.rowCount;
                    int i50 = i49 + 1;
                    this.rowCount = i50;
                    this.addToGroupButtonRow = i49;
                    this.rowCount = i50 + 1;
                    this.addToGroupInfoRow = i50;
                }
                if (!hasMedia && ((userFull = this.userInfo) == null || userFull.common_chats_count == 0)) {
                    if (this.lastSectionRow == -1 && this.needSendMessage) {
                        int i51 = this.rowCount;
                        int i52 = i51 + 1;
                        this.rowCount = i52;
                        this.sendMessageRow = i51;
                        int i53 = i52 + 1;
                        this.rowCount = i53;
                        this.reportRow = i52;
                        this.rowCount = i53 + 1;
                        this.lastSectionRow = i53;
                    }
                } else {
                    int i54 = this.rowCount;
                    this.rowCount = i54 + 1;
                    this.sharedMediaRow = i54;
                }
            }
        } else if (this.chatId != 0) {
            TLRPC.ChatFull chatFull = this.chatInfo;
            if ((chatFull != null && (!TextUtils.isEmpty(chatFull.about) || (this.chatInfo.location instanceof TLRPC.TL_channelLocation))) || !TextUtils.isEmpty(this.currentChat.username)) {
                if (LocaleController.isRTL && ChatObject.isChannel(this.currentChat) && this.chatInfo != null && !this.currentChat.megagroup && this.chatInfo.linked_chat_id != 0) {
                    int i55 = this.rowCount;
                    this.rowCount = i55 + 1;
                    this.emptyRow = i55;
                }
                int i56 = this.rowCount;
                this.rowCount = i56 + 1;
                this.infoHeaderRow = i56;
                TLRPC.ChatFull chatFull2 = this.chatInfo;
                if (chatFull2 != null) {
                    if (!TextUtils.isEmpty(chatFull2.about)) {
                        int i57 = this.rowCount;
                        this.rowCount = i57 + 1;
                        this.channelInfoRow = i57;
                    }
                    if (this.chatInfo.location instanceof TLRPC.TL_channelLocation) {
                        int i58 = this.rowCount;
                        this.rowCount = i58 + 1;
                        this.locationRow = i58;
                    }
                }
                if (!TextUtils.isEmpty(this.currentChat.username)) {
                    int i59 = this.rowCount;
                    this.rowCount = i59 + 1;
                    this.usernameRow = i59;
                }
            }
            if (this.infoHeaderRow != -1) {
                int i60 = this.rowCount;
                this.rowCount = i60 + 1;
                this.notificationsDividerRow = i60;
            }
            int i61 = this.rowCount;
            int i62 = i61 + 1;
            this.rowCount = i62;
            this.notificationsRow = i61;
            this.rowCount = i62 + 1;
            this.infoSectionRow = i62;
            if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup && this.chatInfo != null && (this.currentChat.creator || this.chatInfo.can_view_participants)) {
                int i63 = this.rowCount;
                int i64 = i63 + 1;
                this.rowCount = i64;
                this.membersHeaderRow = i63;
                this.rowCount = i64 + 1;
                this.subscribersRow = i64;
                if (this.chatInfo.requests_pending > 0) {
                    int i65 = this.rowCount;
                    this.rowCount = i65 + 1;
                    this.subscribersRequestsRow = i65;
                }
                int i66 = this.rowCount;
                this.rowCount = i66 + 1;
                this.administratorsRow = i66;
                if (this.chatInfo.banned_count != 0 || this.chatInfo.kicked_count != 0) {
                    int i67 = this.rowCount;
                    this.rowCount = i67 + 1;
                    this.blockedUsersRow = i67;
                }
                int i68 = this.rowCount;
                this.rowCount = i68 + 1;
                this.membersSectionRow = i68;
            }
            if (ChatObject.isChannel(this.currentChat)) {
                if (this.chatInfo != null && this.currentChat.megagroup && this.chatInfo.participants != null && !this.chatInfo.participants.participants.isEmpty()) {
                    if (!ChatObject.isNotInChat(this.currentChat) && ChatObject.canAddUsers(this.currentChat) && this.chatInfo.participants_count < getMessagesController().maxMegagroupCount) {
                        int i69 = this.rowCount;
                        this.rowCount = i69 + 1;
                        this.addMemberRow = i69;
                    }
                    int count = this.chatInfo.participants.participants.size();
                    if ((count <= 5 || !hasMedia || this.usersForceShowingIn == 1) && this.usersForceShowingIn != 2) {
                        if (this.addMemberRow == -1) {
                            int i70 = this.rowCount;
                            this.rowCount = i70 + 1;
                            this.membersHeaderRow = i70;
                        }
                        int i71 = this.rowCount;
                        this.membersStartRow = i71;
                        int i72 = i71 + count;
                        this.rowCount = i72;
                        this.membersEndRow = i72;
                        this.rowCount = i72 + 1;
                        this.membersSectionRow = i72;
                        this.visibleChatParticipants.addAll(this.chatInfo.participants.participants);
                        ArrayList<Integer> arrayList = this.sortedUsers;
                        if (arrayList != null) {
                            this.visibleSortedUsers.addAll(arrayList);
                        }
                        this.usersForceShowingIn = 1;
                        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
                        if (sharedMediaLayout != null) {
                            sharedMediaLayout.setChatUsers(null, null);
                        }
                    } else {
                        if (this.addMemberRow != -1) {
                            int i73 = this.rowCount;
                            this.rowCount = i73 + 1;
                            this.membersSectionRow = i73;
                        }
                        if (this.sharedMediaLayout != null) {
                            if (!this.sortedUsers.isEmpty()) {
                                this.usersForceShowingIn = 2;
                            }
                            this.sharedMediaLayout.setChatUsers(this.sortedUsers, this.chatInfo);
                        }
                    }
                }
                int count2 = this.lastSectionRow;
                if (count2 == -1 && this.currentChat.left && !this.currentChat.kicked) {
                    int i74 = this.rowCount;
                    int i75 = i74 + 1;
                    this.rowCount = i75;
                    this.joinRow = i74;
                    this.rowCount = i75 + 1;
                    this.lastSectionRow = i75;
                }
            } else {
                TLRPC.ChatFull chatFull3 = this.chatInfo;
                if (chatFull3 != null && !(chatFull3.participants instanceof TLRPC.TL_chatParticipantsForbidden)) {
                    if (ChatObject.canAddUsers(this.currentChat) || this.currentChat.default_banned_rights == null || !this.currentChat.default_banned_rights.invite_users) {
                        int i76 = this.rowCount;
                        this.rowCount = i76 + 1;
                        this.addMemberRow = i76;
                    }
                    int count3 = this.chatInfo.participants.participants.size();
                    if (count3 <= 5 || !hasMedia) {
                        if (this.addMemberRow == -1) {
                            int i77 = this.rowCount;
                            this.rowCount = i77 + 1;
                            this.membersHeaderRow = i77;
                        }
                        int i78 = this.rowCount;
                        this.membersStartRow = i78;
                        int size = i78 + this.chatInfo.participants.participants.size();
                        this.rowCount = size;
                        this.membersEndRow = size;
                        this.rowCount = size + 1;
                        this.membersSectionRow = size;
                        this.visibleChatParticipants.addAll(this.chatInfo.participants.participants);
                        ArrayList<Integer> arrayList2 = this.sortedUsers;
                        if (arrayList2 != null) {
                            this.visibleSortedUsers.addAll(arrayList2);
                        }
                        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
                        if (sharedMediaLayout2 != null) {
                            sharedMediaLayout2.setChatUsers(null, null);
                        }
                    } else {
                        if (this.addMemberRow != -1) {
                            int i79 = this.rowCount;
                            this.rowCount = i79 + 1;
                            this.membersSectionRow = i79;
                        }
                        SharedMediaLayout sharedMediaLayout3 = this.sharedMediaLayout;
                        if (sharedMediaLayout3 != null) {
                            sharedMediaLayout3.setChatUsers(this.sortedUsers, this.chatInfo);
                        }
                    }
                }
            }
            if (hasMedia) {
                int i80 = this.rowCount;
                this.rowCount = i80 + 1;
                this.sharedMediaRow = i80;
            }
        }
        if (this.sharedMediaRow == -1) {
            int i81 = this.rowCount;
            this.rowCount = i81 + 1;
            this.bottomPaddingRow = i81;
        }
        if (this.actionBar != null) {
            actionBarHeight = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        } else {
            actionBarHeight = 0;
        }
        if (this.listView == null || prevRowsCount > this.rowCount || ((i = this.listContentHeight) != 0 && i + actionBarHeight + AndroidUtilities.dp(88.0f) < this.listView.getMeasuredHeight())) {
            this.lastMeasuredContentWidth = 0;
        }
    }

    private Drawable getScamDrawable(int type) {
        if (this.scamDrawable == null) {
            ScamDrawable scamDrawable = new ScamDrawable(11, type);
            this.scamDrawable = scamDrawable;
            scamDrawable.setColor(getThemedColor(Theme.key_avatar_subtitleInProfileBlue));
        }
        return this.scamDrawable;
    }

    private Drawable getLockIconDrawable() {
        if (this.lockIconDrawable == null) {
            this.lockIconDrawable = Theme.chat_lockIconDrawable.getConstantState().newDrawable().mutate();
        }
        return this.lockIconDrawable;
    }

    private Drawable getVerifiedCrossfadeDrawable() {
        if (this.verifiedCrossfadeDrawable == null) {
            this.verifiedDrawable = Theme.profile_verifiedDrawable.getConstantState().newDrawable().mutate();
            this.verifiedCheckDrawable = Theme.profile_verifiedCheckDrawable.getConstantState().newDrawable().mutate();
            this.verifiedCrossfadeDrawable = new CrossfadeDrawable(new CombinedDrawable(this.verifiedDrawable, this.verifiedCheckDrawable), ContextCompat.getDrawable(getParentActivity(), R.drawable.verified_profile));
        }
        return this.verifiedCrossfadeDrawable;
    }

    private Drawable getPremiumCrossfadeDrawable() {
        if (this.premuimCrossfadeDrawable == null) {
            Drawable mutate = ContextCompat.getDrawable(getParentActivity(), R.drawable.msg_premium_liststar).mutate();
            this.premiumStarDrawable = mutate;
            mutate.setColorFilter(getThemedColor(Theme.key_profile_verifiedBackground), PorterDuff.Mode.MULTIPLY);
            this.premuimCrossfadeDrawable = new CrossfadeDrawable(this.premiumStarDrawable, ContextCompat.getDrawable(getParentActivity(), R.drawable.msg_premium_prolfilestar).mutate());
        }
        return this.premuimCrossfadeDrawable;
    }

    /* JADX WARN: Code restructure failed: missing block: B:45:0x00fb, code lost:
        if (r15.photoId != r30.prevLoadedImageLocation.photoId) goto L47;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateProfileData(boolean r31) {
        /*
            Method dump skipped, instructions count: 2307
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateProfileData(boolean):void");
    }

    /* renamed from: lambda$updateProfileData$32$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4390lambda$updateProfileData$32$orgtelegramuiProfileActivity(TLRPC.User user, SimpleTextView textView, View v) {
        PremiumPreviewBottomSheet premiumPreviewBottomSheet = new PremiumPreviewBottomSheet(this, this.currentAccount, user);
        int[] coords = new int[2];
        textView.getLocationOnScreen(coords);
        premiumPreviewBottomSheet.startEnterFromX = textView.rightDrawableX;
        premiumPreviewBottomSheet.startEnterFromY = textView.rightDrawableY;
        premiumPreviewBottomSheet.startEnterFromScale = textView.getScaleX();
        premiumPreviewBottomSheet.startEnterFromX1 = textView.getLeft();
        premiumPreviewBottomSheet.startEnterFromY1 = textView.getTop();
        premiumPreviewBottomSheet.startEnterFromView = textView;
        showDialog(premiumPreviewBottomSheet);
    }

    private void createActionBarMenu(boolean animated) {
        String str;
        int i;
        String str2;
        int i2;
        if (this.actionBar == null || this.otherItem == null) {
            return;
        }
        Context context = this.actionBar.getContext();
        this.otherItem.removeAllSubItems();
        this.animatingItem = null;
        this.editItemVisible = false;
        this.callItemVisible = false;
        this.videoCallItemVisible = false;
        this.canSearchMembers = false;
        boolean selfUser = false;
        if (this.userId != 0) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user == null) {
                return;
            }
            if (UserObject.isUserSelf(user)) {
                this.otherItem.addSubItem(30, R.drawable.msg_edit, LocaleController.getString("EditName", R.string.EditName));
                selfUser = true;
            } else {
                TLRPC.UserFull userFull = this.userInfo;
                if (userFull != null && userFull.phone_calls_available) {
                    this.callItemVisible = true;
                    this.videoCallItemVisible = Build.VERSION.SDK_INT >= 18 && this.userInfo.video_calls_available;
                }
                boolean z = this.isBot;
                int i3 = R.string.Unblock;
                String str3 = "Unblock";
                int i4 = R.drawable.msg_block;
                if (z || getContactsController().contactsDict.get(Long.valueOf(this.userId)) == null) {
                    if (MessagesController.isSupportUser(user)) {
                        if (this.userBlocked) {
                            this.otherItem.addSubItem(2, R.drawable.msg_block, LocaleController.getString(str3, R.string.Unblock));
                        }
                    } else {
                        if (this.currentEncryptedChat == null) {
                            createAutoDeleteItem(context);
                        }
                        if (this.isBot) {
                            this.otherItem.addSubItem(10, R.drawable.msg_share, LocaleController.getString("BotShare", R.string.BotShare));
                        } else {
                            this.otherItem.addSubItem(1, R.drawable.msg_addcontact, LocaleController.getString("AddContact", R.string.AddContact));
                        }
                        if (!TextUtils.isEmpty(user.phone)) {
                            this.otherItem.addSubItem(3, R.drawable.msg_share, LocaleController.getString("ShareContact", R.string.ShareContact));
                        }
                        if (!this.isBot) {
                            this.otherItem.addSubItem(2, R.drawable.msg_block, !this.userBlocked ? LocaleController.getString("BlockContact", R.string.BlockContact) : LocaleController.getString(str3, R.string.Unblock));
                        } else {
                            ActionBarMenuItem actionBarMenuItem = this.otherItem;
                            boolean z2 = this.userBlocked;
                            if (z2) {
                                i4 = R.drawable.msg_retry;
                            }
                            if (!z2) {
                                i2 = R.string.BotStop;
                                str2 = "BotStop";
                            } else {
                                i2 = R.string.BotRestart;
                                str2 = "BotRestart";
                            }
                            actionBarMenuItem.addSubItem(2, i4, LocaleController.getString(str2, i2));
                        }
                    }
                } else {
                    if (this.currentEncryptedChat == null) {
                        createAutoDeleteItem(context);
                    }
                    if (!TextUtils.isEmpty(user.phone)) {
                        this.otherItem.addSubItem(3, R.drawable.msg_share, LocaleController.getString("ShareContact", R.string.ShareContact));
                    }
                    ActionBarMenuItem actionBarMenuItem2 = this.otherItem;
                    if (!this.userBlocked) {
                        i3 = R.string.BlockContact;
                        str3 = "BlockContact";
                    }
                    actionBarMenuItem2.addSubItem(2, R.drawable.msg_block, LocaleController.getString(str3, i3));
                    this.otherItem.addSubItem(4, R.drawable.msg_edit, LocaleController.getString("EditContact", R.string.EditContact));
                    this.otherItem.addSubItem(5, R.drawable.msg_delete, LocaleController.getString("DeleteContact", R.string.DeleteContact));
                }
                if (!UserObject.isDeleted(user) && !this.isBot && this.currentEncryptedChat == null && !this.userBlocked) {
                    long j = this.userId;
                    if (j != 333000 && j != 777000 && j != 42777) {
                        this.otherItem.addSubItem(20, R.drawable.msg_secret, LocaleController.getString("StartEncryptedChat", R.string.StartEncryptedChat));
                    }
                }
                this.otherItem.addSubItem(14, R.drawable.msg_home, LocaleController.getString("AddShortcut", R.string.AddShortcut));
            }
        } else if (this.chatId != 0) {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
            this.hasVoiceChatItem = false;
            if (ChatObject.canUserDoAdminAction(chat, 13)) {
                createAutoDeleteItem(context);
            }
            if (ChatObject.isChannel(chat)) {
                if (ChatObject.hasAdminRights(chat) || (chat.megagroup && ChatObject.canChangeChatInfo(chat))) {
                    this.editItemVisible = true;
                }
                if (this.chatInfo != null) {
                    if (ChatObject.canManageCalls(chat) && this.chatInfo.call == null) {
                        ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
                        if (!chat.megagroup || chat.gigagroup) {
                            i = R.string.StartVoipChannel;
                            str = "StartVoipChannel";
                        } else {
                            i = R.string.StartVoipChat;
                            str = "StartVoipChat";
                        }
                        actionBarMenuItem3.addSubItem(15, R.drawable.msg_voicechat, LocaleController.getString(str, i));
                        this.hasVoiceChatItem = true;
                    }
                    if (this.chatInfo.can_view_stats) {
                        this.otherItem.addSubItem(19, R.drawable.msg_stats, LocaleController.getString("Statistics", R.string.Statistics));
                    }
                    ChatObject.Call call = getMessagesController().getGroupCall(this.chatId, false);
                    this.callItemVisible = call != null;
                }
                if (chat.megagroup) {
                    this.canSearchMembers = true;
                    this.otherItem.addSubItem(17, R.drawable.msg_search, LocaleController.getString("SearchMembers", R.string.SearchMembers));
                    if (!chat.creator && !chat.left && !chat.kicked) {
                        this.otherItem.addSubItem(7, R.drawable.msg_leave, LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu));
                    }
                } else {
                    if (!TextUtils.isEmpty(chat.username)) {
                        this.otherItem.addSubItem(10, R.drawable.msg_share, LocaleController.getString("BotShare", R.string.BotShare));
                    }
                    TLRPC.ChatFull chatFull = this.chatInfo;
                    if (chatFull != null && chatFull.linked_chat_id != 0) {
                        this.otherItem.addSubItem(22, R.drawable.msg_discussion, LocaleController.getString("ViewDiscussion", R.string.ViewDiscussion));
                    }
                    if (!this.currentChat.creator && !this.currentChat.left && !this.currentChat.kicked) {
                        this.otherItem.addSubItem(7, R.drawable.msg_leave, LocaleController.getString("LeaveChannelMenu", R.string.LeaveChannelMenu));
                    }
                }
            } else {
                if (this.chatInfo != null) {
                    if (ChatObject.canManageCalls(chat) && this.chatInfo.call == null) {
                        this.otherItem.addSubItem(15, R.drawable.msg_voicechat, LocaleController.getString("StartVoipChat", R.string.StartVoipChat));
                        this.hasVoiceChatItem = true;
                    }
                    ChatObject.Call call2 = getMessagesController().getGroupCall(this.chatId, false);
                    this.callItemVisible = call2 != null;
                }
                if (ChatObject.canChangeChatInfo(chat)) {
                    this.editItemVisible = true;
                }
                if (!ChatObject.isKickedFromChat(chat) && !ChatObject.isLeftFromChat(chat)) {
                    this.canSearchMembers = true;
                    this.otherItem.addSubItem(17, R.drawable.msg_search, LocaleController.getString("SearchMembers", R.string.SearchMembers));
                }
                this.otherItem.addSubItem(7, R.drawable.msg_leave, LocaleController.getString("DeleteAndExit", R.string.DeleteAndExit));
            }
            this.otherItem.addSubItem(14, R.drawable.msg_home, LocaleController.getString("AddShortcut", R.string.AddShortcut));
        }
        if (this.imageUpdater != null) {
            this.otherItem.addSubItem(36, R.drawable.msg_addphoto, LocaleController.getString("AddPhoto", R.string.AddPhoto));
            this.otherItem.addSubItem(33, R.drawable.msg_openprofile, LocaleController.getString("SetAsMain", R.string.SetAsMain));
            this.otherItem.addSubItem(21, R.drawable.msg_gallery, LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
            this.otherItem.addSubItem(35, R.drawable.msg_delete, LocaleController.getString("Delete", R.string.Delete));
        } else {
            this.otherItem.addSubItem(21, R.drawable.msg_gallery, LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
        }
        if (getMessagesController().isChatNoForwards(this.currentChat)) {
            this.otherItem.hideSubItem(21);
        }
        if (selfUser) {
            this.otherItem.addSubItem(31, R.drawable.msg_leave, LocaleController.getString("LogOut", R.string.LogOut));
        }
        if (!this.isPulledDown) {
            this.otherItem.hideSubItem(21);
            this.otherItem.hideSubItem(33);
            this.otherItem.showSubItem(36);
            this.otherItem.hideSubItem(34);
            this.otherItem.hideSubItem(35);
        }
        if (!this.mediaHeaderVisible) {
            if (this.callItemVisible) {
                if (this.callItem.getVisibility() != 0) {
                    this.callItem.setVisibility(0);
                    if (animated) {
                        this.callItem.setAlpha(0.0f);
                        this.callItem.animate().alpha(1.0f).setDuration(150L).start();
                    }
                }
            } else if (this.callItem.getVisibility() != 8) {
                this.callItem.setVisibility(8);
            }
            if (this.videoCallItemVisible) {
                if (this.videoCallItem.getVisibility() != 0) {
                    this.videoCallItem.setVisibility(0);
                    if (animated) {
                        this.videoCallItem.setAlpha(0.0f);
                        this.videoCallItem.animate().alpha(1.0f).setDuration(150L).start();
                    }
                }
            } else if (this.videoCallItem.getVisibility() != 8) {
                this.videoCallItem.setVisibility(8);
            }
            if (this.editItemVisible) {
                if (this.editItem.getVisibility() != 0) {
                    this.editItem.setVisibility(0);
                    if (animated) {
                        this.editItem.setAlpha(0.0f);
                        this.editItem.animate().alpha(1.0f).setDuration(150L).start();
                    }
                }
            } else if (this.editItem.getVisibility() != 8) {
                this.editItem.setVisibility(8);
            }
        }
        PagerIndicatorView pagerIndicatorView = this.avatarsViewPagerIndicatorView;
        if (pagerIndicatorView != null && pagerIndicatorView.isIndicatorFullyVisible()) {
            if (this.editItemVisible) {
                this.editItem.setVisibility(8);
                this.editItem.animate().cancel();
                this.editItem.setAlpha(1.0f);
            }
            if (this.callItemVisible) {
                this.callItem.setVisibility(8);
                this.callItem.animate().cancel();
                this.callItem.setAlpha(1.0f);
            }
            if (this.videoCallItemVisible) {
                this.videoCallItem.setVisibility(8);
                this.videoCallItem.animate().cancel();
                this.videoCallItem.setAlpha(1.0f);
            }
        }
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.getSearchItem().requestLayout();
        }
    }

    private void createAutoDeleteItem(Context context) {
        this.autoDeletePopupWrapper = new AutoDeletePopupWrapper(context, this.otherItem.getPopupLayout().getSwipeBack(), new AutoDeletePopupWrapper.Callback() { // from class: org.telegram.ui.ProfileActivity.35
            @Override // org.telegram.ui.Components.AutoDeletePopupWrapper.Callback
            public void dismiss() {
                ProfileActivity.this.otherItem.toggleSubMenu();
            }

            @Override // org.telegram.ui.Components.AutoDeletePopupWrapper.Callback
            public void setAutoDeleteHistory(int time, int action) {
                ProfileActivity.this.setAutoDeleteHistory(time, action);
            }
        }, false, this.resourcesProvider);
        int ttl = 0;
        TLRPC.UserFull userFull = this.userInfo;
        if (userFull != null || this.chatInfo != null) {
            ttl = userFull != null ? userFull.ttl_period : this.chatInfo.ttl_period;
        }
        TimerDrawable ttlIcon = TimerDrawable.getTtlIcon(ttl);
        this.autoDeleteItemDrawable = ttlIcon;
        this.autoDeleteItem = this.otherItem.addSwipeBackItem(0, ttlIcon, LocaleController.getString("AutoDeletePopupTitle", R.string.AutoDeletePopupTitle), this.autoDeletePopupWrapper.windowLayout);
        this.otherItem.addColoredGap();
        updateAutoDeleteItem();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public int getThemedColor(String key) {
        return Theme.getColor(key, this.resourcesProvider);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public Drawable getThemedDrawable(String drawableKey) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(drawableKey) : null;
        return drawable != null ? drawable : super.getThemedDrawable(drawableKey);
    }

    public void setAutoDeleteHistory(int time, int action) {
        long did = getDialogId();
        getMessagesController().setDialogHistoryTTL(did, time);
        if (this.userInfo != null || this.chatInfo != null) {
            UndoView undoView = this.undoView;
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(did));
            TLRPC.UserFull userFull = this.userInfo;
            undoView.showWithAction(did, action, user, Integer.valueOf(userFull != null ? userFull.ttl_period : this.chatInfo.ttl_period), (Runnable) null, (Runnable) null);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
    }

    @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
    public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        long did = dids.get(0).longValue();
        Bundle args = new Bundle();
        args.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(did)) {
            args.putInt("enc_id", DialogObject.getEncryptedChatId(did));
        } else if (DialogObject.isUserDialog(did)) {
            args.putLong("user_id", did);
        } else if (DialogObject.isChatDialog(did)) {
            args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did);
        }
        if (getMessagesController().checkCanOpenChat(args, fragment)) {
            getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(args), true);
            removeSelfFromStack();
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            getSendMessagesHelper().sendMessage(user, did, (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
            if (!TextUtils.isEmpty(message)) {
                AccountInstance accountInstance = AccountInstance.getInstance(this.currentAccount);
                SendMessagesHelper.prepareSendingText(accountInstance, message.toString(), did, true, 0);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
        if (requestCode == 101 || requestCode == 102) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user == null) {
                return;
            }
            boolean allGranted = true;
            int a = 0;
            while (true) {
                if (a >= grantResults.length) {
                    break;
                } else if (grantResults[a] == 0) {
                    a++;
                } else {
                    allGranted = false;
                    break;
                }
            }
            int a2 = grantResults.length;
            if (a2 > 0 && allGranted) {
                boolean z = requestCode == 102;
                TLRPC.UserFull userFull = this.userInfo;
                VoIPHelper.startCall(user, z, userFull != null && userFull.video_calls_available, getParentActivity(), this.userInfo, getAccountInstance());
                return;
            }
            VoIPHelper.permissionDenied(getParentActivity(), null, requestCode);
        } else if (requestCode != 103 || this.currentChat == null) {
        } else {
            boolean allGranted2 = true;
            int a3 = 0;
            while (true) {
                if (a3 >= grantResults.length) {
                    break;
                } else if (grantResults[a3] == 0) {
                    a3++;
                } else {
                    allGranted2 = false;
                    break;
                }
            }
            int a4 = grantResults.length;
            if (a4 > 0 && allGranted2) {
                ChatObject.Call call = getMessagesController().getGroupCall(this.chatId, false);
                VoIPHelper.startCall(this.currentChat, null, null, call == null, getParentActivity(), this, getAccountInstance());
                return;
            }
            VoIPHelper.permissionDenied(getParentActivity(), null, requestCode);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void dismissCurrentDialog() {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null && imageUpdater.dismissCurrentDialog(this.visibleDialog)) {
            return;
        }
        super.dismissCurrentDialog();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean dismissDialogOnPause(Dialog dialog) {
        ImageUpdater imageUpdater = this.imageUpdater;
        return (imageUpdater == null || imageUpdater.dismissDialogOnPause(dialog)) && super.dismissDialogOnPause(dialog);
    }

    public Animator searchExpandTransition(final boolean enter) {
        if (enter) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), this.classGuid);
        }
        Animator animator = this.searchViewTransition;
        if (animator != null) {
            animator.removeAllListeners();
            this.searchViewTransition.cancel();
        }
        float[] fArr = new float[2];
        fArr[0] = this.searchTransitionProgress;
        fArr[1] = enter ? 0.0f : 1.0f;
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(fArr);
        final float offset = this.extraHeight;
        this.searchListView.setTranslationY(offset);
        this.searchListView.setVisibility(0);
        this.searchItem.setVisibility(0);
        this.listView.setVisibility(0);
        needLayout(true);
        this.avatarContainer.setVisibility(0);
        this.nameTextView[1].setVisibility(0);
        this.onlineTextView[1].setVisibility(0);
        this.actionBar.onSearchFieldVisibilityChanged(this.searchTransitionProgress > 0.5f);
        int i = 8;
        int itemVisibility = this.searchTransitionProgress > 0.5f ? 0 : 8;
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(itemVisibility);
        }
        if (this.qrItem != null) {
            updateQrItemVisibility(false);
        }
        this.searchItem.setVisibility(itemVisibility);
        FrameLayout searchContainer = this.searchItem.getSearchContainer();
        if (this.searchTransitionProgress <= 0.5f) {
            i = 0;
        }
        searchContainer.setVisibility(i);
        this.searchListView.setEmptyView(this.emptyView);
        this.avatarContainer.setClickable(false);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda33
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ProfileActivity.this.m4386lambda$searchExpandTransition$33$orgtelegramuiProfileActivity(valueAnimator, offset, enter, valueAnimator2);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.36
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ProfileActivity.this.updateSearchViewState(enter);
                ProfileActivity.this.avatarContainer.setClickable(true);
                if (enter) {
                    ProfileActivity.this.searchItem.requestFocusOnSearchView();
                }
                ProfileActivity.this.needLayout(true);
                ProfileActivity.this.searchViewTransition = null;
                ProfileActivity.this.fragmentView.invalidate();
                if (enter) {
                    ProfileActivity.this.invalidateScroll = true;
                    ProfileActivity.this.saveScrollPosition();
                    AndroidUtilities.requestAdjustResize(ProfileActivity.this.getParentActivity(), ProfileActivity.this.classGuid);
                    ProfileActivity.this.emptyView.setPreventMoving(false);
                }
            }
        });
        if (!enter) {
            this.invalidateScroll = true;
            saveScrollPosition();
            AndroidUtilities.requestAdjustNothing(getParentActivity(), this.classGuid);
            this.emptyView.setPreventMoving(true);
        }
        valueAnimator.setDuration(220L);
        valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.searchViewTransition = valueAnimator;
        return valueAnimator;
    }

    /* renamed from: lambda$searchExpandTransition$33$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4386lambda$searchExpandTransition$33$orgtelegramuiProfileActivity(ValueAnimator valueAnimator, float offset, boolean enter, ValueAnimator animation) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.searchTransitionProgress = floatValue;
        float progressHalf = (floatValue - 0.5f) / 0.5f;
        float progressHalfEnd = (0.5f - floatValue) / 0.5f;
        if (progressHalf < 0.0f) {
            progressHalf = 0.0f;
        }
        if (progressHalfEnd < 0.0f) {
            progressHalfEnd = 0.0f;
        }
        this.searchTransitionOffset = (int) ((-offset) * (1.0f - floatValue));
        this.searchListView.setTranslationY(floatValue * offset);
        this.emptyView.setTranslationY(this.searchTransitionProgress * offset);
        this.listView.setTranslationY((-offset) * (1.0f - this.searchTransitionProgress));
        this.listView.setScaleX(1.0f - ((1.0f - this.searchTransitionProgress) * 0.01f));
        this.listView.setScaleY(1.0f - ((1.0f - this.searchTransitionProgress) * 0.01f));
        this.listView.setAlpha(this.searchTransitionProgress);
        boolean z = true;
        needLayout(true);
        this.listView.setAlpha(progressHalf);
        this.searchListView.setAlpha(1.0f - this.searchTransitionProgress);
        this.searchListView.setScaleX((this.searchTransitionProgress * 0.05f) + 1.0f);
        this.searchListView.setScaleY((this.searchTransitionProgress * 0.05f) + 1.0f);
        this.emptyView.setAlpha(1.0f - progressHalf);
        this.avatarContainer.setAlpha(progressHalf);
        this.nameTextView[1].setAlpha(progressHalf);
        this.onlineTextView[1].setAlpha(progressHalf);
        this.searchItem.getSearchField().setAlpha(progressHalfEnd);
        if (enter && this.searchTransitionProgress < 0.7f) {
            this.searchItem.requestFocusOnSearchView();
        }
        int i = 8;
        this.searchItem.getSearchContainer().setVisibility(this.searchTransitionProgress < 0.5f ? 0 : 8);
        if (this.searchTransitionProgress > 0.5f) {
            i = 0;
        }
        int visibility = i;
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(visibility);
            this.otherItem.setAlpha(progressHalf);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.qrItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setAlpha(progressHalf);
            updateQrItemVisibility(false);
        }
        this.searchItem.setVisibility(visibility);
        ActionBar actionBar = this.actionBar;
        if (this.searchTransitionProgress >= 0.5f) {
            z = false;
        }
        actionBar.onSearchFieldVisibilityChanged(z);
        ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
        if (actionBarMenuItem3 != null) {
            actionBarMenuItem3.setAlpha(progressHalf);
        }
        this.searchItem.setAlpha(progressHalf);
        this.topView.invalidate();
        this.fragmentView.invalidate();
    }

    public void updateSearchViewState(boolean enter) {
        int i = 0;
        int hide = enter ? 8 : 0;
        this.listView.setVisibility(hide);
        this.searchListView.setVisibility(enter ? 0 : 8);
        this.searchItem.getSearchContainer().setVisibility(enter ? 0 : 8);
        this.actionBar.onSearchFieldVisibilityChanged(enter);
        this.avatarContainer.setVisibility(hide);
        this.nameTextView[1].setVisibility(hide);
        this.onlineTextView[1].setVisibility(hide);
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setAlpha(1.0f);
            this.otherItem.setVisibility(hide);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.qrItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setAlpha(1.0f);
            ActionBarMenuItem actionBarMenuItem3 = this.qrItem;
            if (enter || !isQrNeedVisible()) {
                i = 8;
            }
            actionBarMenuItem3.setVisibility(i);
        }
        this.searchItem.setVisibility(hide);
        this.avatarContainer.setAlpha(1.0f);
        this.nameTextView[1].setAlpha(1.0f);
        this.onlineTextView[1].setAlpha(1.0f);
        this.searchItem.setAlpha(1.0f);
        this.listView.setAlpha(1.0f);
        this.searchListView.setAlpha(1.0f);
        this.emptyView.setAlpha(1.0f);
        if (enter) {
            this.searchListView.setEmptyView(this.emptyView);
        } else {
            this.emptyView.setVisibility(8);
        }
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void onUploadProgressChanged(float progress) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(progress);
        this.avatarsViewPager.setUploadProgress(this.uploadingImageLocation, progress);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void didStartUpload(boolean isVideo) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(0.0f);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void didUploadPhoto(final TLRPC.InputFile photo, final TLRPC.InputFile video, final double videoStartTimestamp, final String videoPath, final TLRPC.PhotoSize bigSize, final TLRPC.PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                ProfileActivity.this.m4368lambda$didUploadPhoto$36$orgtelegramuiProfileActivity(photo, video, videoStartTimestamp, videoPath, smallSize, bigSize);
            }
        });
    }

    /* renamed from: lambda$didUploadPhoto$36$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4368lambda$didUploadPhoto$36$orgtelegramuiProfileActivity(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, final String videoPath, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (photo != null || video != null) {
            TLRPC.TL_photos_uploadProfilePhoto req = new TLRPC.TL_photos_uploadProfilePhoto();
            if (photo != null) {
                req.file = photo;
                req.flags = 1 | req.flags;
            }
            if (video != null) {
                req.video = video;
                req.flags |= 2;
                req.video_start_ts = videoStartTimestamp;
                req.flags |= 4;
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda27
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ProfileActivity.this.m4367lambda$didUploadPhoto$35$orgtelegramuiProfileActivity(videoPath, tLObject, tL_error);
                }
            });
        } else {
            this.avatar = smallSize.location;
            this.avatarBig = bigSize.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", this.avatarDrawable, (Object) null);
            if (this.setAvatarRow != -1) {
                updateRowsIds();
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
                needLayout(true);
            }
            ProfileGalleryView profileGalleryView = this.avatarsViewPager;
            ImageLocation forLocal = ImageLocation.getForLocal(this.avatarBig);
            this.uploadingImageLocation = forLocal;
            profileGalleryView.addUploadingImage(forLocal, ImageLocation.getForLocal(this.avatar));
            showAvatarProgress(true, false);
        }
        this.actionBar.createMenu().requestLayout();
    }

    /* renamed from: lambda$didUploadPhoto$35$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4367lambda$didUploadPhoto$35$orgtelegramuiProfileActivity(final String videoPath, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                ProfileActivity.this.m4366lambda$didUploadPhoto$34$orgtelegramuiProfileActivity(error, response, videoPath);
            }
        });
    }

    /* renamed from: lambda$didUploadPhoto$34$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4366lambda$didUploadPhoto$34$orgtelegramuiProfileActivity(TLRPC.TL_error error, TLObject response, String videoPath) {
        this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
        if (error == null) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
                if (user != null) {
                    getMessagesController().putUser(user, false);
                } else {
                    return;
                }
            } else {
                getUserConfig().setCurrentUser(user);
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
                File destFile = FileLoader.getInstance(this.currentAccount).getPathToAttach(small, true);
                File src = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.avatar, true);
                src.renameTo(destFile);
                String oldKey = this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50";
                String newKey = small.location.volume_id + "_" + small.location.local_id + "@50_50";
                user = user;
                ImageLoader.getInstance().replaceImageInCache(oldKey, newKey, ImageLocation.getForUserOrChat(user, 1), false);
            }
            if (big != null && this.avatarBig != null) {
                File destFile2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(big, true);
                File src2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.avatarBig, true);
                src2.renameTo(destFile2);
            }
            if (videoSize != null && videoPath != null) {
                File destFile3 = FileLoader.getInstance(this.currentAccount).getPathToAttach(videoSize, "mp4", true);
                File src3 = new File(videoPath);
                src3.renameTo(destFile3);
            }
            getMessagesStorage().clearUserPhotos(user.id);
            ArrayList<TLRPC.User> users = new ArrayList<>();
            users.add(user);
            getMessagesStorage().putUsersAndChats(users, null, false, true);
        }
        this.allowPullingDown = !AndroidUtilities.isTablet() && !this.isInLandscapeMode && this.avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled();
        this.avatar = null;
        this.avatarBig = null;
        this.avatarsViewPager.setCreateThumbFromParent(false);
        updateProfileData(true);
        showAvatarProgress(false, true);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getUserConfig().saveConfig(true);
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarProgressView == null) {
            return;
        }
        AnimatorSet animatorSet = this.avatarAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.avatarAnimation = null;
        }
        if (animated) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.avatarAnimation = animatorSet2;
            if (show) {
                this.avatarProgressView.setVisibility(0);
                this.avatarAnimation.playTogether(ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 1.0f));
            } else {
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 0.0f));
            }
            this.avatarAnimation.setDuration(180L);
            this.avatarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.37
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ProfileActivity.this.avatarAnimation == null || ProfileActivity.this.avatarProgressView == null) {
                        return;
                    }
                    if (!show) {
                        ProfileActivity.this.avatarProgressView.setVisibility(4);
                    }
                    ProfileActivity.this.avatarAnimation = null;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    ProfileActivity.this.avatarAnimation = null;
                }
            });
            this.avatarAnimation.start();
        } else if (show) {
            this.avatarProgressView.setAlpha(1.0f);
            this.avatarProgressView.setVisibility(0);
        } else {
            this.avatarProgressView.setAlpha(0.0f);
            this.avatarProgressView.setVisibility(4);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void saveSelfArgs(Bundle args) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null && imageUpdater.currentPicturePath != null) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void restoreSelfArgs(Bundle args) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.currentPicturePath = args.getString("path");
        }
    }

    private void sendLogs(final boolean last) {
        if (getParentActivity() == null) {
            return;
        }
        final AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setCanCancel(false);
        progressDialog.show();
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                ProfileActivity.this.m4388lambda$sendLogs$38$orgtelegramuiProfileActivity(last, progressDialog);
            }
        });
    }

    /* renamed from: lambda$sendLogs$38$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4388lambda$sendLogs$38$orgtelegramuiProfileActivity(boolean last, final AlertDialog progressDialog) {
        Exception e;
        try {
            File sdCard = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            File dir = new File(sdCard.getAbsolutePath() + "/logs");
            final File zipFile = new File(dir, "logs.zip");
            if (zipFile.exists()) {
                zipFile.delete();
            }
            File[] files = dir.listFiles();
            final boolean[] finished = new boolean[1];
            long currentDate = System.currentTimeMillis();
            BufferedInputStream origin = null;
            ZipOutputStream out = null;
            try {
                try {
                    FileOutputStream dest = new FileOutputStream(zipFile);
                    out = new ZipOutputStream(new BufferedOutputStream(dest));
                    byte[] data = new byte[65536];
                    int i = 0;
                    while (true) {
                        int count = 0;
                        if (i >= files.length) {
                            break;
                        }
                        if (!last || currentDate - files[i].lastModified() <= 86400000) {
                            FileInputStream fi = new FileInputStream(files[i]);
                            BufferedInputStream origin2 = new BufferedInputStream(fi, data.length);
                            ZipEntry entry = new ZipEntry(files[i].getName());
                            out.putNextEntry(entry);
                            while (true) {
                                int count2 = origin2.read(data, count, data.length);
                                if (count2 == -1) {
                                    break;
                                }
                                out.write(data, 0, count2);
                                count = 0;
                            }
                            origin2.close();
                            origin = null;
                        }
                        i++;
                    }
                    finished[0] = true;
                    if (origin != null) {
                        origin.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    if (origin != null) {
                        origin.close();
                    }
                    if (out != null) {
                    }
                }
                out.close();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda17
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProfileActivity.this.m4387lambda$sendLogs$37$orgtelegramuiProfileActivity(progressDialog, finished, zipFile);
                    }
                });
            } catch (Exception e3) {
                e = e3;
                e.printStackTrace();
            }
        } catch (Exception e4) {
            e = e4;
        }
    }

    /* renamed from: lambda$sendLogs$37$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4387lambda$sendLogs$37$orgtelegramuiProfileActivity(AlertDialog progressDialog, boolean[] finished, File zipFile) {
        Uri uri;
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
        if (finished[0]) {
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", zipFile);
            } else {
                uri = Uri.fromFile(zipFile);
            }
            Intent i = new Intent("android.intent.action.SEND");
            if (Build.VERSION.SDK_INT >= 24) {
                i.addFlags(1);
            }
            i.setType("message/rfc822");
            i.putExtra("android.intent.extra.EMAIL", "");
            i.putExtra("android.intent.extra.SUBJECT", "Logs from " + LocaleController.getInstance().formatterStats.format(System.currentTimeMillis()));
            i.putExtra("android.intent.extra.STREAM", uri);
            if (getParentActivity() != null) {
                try {
                    getParentActivity().startActivityForResult(Intent.createChooser(i, "Select email application."), 500);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
        } else if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private static final int VIEW_TYPE_ABOUT_LINK = 3;
        private static final int VIEW_TYPE_ADDTOGROUP_INFO = 17;
        private static final int VIEW_TYPE_BOTTOM_PADDING = 12;
        private static final int VIEW_TYPE_DIVIDER = 5;
        private static final int VIEW_TYPE_EMPTY = 11;
        private static final int VIEW_TYPE_HEADER = 1;
        private static final int VIEW_TYPE_NOTIFICATIONS_CHECK = 6;
        private static final int VIEW_TYPE_PREMIUM_TEXT_CELL = 18;
        private static final int VIEW_TYPE_SHADOW = 7;
        private static final int VIEW_TYPE_SHARED_MEDIA = 13;
        private static final int VIEW_TYPE_SUGGESTION = 15;
        private static final int VIEW_TYPE_TEXT = 4;
        private static final int VIEW_TYPE_TEXT_DETAIL = 2;
        private static final int VIEW_TYPE_USER = 8;
        private static final int VIEW_TYPE_VERSION = 14;
        private Context mContext;

        public ListAdapter(Context context) {
            ProfileActivity.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            String abi;
            switch (viewType) {
                case 1:
                    view = new HeaderCell(this.mContext, 23, ProfileActivity.this.resourcesProvider);
                    break;
                case 2:
                    TextDetailCell textDetailCell = new TextDetailCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    textDetailCell.setContentDescriptionValueFirst(true);
                    final ProfileActivity profileActivity = ProfileActivity.this;
                    textDetailCell.setImageClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            ProfileActivity.this.onTextDetailCellImageClicked(view2);
                        }
                    });
                    view = textDetailCell;
                    break;
                case 3:
                    ProfileActivity profileActivity2 = ProfileActivity.this;
                    Context context = this.mContext;
                    ProfileActivity profileActivity3 = ProfileActivity.this;
                    view = profileActivity2.aboutLinkCell = new AboutLinkCell(context, profileActivity3, profileActivity3.resourcesProvider) { // from class: org.telegram.ui.ProfileActivity.ListAdapter.1
                        @Override // org.telegram.ui.Cells.AboutLinkCell
                        protected void didPressUrl(String url) {
                            ProfileActivity.this.openUrl(url);
                        }

                        @Override // org.telegram.ui.Cells.AboutLinkCell
                        protected void didResizeEnd() {
                            ProfileActivity.this.layoutManager.mIgnoreTopPadding = false;
                        }

                        @Override // org.telegram.ui.Cells.AboutLinkCell
                        protected void didResizeStart() {
                            ProfileActivity.this.layoutManager.mIgnoreTopPadding = true;
                        }
                    };
                    break;
                case 4:
                    view = new TextCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    break;
                case 5:
                    view = new DividerCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    view.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f), 0, 0);
                    break;
                case 6:
                    view = new NotificationsCheckCell(this.mContext, 23, 70, false, ProfileActivity.this.resourcesProvider);
                    break;
                case 7:
                    view = new ShadowSectionCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    break;
                case 8:
                    view = new UserCell(this.mContext, ProfileActivity.this.addMemberRow == -1 ? 9 : 6, 0, true, ProfileActivity.this.resourcesProvider);
                    break;
                case 9:
                case 10:
                case 14:
                case 16:
                default:
                    TextInfoPrivacyCell cell = new TextInfoPrivacyCell(this.mContext, 10, ProfileActivity.this.resourcesProvider);
                    cell.getTextView().setGravity(1);
                    cell.getTextView().setTextColor(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
                    cell.getTextView().setMovementMethod(null);
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        int code = pInfo.versionCode / 10;
                        switch (pInfo.versionCode % 10) {
                            case 1:
                            case 2:
                                abi = "store bundled " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                                break;
                            default:
                                if (BuildVars.isStandaloneApp()) {
                                    abi = "direct " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                                    break;
                                } else {
                                    abi = "universal " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                                    break;
                                }
                        }
                        cell.setText(LocaleController.formatString("TelegramVersion", R.string.TelegramVersion, String.format(Locale.US, "v%s (%d) %s", pInfo.versionName, Integer.valueOf(code), abi)));
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    cell.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
                    view = cell;
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundGrayShadow)));
                    break;
                case 11:
                    view = new View(this.mContext) { // from class: org.telegram.ui.ProfileActivity.ListAdapter.2
                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    break;
                case 12:
                    view = new View(this.mContext) { // from class: org.telegram.ui.ProfileActivity.ListAdapter.3
                        private int lastPaddingHeight = 0;
                        private int lastListViewHeight = 0;

                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            if (this.lastListViewHeight != ProfileActivity.this.listView.getMeasuredHeight()) {
                                this.lastPaddingHeight = 0;
                            }
                            this.lastListViewHeight = ProfileActivity.this.listView.getMeasuredHeight();
                            int n = ProfileActivity.this.listView.getChildCount();
                            if (n != ProfileActivity.this.listAdapter.getItemCount()) {
                                setMeasuredDimension(ProfileActivity.this.listView.getMeasuredWidth(), this.lastPaddingHeight);
                                return;
                            }
                            int totalHeight = 0;
                            for (int i = 0; i < n; i++) {
                                View view2 = ProfileActivity.this.listView.getChildAt(i);
                                int p = ProfileActivity.this.listView.getChildAdapterPosition(view2);
                                if (p >= 0 && p != ProfileActivity.this.bottomPaddingRow) {
                                    totalHeight += ProfileActivity.this.listView.getChildAt(i).getMeasuredHeight();
                                }
                            }
                            int paddingHeight = ((ProfileActivity.this.fragmentView.getMeasuredHeight() - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) - totalHeight;
                            if (paddingHeight > AndroidUtilities.dp(88.0f)) {
                                paddingHeight = 0;
                            }
                            if (paddingHeight <= 0) {
                                paddingHeight = 0;
                            }
                            int measuredWidth = ProfileActivity.this.listView.getMeasuredWidth();
                            this.lastPaddingHeight = paddingHeight;
                            setMeasuredDimension(measuredWidth, paddingHeight);
                        }
                    };
                    view.setBackground(new ColorDrawable(0));
                    break;
                case 13:
                    if (ProfileActivity.this.sharedMediaLayout.getParent() != null) {
                        ((ViewGroup) ProfileActivity.this.sharedMediaLayout.getParent()).removeView(ProfileActivity.this.sharedMediaLayout);
                    }
                    view = ProfileActivity.this.sharedMediaLayout;
                    break;
                case 15:
                    view = new SettingsSuggestionCell(this.mContext, ProfileActivity.this.resourcesProvider) { // from class: org.telegram.ui.ProfileActivity.ListAdapter.4
                        @Override // org.telegram.ui.Cells.SettingsSuggestionCell
                        protected void onYesClick(int type) {
                            ProfileActivity.this.getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.newSuggestionsAvailable);
                            ProfileActivity.this.getMessagesController().removeSuggestion(0L, type == 0 ? "VALIDATE_PHONE_NUMBER" : "VALIDATE_PASSWORD");
                            ProfileActivity.this.getNotificationCenter().addObserver(ProfileActivity.this, NotificationCenter.newSuggestionsAvailable);
                            ProfileActivity profileActivity4 = ProfileActivity.this;
                            if (type == 0) {
                                int unused = profileActivity4.phoneSuggestionRow;
                            } else {
                                int unused2 = profileActivity4.passwordSuggestionRow;
                            }
                            ProfileActivity.this.updateListAnimated(false);
                        }

                        @Override // org.telegram.ui.Cells.SettingsSuggestionCell
                        protected void onNoClick(int type) {
                            if (type == 0) {
                                ProfileActivity.this.presentFragment(new ActionIntroActivity(3));
                            } else {
                                ProfileActivity.this.presentFragment(new TwoStepVerificationSetupActivity(8, null));
                            }
                        }
                    };
                    break;
                case 17:
                    view = new TextInfoPrivacyCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    break;
                case 18:
                    view = new ProfilePremiumCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    break;
            }
            if (viewType != 13) {
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView == ProfileActivity.this.sharedMediaLayout) {
                ProfileActivity.this.sharedMediaLayoutAttached = true;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView == ProfileActivity.this.sharedMediaLayout) {
                ProfileActivity.this.sharedMediaLayoutAttached = false;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            String value;
            String value2;
            String text;
            TLRPC.User user;
            String text2;
            String value3;
            String val;
            boolean enabled;
            String str;
            int i;
            TLRPC.ChatParticipant part;
            String role;
            String role2;
            String str2 = null;
            boolean z = true;
            int i2 = 1;
            boolean z2 = true;
            boolean z3 = true;
            boolean z4 = true;
            boolean z5 = true;
            boolean z6 = true;
            boolean z7 = true;
            boolean z8 = true;
            boolean z9 = true;
            boolean z10 = true;
            boolean z11 = true;
            boolean z12 = true;
            r12 = true;
            boolean z13 = true;
            switch (holder.getItemViewType()) {
                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == ProfileActivity.this.infoHeaderRow) {
                        if (ChatObject.isChannel(ProfileActivity.this.currentChat) && !ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.channelInfoRow != -1) {
                            headerCell.setText(LocaleController.getString("ReportChatDescription", R.string.ReportChatDescription));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("Info", R.string.Info));
                            return;
                        }
                    } else if (position != ProfileActivity.this.membersHeaderRow) {
                        if (position != ProfileActivity.this.settingsSectionRow2) {
                            if (position != ProfileActivity.this.numberSectionRow) {
                                if (position != ProfileActivity.this.helpHeaderRow) {
                                    if (position == ProfileActivity.this.debugHeaderRow) {
                                        headerCell.setText(LocaleController.getString("SettingsDebug", R.string.SettingsDebug));
                                        return;
                                    }
                                    return;
                                }
                                headerCell.setText(LocaleController.getString("SettingsHelp", R.string.SettingsHelp));
                                return;
                            }
                            headerCell.setText(LocaleController.getString("Account", R.string.Account));
                            return;
                        }
                        headerCell.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                        return;
                    } else {
                        headerCell.setText(LocaleController.getString("ChannelMembers", R.string.ChannelMembers));
                        return;
                    }
                case 2:
                    TextDetailCell detailCell = (TextDetailCell) holder.itemView;
                    if (position == ProfileActivity.this.usernameRow) {
                        Drawable drawable = ContextCompat.getDrawable(detailCell.getContext(), R.drawable.msg_qr_mini);
                        drawable.setColorFilter(new PorterDuffColorFilter(ProfileActivity.this.getThemedColor(Theme.key_switch2TrackChecked), PorterDuff.Mode.MULTIPLY));
                        detailCell.setImage(drawable, LocaleController.getString("GetQRCode", R.string.GetQRCode));
                    } else {
                        detailCell.setImage(null);
                    }
                    if (position == ProfileActivity.this.phoneRow) {
                        if (!TextUtils.isEmpty(ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId)).phone)) {
                            text2 = PhoneFormat.getInstance().format("+" + user.phone);
                        } else {
                            text2 = LocaleController.getString("PhoneHidden", R.string.PhoneHidden);
                        }
                        detailCell.setTextAndValue(text2, LocaleController.getString("PhoneMobile", R.string.PhoneMobile), false);
                    } else if (position == ProfileActivity.this.usernameRow) {
                        if (ProfileActivity.this.userId != 0) {
                            TLRPC.User user2 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                            if (user2 != null && !TextUtils.isEmpty(user2.username)) {
                                text = "@" + user2.username;
                            } else {
                                text = "-";
                            }
                            detailCell.setTextAndValue(text, LocaleController.getString("Username", R.string.Username), false);
                        } else if (ProfileActivity.this.currentChat != null) {
                            TLRPC.Chat chat = ProfileActivity.this.getMessagesController().getChat(Long.valueOf(ProfileActivity.this.chatId));
                            detailCell.setTextAndValue(ProfileActivity.this.getMessagesController().linkPrefix + "/" + chat.username, LocaleController.getString("InviteLink", R.string.InviteLink), false);
                        }
                    } else if (position == ProfileActivity.this.locationRow) {
                        if (ProfileActivity.this.chatInfo != null && (ProfileActivity.this.chatInfo.location instanceof TLRPC.TL_channelLocation)) {
                            TLRPC.TL_channelLocation location = (TLRPC.TL_channelLocation) ProfileActivity.this.chatInfo.location;
                            detailCell.setTextAndValue(location.address, LocaleController.getString("AttachLocation", R.string.AttachLocation), false);
                        }
                    } else if (position == ProfileActivity.this.numberRow) {
                        TLRPC.User user3 = UserConfig.getInstance(ProfileActivity.this.currentAccount).getCurrentUser();
                        if (user3 != null && user3.phone != null && user3.phone.length() != 0) {
                            value2 = PhoneFormat.getInstance().format("+" + user3.phone);
                        } else {
                            value2 = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                        }
                        detailCell.setTextAndValue(value2, LocaleController.getString("TapToChangePhone", R.string.TapToChangePhone), true);
                        detailCell.setContentDescriptionValueFirst(false);
                    } else if (position == ProfileActivity.this.setUsernameRow) {
                        TLRPC.User user4 = UserConfig.getInstance(ProfileActivity.this.currentAccount).getCurrentUser();
                        if (user4 != null && !TextUtils.isEmpty(user4.username)) {
                            value = "@" + user4.username;
                        } else {
                            value = LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty);
                        }
                        detailCell.setTextAndValue(value, LocaleController.getString("Username", R.string.Username), true);
                        detailCell.setContentDescriptionValueFirst(true);
                    }
                    detailCell.setTag(Integer.valueOf(position));
                    return;
                case 3:
                    final AboutLinkCell aboutLinkCell = (AboutLinkCell) holder.itemView;
                    if (position == ProfileActivity.this.userInfoRow) {
                        TLRPC.User user5 = ProfileActivity.this.userInfo.user != null ? ProfileActivity.this.userInfo.user : ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userInfo.id));
                        if (!ProfileActivity.this.isBot && (user5 == null || !user5.premium || ProfileActivity.this.userInfo.about == null)) {
                            z13 = false;
                        }
                        boolean addlinks = z13;
                        aboutLinkCell.setTextAndValue(ProfileActivity.this.userInfo.about, LocaleController.getString("UserBio", R.string.UserBio), addlinks);
                    } else if (position == ProfileActivity.this.channelInfoRow) {
                        String text3 = ProfileActivity.this.chatInfo.about;
                        while (text3.contains("\n\n\n")) {
                            text3 = text3.replace("\n\n\n", "\n\n");
                        }
                        if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                            z = false;
                        }
                        aboutLinkCell.setText(text3, z);
                    } else if (position == ProfileActivity.this.bioRow) {
                        if (ProfileActivity.this.userInfo == null || !TextUtils.isEmpty(ProfileActivity.this.userInfo.about)) {
                            String value4 = ProfileActivity.this.userInfo == null ? LocaleController.getString("Loading", R.string.Loading) : ProfileActivity.this.userInfo.about;
                            aboutLinkCell.setTextAndValue(value4, LocaleController.getString("UserBio", R.string.UserBio), ProfileActivity.this.getUserConfig().isPremium());
                            ProfileActivity profileActivity = ProfileActivity.this;
                            if (profileActivity.userInfo != null) {
                                str2 = ProfileActivity.this.userInfo.about;
                            }
                            profileActivity.currentBio = str2;
                        } else {
                            aboutLinkCell.setTextAndValue(LocaleController.getString("UserBio", R.string.UserBio), LocaleController.getString("UserBioDetail", R.string.UserBioDetail), false);
                            ProfileActivity.this.currentBio = null;
                        }
                    }
                    if (position == ProfileActivity.this.bioRow) {
                        aboutLinkCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda0
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view) {
                                ProfileActivity.ListAdapter.this.m4406x59793722(view);
                            }
                        });
                        return;
                    } else {
                        aboutLinkCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda1
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view) {
                                ProfileActivity.ListAdapter.this.m4407xf419f9a3(position, aboutLinkCell, view);
                            }
                        });
                        return;
                    }
                case 4:
                case 18:
                    TextCell textCell = (TextCell) holder.itemView;
                    textCell.setColors(Theme.key_windowBackgroundWhiteGrayIcon, Theme.key_windowBackgroundWhiteBlackText);
                    textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    if (position == ProfileActivity.this.settingsTimerRow) {
                        TLRPC.EncryptedChat encryptedChat = ProfileActivity.this.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(ProfileActivity.this.dialogId)));
                        if (encryptedChat.ttl == 0) {
                            value3 = LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
                        } else {
                            value3 = LocaleController.formatTTLString(encryptedChat.ttl);
                        }
                        textCell.setTextAndValue(LocaleController.getString("MessageLifetime", R.string.MessageLifetime), value3, false);
                        return;
                    } else if (position != ProfileActivity.this.unblockRow) {
                        if (position != ProfileActivity.this.settingsKeyRow) {
                            if (position != ProfileActivity.this.joinRow) {
                                if (position == ProfileActivity.this.subscribersRow) {
                                    if (ProfileActivity.this.chatInfo != null) {
                                        if (ChatObject.isChannel(ProfileActivity.this.currentChat) && !ProfileActivity.this.currentChat.megagroup) {
                                            String string = LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers);
                                            String format = String.format("%d", Integer.valueOf(ProfileActivity.this.chatInfo.participants_count));
                                            if (position == ProfileActivity.this.membersSectionRow - 1) {
                                                z2 = false;
                                            }
                                            textCell.setTextAndValueAndIcon(string, format, R.drawable.msg_groups, z2);
                                            return;
                                        }
                                        String string2 = LocaleController.getString("ChannelMembers", R.string.ChannelMembers);
                                        String format2 = String.format("%d", Integer.valueOf(ProfileActivity.this.chatInfo.participants_count));
                                        if (position == ProfileActivity.this.membersSectionRow - 1) {
                                            z3 = false;
                                        }
                                        textCell.setTextAndValueAndIcon(string2, format2, R.drawable.msg_groups, z3);
                                        return;
                                    } else if (ChatObject.isChannel(ProfileActivity.this.currentChat) && !ProfileActivity.this.currentChat.megagroup) {
                                        String string3 = LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers);
                                        if (position == ProfileActivity.this.membersSectionRow - 1) {
                                            z4 = false;
                                        }
                                        textCell.setTextAndIcon(string3, R.drawable.msg_groups, z4);
                                        return;
                                    } else {
                                        String string4 = LocaleController.getString("ChannelMembers", R.string.ChannelMembers);
                                        if (position == ProfileActivity.this.membersSectionRow - 1) {
                                            z5 = false;
                                        }
                                        textCell.setTextAndIcon(string4, R.drawable.msg_groups, z5);
                                        return;
                                    }
                                } else if (position == ProfileActivity.this.subscribersRequestsRow) {
                                    if (ProfileActivity.this.chatInfo != null) {
                                        String string5 = LocaleController.getString("SubscribeRequests", R.string.SubscribeRequests);
                                        String format3 = String.format("%d", Integer.valueOf(ProfileActivity.this.chatInfo.requests_pending));
                                        if (position == ProfileActivity.this.membersSectionRow - 1) {
                                            z6 = false;
                                        }
                                        textCell.setTextAndValueAndIcon(string5, format3, R.drawable.msg_requests, z6);
                                        return;
                                    }
                                    return;
                                } else if (position == ProfileActivity.this.administratorsRow) {
                                    if (ProfileActivity.this.chatInfo != null) {
                                        String string6 = LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators);
                                        String format4 = String.format("%d", Integer.valueOf(ProfileActivity.this.chatInfo.admins_count));
                                        if (position == ProfileActivity.this.membersSectionRow - 1) {
                                            z7 = false;
                                        }
                                        textCell.setTextAndValueAndIcon(string6, format4, R.drawable.msg_admins, z7);
                                        return;
                                    }
                                    String string7 = LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators);
                                    if (position == ProfileActivity.this.membersSectionRow - 1) {
                                        z8 = false;
                                    }
                                    textCell.setTextAndIcon(string7, R.drawable.msg_admins, z8);
                                    return;
                                } else if (position == ProfileActivity.this.blockedUsersRow) {
                                    if (ProfileActivity.this.chatInfo != null) {
                                        String string8 = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                                        String format5 = String.format("%d", Integer.valueOf(Math.max(ProfileActivity.this.chatInfo.banned_count, ProfileActivity.this.chatInfo.kicked_count)));
                                        if (position == ProfileActivity.this.membersSectionRow - 1) {
                                            z9 = false;
                                        }
                                        textCell.setTextAndValueAndIcon(string8, format5, R.drawable.msg_user_remove, z9);
                                        return;
                                    }
                                    String string9 = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                                    if (position == ProfileActivity.this.membersSectionRow - 1) {
                                        z10 = false;
                                    }
                                    textCell.setTextAndIcon(string9, R.drawable.msg_user_remove, z10);
                                    return;
                                } else if (position != ProfileActivity.this.addMemberRow) {
                                    if (position != ProfileActivity.this.sendMessageRow) {
                                        if (position != ProfileActivity.this.reportRow) {
                                            if (position != ProfileActivity.this.languageRow) {
                                                if (position != ProfileActivity.this.notificationRow) {
                                                    if (position != ProfileActivity.this.privacyRow) {
                                                        if (position != ProfileActivity.this.dataRow) {
                                                            if (position != ProfileActivity.this.chatRow) {
                                                                if (position != ProfileActivity.this.filtersRow) {
                                                                    if (position != ProfileActivity.this.stickersRow) {
                                                                        if (position != ProfileActivity.this.questionRow) {
                                                                            if (position != ProfileActivity.this.faqRow) {
                                                                                if (position != ProfileActivity.this.policyRow) {
                                                                                    if (position != ProfileActivity.this.sendLogsRow) {
                                                                                        if (position != ProfileActivity.this.sendLastLogsRow) {
                                                                                            if (position != ProfileActivity.this.clearLogsRow) {
                                                                                                if (position != ProfileActivity.this.switchBackendRow) {
                                                                                                    if (position != ProfileActivity.this.devicesRow) {
                                                                                                        if (position == ProfileActivity.this.setAvatarRow) {
                                                                                                            ProfileActivity.this.cellCameraDrawable.setCustomEndFrame(86);
                                                                                                            ProfileActivity.this.cellCameraDrawable.setCurrentFrame(85, false);
                                                                                                            textCell.setTextAndIcon(LocaleController.getString("SetProfilePhoto", R.string.SetProfilePhoto), ProfileActivity.this.cellCameraDrawable, false);
                                                                                                            textCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                                                                                                            textCell.getImageView().setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
                                                                                                            textCell.setImageLeft(12);
                                                                                                            ProfileActivity.this.setAvatarCell = textCell;
                                                                                                            return;
                                                                                                        } else if (position != ProfileActivity.this.addToGroupButtonRow) {
                                                                                                            if (position == ProfileActivity.this.premiumRow) {
                                                                                                                textCell.setTextAndIcon(LocaleController.getString("TelegramPremium", R.string.TelegramPremium), PremiumGradient.getInstance().premiumStarMenuDrawable, false);
                                                                                                                textCell.setImageLeft(23);
                                                                                                                return;
                                                                                                            }
                                                                                                            return;
                                                                                                        } else {
                                                                                                            textCell.setTextAndIcon(LocaleController.getString("AddToGroupOrChannel", R.string.AddToGroupOrChannel), R.drawable.msg_groups_create, false);
                                                                                                            textCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                                                                                                            return;
                                                                                                        }
                                                                                                    }
                                                                                                    textCell.setTextAndIcon(LocaleController.getString("Devices", R.string.Devices), R.drawable.menu_devices, true);
                                                                                                    return;
                                                                                                }
                                                                                                textCell.setText("Switch Backend", false);
                                                                                                return;
                                                                                            }
                                                                                            String string10 = LocaleController.getString("DebugClearLogs", R.string.DebugClearLogs);
                                                                                            if (ProfileActivity.this.switchBackendRow == -1) {
                                                                                                z12 = false;
                                                                                            }
                                                                                            textCell.setText(string10, z12);
                                                                                            return;
                                                                                        }
                                                                                        textCell.setText(LocaleController.getString("DebugSendLastLogs", R.string.DebugSendLastLogs), true);
                                                                                        return;
                                                                                    }
                                                                                    textCell.setText(LocaleController.getString("DebugSendLogs", R.string.DebugSendLogs), true);
                                                                                    return;
                                                                                }
                                                                                textCell.setTextAndIcon(LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy), R.drawable.msg_policy, false);
                                                                                return;
                                                                            }
                                                                            textCell.setTextAndIcon(LocaleController.getString("TelegramFAQ", R.string.TelegramFAQ), R.drawable.msg_help, true);
                                                                            return;
                                                                        }
                                                                        textCell.setTextAndIcon(LocaleController.getString("AskAQuestion", R.string.AskAQuestion), R.drawable.msg_ask_question, true);
                                                                        return;
                                                                    }
                                                                    textCell.setTextAndIcon(LocaleController.getString((int) R.string.StickersName), R.drawable.msg_sticker, true);
                                                                    return;
                                                                }
                                                                textCell.setTextAndIcon(LocaleController.getString("Filters", R.string.Filters), R.drawable.msg_folders, true);
                                                                return;
                                                            }
                                                            textCell.setTextAndIcon(LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, true);
                                                            return;
                                                        }
                                                        textCell.setTextAndIcon(LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, true);
                                                        return;
                                                    }
                                                    textCell.setTextAndIcon(LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, true);
                                                    return;
                                                }
                                                textCell.setTextAndIcon(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, true);
                                                return;
                                            }
                                            textCell.setTextAndIcon(LocaleController.getString("Language", R.string.Language), R.drawable.msg_language, false);
                                            textCell.setImageLeft(23);
                                            return;
                                        }
                                        textCell.setText(LocaleController.getString("ReportUserLocation", R.string.ReportUserLocation), false);
                                        textCell.setColors(null, Theme.key_windowBackgroundWhiteRedText5);
                                        return;
                                    }
                                    textCell.setText(LocaleController.getString("SendMessageLocation", R.string.SendMessageLocation), true);
                                    return;
                                } else {
                                    textCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                                    String string11 = LocaleController.getString("AddMember", R.string.AddMember);
                                    if (ProfileActivity.this.membersSectionRow != -1) {
                                        z11 = false;
                                    }
                                    textCell.setTextAndIcon(string11, R.drawable.msg_contact_add, z11);
                                    return;
                                }
                            }
                            textCell.setColors(null, Theme.key_windowBackgroundWhiteBlueText2);
                            if (ProfileActivity.this.currentChat.megagroup) {
                                textCell.setText(LocaleController.getString("ProfileJoinGroup", R.string.ProfileJoinGroup), false);
                                return;
                            } else {
                                textCell.setText(LocaleController.getString("ProfileJoinChannel", R.string.ProfileJoinChannel), false);
                                return;
                            }
                        }
                        IdenticonDrawable identiconDrawable = new IdenticonDrawable();
                        identiconDrawable.setEncryptedChat(ProfileActivity.this.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(ProfileActivity.this.dialogId))));
                        textCell.setTextAndValueDrawable(LocaleController.getString("EncryptionKey", R.string.EncryptionKey), identiconDrawable, false);
                        return;
                    } else {
                        textCell.setText(LocaleController.getString("Unblock", R.string.Unblock), false);
                        textCell.setColors(null, Theme.key_windowBackgroundWhiteRedText5);
                        return;
                    }
                case 5:
                case 9:
                case 10:
                case 11:
                case 13:
                case 14:
                case 16:
                default:
                    return;
                case 6:
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) holder.itemView;
                    if (position == ProfileActivity.this.notificationsRow) {
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount);
                        long did = ProfileActivity.this.dialogId != 0 ? ProfileActivity.this.dialogId : ProfileActivity.this.userId != 0 ? ProfileActivity.this.userId : -ProfileActivity.this.chatId;
                        boolean custom = preferences.getBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + did, false);
                        boolean hasOverride = preferences.contains("notify2_" + did);
                        int value5 = preferences.getInt("notify2_" + did, 0);
                        int delta = preferences.getInt("notifyuntil_" + did, 0);
                        if (value5 == 3 && delta != Integer.MAX_VALUE) {
                            int delta2 = delta - ProfileActivity.this.getConnectionsManager().getCurrentTime();
                            if (delta2 <= 0) {
                                if (custom) {
                                    val = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                                } else {
                                    val = LocaleController.getString("NotificationsOn", R.string.NotificationsOn);
                                }
                                enabled = true;
                            } else if (delta2 < 3600) {
                                val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta2 / 60, new Object[0]));
                                enabled = false;
                            } else if (delta2 < 86400) {
                                val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((delta2 / 60.0f) / 60.0f), new Object[0]));
                                enabled = false;
                            } else if (delta2 < 31536000) {
                                val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil(((delta2 / 60.0f) / 60.0f) / 24.0f), new Object[0]));
                                enabled = false;
                            } else {
                                val = null;
                                enabled = false;
                            }
                        } else {
                            if (value5 == 0) {
                                if (!hasOverride) {
                                    enabled = ProfileActivity.this.getNotificationsController().isGlobalNotificationsEnabled(did);
                                } else {
                                    enabled = true;
                                }
                            } else if (value5 != 1) {
                                enabled = false;
                            } else {
                                enabled = true;
                            }
                            if (enabled && custom) {
                                val = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                            } else {
                                if (enabled) {
                                    i = R.string.NotificationsOn;
                                    str = "NotificationsOn";
                                } else {
                                    i = R.string.NotificationsOff;
                                    str = "NotificationsOff";
                                }
                                val = LocaleController.getString(str, i);
                            }
                        }
                        if (val == null) {
                            val = LocaleController.getString("NotificationsOff", R.string.NotificationsOff);
                        }
                        checkCell.setAnimationsEnabled(ProfileActivity.this.fragmentOpened);
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("Notifications", R.string.Notifications), val, enabled, false);
                        return;
                    }
                    return;
                case 7:
                    View sectionCell = holder.itemView;
                    sectionCell.setTag(Integer.valueOf(position));
                    if ((position != ProfileActivity.this.infoSectionRow || ProfileActivity.this.lastSectionRow != -1 || ProfileActivity.this.secretSettingsSectionRow != -1 || ProfileActivity.this.sharedMediaRow != -1 || ProfileActivity.this.membersSectionRow != -1) && position != ProfileActivity.this.secretSettingsSectionRow && position != ProfileActivity.this.lastSectionRow && (position != ProfileActivity.this.membersSectionRow || ProfileActivity.this.lastSectionRow != -1 || ProfileActivity.this.sharedMediaRow != -1)) {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundGrayShadow)));
                        return;
                    } else {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundGrayShadow)));
                        return;
                    }
                case 8:
                    UserCell userCell = (UserCell) holder.itemView;
                    try {
                        part = !ProfileActivity.this.visibleSortedUsers.isEmpty() ? (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(((Integer) ProfileActivity.this.visibleSortedUsers.get(position - ProfileActivity.this.membersStartRow)).intValue()) : (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(position - ProfileActivity.this.membersStartRow);
                    } catch (Exception e) {
                        FileLog.e(e);
                        part = null;
                    }
                    if (part != null) {
                        if (part instanceof TLRPC.TL_chatChannelParticipant) {
                            TLRPC.ChannelParticipant channelParticipant = ((TLRPC.TL_chatChannelParticipant) part).channelParticipant;
                            if (!TextUtils.isEmpty(channelParticipant.rank)) {
                                role2 = channelParticipant.rank;
                            } else if (channelParticipant instanceof TLRPC.TL_channelParticipantCreator) {
                                role2 = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                            } else if (channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) {
                                role2 = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                            } else {
                                role2 = null;
                            }
                            role = role2;
                        } else if (part instanceof TLRPC.TL_chatParticipantCreator) {
                            role = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                        } else if (part instanceof TLRPC.TL_chatParticipantAdmin) {
                            role = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                        } else {
                            role = null;
                        }
                        userCell.setAdminRole(role);
                        userCell.setData(ProfileActivity.this.getMessagesController().getUser(Long.valueOf(part.user_id)), null, null, 0, position != ProfileActivity.this.membersEndRow - 1);
                        return;
                    }
                    return;
                case 12:
                    holder.itemView.requestLayout();
                    return;
                case 15:
                    SettingsSuggestionCell suggestionCell = (SettingsSuggestionCell) holder.itemView;
                    if (position != ProfileActivity.this.passwordSuggestionRow) {
                        i2 = 0;
                    }
                    suggestionCell.setType(i2);
                    return;
                case 17:
                    TextInfoPrivacyCell addToGroupInfo = (TextInfoPrivacyCell) holder.itemView;
                    addToGroupInfo.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundGrayShadow)));
                    addToGroupInfo.setText(LocaleController.getString("BotAddToGroupOrChannelInfo", R.string.BotAddToGroupOrChannelInfo));
                    return;
            }
        }

        /* renamed from: lambda$onBindViewHolder$1$org-telegram-ui-ProfileActivity$ListAdapter */
        public /* synthetic */ void m4406x59793722(View e) {
            if (ProfileActivity.this.userInfo != null) {
                ProfileActivity.this.presentFragment(new ChangeBioActivity());
            }
        }

        /* renamed from: lambda$onBindViewHolder$2$org-telegram-ui-ProfileActivity$ListAdapter */
        public /* synthetic */ void m4407xf419f9a3(int position, AboutLinkCell aboutLinkCell, View e) {
            ProfileActivity.this.processOnClickOrPress(position, aboutLinkCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == ProfileActivity.this.setAvatarRow) {
                ProfileActivity.this.setAvatarCell = null;
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (ProfileActivity.this.notificationRow != -1) {
                int position = holder.getAdapterPosition();
                return position == ProfileActivity.this.notificationRow || position == ProfileActivity.this.numberRow || position == ProfileActivity.this.privacyRow || position == ProfileActivity.this.languageRow || position == ProfileActivity.this.setUsernameRow || position == ProfileActivity.this.bioRow || position == ProfileActivity.this.versionRow || position == ProfileActivity.this.dataRow || position == ProfileActivity.this.chatRow || position == ProfileActivity.this.questionRow || position == ProfileActivity.this.devicesRow || position == ProfileActivity.this.filtersRow || position == ProfileActivity.this.stickersRow || position == ProfileActivity.this.faqRow || position == ProfileActivity.this.policyRow || position == ProfileActivity.this.sendLogsRow || position == ProfileActivity.this.sendLastLogsRow || position == ProfileActivity.this.clearLogsRow || position == ProfileActivity.this.switchBackendRow || position == ProfileActivity.this.setAvatarRow || position == ProfileActivity.this.addToGroupButtonRow || position == ProfileActivity.this.premiumRow;
            }
            if (holder.itemView instanceof UserCell) {
                UserCell userCell = (UserCell) holder.itemView;
                Object object = userCell.getCurrentObject();
                if (object instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) object;
                    if (UserObject.isUserSelf(user)) {
                        return false;
                    }
                }
            }
            int type = holder.getItemViewType();
            return (type == 1 || type == 5 || type == 7 || type == 11 || type == 12 || type == 13 || type == 9 || type == 10) ? false : true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != ProfileActivity.this.infoHeaderRow && position != ProfileActivity.this.membersHeaderRow && position != ProfileActivity.this.settingsSectionRow2 && position != ProfileActivity.this.numberSectionRow && position != ProfileActivity.this.helpHeaderRow && position != ProfileActivity.this.debugHeaderRow) {
                if (position != ProfileActivity.this.phoneRow && position != ProfileActivity.this.usernameRow && position != ProfileActivity.this.locationRow && position != ProfileActivity.this.numberRow && position != ProfileActivity.this.setUsernameRow) {
                    if (position != ProfileActivity.this.userInfoRow && position != ProfileActivity.this.channelInfoRow && position != ProfileActivity.this.bioRow) {
                        if (position != ProfileActivity.this.settingsTimerRow && position != ProfileActivity.this.settingsKeyRow && position != ProfileActivity.this.reportRow && position != ProfileActivity.this.subscribersRow && position != ProfileActivity.this.subscribersRequestsRow && position != ProfileActivity.this.administratorsRow && position != ProfileActivity.this.blockedUsersRow && position != ProfileActivity.this.addMemberRow && position != ProfileActivity.this.joinRow && position != ProfileActivity.this.unblockRow && position != ProfileActivity.this.sendMessageRow && position != ProfileActivity.this.notificationRow && position != ProfileActivity.this.privacyRow && position != ProfileActivity.this.languageRow && position != ProfileActivity.this.dataRow && position != ProfileActivity.this.chatRow && position != ProfileActivity.this.questionRow && position != ProfileActivity.this.devicesRow && position != ProfileActivity.this.filtersRow && position != ProfileActivity.this.stickersRow && position != ProfileActivity.this.faqRow && position != ProfileActivity.this.policyRow && position != ProfileActivity.this.sendLogsRow && position != ProfileActivity.this.sendLastLogsRow && position != ProfileActivity.this.clearLogsRow && position != ProfileActivity.this.switchBackendRow && position != ProfileActivity.this.setAvatarRow && position != ProfileActivity.this.addToGroupButtonRow) {
                            if (position != ProfileActivity.this.notificationsDividerRow) {
                                if (position != ProfileActivity.this.notificationsRow) {
                                    if (position != ProfileActivity.this.infoSectionRow && position != ProfileActivity.this.lastSectionRow && position != ProfileActivity.this.membersSectionRow && position != ProfileActivity.this.secretSettingsSectionRow && position != ProfileActivity.this.settingsSectionRow && position != ProfileActivity.this.devicesSectionRow && position != ProfileActivity.this.helpSectionCell && position != ProfileActivity.this.setAvatarSectionRow && position != ProfileActivity.this.passwordSuggestionSectionRow && position != ProfileActivity.this.phoneSuggestionSectionRow && position != ProfileActivity.this.premiumSectionsRow) {
                                        if (position < ProfileActivity.this.membersStartRow || position >= ProfileActivity.this.membersEndRow) {
                                            if (position != ProfileActivity.this.emptyRow) {
                                                if (position != ProfileActivity.this.bottomPaddingRow) {
                                                    if (position != ProfileActivity.this.sharedMediaRow) {
                                                        if (position != ProfileActivity.this.versionRow) {
                                                            if (position != ProfileActivity.this.passwordSuggestionRow && position != ProfileActivity.this.phoneSuggestionRow) {
                                                                if (position != ProfileActivity.this.addToGroupInfoRow) {
                                                                    if (position == ProfileActivity.this.premiumRow) {
                                                                        return 18;
                                                                    }
                                                                    return 0;
                                                                }
                                                                return 17;
                                                            }
                                                            return 15;
                                                        }
                                                        return 14;
                                                    }
                                                    return 13;
                                                }
                                                return 12;
                                            }
                                            return 11;
                                        }
                                        return 8;
                                    }
                                    return 7;
                                }
                                return 6;
                            }
                            return 5;
                        }
                        return 4;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }
    }

    /* loaded from: classes4.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<MessagesController.FaqSearchResult> faqSearchArray;
        private ArrayList<MessagesController.FaqSearchResult> faqSearchResults;
        private TLRPC.WebPage faqWebPage;
        private String lastSearchString;
        private boolean loadingFaqPage;
        private Context mContext;
        private ArrayList<Object> recentSearches;
        private ArrayList<CharSequence> resultNames;
        private SearchResult[] searchArray;
        private ArrayList<SearchResult> searchResults;
        private Runnable searchRunnable;
        private boolean searchWas;

        /* loaded from: classes4.dex */
        public class SearchResult {
            private int guid;
            private int iconResId;
            private int num;
            private Runnable openRunnable;
            private String[] path;
            private String rowName;
            private String searchTitle;

            public SearchResult(SearchAdapter searchAdapter, int g, String search, int icon, Runnable open) {
                this(g, search, null, null, null, icon, open);
            }

            public SearchResult(SearchAdapter searchAdapter, int g, String search, String pathArg1, int icon, Runnable open) {
                this(g, search, null, pathArg1, null, icon, open);
            }

            public SearchResult(SearchAdapter searchAdapter, int g, String search, String row, String pathArg1, int icon, Runnable open) {
                this(g, search, row, pathArg1, null, icon, open);
            }

            public SearchResult(int g, String search, String row, String pathArg1, String pathArg2, int icon, Runnable open) {
                SearchAdapter.this = r3;
                this.guid = g;
                this.searchTitle = search;
                this.rowName = row;
                this.openRunnable = open;
                this.iconResId = icon;
                if (pathArg1 == null || pathArg2 == null) {
                    if (pathArg1 != null) {
                        this.path = new String[]{pathArg1};
                        return;
                    }
                    return;
                }
                this.path = new String[]{pathArg1, pathArg2};
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof SearchResult)) {
                    return false;
                }
                SearchResult result = (SearchResult) obj;
                return this.guid == result.guid;
            }

            public String toString() {
                SerializedData data = new SerializedData();
                data.writeInt32(this.num);
                data.writeInt32(1);
                data.writeInt32(this.guid);
                return Utilities.bytesToHex(data.toByteArray());
            }

            public void open() {
                this.openRunnable.run();
                AndroidUtilities.scrollToFragmentRow(ProfileActivity.this.parentLayout, this.rowName);
            }
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4412lambda$new$0$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ChangeNameActivity(ProfileActivity.this.resourcesProvider));
        }

        /* renamed from: lambda$new$1$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4413lambda$new$1$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ActionIntroActivity(3));
        }

        /* renamed from: lambda$new$2$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4424lambda$new$2$orgtelegramuiProfileActivity$SearchAdapter() {
            int freeAccount = -1;
            int a = 0;
            while (true) {
                if (a >= 4) {
                    break;
                } else if (UserConfig.getInstance(a).isClientActivated()) {
                    a++;
                } else {
                    freeAccount = a;
                    break;
                }
            }
            if (freeAccount >= 0) {
                ProfileActivity.this.presentFragment(new LoginActivity(freeAccount));
            }
        }

        /* renamed from: lambda$new$3$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4435lambda$new$3$orgtelegramuiProfileActivity$SearchAdapter() {
            if (ProfileActivity.this.userInfo != null) {
                ProfileActivity.this.presentFragment(new ChangeBioActivity());
            }
        }

        /* renamed from: lambda$new$4$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4446lambda$new$4$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$5$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4457lambda$new$5$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new NotificationsCustomSettingsActivity(1, new ArrayList(), true));
        }

        /* renamed from: lambda$new$6$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4468lambda$new$6$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new NotificationsCustomSettingsActivity(0, new ArrayList(), true));
        }

        /* renamed from: lambda$new$7$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4479lambda$new$7$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new NotificationsCustomSettingsActivity(2, new ArrayList(), true));
        }

        /* renamed from: lambda$new$8$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4490lambda$new$8$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$9$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4495lambda$new$9$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$10$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4414lambda$new$10$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$11$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4415lambda$new$11$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$12$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4416lambda$new$12$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$13$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4417lambda$new$13$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$14$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4418lambda$new$14$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$15$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4419lambda$new$15$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacyUsersActivity());
        }

        /* renamed from: lambda$new$16$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4420lambda$new$16$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(6, true));
        }

        /* renamed from: lambda$new$17$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4421lambda$new$17$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(0, true));
        }

        /* renamed from: lambda$new$18$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4422lambda$new$18$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(4, true));
        }

        /* renamed from: lambda$new$19$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4423lambda$new$19$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(5, true));
        }

        /* renamed from: lambda$new$20$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4425lambda$new$20$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(3, true));
        }

        /* renamed from: lambda$new$21$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4426lambda$new$21$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(2, true));
        }

        /* renamed from: lambda$new$22$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4427lambda$new$22$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(1, true));
        }

        /* renamed from: lambda$new$23$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4428lambda$new$23$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(PasscodeActivity.determineOpenFragment());
        }

        /* renamed from: lambda$new$24$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4429lambda$new$24$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new TwoStepVerificationActivity());
        }

        /* renamed from: lambda$new$25$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4430lambda$new$25$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new SessionsActivity(0));
        }

        /* renamed from: lambda$new$26$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4431lambda$new$26$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$27$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4432lambda$new$27$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$28$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4433lambda$new$28$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$29$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4434lambda$new$29$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new SessionsActivity(1));
        }

        /* renamed from: lambda$new$30$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4436lambda$new$30$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$31$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4437lambda$new$31$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$32$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4438lambda$new$32$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$33$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4439lambda$new$33$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$34$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4440lambda$new$34$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$35$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4441lambda$new$35$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new SessionsActivity(0));
        }

        /* renamed from: lambda$new$36$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4442lambda$new$36$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$37$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4443lambda$new$37$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$38$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4444lambda$new$38$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        /* renamed from: lambda$new$39$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4445lambda$new$39$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        /* renamed from: lambda$new$40$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4447lambda$new$40$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        /* renamed from: lambda$new$41$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4448lambda$new$41$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        /* renamed from: lambda$new$42$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4449lambda$new$42$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataUsageActivity());
        }

        /* renamed from: lambda$new$43$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4450lambda$new$43$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$44$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4451lambda$new$44$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataAutoDownloadActivity(0));
        }

        /* renamed from: lambda$new$45$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4452lambda$new$45$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataAutoDownloadActivity(1));
        }

        /* renamed from: lambda$new$46$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4453lambda$new$46$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataAutoDownloadActivity(2));
        }

        /* renamed from: lambda$new$47$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4454lambda$new$47$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$48$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4455lambda$new$48$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$49$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4456lambda$new$49$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$50$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4458lambda$new$50$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$51$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4459lambda$new$51$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$52$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4460lambda$new$52$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$53$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4461lambda$new$53$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$54$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4462lambda$new$54$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$55$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4463lambda$new$55$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$56$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4464lambda$new$56$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ProxyListActivity());
        }

        /* renamed from: lambda$new$57$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4465lambda$new$57$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ProxyListActivity());
        }

        /* renamed from: lambda$new$58$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4466lambda$new$58$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$59$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4467lambda$new$59$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$60$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4469lambda$new$60$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$61$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4470lambda$new$61$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new WallpapersListActivity(0));
        }

        /* renamed from: lambda$new$62$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4471lambda$new$62$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new WallpapersListActivity(1));
        }

        /* renamed from: lambda$new$63$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4472lambda$new$63$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new WallpapersListActivity(0));
        }

        /* renamed from: lambda$new$64$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4473lambda$new$64$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ThemeActivity(1));
        }

        /* renamed from: lambda$new$65$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4474lambda$new$65$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$66$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4475lambda$new$66$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$67$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4476lambda$new$67$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$68$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4477lambda$new$68$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$69$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4478lambda$new$69$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$70$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4480lambda$new$70$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$71$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4481lambda$new$71$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$72$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4482lambda$new$72$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$73$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4483lambda$new$73$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new StickersActivity(0));
        }

        /* renamed from: lambda$new$74$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4484lambda$new$74$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new StickersActivity(0));
        }

        /* renamed from: lambda$new$75$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4485lambda$new$75$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new FeaturedStickersActivity());
        }

        /* renamed from: lambda$new$76$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4486lambda$new$76$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new StickersActivity(1));
        }

        /* renamed from: lambda$new$77$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4487lambda$new$77$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ArchivedStickersActivity(0));
        }

        /* renamed from: lambda$new$78$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4488lambda$new$78$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new ArchivedStickersActivity(1));
        }

        /* renamed from: lambda$new$79$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4489lambda$new$79$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity.this.presentFragment(new LanguageSelectActivity());
        }

        /* renamed from: lambda$new$80$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4491lambda$new$80$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity profileActivity = ProfileActivity.this;
            profileActivity.showDialog(AlertsCreator.createSupportAlert(profileActivity, null));
        }

        /* renamed from: lambda$new$81$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4492lambda$new$81$orgtelegramuiProfileActivity$SearchAdapter() {
            Browser.openUrl(ProfileActivity.this.getParentActivity(), LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
        }

        /* renamed from: lambda$new$82$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4493lambda$new$82$orgtelegramuiProfileActivity$SearchAdapter() {
            Browser.openUrl(ProfileActivity.this.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl));
        }

        public SearchAdapter(Context context) {
            ProfileActivity.this = r22;
            SearchResult[] searchResultArr = new SearchResult[83];
            searchResultArr[0] = new SearchResult(this, 500, LocaleController.getString("EditName", R.string.EditName), 0, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4412lambda$new$0$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[1] = new SearchResult(this, 501, LocaleController.getString("ChangePhoneNumber", R.string.ChangePhoneNumber), 0, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4413lambda$new$1$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[2] = new SearchResult(this, 502, LocaleController.getString("AddAnotherAccount", R.string.AddAnotherAccount), 0, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4424lambda$new$2$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[3] = new SearchResult(this, 503, LocaleController.getString("UserBio", R.string.UserBio), 0, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4435lambda$new$3$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[4] = new SearchResult(this, 1, LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4446lambda$new$4$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[5] = new SearchResult(this, 2, LocaleController.getString("NotificationsPrivateChats", R.string.NotificationsPrivateChats), LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda39
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4457lambda$new$5$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[6] = new SearchResult(this, 3, LocaleController.getString("NotificationsGroups", R.string.NotificationsGroups), LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda51
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4468lambda$new$6$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[7] = new SearchResult(this, 4, LocaleController.getString("NotificationsChannels", R.string.NotificationsChannels), LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda63
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4479lambda$new$7$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[8] = new SearchResult(this, 5, LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings), "callsSectionRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda75
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4490lambda$new$8$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[9] = new SearchResult(this, 6, LocaleController.getString("BadgeNumber", R.string.BadgeNumber), "badgeNumberSection", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda80
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4495lambda$new$9$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[10] = new SearchResult(this, 7, LocaleController.getString("InAppNotifications", R.string.InAppNotifications), "inappSectionRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4414lambda$new$10$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[11] = new SearchResult(this, 8, LocaleController.getString("ContactJoined", R.string.ContactJoined), "contactJoinedRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4415lambda$new$11$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[12] = new SearchResult(this, 9, LocaleController.getString("PinnedMessages", R.string.PinnedMessages), "pinnedMessageRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda44
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4416lambda$new$12$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[13] = new SearchResult(this, 10, LocaleController.getString("ResetAllNotifications", R.string.ResetAllNotifications), "resetNotificationsRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda55
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4417lambda$new$13$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[14] = new SearchResult(this, 100, LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda66
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4418lambda$new$14$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[15] = new SearchResult(this, 101, LocaleController.getString("BlockedUsers", R.string.BlockedUsers), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda77
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4419lambda$new$15$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[16] = new SearchResult(this, LocationRequest.PRIORITY_NO_POWER, LocaleController.getString("PrivacyPhone", R.string.PrivacyPhone), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda86
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4420lambda$new$16$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[17] = new SearchResult(this, 102, LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda87
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4421lambda$new$17$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[18] = new SearchResult(this, 103, LocaleController.getString("PrivacyProfilePhoto", R.string.PrivacyProfilePhoto), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4422lambda$new$18$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[19] = new SearchResult(this, LocationRequest.PRIORITY_LOW_POWER, LocaleController.getString("PrivacyForwards", R.string.PrivacyForwards), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4423lambda$new$19$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[20] = new SearchResult(this, 122, LocaleController.getString("PrivacyP2P", R.string.PrivacyP2P), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4425lambda$new$20$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[21] = new SearchResult(this, 106, LocaleController.getString("Calls", R.string.Calls), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4426lambda$new$21$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[22] = new SearchResult(this, 107, LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4427lambda$new$22$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[23] = new SearchResult(this, 108, LocaleController.getString("Passcode", R.string.Passcode), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4428lambda$new$23$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[24] = new SearchResult(this, 109, LocaleController.getString("TwoStepVerification", R.string.TwoStepVerification), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4429lambda$new$24$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[25] = new SearchResult(this, 110, LocaleController.getString("SessionsTitle", R.string.SessionsTitle), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4430lambda$new$25$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            SearchResult searchResult = null;
            if (r22.getMessagesController().autoarchiveAvailable) {
                searchResult = new SearchResult(this, 121, LocaleController.getString("ArchiveAndMute", R.string.ArchiveAndMute), "newChatsRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda10
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProfileActivity.SearchAdapter.this.m4431lambda$new$26$orgtelegramuiProfileActivity$SearchAdapter();
                    }
                });
            }
            searchResultArr[26] = searchResult;
            searchResultArr[27] = new SearchResult(this, 112, LocaleController.getString("DeleteAccountIfAwayFor2", R.string.DeleteAccountIfAwayFor2), "deleteAccountRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4432lambda$new$27$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[28] = new SearchResult(this, 113, LocaleController.getString("PrivacyPaymentsClear", R.string.PrivacyPaymentsClear), "paymentsClearRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4433lambda$new$28$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[29] = new SearchResult(this, 114, LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4434lambda$new$29$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[30] = new SearchResult(this, 115, LocaleController.getString("SyncContactsDelete", R.string.SyncContactsDelete), "contactsDeleteRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4436lambda$new$30$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[31] = new SearchResult(this, 116, LocaleController.getString("SyncContacts", R.string.SyncContacts), "contactsSyncRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4437lambda$new$31$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[32] = new SearchResult(this, 117, LocaleController.getString("SuggestContacts", R.string.SuggestContacts), "contactsSuggestRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4438lambda$new$32$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[33] = new SearchResult(this, 118, LocaleController.getString("MapPreviewProvider", R.string.MapPreviewProvider), "secretMapRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4439lambda$new$33$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[34] = new SearchResult(this, 119, LocaleController.getString("SecretWebPage", R.string.SecretWebPage), "secretWebpageRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4440lambda$new$34$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[35] = new SearchResult(this, 120, LocaleController.getString("Devices", R.string.Devices), R.drawable.msg_secret, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4441lambda$new$35$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[36] = new SearchResult(this, 200, LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda23
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4442lambda$new$36$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[37] = new SearchResult(this, SearchViewPager.forwardItemId, LocaleController.getString("DataUsage", R.string.DataUsage), "usageSectionRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4443lambda$new$37$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[38] = new SearchResult(this, SearchViewPager.deleteItemId, LocaleController.getString("StorageUsage", R.string.StorageUsage), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4444lambda$new$38$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[39] = new SearchResult(203, LocaleController.getString("KeepMedia", R.string.KeepMedia), "keepMediaRow", LocaleController.getString("DataSettings", R.string.DataSettings), LocaleController.getString("StorageUsage", R.string.StorageUsage), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4445lambda$new$39$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[40] = new SearchResult(204, LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), "cacheRow", LocaleController.getString("DataSettings", R.string.DataSettings), LocaleController.getString("StorageUsage", R.string.StorageUsage), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4447lambda$new$40$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[41] = new SearchResult(205, LocaleController.getString("LocalDatabase", R.string.LocalDatabase), "databaseRow", LocaleController.getString("DataSettings", R.string.DataSettings), LocaleController.getString("StorageUsage", R.string.StorageUsage), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda29
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4448lambda$new$41$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[42] = new SearchResult(this, 206, LocaleController.getString("NetworkUsage", R.string.NetworkUsage), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda30
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4449lambda$new$42$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[43] = new SearchResult(this, 207, LocaleController.getString("AutomaticMediaDownload", R.string.AutomaticMediaDownload), "mediaDownloadSectionRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4450lambda$new$43$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[44] = new SearchResult(this, 208, LocaleController.getString("WhenUsingMobileData", R.string.WhenUsingMobileData), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4451lambda$new$44$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[45] = new SearchResult(this, 209, LocaleController.getString("WhenConnectedOnWiFi", R.string.WhenConnectedOnWiFi), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda34
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4452lambda$new$45$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[46] = new SearchResult(this, 210, LocaleController.getString("WhenRoaming", R.string.WhenRoaming), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda35
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4453lambda$new$46$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[47] = new SearchResult(this, 211, LocaleController.getString("ResetAutomaticMediaDownload", R.string.ResetAutomaticMediaDownload), "resetDownloadRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda36
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4454lambda$new$47$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[48] = new SearchResult(this, 212, LocaleController.getString("AutoplayMedia", R.string.AutoplayMedia), "autoplayHeaderRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda37
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4455lambda$new$48$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[49] = new SearchResult(this, 213, LocaleController.getString("AutoplayGIF", R.string.AutoplayGIF), "autoplayGifsRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda38
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4456lambda$new$49$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[50] = new SearchResult(this, 214, LocaleController.getString("AutoplayVideo", R.string.AutoplayVideo), "autoplayVideoRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda40
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4458lambda$new$50$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[51] = new SearchResult(this, 215, LocaleController.getString("Streaming", R.string.Streaming), "streamSectionRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda41
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4459lambda$new$51$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[52] = new SearchResult(this, 216, LocaleController.getString("EnableStreaming", R.string.EnableStreaming), "enableStreamRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda42
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4460lambda$new$52$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[53] = new SearchResult(this, 217, LocaleController.getString("Calls", R.string.Calls), "callsSectionRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda43
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4461lambda$new$53$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[54] = new SearchResult(this, 218, LocaleController.getString("VoipUseLessData", R.string.VoipUseLessData), "useLessDataForCallsRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda45
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4462lambda$new$54$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[55] = new SearchResult(this, 219, LocaleController.getString("VoipQuickReplies", R.string.VoipQuickReplies), "quickRepliesRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda46
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4463lambda$new$55$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[56] = new SearchResult(this, 220, LocaleController.getString("ProxySettings", R.string.ProxySettings), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda47
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4464lambda$new$56$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[57] = new SearchResult(221, LocaleController.getString("UseProxyForCalls", R.string.UseProxyForCalls), "callsRow", LocaleController.getString("DataSettings", R.string.DataSettings), LocaleController.getString("ProxySettings", R.string.ProxySettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda48
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4465lambda$new$57$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[58] = new SearchResult(this, 111, LocaleController.getString("PrivacyDeleteCloudDrafts", R.string.PrivacyDeleteCloudDrafts), "clearDraftsRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg_data, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda49
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4466lambda$new$58$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[59] = new SearchResult(this, 300, LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda50
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4467lambda$new$59$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[60] = new SearchResult(this, 301, LocaleController.getString("TextSizeHeader", R.string.TextSizeHeader), "textSizeHeaderRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda52
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4469lambda$new$60$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[61] = new SearchResult(this, 302, LocaleController.getString("ChatBackground", R.string.ChatBackground), LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda53
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4470lambda$new$61$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[62] = new SearchResult(303, LocaleController.getString("SetColor", R.string.SetColor), null, LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("ChatBackground", R.string.ChatBackground), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda54
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4471lambda$new$62$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[63] = new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", R.string.ResetChatBackgrounds), "resetRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("ChatBackground", R.string.ChatBackground), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda56
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4472lambda$new$63$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[64] = new SearchResult(this, 305, LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda57
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4473lambda$new$64$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[65] = new SearchResult(this, 306, LocaleController.getString("ColorTheme", R.string.ColorTheme), "themeHeaderRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda58
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4474lambda$new$65$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[66] = new SearchResult(this, 307, LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), "customTabsRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda59
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4475lambda$new$66$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[67] = new SearchResult(this, 308, LocaleController.getString("DirectShare", R.string.DirectShare), "directShareRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda60
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4476lambda$new$67$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[68] = new SearchResult(this, 309, LocaleController.getString("EnableAnimations", R.string.EnableAnimations), "enableAnimationsRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda61
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4477lambda$new$68$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[69] = new SearchResult(this, 310, LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), "raiseToSpeakRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda62
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4478lambda$new$69$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[70] = new SearchResult(this, 311, LocaleController.getString("SendByEnter", R.string.SendByEnter), "sendByEnterRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda64
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4480lambda$new$70$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[71] = new SearchResult(this, 312, LocaleController.getString("SaveToGallerySettings", R.string.SaveToGallerySettings), "saveToGalleryRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda65
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4481lambda$new$71$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[72] = new SearchResult(this, 318, LocaleController.getString("DistanceUnits", R.string.DistanceUnits), "distanceRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda67
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4482lambda$new$72$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[73] = new SearchResult(this, 313, LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda68
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4483lambda$new$73$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[74] = new SearchResult(314, LocaleController.getString("SuggestStickers", R.string.SuggestStickers), "suggestRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda69
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4484lambda$new$74$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[75] = new SearchResult(315, LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers), null, LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda70
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4485lambda$new$75$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[76] = new SearchResult(316, LocaleController.getString("Masks", R.string.Masks), null, LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda71
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4486lambda$new$76$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[77] = new SearchResult(317, LocaleController.getString("ArchivedStickers", R.string.ArchivedStickers), null, LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda72
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4487lambda$new$77$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[78] = new SearchResult(317, LocaleController.getString("ArchivedMasks", R.string.ArchivedMasks), null, LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), R.drawable.msg_msgbubble3, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda73
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4488lambda$new$78$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[79] = new SearchResult(this, 400, LocaleController.getString("Language", R.string.Language), R.drawable.msg_language, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda74
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4489lambda$new$79$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[80] = new SearchResult(this, WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE, LocaleController.getString("AskAQuestion", R.string.AskAQuestion), LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.msg_help, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda76
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4491lambda$new$80$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[81] = new SearchResult(this, 403, LocaleController.getString("TelegramFAQ", R.string.TelegramFAQ), LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.msg_help, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda78
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4492lambda$new$81$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[82] = new SearchResult(this, WalletConstants.ERROR_CODE_INVALID_PARAMETERS, LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy), LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.msg_help, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda79
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4493lambda$new$82$orgtelegramuiProfileActivity$SearchAdapter();
                }
            });
            this.searchArray = searchResultArr;
            this.faqSearchArray = new ArrayList<>();
            this.resultNames = new ArrayList<>();
            this.searchResults = new ArrayList<>();
            this.faqSearchResults = new ArrayList<>();
            this.recentSearches = new ArrayList<>();
            this.mContext = context;
            HashMap<Integer, SearchResult> resultHashMap = new HashMap<>();
            int a = 0;
            while (true) {
                SearchResult[] searchResultArr2 = this.searchArray;
                if (a >= searchResultArr2.length) {
                    break;
                }
                if (searchResultArr2[a] != null) {
                    resultHashMap.put(Integer.valueOf(searchResultArr2[a].guid), this.searchArray[a]);
                }
                a++;
            }
            Set<String> set = MessagesController.getGlobalMainSettings().getStringSet("settingsSearchRecent2", null);
            if (set != null) {
                for (String value : set) {
                    try {
                        SerializedData data = new SerializedData(Utilities.hexToBytes(value));
                        int num = data.readInt32(false);
                        int type = data.readInt32(false);
                        if (type == 0) {
                            String title = data.readString(false);
                            int count = data.readInt32(false);
                            String[] path = null;
                            if (count > 0) {
                                path = new String[count];
                                for (int a2 = 0; a2 < count; a2++) {
                                    path[a2] = data.readString(false);
                                }
                            }
                            String url = data.readString(false);
                            MessagesController.FaqSearchResult result = new MessagesController.FaqSearchResult(title, path, url);
                            result.num = num;
                            this.recentSearches.add(result);
                        } else if (type == 1) {
                            try {
                                SearchResult result2 = resultHashMap.get(Integer.valueOf(data.readInt32(false)));
                                if (result2 != null) {
                                    result2.num = num;
                                    this.recentSearches.add(result2);
                                }
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e2) {
                    }
                }
            }
            Collections.sort(this.recentSearches, new Comparator() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda84
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return ProfileActivity.SearchAdapter.this.m4494lambda$new$83$orgtelegramuiProfileActivity$SearchAdapter(obj, obj2);
                }
            });
        }

        /* renamed from: lambda$new$83$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ int m4494lambda$new$83$orgtelegramuiProfileActivity$SearchAdapter(Object o1, Object o2) {
            int n1 = getNum(o1);
            int n2 = getNum(o2);
            if (n1 < n2) {
                return -1;
            }
            if (n1 > n2) {
                return 1;
            }
            return 0;
        }

        public void loadFaqWebPage() {
            TLRPC.WebPage webPage = ProfileActivity.this.getMessagesController().faqWebPage;
            this.faqWebPage = webPage;
            if (webPage != null) {
                this.faqSearchArray.addAll(ProfileActivity.this.getMessagesController().faqSearchArray);
            }
            if (this.faqWebPage != null || this.loadingFaqPage) {
                return;
            }
            this.loadingFaqPage = true;
            TLRPC.TL_messages_getWebPage req2 = new TLRPC.TL_messages_getWebPage();
            req2.url = LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl);
            req2.hash = 0;
            ProfileActivity.this.getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda85
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ProfileActivity.SearchAdapter.this.m4411xd152d752(tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$loadFaqWebPage$85$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4411xd152d752(TLObject response2, TLRPC.TL_error error2) {
            int N;
            int N2;
            String[] path;
            if (response2 instanceof TLRPC.WebPage) {
                final ArrayList<MessagesController.FaqSearchResult> arrayList = new ArrayList<>();
                TLRPC.WebPage page = (TLRPC.WebPage) response2;
                if (page.cached_page != null) {
                    int a = 0;
                    int N3 = page.cached_page.blocks.size();
                    while (a < N3) {
                        TLRPC.PageBlock block = page.cached_page.blocks.get(a);
                        if (block instanceof TLRPC.TL_pageBlockList) {
                            String paragraph = null;
                            if (a != 0) {
                                TLRPC.PageBlock prevBlock = page.cached_page.blocks.get(a - 1);
                                if (prevBlock instanceof TLRPC.TL_pageBlockParagraph) {
                                    TLRPC.TL_pageBlockParagraph pageBlockParagraph = (TLRPC.TL_pageBlockParagraph) prevBlock;
                                    paragraph = ArticleViewer.getPlainText(pageBlockParagraph.text).toString();
                                }
                            }
                            TLRPC.TL_pageBlockList list = (TLRPC.TL_pageBlockList) block;
                            int b = 0;
                            int N22 = list.items.size();
                            while (b < N22) {
                                TLRPC.PageListItem item = list.items.get(b);
                                if (!(item instanceof TLRPC.TL_pageListItemText)) {
                                    N2 = N3;
                                } else {
                                    TLRPC.TL_pageListItemText itemText = (TLRPC.TL_pageListItemText) item;
                                    String url = ArticleViewer.getUrl(itemText.text);
                                    String text = ArticleViewer.getPlainText(itemText.text).toString();
                                    if (TextUtils.isEmpty(url)) {
                                        N2 = N3;
                                    } else if (TextUtils.isEmpty(text)) {
                                        N2 = N3;
                                    } else {
                                        if (paragraph != null) {
                                            N2 = N3;
                                            path = new String[]{LocaleController.getString("SettingsSearchFaq", R.string.SettingsSearchFaq), paragraph};
                                        } else {
                                            N2 = N3;
                                            path = new String[]{LocaleController.getString("SettingsSearchFaq", R.string.SettingsSearchFaq)};
                                        }
                                        arrayList.add(new MessagesController.FaqSearchResult(text, path, url));
                                    }
                                }
                                b++;
                                N3 = N2;
                            }
                            N = N3;
                        } else {
                            N = N3;
                            if (block instanceof TLRPC.TL_pageBlockAnchor) {
                                break;
                            }
                        }
                        a++;
                        N3 = N;
                    }
                    this.faqWebPage = page;
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda83
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProfileActivity.SearchAdapter.this.m4410x5bd8b111(arrayList);
                    }
                });
            }
            this.loadingFaqPage = false;
        }

        /* renamed from: lambda$loadFaqWebPage$84$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4410x5bd8b111(ArrayList arrayList) {
            this.faqSearchArray.addAll(arrayList);
            ProfileActivity.this.getMessagesController().faqSearchArray = arrayList;
            ProfileActivity.this.getMessagesController().faqWebPage = this.faqWebPage;
            if (!this.searchWas) {
                notifyDataSetChanged();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int i = 0;
            if (this.searchWas) {
                int size = this.searchResults.size();
                if (!this.faqSearchResults.isEmpty()) {
                    i = this.faqSearchResults.size() + 1;
                }
                return size + i;
            }
            int size2 = this.recentSearches.isEmpty() ? 0 : this.recentSearches.size() + 1;
            if (!this.faqSearchArray.isEmpty()) {
                i = this.faqSearchArray.size() + 1;
            }
            return size2 + i;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int icon;
            switch (holder.getItemViewType()) {
                case 0:
                    SettingsSearchCell searchCell = (SettingsSearchCell) holder.itemView;
                    boolean z = false;
                    boolean z2 = true;
                    if (this.searchWas) {
                        if (position < this.searchResults.size()) {
                            SearchResult result = this.searchResults.get(position);
                            SearchResult prevResult = position > 0 ? this.searchResults.get(position - 1) : null;
                            if (prevResult == null || prevResult.iconResId != result.iconResId) {
                                icon = result.iconResId;
                            } else {
                                icon = 0;
                            }
                            CharSequence charSequence = this.resultNames.get(position);
                            String[] strArr = result.path;
                            if (position < this.searchResults.size() - 1) {
                                z = true;
                            }
                            searchCell.setTextAndValueAndIcon(charSequence, strArr, icon, z);
                            return;
                        }
                        int position2 = position - (this.searchResults.size() + 1);
                        CharSequence charSequence2 = this.resultNames.get(this.searchResults.size() + position2);
                        String[] strArr2 = this.faqSearchResults.get(position2).path;
                        if (position2 < this.searchResults.size() - 1) {
                            z = true;
                        }
                        searchCell.setTextAndValue(charSequence2, strArr2, true, z);
                        return;
                    }
                    if (!this.recentSearches.isEmpty()) {
                        position--;
                    }
                    if (position < this.recentSearches.size()) {
                        Object object = this.recentSearches.get(position);
                        if (object instanceof SearchResult) {
                            SearchResult result2 = (SearchResult) object;
                            String str = result2.searchTitle;
                            String[] strArr3 = result2.path;
                            if (position >= this.recentSearches.size() - 1) {
                                z2 = false;
                            }
                            searchCell.setTextAndValue(str, strArr3, false, z2);
                            return;
                        } else if (object instanceof MessagesController.FaqSearchResult) {
                            MessagesController.FaqSearchResult result3 = (MessagesController.FaqSearchResult) object;
                            String str2 = result3.title;
                            String[] strArr4 = result3.path;
                            if (position < this.recentSearches.size() - 1) {
                                z = true;
                            }
                            searchCell.setTextAndValue(str2, strArr4, true, z);
                            return;
                        } else {
                            return;
                        }
                    }
                    int position3 = position - (this.recentSearches.size() + 1);
                    MessagesController.FaqSearchResult result4 = this.faqSearchArray.get(position3);
                    String str3 = result4.title;
                    String[] strArr5 = result4.path;
                    if (position3 < this.recentSearches.size() - 1) {
                        z = true;
                    }
                    searchCell.setTextAndValue(str3, strArr5, true, z);
                    return;
                case 1:
                    GraySectionCell sectionCell = (GraySectionCell) holder.itemView;
                    sectionCell.setText(LocaleController.getString("SettingsFaqSearchTitle", R.string.SettingsFaqSearchTitle));
                    return;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    headerCell.setText(LocaleController.getString("SettingsRecent", R.string.SettingsRecent));
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new SettingsSearchCell(this.mContext);
                    break;
                case 1:
                    view = new GraySectionCell(this.mContext);
                    break;
                default:
                    view = new HeaderCell(this.mContext, 16);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (this.searchWas) {
                if (position >= this.searchResults.size() && position == this.searchResults.size()) {
                    return 1;
                }
            } else if (position == 0) {
                return !this.recentSearches.isEmpty() ? 2 : 1;
            } else if (!this.recentSearches.isEmpty() && position == this.recentSearches.size() + 1) {
                return 1;
            }
            return 0;
        }

        public void addRecent(Object object) {
            int index = this.recentSearches.indexOf(object);
            if (index >= 0) {
                this.recentSearches.remove(index);
            }
            this.recentSearches.add(0, object);
            if (!this.searchWas) {
                notifyDataSetChanged();
            }
            if (this.recentSearches.size() > 20) {
                ArrayList<Object> arrayList = this.recentSearches;
                arrayList.remove(arrayList.size() - 1);
            }
            LinkedHashSet<String> toSave = new LinkedHashSet<>();
            int N = this.recentSearches.size();
            for (int a = 0; a < N; a++) {
                Object o = this.recentSearches.get(a);
                if (o instanceof SearchResult) {
                    ((SearchResult) o).num = a;
                } else if (o instanceof MessagesController.FaqSearchResult) {
                    ((MessagesController.FaqSearchResult) o).num = a;
                }
                toSave.add(o.toString());
            }
            MessagesController.getGlobalMainSettings().edit().putStringSet("settingsSearchRecent2", toSave).commit();
        }

        public void clearRecent() {
            this.recentSearches.clear();
            MessagesController.getGlobalMainSettings().edit().remove("settingsSearchRecent2").commit();
            notifyDataSetChanged();
        }

        private int getNum(Object o) {
            if (o instanceof SearchResult) {
                return ((SearchResult) o).num;
            }
            if (o instanceof MessagesController.FaqSearchResult) {
                return ((MessagesController.FaqSearchResult) o).num;
            }
            return 0;
        }

        public void search(final String text) {
            this.lastSearchString = text;
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(text)) {
                this.searchWas = false;
                this.searchResults.clear();
                this.faqSearchResults.clear();
                this.resultNames.clear();
                ProfileActivity.this.emptyView.stickerView.getImageReceiver().startAnimation();
                ProfileActivity.this.emptyView.title.setText(LocaleController.getString("SettingsNoRecent", R.string.SettingsNoRecent));
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda81
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4497lambda$search$87$orgtelegramuiProfileActivity$SearchAdapter(text);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }

        /* renamed from: lambda$search$87$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4497lambda$search$87$orgtelegramuiProfileActivity$SearchAdapter(final String text) {
            String str;
            int N;
            String title;
            String str2;
            String title2;
            int index;
            final ArrayList<SearchResult> results = new ArrayList<>();
            final ArrayList<MessagesController.FaqSearchResult> faqResults = new ArrayList<>();
            final ArrayList<CharSequence> names = new ArrayList<>();
            String str3 = " ";
            String[] searchArgs = text.split(str3);
            String[] translitArgs = new String[searchArgs.length];
            for (int a = 0; a < searchArgs.length; a++) {
                translitArgs[a] = LocaleController.getInstance().getTranslitString(searchArgs[a]);
                if (translitArgs[a].equals(searchArgs[a])) {
                    translitArgs[a] = null;
                }
            }
            int a2 = 0;
            while (true) {
                SearchResult[] searchResultArr = this.searchArray;
                if (a2 >= searchResultArr.length) {
                    break;
                }
                SearchResult result = searchResultArr[a2];
                if (result != null) {
                    String title3 = str3 + result.searchTitle.toLowerCase();
                    SpannableStringBuilder stringBuilder = null;
                    int i = 0;
                    while (i < searchArgs.length) {
                        if (searchArgs[i].length() == 0) {
                            title2 = title3;
                        } else {
                            String searchString = searchArgs[i];
                            int index2 = title3.indexOf(str3 + searchString);
                            if (index2 < 0 && translitArgs[i] != null) {
                                searchString = translitArgs[i];
                                index = title3.indexOf(str3 + searchString);
                            } else {
                                index = index2;
                            }
                            if (index >= 0) {
                                if (stringBuilder != null) {
                                    title2 = title3;
                                } else {
                                    title2 = title3;
                                    stringBuilder = new SpannableStringBuilder(result.searchTitle);
                                }
                                stringBuilder.setSpan(new ForegroundColorSpan(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText4)), index, searchString.length() + index, 33);
                            }
                        }
                        if (stringBuilder != null && i == searchArgs.length - 1) {
                            if (result.guid == 502) {
                                int freeAccount = -1;
                                int b = 0;
                                while (true) {
                                    if (b < 4) {
                                        if (UserConfig.getInstance(a2).isClientActivated()) {
                                            b++;
                                        } else {
                                            freeAccount = b;
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                                if (freeAccount < 0) {
                                }
                            }
                            results.add(result);
                            names.add(stringBuilder);
                        }
                        i++;
                        title3 = title2;
                    }
                }
                a2++;
            }
            if (this.faqWebPage != null) {
                int a3 = 0;
                int N2 = this.faqSearchArray.size();
                while (a3 < N2) {
                    MessagesController.FaqSearchResult result2 = this.faqSearchArray.get(a3);
                    String title4 = str3 + result2.title.toLowerCase();
                    SpannableStringBuilder stringBuilder2 = null;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= searchArgs.length) {
                            str = str3;
                            N = N2;
                            break;
                        }
                        if (searchArgs[i2].length() == 0) {
                            str2 = str3;
                            N = N2;
                            title = title4;
                        } else {
                            String searchString2 = searchArgs[i2];
                            int index3 = title4.indexOf(str3 + searchString2);
                            if (index3 >= 0 || translitArgs[i2] == null) {
                                N = N2;
                            } else {
                                searchString2 = translitArgs[i2];
                                N = N2;
                                index3 = title4.indexOf(str3 + searchString2);
                            }
                            if (index3 < 0) {
                                str = str3;
                                break;
                            }
                            if (stringBuilder2 != null) {
                                str2 = str3;
                            } else {
                                str2 = str3;
                                stringBuilder2 = new SpannableStringBuilder(result2.title);
                            }
                            title = title4;
                            stringBuilder2.setSpan(new ForegroundColorSpan(ProfileActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText4)), index3, searchString2.length() + index3, 33);
                        }
                        if (stringBuilder2 != null && i2 == searchArgs.length - 1) {
                            faqResults.add(result2);
                            names.add(stringBuilder2);
                        }
                        i2++;
                        N2 = N;
                        str3 = str2;
                        title4 = title;
                    }
                    a3++;
                    N2 = N;
                    str3 = str;
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda82
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.m4496lambda$search$86$orgtelegramuiProfileActivity$SearchAdapter(text, results, faqResults, names);
                }
            });
        }

        /* renamed from: lambda$search$86$org-telegram-ui-ProfileActivity$SearchAdapter */
        public /* synthetic */ void m4496lambda$search$86$orgtelegramuiProfileActivity$SearchAdapter(String text, ArrayList results, ArrayList faqResults, ArrayList names) {
            if (!text.equals(this.lastSearchString)) {
                return;
            }
            if (!this.searchWas) {
                ProfileActivity.this.emptyView.stickerView.getImageReceiver().startAnimation();
                ProfileActivity.this.emptyView.title.setText(LocaleController.getString("SettingsNoResults", R.string.SettingsNoResults));
            }
            this.searchWas = true;
            this.searchResults = results;
            this.faqSearchResults = faqResults;
            this.resultNames = names;
            notifyDataSetChanged();
            ProfileActivity.this.emptyView.stickerView.getImageReceiver().startAnimation();
        }

        public boolean isSearchWas() {
            return this.searchWas;
        }
    }

    public void openUrl(String url) {
        if (url.startsWith("@")) {
            getMessagesController().openByUserName(url.substring(1), this, 0);
        } else if (url.startsWith("#")) {
            DialogsActivity fragment = new DialogsActivity(null);
            fragment.setSearchString(url);
            presentFragment(fragment);
        } else if (url.startsWith("/") && this.parentLayout.fragmentsStack.size() > 1) {
            BaseFragment previousFragment = this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2);
            if (previousFragment instanceof ChatActivity) {
                finishFragment();
                ((ChatActivity) previousFragment).chatActivityEnterView.setCommand(null, url, false, false);
            }
        }
    }

    private void dimBehindView(View view, boolean enable) {
        this.scrimView = view;
        dimBehindView(enable);
    }

    private void dimBehindView(View view, float value) {
        this.scrimView = view;
        dimBehindView(value);
    }

    private void dimBehindView(boolean enable) {
        dimBehindView(enable ? 0.2f : 0.0f);
    }

    private void dimBehindView(float value) {
        ValueAnimator scrimPaintAlphaAnimator;
        boolean enable = value > 0.0f;
        this.fragmentView.invalidate();
        AnimatorSet animatorSet = this.scrimAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.scrimAnimatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        if (enable) {
            Animator ofFloat = ValueAnimator.ofFloat(0.0f, value);
            scrimPaintAlphaAnimator = ofFloat;
            animators.add(ofFloat);
        } else {
            Animator ofFloat2 = ValueAnimator.ofFloat(this.scrimPaint.getAlpha() / 255.0f, 0.0f);
            scrimPaintAlphaAnimator = ofFloat2;
            animators.add(ofFloat2);
        }
        scrimPaintAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda11
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProfileActivity.this.m4369lambda$dimBehindView$39$orgtelegramuiProfileActivity(valueAnimator);
            }
        });
        this.scrimAnimatorSet.playTogether(animators);
        this.scrimAnimatorSet.setDuration(enable ? 150L : 220L);
        if (!enable) {
            this.scrimAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.38
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ProfileActivity.this.scrimView = null;
                    ProfileActivity.this.fragmentView.invalidate();
                }
            });
        }
        this.scrimAnimatorSet.start();
    }

    /* renamed from: lambda$dimBehindView$39$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4369lambda$dimBehindView$39$orgtelegramuiProfileActivity(ValueAnimator a) {
        this.scrimPaint.setAlpha((int) (((Float) a.getAnimatedValue()).floatValue() * 255.0f));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        if (this.resourcesProvider == null) {
            ThemeDescription.ThemeDescriptionDelegate themeDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda29
                @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
                public final void didSetColor() {
                    ProfileActivity.this.m4372lambda$getThemeDescriptions$40$orgtelegramuiProfileActivity();
                }

                @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
                public /* synthetic */ void onAnimationProgress(float f) {
                    ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
                }
            };
            ArrayList<ThemeDescription> arrayList = new ArrayList<>();
            SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
            if (sharedMediaLayout != null) {
                arrayList.addAll(sharedMediaLayout.getThemeDescriptions());
            }
            arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.searchListView, 0, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_windowBackgroundGray));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_actionBarDefaultIcon));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_actionBarSelectorBlue));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_chat_lockIcon));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_subtitleInProfileBlue));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundActionBarBlue));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_profile_title));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_profile_status));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_subtitleInProfileBlue));
            if (this.mediaCounterTextView != null) {
                arrayList.add(new ThemeDescription(this.mediaCounterTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, themeDelegate, Theme.key_player_actionBarSubtitle));
                arrayList.add(new ThemeDescription(this.mediaCounterTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, themeDelegate, Theme.key_player_actionBarSubtitle));
            }
            arrayList.add(new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
            arrayList.add(new ThemeDescription(this.avatarImage, 0, null, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
            arrayList.add(new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileBlue));
            arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon));
            arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground));
            arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGreenText2));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText2));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{SettingsSuggestionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_buttonText));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButton));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButtonPressed));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_buttonText));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButton));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButtonPressed));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_creatorIcon));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_windowBackgroundWhiteGrayText));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_windowBackgroundWhiteBlueText));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundRed));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundOrange));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundViolet));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundGreen));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundCyan));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundBlue));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundPink));
            arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
            arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
            arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
            arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
            arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
            arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
            arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, Theme.key_windowBackgroundWhiteLinkText));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
            arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
            arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
            arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
            arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
            if (this.mediaHeaderVisible) {
                arrayList.add(new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{this.verifiedCheckDrawable}, null, Theme.key_player_actionBarTitle));
                arrayList.add(new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{this.verifiedDrawable}, null, Theme.key_windowBackgroundWhite));
            } else {
                arrayList.add(new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{this.verifiedCheckDrawable}, null, Theme.key_profile_verifiedCheck));
                arrayList.add(new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{this.verifiedDrawable}, null, Theme.key_profile_verifiedBackground));
            }
            return arrayList;
        }
        return null;
    }

    /* renamed from: lambda$getThemeDescriptions$40$org-telegram-ui-ProfileActivity */
    public /* synthetic */ void m4372lambda$getThemeDescriptions$40$orgtelegramuiProfileActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
        if (!this.isPulledDown) {
            SimpleTextView[] simpleTextViewArr = this.onlineTextView;
            if (simpleTextViewArr[1] != null) {
                Object onlineTextViewTag = simpleTextViewArr[1].getTag();
                if (onlineTextViewTag instanceof String) {
                    this.onlineTextView[1].setTextColor(getThemedColor((String) onlineTextViewTag));
                } else {
                    this.onlineTextView[1].setTextColor(getThemedColor(Theme.key_avatar_subtitleInProfileBlue));
                }
            }
            Drawable drawable = this.lockIconDrawable;
            if (drawable != null) {
                drawable.setColorFilter(getThemedColor(Theme.key_chat_lockIcon), PorterDuff.Mode.MULTIPLY);
            }
            ScamDrawable scamDrawable = this.scamDrawable;
            if (scamDrawable != null) {
                scamDrawable.setColor(getThemedColor(Theme.key_avatar_subtitleInProfileBlue));
            }
            SimpleTextView[] simpleTextViewArr2 = this.nameTextView;
            if (simpleTextViewArr2[1] != null) {
                simpleTextViewArr2[1].setTextColor(getThemedColor(Theme.key_profile_title));
            }
            if (this.actionBar != null) {
                this.actionBar.setItemsColor(getThemedColor(Theme.key_actionBarDefaultIcon), false);
                this.actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_avatar_actionBarSelectorBlue), false);
            }
        }
    }

    public void updateListAnimated(boolean updateOnlineCount) {
        if (this.listAdapter == null) {
            if (updateOnlineCount) {
                updateOnlineCount(false);
            }
            updateRowsIds();
            return;
        }
        DiffCallback diffCallback = new DiffCallback();
        diffCallback.oldRowCount = this.rowCount;
        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        diffCallback.oldChatParticipant.clear();
        diffCallback.oldChatParticipantSorted.clear();
        diffCallback.oldChatParticipant.addAll(this.visibleChatParticipants);
        diffCallback.oldChatParticipantSorted.addAll(this.visibleSortedUsers);
        diffCallback.oldMembersStartRow = this.membersStartRow;
        diffCallback.oldMembersEndRow = this.membersEndRow;
        if (updateOnlineCount) {
            updateOnlineCount(false);
        }
        saveScrollPosition();
        updateRowsIds();
        diffCallback.fillPositions(diffCallback.newPositionToItem);
        try {
            DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(this.listAdapter);
        } catch (Exception e) {
            this.listAdapter.notifyDataSetChanged();
        }
        int i = this.savedScrollPosition;
        if (i >= 0) {
            this.layoutManager.scrollToPositionWithOffset(i, this.savedScrollOffset - this.listView.getPaddingTop());
        }
        AndroidUtilities.updateVisibleRows(this.listView);
    }

    public void saveScrollPosition() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && this.layoutManager != null && recyclerListView.getChildCount() > 0) {
            View view = null;
            int position = -1;
            int top = Integer.MAX_VALUE;
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                RecyclerListView recyclerListView2 = this.listView;
                int childPosition = recyclerListView2.getChildAdapterPosition(recyclerListView2.getChildAt(i));
                View child = this.listView.getChildAt(i);
                if (childPosition != -1 && child.getTop() < top) {
                    view = child;
                    position = childPosition;
                    top = child.getTop();
                }
            }
            if (view != null) {
                this.savedScrollPosition = position;
                int top2 = view.getTop();
                this.savedScrollOffset = top2;
                if (this.savedScrollPosition == 0 && !this.allowPullingDown && top2 > AndroidUtilities.dp(88.0f)) {
                    this.savedScrollOffset = AndroidUtilities.dp(88.0f);
                }
                this.layoutManager.scrollToPositionWithOffset(position, view.getTop() - this.listView.getPaddingTop());
            }
        }
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public void scrollToSharedMedia() {
        this.layoutManager.scrollToPositionWithOffset(this.sharedMediaRow, -this.listView.getPaddingTop());
    }

    public void onTextDetailCellImageClicked(View view) {
        View parent = (View) view.getParent();
        if (parent.getTag() != null && ((Integer) parent.getTag()).intValue() == this.usernameRow) {
            Bundle args = new Bundle();
            args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
            args.putLong("user_id", this.userId);
            presentFragment(new QrActivity(args));
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        try {
            Drawable shadowDrawable = this.fragmentView.getContext().getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor(Theme.key_profile_actionBackground), getThemedColor(Theme.key_profile_actionPressedBackground)), 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            this.writeButton.setBackground(combinedDrawable);
        } catch (Exception e) {
        }
    }

    private boolean isQrNeedVisible() {
        if (!TextUtils.isEmpty(getUserConfig().getCurrentUser().username)) {
            return true;
        }
        ArrayList<TLRPC.PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(6);
        if (privacyRules == null) {
            return false;
        }
        int type = 2;
        int i = 0;
        while (true) {
            if (i >= privacyRules.size()) {
                break;
            }
            TLRPC.PrivacyRule rule = privacyRules.get(i);
            if (rule instanceof TLRPC.TL_privacyValueAllowAll) {
                type = 0;
                break;
            } else if (rule instanceof TLRPC.TL_privacyValueDisallowAll) {
                type = 2;
                break;
            } else if (!(rule instanceof TLRPC.TL_privacyValueAllowContacts)) {
                i++;
            } else {
                type = 1;
                break;
            }
        }
        return type == 0 || type == 1;
    }

    /* loaded from: classes4.dex */
    public class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        ArrayList<TLRPC.ChatParticipant> oldChatParticipant;
        ArrayList<Integer> oldChatParticipantSorted;
        int oldMembersEndRow;
        int oldMembersStartRow;
        SparseIntArray oldPositionToItem;
        int oldRowCount;

        private DiffCallback() {
            ProfileActivity.this = r1;
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldChatParticipant = new ArrayList<>();
            this.oldChatParticipantSorted = new ArrayList<>();
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getOldListSize() {
            return this.oldRowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getNewListSize() {
            return ProfileActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            TLRPC.ChatParticipant oldItem;
            if (newItemPosition >= ProfileActivity.this.membersStartRow && newItemPosition < ProfileActivity.this.membersEndRow && oldItemPosition >= this.oldMembersStartRow && oldItemPosition < this.oldMembersEndRow) {
                if (!this.oldChatParticipantSorted.isEmpty()) {
                    oldItem = this.oldChatParticipant.get(this.oldChatParticipantSorted.get(oldItemPosition - this.oldMembersStartRow).intValue());
                } else {
                    oldItem = this.oldChatParticipant.get(oldItemPosition - this.oldMembersStartRow);
                }
                TLRPC.ChatParticipant newItem = !ProfileActivity.this.sortedUsers.isEmpty() ? (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(((Integer) ProfileActivity.this.visibleSortedUsers.get(newItemPosition - ProfileActivity.this.membersStartRow)).intValue()) : (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(newItemPosition - ProfileActivity.this.membersStartRow);
                return oldItem.user_id == newItem.user_id;
            }
            int oldIndex = this.oldPositionToItem.get(oldItemPosition, -1);
            int newIndex = this.newPositionToItem.get(newItemPosition, -1);
            return oldIndex == newIndex && oldIndex >= 0;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return areItemsTheSame(oldItemPosition, newItemPosition);
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            int pointer = 0 + 1;
            put(pointer, ProfileActivity.this.setAvatarRow, sparseIntArray);
            int pointer2 = pointer + 1;
            put(pointer2, ProfileActivity.this.setAvatarSectionRow, sparseIntArray);
            int pointer3 = pointer2 + 1;
            put(pointer3, ProfileActivity.this.numberSectionRow, sparseIntArray);
            int pointer4 = pointer3 + 1;
            put(pointer4, ProfileActivity.this.numberRow, sparseIntArray);
            int pointer5 = pointer4 + 1;
            put(pointer5, ProfileActivity.this.setUsernameRow, sparseIntArray);
            int pointer6 = pointer5 + 1;
            put(pointer6, ProfileActivity.this.bioRow, sparseIntArray);
            int pointer7 = pointer6 + 1;
            put(pointer7, ProfileActivity.this.phoneSuggestionRow, sparseIntArray);
            int pointer8 = pointer7 + 1;
            put(pointer8, ProfileActivity.this.phoneSuggestionSectionRow, sparseIntArray);
            int pointer9 = pointer8 + 1;
            put(pointer9, ProfileActivity.this.passwordSuggestionRow, sparseIntArray);
            int pointer10 = pointer9 + 1;
            put(pointer10, ProfileActivity.this.passwordSuggestionSectionRow, sparseIntArray);
            int pointer11 = pointer10 + 1;
            put(pointer11, ProfileActivity.this.settingsSectionRow, sparseIntArray);
            int pointer12 = pointer11 + 1;
            put(pointer12, ProfileActivity.this.settingsSectionRow2, sparseIntArray);
            int pointer13 = pointer12 + 1;
            put(pointer13, ProfileActivity.this.notificationRow, sparseIntArray);
            int pointer14 = pointer13 + 1;
            put(pointer14, ProfileActivity.this.languageRow, sparseIntArray);
            int pointer15 = pointer14 + 1;
            put(pointer15, ProfileActivity.this.premiumRow, sparseIntArray);
            int pointer16 = pointer15 + 1;
            put(pointer16, ProfileActivity.this.premiumSectionsRow, sparseIntArray);
            int pointer17 = pointer16 + 1;
            put(pointer17, ProfileActivity.this.privacyRow, sparseIntArray);
            int pointer18 = pointer17 + 1;
            put(pointer18, ProfileActivity.this.dataRow, sparseIntArray);
            int pointer19 = pointer18 + 1;
            put(pointer19, ProfileActivity.this.chatRow, sparseIntArray);
            int pointer20 = pointer19 + 1;
            put(pointer20, ProfileActivity.this.filtersRow, sparseIntArray);
            int pointer21 = pointer20 + 1;
            put(pointer21, ProfileActivity.this.stickersRow, sparseIntArray);
            int pointer22 = pointer21 + 1;
            put(pointer22, ProfileActivity.this.devicesRow, sparseIntArray);
            int pointer23 = pointer22 + 1;
            put(pointer23, ProfileActivity.this.devicesSectionRow, sparseIntArray);
            int pointer24 = pointer23 + 1;
            put(pointer24, ProfileActivity.this.helpHeaderRow, sparseIntArray);
            int pointer25 = pointer24 + 1;
            put(pointer25, ProfileActivity.this.questionRow, sparseIntArray);
            int pointer26 = pointer25 + 1;
            put(pointer26, ProfileActivity.this.faqRow, sparseIntArray);
            int pointer27 = pointer26 + 1;
            put(pointer27, ProfileActivity.this.policyRow, sparseIntArray);
            int pointer28 = pointer27 + 1;
            put(pointer28, ProfileActivity.this.helpSectionCell, sparseIntArray);
            int pointer29 = pointer28 + 1;
            put(pointer29, ProfileActivity.this.debugHeaderRow, sparseIntArray);
            int pointer30 = pointer29 + 1;
            put(pointer30, ProfileActivity.this.sendLogsRow, sparseIntArray);
            int pointer31 = pointer30 + 1;
            put(pointer31, ProfileActivity.this.sendLastLogsRow, sparseIntArray);
            int pointer32 = pointer31 + 1;
            put(pointer32, ProfileActivity.this.clearLogsRow, sparseIntArray);
            int pointer33 = pointer32 + 1;
            put(pointer33, ProfileActivity.this.switchBackendRow, sparseIntArray);
            int pointer34 = pointer33 + 1;
            put(pointer34, ProfileActivity.this.versionRow, sparseIntArray);
            int pointer35 = pointer34 + 1;
            put(pointer35, ProfileActivity.this.emptyRow, sparseIntArray);
            int pointer36 = pointer35 + 1;
            put(pointer36, ProfileActivity.this.bottomPaddingRow, sparseIntArray);
            int pointer37 = pointer36 + 1;
            put(pointer37, ProfileActivity.this.infoHeaderRow, sparseIntArray);
            int pointer38 = pointer37 + 1;
            put(pointer38, ProfileActivity.this.phoneRow, sparseIntArray);
            int pointer39 = pointer38 + 1;
            put(pointer39, ProfileActivity.this.locationRow, sparseIntArray);
            int pointer40 = pointer39 + 1;
            put(pointer40, ProfileActivity.this.userInfoRow, sparseIntArray);
            int pointer41 = pointer40 + 1;
            put(pointer41, ProfileActivity.this.channelInfoRow, sparseIntArray);
            int pointer42 = pointer41 + 1;
            put(pointer42, ProfileActivity.this.usernameRow, sparseIntArray);
            int pointer43 = pointer42 + 1;
            put(pointer43, ProfileActivity.this.notificationsDividerRow, sparseIntArray);
            int pointer44 = pointer43 + 1;
            put(pointer44, ProfileActivity.this.notificationsRow, sparseIntArray);
            int pointer45 = pointer44 + 1;
            put(pointer45, ProfileActivity.this.infoSectionRow, sparseIntArray);
            int pointer46 = pointer45 + 1;
            put(pointer46, ProfileActivity.this.sendMessageRow, sparseIntArray);
            int pointer47 = pointer46 + 1;
            put(pointer47, ProfileActivity.this.reportRow, sparseIntArray);
            int pointer48 = pointer47 + 1;
            put(pointer48, ProfileActivity.this.settingsTimerRow, sparseIntArray);
            int pointer49 = pointer48 + 1;
            put(pointer49, ProfileActivity.this.settingsKeyRow, sparseIntArray);
            int pointer50 = pointer49 + 1;
            put(pointer50, ProfileActivity.this.secretSettingsSectionRow, sparseIntArray);
            int pointer51 = pointer50 + 1;
            put(pointer51, ProfileActivity.this.membersHeaderRow, sparseIntArray);
            int pointer52 = pointer51 + 1;
            put(pointer52, ProfileActivity.this.addMemberRow, sparseIntArray);
            int pointer53 = pointer52 + 1;
            put(pointer53, ProfileActivity.this.subscribersRow, sparseIntArray);
            int pointer54 = pointer53 + 1;
            put(pointer54, ProfileActivity.this.subscribersRequestsRow, sparseIntArray);
            int pointer55 = pointer54 + 1;
            put(pointer55, ProfileActivity.this.administratorsRow, sparseIntArray);
            int pointer56 = pointer55 + 1;
            put(pointer56, ProfileActivity.this.blockedUsersRow, sparseIntArray);
            int pointer57 = pointer56 + 1;
            put(pointer57, ProfileActivity.this.membersSectionRow, sparseIntArray);
            int pointer58 = pointer57 + 1;
            put(pointer58, ProfileActivity.this.sharedMediaRow, sparseIntArray);
            int pointer59 = pointer58 + 1;
            put(pointer59, ProfileActivity.this.unblockRow, sparseIntArray);
            int pointer60 = pointer59 + 1;
            put(pointer60, ProfileActivity.this.addToGroupButtonRow, sparseIntArray);
            int pointer61 = pointer60 + 1;
            put(pointer61, ProfileActivity.this.addToGroupInfoRow, sparseIntArray);
            int pointer62 = pointer61 + 1;
            put(pointer62, ProfileActivity.this.joinRow, sparseIntArray);
            put(pointer62 + 1, ProfileActivity.this.lastSectionRow, sparseIntArray);
        }

        private void put(int id, int position, SparseIntArray sparseIntArray) {
            if (position >= 0) {
                sparseIntArray.put(position, id);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        int color;
        if (this.isPulledDown) {
            return false;
        }
        if (this.actionBar.isActionModeShowed()) {
            color = getThemedColor(Theme.key_actionBarActionModeDefault);
        } else if (this.mediaHeaderVisible) {
            color = getThemedColor(Theme.key_windowBackgroundWhite);
        } else {
            color = getThemedColor(Theme.key_actionBarDefault);
        }
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }
}
