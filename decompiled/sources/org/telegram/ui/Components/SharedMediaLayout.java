package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.net.MailTo;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.CalendarActivity;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedMediaSectionCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell2;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatReactionsEditActivity;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;
/* loaded from: classes5.dex */
public class SharedMediaLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public static final int FILTER_PHOTOS_AND_VIDEOS = 0;
    public static final int FILTER_PHOTOS_ONLY = 1;
    public static final int FILTER_VIDEOS_ONLY = 2;
    public static final int VIEW_TYPE_MEDIA_ACTIVITY = 0;
    public static final int VIEW_TYPE_PROFILE_ACTIVITY = 1;
    private static final int delete = 101;
    private static final int forward = 100;
    private static final int gotochat = 102;
    private ActionBar actionBar;
    private AnimatorSet actionModeAnimation;
    private LinearLayout actionModeLayout;
    private float additionalFloatingTranslation;
    private int animateToColumnsCount;
    private boolean animatingForward;
    int animationIndex;
    private SharedPhotoVideoAdapter animationSupportingPhotoVideoAdapter;
    private SharedDocumentsAdapter audioAdapter;
    private MediaSearchAdapter audioSearchAdapter;
    private boolean backAnimation;
    private BackDrawable backDrawable;
    private int cantDeleteMessagesCount;
    private boolean changeTypeAnimation;
    private ChatUsersAdapter chatUsersAdapter;
    private ImageView closeButton;
    private CommonGroupsAdapter commonGroupsAdapter;
    final Delegate delegate;
    private ActionBarMenuItem deleteItem;
    private long dialog_id;
    private SharedDocumentsAdapter documentsAdapter;
    private MediaSearchAdapter documentsSearchAdapter;
    private AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private ActionBarMenuItem forwardItem;
    private FragmentContextView fragmentContextView;
    private HintView fwdRestrictedHint;
    private GifAdapter gifAdapter;
    FlickerLoadingView globalGradientView;
    private ActionBarMenuItem gotoItem;
    private GroupUsersSearchAdapter groupUsersSearchAdapter;
    private int[] hasMedia;
    private boolean ignoreSearchCollapse;
    private TLRPC.ChatFull info;
    private int initialTab;
    private boolean isActionModeShowed;
    boolean isInPinchToZoomTouchMode;
    boolean isPinnedToTop;
    Runnable jumpToRunnable;
    int lastMeasuredTopPadding;
    private SharedLinksAdapter linksAdapter;
    private MediaSearchAdapter linksSearchAdapter;
    private int maximumVelocity;
    boolean maybePinchToZoomTouchMode;
    boolean maybePinchToZoomTouchMode2;
    private boolean maybeStartTracking;
    private int mediaColumnsCount;
    private long mergeDialogId;
    ActionBarPopupWindow optionsWindow;
    private SharedPhotoVideoAdapter photoVideoAdapter;
    private boolean photoVideoChangeColumnsAnimation;
    private float photoVideoChangeColumnsProgress;
    public ImageView photoVideoOptionsItem;
    int pinchCenterOffset;
    int pinchCenterPosition;
    int pinchCenterX;
    int pinchCenterY;
    float pinchScale;
    boolean pinchScaleUp;
    float pinchStartDistance;
    private Drawable pinnedHeaderShadowDrawable;
    private int pointerId1;
    private int pointerId2;
    private BaseFragment profileActivity;
    private Theme.ResourcesProvider resourcesProvider;
    private ScrollSlidingTextTabStripInner scrollSlidingTextTabStrip;
    private boolean scrolling;
    public boolean scrollingByUser;
    private ActionBarMenuItem searchItem;
    private int searchItemState;
    private boolean searchWas;
    private boolean searching;
    private NumberTextView selectedMessagesCountTextView;
    private View shadowLine;
    private SharedMediaPreloader sharedMediaPreloader;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    int topPadding;
    private UndoView undoView;
    private VelocityTracker velocityTracker;
    private final int viewType;
    private SharedDocumentsAdapter voiceAdapter;
    private static final int[] supportedFastScrollTypes = {0, 1, 2, 4};
    private static final Interpolator interpolator = SharedMediaLayout$$ExternalSyntheticLambda13.INSTANCE;
    android.graphics.Rect rect = new android.graphics.Rect();
    private MediaPage[] mediaPages = new MediaPage[2];
    private ArrayList<SharedPhotoVideoCell> cellCache = new ArrayList<>(10);
    private ArrayList<SharedPhotoVideoCell> cache = new ArrayList<>(10);
    private ArrayList<SharedAudioCell> audioCellCache = new ArrayList<>(10);
    private ArrayList<SharedAudioCell> audioCache = new ArrayList<>(10);
    private Runnable hideFloatingDateRunnable = new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda15
        @Override // java.lang.Runnable
        public final void run() {
            SharedMediaLayout.this.m3029lambda$new$0$orgtelegramuiComponentsSharedMediaLayout();
        }
    };
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private Paint backgroundPaint = new Paint();
    private SparseArray<MessageObject>[] selectedFiles = {new SparseArray<>(), new SparseArray<>()};
    private ArrayList<SharedPhotoVideoCell2> animationSupportingSortedCells = new ArrayList<>();
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.Components.SharedMediaLayout.1
        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            View pinnedHeader;
            SharedLinkCell cell;
            MessageObject message;
            if (messageObject != null) {
                char c = 0;
                if (SharedMediaLayout.this.mediaPages[0].selectedType != 0 && SharedMediaLayout.this.mediaPages[0].selectedType != 1 && SharedMediaLayout.this.mediaPages[0].selectedType != 3 && SharedMediaLayout.this.mediaPages[0].selectedType != 5) {
                    return null;
                }
                RecyclerListView listView = SharedMediaLayout.this.mediaPages[0].listView;
                int firstVisiblePosition = -1;
                int lastVisiblePosition = -1;
                int a = 0;
                int count = listView.getChildCount();
                while (a < count) {
                    View view = listView.getChildAt(a);
                    int visibleHeight = SharedMediaLayout.this.mediaPages[c].listView.getMeasuredHeight();
                    View parent = (View) SharedMediaLayout.this.getParent();
                    if (parent != null && SharedMediaLayout.this.getY() + SharedMediaLayout.this.getMeasuredHeight() > parent.getMeasuredHeight()) {
                        visibleHeight -= SharedMediaLayout.this.getBottom() - parent.getMeasuredHeight();
                    }
                    if (view.getTop() < visibleHeight) {
                        int adapterPosition = listView.getChildAdapterPosition(view);
                        if (adapterPosition < firstVisiblePosition || firstVisiblePosition == -1) {
                            firstVisiblePosition = adapterPosition;
                        }
                        if (adapterPosition > lastVisiblePosition || lastVisiblePosition == -1) {
                            lastVisiblePosition = adapterPosition;
                        }
                        int[] coords = new int[2];
                        ImageReceiver imageReceiver = null;
                        if (view instanceof SharedPhotoVideoCell2) {
                            SharedPhotoVideoCell2 cell2 = (SharedPhotoVideoCell2) view;
                            MessageObject message2 = cell2.getMessageObject();
                            if (message2 == null) {
                                continue;
                            } else if (message2.getId() == messageObject.getId()) {
                                imageReceiver = cell2.imageReceiver;
                                cell2.getLocationInWindow(coords);
                                coords[c] = coords[c] + Math.round(cell2.imageReceiver.getImageX());
                                coords[1] = coords[1] + Math.round(cell2.imageReceiver.getImageY());
                            }
                        } else if (view instanceof SharedDocumentCell) {
                            SharedDocumentCell cell3 = (SharedDocumentCell) view;
                            if (cell3.getMessage().getId() == messageObject.getId()) {
                                BackupImageView imageView = cell3.getImageView();
                                imageReceiver = imageView.getImageReceiver();
                                imageView.getLocationInWindow(coords);
                            }
                        } else if (view instanceof ContextLinkCell) {
                            ContextLinkCell cell4 = (ContextLinkCell) view;
                            MessageObject message3 = (MessageObject) cell4.getParentObject();
                            if (message3 != null && message3.getId() == messageObject.getId()) {
                                imageReceiver = cell4.getPhotoImage();
                                cell4.getLocationInWindow(coords);
                            }
                        } else if ((view instanceof SharedLinkCell) && (message = (cell = (SharedLinkCell) view).getMessage()) != null && message.getId() == messageObject.getId()) {
                            imageReceiver = cell.getLinkImageView();
                            cell.getLocationInWindow(coords);
                        }
                        if (imageReceiver != null) {
                            PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                            object.viewX = coords[0];
                            object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                            object.parentView = listView;
                            object.animatingImageView = SharedMediaLayout.this.mediaPages[0].animatingImageView;
                            SharedMediaLayout.this.mediaPages[0].listView.getLocationInWindow(coords);
                            object.animatingImageViewYOffset = -coords[1];
                            object.imageReceiver = imageReceiver;
                            object.allowTakeAnimation = false;
                            object.radius = object.imageReceiver.getRoundRadius();
                            object.thumb = object.imageReceiver.getBitmapSafe();
                            object.parentView.getLocationInWindow(coords);
                            object.clipTopAddition = 0;
                            object.starOffset = SharedMediaLayout.this.sharedMediaData[0].startOffset;
                            if (SharedMediaLayout.this.fragmentContextView != null && SharedMediaLayout.this.fragmentContextView.getVisibility() == 0) {
                                object.clipTopAddition += AndroidUtilities.dp(36.0f);
                            }
                            if (PhotoViewer.isShowingImage(messageObject) && (pinnedHeader = listView.getPinnedHeader()) != null) {
                                int top = 0;
                                if (SharedMediaLayout.this.fragmentContextView != null && SharedMediaLayout.this.fragmentContextView.getVisibility() == 0) {
                                    top = 0 + (SharedMediaLayout.this.fragmentContextView.getHeight() - AndroidUtilities.dp(2.5f));
                                }
                                if (view instanceof SharedDocumentCell) {
                                    top += AndroidUtilities.dp(8.0f);
                                }
                                int topOffset = top - object.viewY;
                                if (topOffset > view.getHeight()) {
                                    listView.scrollBy(0, -(pinnedHeader.getHeight() + topOffset));
                                } else {
                                    int bottomOffset = object.viewY - listView.getHeight();
                                    if (view instanceof SharedDocumentCell) {
                                        bottomOffset -= AndroidUtilities.dp(8.0f);
                                    }
                                    if (bottomOffset >= 0) {
                                        listView.scrollBy(0, view.getHeight() + bottomOffset);
                                    }
                                }
                            }
                            return object;
                        }
                    }
                    a++;
                    c = 0;
                }
                if (SharedMediaLayout.this.mediaPages[0].selectedType == 0 && firstVisiblePosition >= 0 && lastVisiblePosition >= 0) {
                    int position = SharedMediaLayout.this.photoVideoAdapter.getPositionForIndex(index);
                    if (position <= firstVisiblePosition) {
                        SharedMediaLayout.this.mediaPages[0].layoutManager.scrollToPositionWithOffset(position, 0);
                        SharedMediaLayout.this.delegate.scrollToSharedMedia();
                        return null;
                    } else if (position >= lastVisiblePosition && lastVisiblePosition >= 0) {
                        SharedMediaLayout.this.mediaPages[0].layoutManager.scrollToPositionWithOffset(position, 0, true);
                        SharedMediaLayout.this.delegate.scrollToSharedMedia();
                        return null;
                    } else {
                        return null;
                    }
                }
                return null;
            }
            return null;
        }
    };
    private SharedMediaData[] sharedMediaData = new SharedMediaData[6];
    SparseArray<Float> messageAlphaEnter = new SparseArray<>();
    SharedLinkCell.SharedLinkCellDelegate sharedLinkCellDelegate = new AnonymousClass30();

    /* loaded from: classes5.dex */
    public interface Delegate {
        boolean canSearchMembers();

        TLRPC.Chat getCurrentChat();

        RecyclerListView getListView();

        boolean isFragmentOpened();

        boolean onMemberClick(TLRPC.ChatParticipant chatParticipant, boolean z, boolean z2);

        void scrollToSharedMedia();

        void updateSelectedMediaTabText();
    }

    /* loaded from: classes5.dex */
    public interface SharedMediaPreloaderDelegate {
        void mediaCountUpdated();
    }

    public boolean isInFastScroll() {
        MediaPage[] mediaPageArr = this.mediaPages;
        return (mediaPageArr[0] == null || mediaPageArr[0].listView.getFastScroll() == null || !this.mediaPages[0].listView.getFastScroll().isPressed()) ? false : true;
    }

    public boolean dispatchFastScrollEvent(MotionEvent ev) {
        View view = (View) getParent();
        ev.offsetLocation(((-view.getX()) - getX()) - this.mediaPages[0].listView.getFastScroll().getX(), (((-view.getY()) - getY()) - this.mediaPages[0].getY()) - this.mediaPages[0].listView.getFastScroll().getY());
        return this.mediaPages[0].listView.getFastScroll().dispatchTouchEvent(ev);
    }

    public boolean checkPinchToZoom(MotionEvent ev) {
        int i;
        if (this.mediaPages[0].selectedType != 0 || getParent() == null) {
            return false;
        }
        if (this.photoVideoChangeColumnsAnimation && !this.isInPinchToZoomTouchMode) {
            return true;
        }
        if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
            if (this.maybePinchToZoomTouchMode && !this.isInPinchToZoomTouchMode && ev.getPointerCount() == 2) {
                this.pinchStartDistance = (float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0));
                this.pinchScale = 1.0f;
                this.pointerId1 = ev.getPointerId(0);
                this.pointerId2 = ev.getPointerId(1);
                this.mediaPages[0].listView.cancelClickRunnables(false);
                this.mediaPages[0].listView.cancelLongPress();
                this.mediaPages[0].listView.dispatchTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
                View view = (View) getParent();
                this.pinchCenterX = (int) (((((int) ((ev.getX(0) + ev.getX(1)) / 2.0f)) - view.getX()) - getX()) - this.mediaPages[0].getX());
                int y = (int) (((((int) ((ev.getY(0) + ev.getY(1)) / 2.0f)) - view.getY()) - getY()) - this.mediaPages[0].getY());
                this.pinchCenterY = y;
                selectPinchPosition(this.pinchCenterX, y);
                this.maybePinchToZoomTouchMode2 = true;
            }
            if (ev.getActionMasked() == 0) {
                float y2 = ((ev.getY() - ((View) getParent()).getY()) - getY()) - this.mediaPages[0].getY();
                if (y2 > 0.0f) {
                    this.maybePinchToZoomTouchMode = true;
                }
            }
        } else if (ev.getActionMasked() == 2 && (this.isInPinchToZoomTouchMode || this.maybePinchToZoomTouchMode2)) {
            int index1 = -1;
            int index2 = -1;
            for (int i2 = 0; i2 < ev.getPointerCount(); i2++) {
                if (this.pointerId1 == ev.getPointerId(i2)) {
                    index1 = i2;
                }
                if (this.pointerId2 == ev.getPointerId(i2)) {
                    index2 = i2;
                }
            }
            if (index1 == -1 || index2 == -1) {
                this.maybePinchToZoomTouchMode = false;
                this.maybePinchToZoomTouchMode2 = false;
                this.isInPinchToZoomTouchMode = false;
                finishPinchToMediaColumnsCount();
                return false;
            }
            float hypot = ((float) Math.hypot(ev.getX(index2) - ev.getX(index1), ev.getY(index2) - ev.getY(index1))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            if (!this.isInPinchToZoomTouchMode && (hypot > 1.01f || hypot < 0.99f)) {
                this.isInPinchToZoomTouchMode = true;
                boolean z = hypot > 1.0f;
                this.pinchScaleUp = z;
                startPinchToMediaColumnsCount(z);
            }
            if (this.isInPinchToZoomTouchMode) {
                boolean z2 = this.pinchScaleUp;
                if ((!z2 || this.pinchScale >= 1.0f) && (z2 || this.pinchScale <= 1.0f)) {
                    this.photoVideoChangeColumnsProgress = Math.max(0.0f, Math.min(1.0f, z2 ? 1.0f - ((2.0f - this.pinchScale) / 1.0f) : (1.0f - this.pinchScale) / 0.5f));
                } else {
                    this.photoVideoChangeColumnsProgress = 0.0f;
                }
                float f = this.photoVideoChangeColumnsProgress;
                if (f == 1.0f || f == 0.0f) {
                    if (f == 1.0f) {
                        int newRow = (int) Math.ceil(this.pinchCenterPosition / this.animateToColumnsCount);
                        int columnWidth = (int) (this.mediaPages[0].listView.getMeasuredWidth() / this.animateToColumnsCount);
                        int newColumn = (int) ((this.startedTrackingX / (this.mediaPages[0].listView.getMeasuredWidth() - columnWidth)) * (i - 1));
                        int newPosition = (this.animateToColumnsCount * newRow) + newColumn;
                        if (newPosition >= this.photoVideoAdapter.getItemCount()) {
                            newPosition = this.photoVideoAdapter.getItemCount() - 1;
                        }
                        this.pinchCenterPosition = newPosition;
                    }
                    finishPinchToMediaColumnsCount();
                    if (this.photoVideoChangeColumnsProgress == 0.0f) {
                        this.pinchScaleUp = !this.pinchScaleUp;
                    }
                    startPinchToMediaColumnsCount(this.pinchScaleUp);
                    this.pinchStartDistance = (float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0));
                }
                this.mediaPages[0].listView.invalidate();
                if (this.mediaPages[0].fastScrollHintView != null) {
                    this.mediaPages[0].invalidate();
                }
            }
        } else {
            int index12 = ev.getActionMasked();
            if ((index12 == 1 || ((ev.getActionMasked() == 6 && checkPointerIds(ev)) || ev.getActionMasked() == 3)) && this.isInPinchToZoomTouchMode) {
                this.maybePinchToZoomTouchMode2 = false;
                this.maybePinchToZoomTouchMode = false;
                this.isInPinchToZoomTouchMode = false;
                finishPinchToMediaColumnsCount();
            }
        }
        return this.isInPinchToZoomTouchMode;
    }

    private void selectPinchPosition(int pinchCenterX, int pinchCenterY) {
        this.pinchCenterPosition = -1;
        int y = this.mediaPages[0].listView.blurTopPadding + pinchCenterY;
        if (getY() != 0.0f && this.viewType == 1) {
            y = 0;
        }
        for (int i = 0; i < this.mediaPages[0].listView.getChildCount(); i++) {
            View child = this.mediaPages[0].listView.getChildAt(i);
            child.getHitRect(this.rect);
            if (this.rect.contains(pinchCenterX, y)) {
                this.pinchCenterPosition = this.mediaPages[0].listView.getChildLayoutPosition(child);
                this.pinchCenterOffset = child.getTop();
            }
        }
        if (this.delegate.canSearchMembers() && this.pinchCenterPosition == -1) {
            float x = Math.min(1.0f, Math.max(pinchCenterX / this.mediaPages[0].listView.getMeasuredWidth(), 0.0f));
            this.pinchCenterPosition = (int) (this.mediaPages[0].layoutManager.findFirstVisibleItemPosition() + ((this.mediaColumnsCount - 1) * x));
            this.pinchCenterOffset = 0;
        }
    }

    private boolean checkPointerIds(MotionEvent ev) {
        if (ev.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == ev.getPointerId(0) && this.pointerId2 == ev.getPointerId(1)) {
            return true;
        }
        return this.pointerId1 == ev.getPointerId(1) && this.pointerId2 == ev.getPointerId(0);
    }

    public boolean isSwipeBackEnabled() {
        return !this.photoVideoChangeColumnsAnimation && !this.tabsAnimationInProgress;
    }

    public int getPhotosVideosTypeFilter() {
        return this.sharedMediaData[0].filterType;
    }

    public boolean isPinnedToTop() {
        return this.isPinnedToTop;
    }

    public void setPinnedToTop(boolean pinnedToTop) {
        if (this.isPinnedToTop != pinnedToTop) {
            this.isPinnedToTop = pinnedToTop;
            int i = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i < mediaPageArr.length) {
                    updateFastScrollVisibility(mediaPageArr[i], true);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public void drawListForBlur(Canvas blurCanvas) {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                if (mediaPageArr[i] != null && mediaPageArr[i].getVisibility() == 0) {
                    for (int j = 0; j < this.mediaPages[i].listView.getChildCount(); j++) {
                        View child = this.mediaPages[i].listView.getChildAt(j);
                        if (child.getY() < this.mediaPages[i].listView.blurTopPadding + AndroidUtilities.dp(100.0f)) {
                            int restore = blurCanvas.save();
                            blurCanvas.translate(this.mediaPages[i].getX() + child.getX(), getY() + this.mediaPages[i].getY() + this.mediaPages[i].listView.getY() + child.getY());
                            child.draw(blurCanvas);
                            blurCanvas.restoreToCount(restore);
                        }
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    /* loaded from: classes5.dex */
    public static class MediaPage extends FrameLayout {
        private ClippingImageView animatingImageView;
        private GridLayoutManager animationSupportingLayoutManager;
        private BlurredRecyclerView animationSupportingListView;
        private StickerEmptyView emptyView;
        public ObjectAnimator fastScrollAnimator;
        public boolean fastScrollEnabled;
        public Runnable fastScrollHideHintRunnable;
        public boolean fastScrollHinWasShown;
        public SharedMediaFastScrollTooltip fastScrollHintView;
        public boolean highlightAnimation;
        public int highlightMessageId;
        public float highlightProgress;
        public long lastCheckScrollTime;
        private ExtendedGridLayoutManager layoutManager;
        private BlurredRecyclerView listView;
        private FlickerLoadingView progressView;
        private RecyclerAnimationScrollHelper scrollHelper;
        private int selectedType;

        public MediaPage(Context context) {
            super(context);
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child == this.animationSupportingListView) {
                return true;
            }
            return super.drawChild(canvas, child, drawingTime);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip;
            super.dispatchDraw(canvas);
            SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip2 = this.fastScrollHintView;
            if (sharedMediaFastScrollTooltip2 != null && sharedMediaFastScrollTooltip2.getVisibility() == 0) {
                RecyclerListView.FastScroll fastScroll = this.listView.getFastScroll();
                if (fastScroll != null) {
                    float y = fastScroll.getScrollBarY() + AndroidUtilities.dp(36.0f);
                    float x = (getMeasuredWidth() - this.fastScrollHintView.getMeasuredWidth()) - AndroidUtilities.dp(16.0f);
                    this.fastScrollHintView.setPivotX(sharedMediaFastScrollTooltip.getMeasuredWidth());
                    this.fastScrollHintView.setPivotY(0.0f);
                    this.fastScrollHintView.setTranslationX(x);
                    this.fastScrollHintView.setTranslationY(y);
                }
                float y2 = fastScroll.getProgress();
                if (y2 > 0.85f) {
                    SharedMediaLayout.showFastScrollHint(this, null, false);
                }
            }
        }
    }

    public void updateFastScrollVisibility(MediaPage mediaPage, boolean animated) {
        Object obj = 1;
        int i = 0;
        boolean show = mediaPage.fastScrollEnabled && this.isPinnedToTop;
        View view = mediaPage.listView.getFastScroll();
        if (mediaPage.fastScrollAnimator != null) {
            mediaPage.fastScrollAnimator.removeAllListeners();
            mediaPage.fastScrollAnimator.cancel();
        }
        if (!animated) {
            view.animate().setListener(null).cancel();
            if (!show) {
                i = 8;
            }
            view.setVisibility(i);
            if (!show) {
                obj = null;
            }
            view.setTag(obj);
            view.setAlpha(1.0f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
        } else if (show && view.getTag() == null) {
            view.animate().setListener(null).cancel();
            if (view.getVisibility() != 0) {
                view.setVisibility(0);
                view.setAlpha(0.0f);
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, view.getAlpha(), 1.0f);
            mediaPage.fastScrollAnimator = objectAnimator;
            objectAnimator.setDuration(150L).start();
            view.setTag(obj);
        } else if (!show && view.getTag() != null) {
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, View.ALPHA, view.getAlpha(), 0.0f);
            objectAnimator2.addListener(new HideViewAfterAnimation(view));
            mediaPage.fastScrollAnimator = objectAnimator2;
            objectAnimator2.setDuration(150L).start();
            view.animate().setListener(null).cancel();
            view.setTag(null);
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3029lambda$new$0$orgtelegramuiComponentsSharedMediaLayout() {
        hideFloatingDateView(true);
    }

    public static /* synthetic */ float lambda$static$1(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    /* loaded from: classes5.dex */
    public static class SharedMediaPreloader implements NotificationCenter.NotificationCenterDelegate {
        private long dialogId;
        private boolean mediaWasLoaded;
        private long mergeDialogId;
        private BaseFragment parentFragment;
        private SharedMediaData[] sharedMediaData;
        private int[] mediaCount = {-1, -1, -1, -1, -1, -1, -1, -1};
        private int[] mediaMergeCount = {-1, -1, -1, -1, -1, -1, -1, -1};
        private int[] lastMediaCount = {-1, -1, -1, -1, -1, -1, -1, -1};
        private int[] lastLoadMediaCount = {-1, -1, -1, -1, -1, -1, -1, -1};
        private ArrayList<SharedMediaPreloaderDelegate> delegates = new ArrayList<>();

        public SharedMediaPreloader(BaseFragment fragment) {
            this.parentFragment = fragment;
            if (fragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) fragment;
                this.dialogId = chatActivity.getDialogId();
                this.mergeDialogId = chatActivity.getMergeDialogId();
            } else if (fragment instanceof ProfileActivity) {
                ProfileActivity profileActivity = (ProfileActivity) fragment;
                this.dialogId = profileActivity.getDialogId();
            } else if (fragment instanceof MediaActivity) {
                MediaActivity mediaActivity = (MediaActivity) fragment;
                this.dialogId = mediaActivity.getDialogId();
            }
            this.sharedMediaData = new SharedMediaData[6];
            int a = 0;
            while (true) {
                SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                if (a < sharedMediaDataArr.length) {
                    sharedMediaDataArr[a] = new SharedMediaData();
                    this.sharedMediaData[a].setMaxId(0, DialogObject.isEncryptedDialog(this.dialogId) ? Integer.MIN_VALUE : Integer.MAX_VALUE);
                    a++;
                } else {
                    loadMediaCounts();
                    NotificationCenter notificationCenter = this.parentFragment.getNotificationCenter();
                    notificationCenter.addObserver(this, NotificationCenter.mediaCountsDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.mediaCountDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.didReceiveNewMessages);
                    notificationCenter.addObserver(this, NotificationCenter.messageReceivedByServer);
                    notificationCenter.addObserver(this, NotificationCenter.mediaDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.messagesDeleted);
                    notificationCenter.addObserver(this, NotificationCenter.replaceMessagesObjects);
                    notificationCenter.addObserver(this, NotificationCenter.chatInfoDidLoad);
                    notificationCenter.addObserver(this, NotificationCenter.fileLoaded);
                    return;
                }
            }
        }

        public void addDelegate(SharedMediaPreloaderDelegate delegate) {
            this.delegates.add(delegate);
        }

        public void removeDelegate(SharedMediaPreloaderDelegate delegate) {
            this.delegates.remove(delegate);
        }

        public void onDestroy(BaseFragment fragment) {
            if (fragment != this.parentFragment) {
                return;
            }
            this.delegates.clear();
            NotificationCenter notificationCenter = this.parentFragment.getNotificationCenter();
            notificationCenter.removeObserver(this, NotificationCenter.mediaCountsDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.mediaCountDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.didReceiveNewMessages);
            notificationCenter.removeObserver(this, NotificationCenter.messageReceivedByServer);
            notificationCenter.removeObserver(this, NotificationCenter.mediaDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.messagesDeleted);
            notificationCenter.removeObserver(this, NotificationCenter.replaceMessagesObjects);
            notificationCenter.removeObserver(this, NotificationCenter.chatInfoDidLoad);
            notificationCenter.removeObserver(this, NotificationCenter.fileLoaded);
        }

        public int[] getLastMediaCount() {
            return this.lastMediaCount;
        }

        public SharedMediaData[] getSharedMediaData() {
            return this.sharedMediaData;
        }

        /* JADX WARN: Removed duplicated region for block: B:41:0x009e  */
        /* JADX WARN: Removed duplicated region for block: B:42:0x00a3  */
        /* JADX WARN: Removed duplicated region for block: B:71:0x015b A[LOOP:2: B:70:0x0159->B:71:0x015b, LOOP_END] */
        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void didReceivedNotification(int r23, final int r24, java.lang.Object... r25) {
            /*
                Method dump skipped, instructions count: 1245
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloader.didReceivedNotification(int, int, java.lang.Object[]):void");
        }

        private void loadMediaCounts() {
            this.parentFragment.getMediaDataController().getMediaCounts(this.dialogId, this.parentFragment.getClassGuid());
            if (this.mergeDialogId != 0) {
                this.parentFragment.getMediaDataController().getMediaCounts(this.mergeDialogId, this.parentFragment.getClassGuid());
            }
        }

        private void setChatInfo(TLRPC.ChatFull chatInfo) {
            if (chatInfo != null && chatInfo.migrated_from_chat_id != 0 && this.mergeDialogId == 0) {
                this.mergeDialogId = -chatInfo.migrated_from_chat_id;
                this.parentFragment.getMediaDataController().getMediaCounts(this.mergeDialogId, this.parentFragment.getClassGuid());
            }
        }

        public boolean isMediaWasLoaded() {
            return this.mediaWasLoaded;
        }
    }

    /* loaded from: classes5.dex */
    public static class SharedMediaData {
        private int endLoadingStubs;
        public boolean fastScrollDataLoaded;
        public int frozenEndLoadingStubs;
        public int frozenStartOffset;
        private boolean hasPhotos;
        private boolean hasVideos;
        public boolean isFrozen;
        public boolean loading;
        public boolean loadingAfterFastScroll;
        public int min_id;
        public int requestIndex;
        private int startOffset;
        public int totalCount;
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public SparseArray<MessageObject>[] messagesDict = {new SparseArray<>(), new SparseArray<>()};
        public ArrayList<String> sections = new ArrayList<>();
        public HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap<>();
        public ArrayList<Period> fastScrollPeriods = new ArrayList<>();
        public boolean[] endReached = {false, true};
        public int[] max_id = {0, 0};
        public boolean startReached = true;
        public int filterType = 0;
        public ArrayList<MessageObject> frozenMessages = new ArrayList<>();
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();

        static /* synthetic */ int access$7010(SharedMediaData x0) {
            int i = x0.endLoadingStubs;
            x0.endLoadingStubs = i - 1;
            return i;
        }

        static /* synthetic */ int access$710(SharedMediaData x0) {
            int i = x0.startOffset;
            x0.startOffset = i - 1;
            return i;
        }

        public void setTotalCount(int count) {
            this.totalCount = count;
        }

        public void setMaxId(int num, int value) {
            this.max_id[num] = value;
        }

        public void setEndReached(int num, boolean value) {
            this.endReached[num] = value;
        }

        public boolean addMessage(MessageObject messageObject, int loadIndex, boolean isNew, boolean enc) {
            if (this.messagesDict[loadIndex].indexOfKey(messageObject.getId()) >= 0) {
                return false;
            }
            ArrayList<MessageObject> messageObjects = this.sectionArrays.get(messageObject.monthKey);
            if (messageObjects == null) {
                messageObjects = new ArrayList<>();
                this.sectionArrays.put(messageObject.monthKey, messageObjects);
                if (isNew) {
                    this.sections.add(0, messageObject.monthKey);
                } else {
                    this.sections.add(messageObject.monthKey);
                }
            }
            if (isNew) {
                messageObjects.add(0, messageObject);
                this.messages.add(0, messageObject);
            } else {
                messageObjects.add(messageObject);
                this.messages.add(messageObject);
            }
            this.messagesDict[loadIndex].put(messageObject.getId(), messageObject);
            if (!enc) {
                if (messageObject.getId() > 0) {
                    this.max_id[loadIndex] = Math.min(messageObject.getId(), this.max_id[loadIndex]);
                    this.min_id = Math.max(messageObject.getId(), this.min_id);
                }
            } else {
                this.max_id[loadIndex] = Math.max(messageObject.getId(), this.max_id[loadIndex]);
                this.min_id = Math.min(messageObject.getId(), this.min_id);
            }
            if (!this.hasVideos && messageObject.isVideo()) {
                this.hasVideos = true;
            }
            if (!this.hasPhotos && messageObject.isPhoto()) {
                this.hasPhotos = true;
            }
            return true;
        }

        public MessageObject deleteMessage(int mid, int loadIndex) {
            ArrayList<MessageObject> messageObjects;
            MessageObject messageObject = this.messagesDict[loadIndex].get(mid);
            if (messageObject == null || (messageObjects = this.sectionArrays.get(messageObject.monthKey)) == null) {
                return null;
            }
            messageObjects.remove(messageObject);
            this.messages.remove(messageObject);
            this.messagesDict[loadIndex].remove(messageObject.getId());
            if (messageObjects.isEmpty()) {
                this.sectionArrays.remove(messageObject.monthKey);
                this.sections.remove(messageObject.monthKey);
            }
            this.totalCount--;
            return messageObject;
        }

        public void replaceMid(int oldMid, int newMid) {
            MessageObject obj = this.messagesDict[0].get(oldMid);
            if (obj != null) {
                this.messagesDict[0].remove(oldMid);
                this.messagesDict[0].put(newMid, obj);
                obj.messageOwner.id = newMid;
                int[] iArr = this.max_id;
                iArr[0] = Math.min(newMid, iArr[0]);
            }
        }

        public ArrayList<MessageObject> getMessages() {
            return this.isFrozen ? this.frozenMessages : this.messages;
        }

        public int getStartOffset() {
            return this.isFrozen ? this.frozenStartOffset : this.startOffset;
        }

        public void setListFrozen(boolean frozen) {
            if (this.isFrozen == frozen) {
                return;
            }
            this.isFrozen = frozen;
            if (frozen) {
                this.frozenStartOffset = this.startOffset;
                this.frozenEndLoadingStubs = this.endLoadingStubs;
                this.frozenMessages.clear();
                this.frozenMessages.addAll(this.messages);
            }
        }

        public int getEndLoadingStubs() {
            return this.isFrozen ? this.frozenEndLoadingStubs : this.endLoadingStubs;
        }
    }

    /* loaded from: classes5.dex */
    public static class Period {
        int date;
        public String formatedDate;
        int maxId;
        public int startOffset;

        public Period(TLRPC.TL_searchResultPosition calendarPeriod) {
            this.date = calendarPeriod.date;
            this.maxId = calendarPeriod.msg_id;
            this.startOffset = calendarPeriod.offset;
            this.formatedDate = LocaleController.formatYearMont(this.date, true);
        }
    }

    public SharedMediaLayout(Context context, long did, SharedMediaPreloader preloader, int commonGroupsCount, ArrayList<Integer> sortedUsers, TLRPC.ChatFull chatInfo, boolean membersFirst, BaseFragment parent, Delegate delegate, int viewType, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        SizeNotifierFrameLayout sizeNotifierFrameLayout;
        String str;
        this.mediaColumnsCount = 3;
        this.viewType = viewType;
        this.resourcesProvider = resourcesProvider;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.globalGradientView = flickerLoadingView;
        flickerLoadingView.setIsSingleCell(true);
        this.sharedMediaPreloader = preloader;
        this.delegate = delegate;
        int[] mediaCount = preloader.getLastMediaCount();
        this.hasMedia = new int[]{mediaCount[0], mediaCount[1], mediaCount[2], mediaCount[3], mediaCount[4], mediaCount[5], commonGroupsCount};
        if (membersFirst) {
            this.initialTab = 7;
        } else {
            int a = 0;
            while (true) {
                int[] iArr = this.hasMedia;
                if (a >= iArr.length) {
                    break;
                } else if (iArr[a] == -1 || iArr[a] > 0) {
                    break;
                } else {
                    a++;
                }
            }
            this.initialTab = a;
        }
        this.info = chatInfo;
        if (chatInfo != null) {
            this.mergeDialogId = -chatInfo.migrated_from_chat_id;
        }
        this.dialog_id = did;
        int a2 = 0;
        while (true) {
            SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (a2 >= sharedMediaDataArr.length) {
                break;
            }
            sharedMediaDataArr[a2] = new SharedMediaData();
            this.sharedMediaData[a2].max_id[0] = DialogObject.isEncryptedDialog(this.dialog_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            fillMediaData(a2);
            if (this.mergeDialogId != 0 && this.info != null) {
                this.sharedMediaData[a2].max_id[1] = this.info.migrated_from_max_id;
                this.sharedMediaData[a2].endReached[1] = false;
            }
            a2++;
        }
        this.profileActivity = parent;
        this.actionBar = parent.getActionBar();
        this.mediaColumnsCount = SharedConfig.mediaColumnsCount;
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.mediaDidLoad);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messageReceivedByServer);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingDidReset);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        this.profileActivity.getNotificationCenter().addObserver(this, NotificationCenter.messagePlayingDidStart);
        int a3 = 0;
        for (int i = 10; a3 < i; i = 10) {
            if (this.initialTab == 4) {
                SharedAudioCell cell = new SharedAudioCell(context) { // from class: org.telegram.ui.Components.SharedMediaLayout.2
                    @Override // org.telegram.ui.Cells.SharedAudioCell
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean result = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(result ? SharedMediaLayout.this.sharedMediaData[4].messages : null, false);
                            return result;
                        } else if (!messageObject.isMusic()) {
                            return false;
                        } else {
                            return MediaController.getInstance().setPlaylist(SharedMediaLayout.this.sharedMediaData[4].messages, messageObject, SharedMediaLayout.this.mergeDialogId);
                        }
                    }
                };
                cell.initStreamingIcons();
                this.audioCellCache.add(cell);
            }
            a3++;
        }
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.searching = false;
        this.searchWas = false;
        Drawable drawable = context.getResources().getDrawable(R.drawable.photos_header_shadow);
        this.pinnedHeaderShadowDrawable = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_windowBackgroundGrayShadow), PorterDuff.Mode.MULTIPLY));
        ScrollSlidingTextTabStripInner scrollSlidingTextTabStripInner = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStripInner != null) {
            this.initialTab = scrollSlidingTextTabStripInner.getCurrentTabId();
        }
        this.scrollSlidingTextTabStrip = createScrollingTextTabStrip(context);
        for (int a4 = 1; a4 >= 0; a4--) {
            this.selectedFiles[a4].clear();
        }
        this.cantDeleteMessagesCount = 0;
        this.actionModeViews.clear();
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: org.telegram.ui.Components.SharedMediaLayout.3
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i2, int i1, int i22, int i3, int i4, int i5, int i6, int i7) {
                if (SharedMediaLayout.this.searchItem != null) {
                    View parent2 = (View) SharedMediaLayout.this.searchItem.getParent();
                    SharedMediaLayout.this.searchItem.setTranslationX(parent2.getMeasuredWidth() - SharedMediaLayout.this.searchItem.getRight());
                }
            }
        });
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.Components.SharedMediaLayout.4
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                SharedMediaLayout.this.searching = true;
                SharedMediaLayout.this.onSearchStateChanged(true);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                SharedMediaLayout.this.searching = false;
                SharedMediaLayout.this.searchWas = false;
                SharedMediaLayout.this.documentsSearchAdapter.search(null, true);
                SharedMediaLayout.this.linksSearchAdapter.search(null, true);
                SharedMediaLayout.this.audioSearchAdapter.search(null, true);
                SharedMediaLayout.this.groupUsersSearchAdapter.search(null, true);
                SharedMediaLayout.this.onSearchStateChanged(false);
                if (SharedMediaLayout.this.ignoreSearchCollapse) {
                    SharedMediaLayout.this.ignoreSearchCollapse = false;
                } else {
                    SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    SharedMediaLayout.this.searchWas = true;
                } else {
                    SharedMediaLayout.this.searchWas = false;
                }
                SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                if (SharedMediaLayout.this.mediaPages[0].selectedType == 1) {
                    if (SharedMediaLayout.this.documentsSearchAdapter != null) {
                        SharedMediaLayout.this.documentsSearchAdapter.search(text, true);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 3) {
                    if (SharedMediaLayout.this.linksSearchAdapter != null) {
                        SharedMediaLayout.this.linksSearchAdapter.search(text, true);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 4) {
                    if (SharedMediaLayout.this.audioSearchAdapter != null) {
                        SharedMediaLayout.this.audioSearchAdapter.search(text, true);
                    }
                } else if (SharedMediaLayout.this.mediaPages[0].selectedType == 7 && SharedMediaLayout.this.groupUsersSearchAdapter != null) {
                    SharedMediaLayout.this.groupUsersSearchAdapter.search(text, true);
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onLayout(int l, int t, int r, int b) {
                View parent2 = (View) SharedMediaLayout.this.searchItem.getParent();
                SharedMediaLayout.this.searchItem.setTranslationX(parent2.getMeasuredWidth() - SharedMediaLayout.this.searchItem.getRight());
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setTranslationY(AndroidUtilities.dp(10.0f));
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.searchItem.setContentDescription(LocaleController.getString("Search", R.string.Search));
        this.searchItem.setVisibility(4);
        ImageView imageView = new ImageView(context);
        this.photoVideoOptionsItem = imageView;
        imageView.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.photoVideoOptionsItem.setTranslationY(AndroidUtilities.dp(10.0f));
        this.photoVideoOptionsItem.setVisibility(4);
        Drawable calendarDrawable = ContextCompat.getDrawable(context, R.drawable.ic_ab_other).mutate();
        calendarDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_windowBackgroundWhiteGrayText2), PorterDuff.Mode.MULTIPLY));
        this.photoVideoOptionsItem.setImageDrawable(calendarDrawable);
        this.photoVideoOptionsItem.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.actionBar.addView(this.photoVideoOptionsItem, LayoutHelper.createFrame(48, 56, 85));
        this.photoVideoOptionsItem.setOnClickListener(new AnonymousClass5(context));
        EditTextBoldCursor editText = this.searchItem.getSearchField();
        editText.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        editText.setHintTextColor(getThemedColor(Theme.key_player_time));
        editText.setCursorColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        this.searchItemState = 0;
        BaseFragment baseFragment = this.profileActivity;
        if (baseFragment != null && (baseFragment.getFragmentView() instanceof SizeNotifierFrameLayout)) {
            SizeNotifierFrameLayout sizeNotifierFrameLayout2 = (SizeNotifierFrameLayout) this.profileActivity.getFragmentView();
            sizeNotifierFrameLayout = sizeNotifierFrameLayout2;
        } else {
            sizeNotifierFrameLayout = null;
        }
        BlurredLinearLayout blurredLinearLayout = new BlurredLinearLayout(context, sizeNotifierFrameLayout);
        this.actionModeLayout = blurredLinearLayout;
        blurredLinearLayout.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        this.actionModeLayout.setAlpha(0.0f);
        this.actionModeLayout.setClickable(true);
        this.actionModeLayout.setVisibility(4);
        ImageView imageView2 = new ImageView(context);
        this.closeButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView3 = this.closeButton;
        BackDrawable backDrawable = new BackDrawable(true);
        this.backDrawable = backDrawable;
        imageView3.setImageDrawable(backDrawable);
        this.backDrawable.setColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.closeButton.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), 1));
        this.closeButton.setContentDescription(LocaleController.getString("Close", R.string.Close));
        this.actionModeLayout.addView(this.closeButton, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
        this.actionModeViews.add(this.closeButton);
        this.closeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SharedMediaLayout.this.m3030lambda$new$2$orgtelegramuiComponentsSharedMediaLayout(view);
            }
        });
        NumberTextView numberTextView = new NumberTextView(context);
        this.selectedMessagesCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.selectedMessagesCountTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.actionModeLayout.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 18, 0, 0, 0));
        this.actionModeViews.add(this.selectedMessagesCountTextView);
        if (!DialogObject.isEncryptedDialog(this.dialog_id)) {
            ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, (ActionBarMenu) null, getThemedColor(Theme.key_actionBarActionModeDefaultSelector), getThemedColor(Theme.key_windowBackgroundWhiteGrayText2), false);
            this.gotoItem = actionBarMenuItem;
            actionBarMenuItem.setIcon(R.drawable.msg_message);
            this.gotoItem.setContentDescription(LocaleController.getString("AccDescrGoToMessage", R.string.AccDescrGoToMessage));
            this.gotoItem.setDuplicateParentStateEnabled(false);
            this.actionModeLayout.addView(this.gotoItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
            this.actionModeViews.add(this.gotoItem);
            this.gotoItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda9
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SharedMediaLayout.this.m3031lambda$new$3$orgtelegramuiComponentsSharedMediaLayout(view);
                }
            });
            int themedColor = getThemedColor(Theme.key_actionBarActionModeDefaultSelector);
            int themedColor2 = getThemedColor(Theme.key_windowBackgroundWhiteGrayText2);
            str = Theme.key_actionBarActionModeDefaultSelector;
            ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, themedColor, themedColor2, false);
            this.forwardItem = actionBarMenuItem2;
            actionBarMenuItem2.setIcon(R.drawable.msg_forward);
            this.forwardItem.setContentDescription(LocaleController.getString("Forward", R.string.Forward));
            this.forwardItem.setDuplicateParentStateEnabled(false);
            this.actionModeLayout.addView(this.forwardItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
            this.actionModeViews.add(this.forwardItem);
            this.forwardItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda10
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SharedMediaLayout.this.m3032lambda$new$4$orgtelegramuiComponentsSharedMediaLayout(view);
                }
            });
            updateForwardItem();
        } else {
            str = Theme.key_actionBarActionModeDefaultSelector;
        }
        ActionBarMenuItem actionBarMenuItem3 = new ActionBarMenuItem(context, (ActionBarMenu) null, getThemedColor(str), getThemedColor(Theme.key_windowBackgroundWhiteGrayText2), false);
        this.deleteItem = actionBarMenuItem3;
        actionBarMenuItem3.setIcon(R.drawable.msg_delete);
        this.deleteItem.setContentDescription(LocaleController.getString("Delete", R.string.Delete));
        this.deleteItem.setDuplicateParentStateEnabled(false);
        this.actionModeLayout.addView(this.deleteItem, new LinearLayout.LayoutParams(AndroidUtilities.dp(54.0f), -1));
        this.actionModeViews.add(this.deleteItem);
        this.deleteItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SharedMediaLayout.this.m3033lambda$new$5$orgtelegramuiComponentsSharedMediaLayout(view);
            }
        });
        this.photoVideoAdapter = new SharedPhotoVideoAdapter(context) { // from class: org.telegram.ui.Components.SharedMediaLayout.6
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                MediaPage mediaPage = SharedMediaLayout.this.getMediaPage(0);
                if (mediaPage != null && mediaPage.animationSupportingListView.getVisibility() == 0) {
                    SharedMediaLayout.this.animationSupportingPhotoVideoAdapter.notifyDataSetChanged();
                }
            }
        };
        this.animationSupportingPhotoVideoAdapter = new SharedPhotoVideoAdapter(context);
        this.documentsAdapter = new SharedDocumentsAdapter(context, 1);
        this.voiceAdapter = new SharedDocumentsAdapter(context, 2);
        this.audioAdapter = new SharedDocumentsAdapter(context, 4);
        this.gifAdapter = new GifAdapter(context);
        this.documentsSearchAdapter = new MediaSearchAdapter(context, 1);
        this.audioSearchAdapter = new MediaSearchAdapter(context, 4);
        this.linksSearchAdapter = new MediaSearchAdapter(context, 3);
        this.groupUsersSearchAdapter = new GroupUsersSearchAdapter(context);
        this.commonGroupsAdapter = new CommonGroupsAdapter(context);
        ChatUsersAdapter chatUsersAdapter = new ChatUsersAdapter(context);
        this.chatUsersAdapter = chatUsersAdapter;
        chatUsersAdapter.sortedUsers = sortedUsers;
        this.chatUsersAdapter.chatInfo = membersFirst ? chatInfo : null;
        this.linksAdapter = new SharedLinksAdapter(context);
        setWillNotDraw(false);
        int a5 = 0;
        int scrollToPositionOnRecreate = -1;
        int scrollToOffsetOnRecreate = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (a5 >= mediaPageArr.length) {
                break;
            }
            if (a5 == 0 && mediaPageArr[a5] != null && mediaPageArr[a5].layoutManager != null) {
                int scrollToPositionOnRecreate2 = this.mediaPages[a5].layoutManager.findFirstVisibleItemPosition();
                if (scrollToPositionOnRecreate2 != this.mediaPages[a5].layoutManager.getItemCount() - 1) {
                    RecyclerListView.Holder holder = (RecyclerListView.Holder) this.mediaPages[a5].listView.findViewHolderForAdapterPosition(scrollToPositionOnRecreate2);
                    if (holder != null) {
                        scrollToOffsetOnRecreate = holder.itemView.getTop();
                    } else {
                        scrollToPositionOnRecreate2 = -1;
                    }
                    scrollToPositionOnRecreate = scrollToPositionOnRecreate2;
                } else {
                    scrollToPositionOnRecreate = -1;
                }
            }
            final MediaPage mediaPage = new MediaPage(context) { // from class: org.telegram.ui.Components.SharedMediaLayout.7
                @Override // android.view.View
                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (SharedMediaLayout.this.tabsAnimationInProgress) {
                        int i2 = 0;
                        if (SharedMediaLayout.this.mediaPages[0] == this) {
                            float scrollProgress = Math.abs(SharedMediaLayout.this.mediaPages[0].getTranslationX()) / SharedMediaLayout.this.mediaPages[0].getMeasuredWidth();
                            SharedMediaLayout.this.scrollSlidingTextTabStrip.selectTabWithId(SharedMediaLayout.this.mediaPages[1].selectedType, scrollProgress);
                            if (SharedMediaLayout.this.canShowSearchItem()) {
                                if (SharedMediaLayout.this.searchItemState == 2) {
                                    SharedMediaLayout.this.searchItem.setAlpha(1.0f - scrollProgress);
                                } else if (SharedMediaLayout.this.searchItemState == 1) {
                                    SharedMediaLayout.this.searchItem.setAlpha(scrollProgress);
                                }
                                float photoVideoOptionsAlpha = 0.0f;
                                if (SharedMediaLayout.this.mediaPages[1] != null && SharedMediaLayout.this.mediaPages[1].selectedType == 0) {
                                    photoVideoOptionsAlpha = scrollProgress;
                                }
                                if (SharedMediaLayout.this.mediaPages[0].selectedType == 0) {
                                    photoVideoOptionsAlpha = 1.0f - scrollProgress;
                                }
                                SharedMediaLayout.this.photoVideoOptionsItem.setAlpha(photoVideoOptionsAlpha);
                                ImageView imageView4 = SharedMediaLayout.this.photoVideoOptionsItem;
                                if (photoVideoOptionsAlpha == 0.0f || !SharedMediaLayout.this.canShowSearchItem()) {
                                    i2 = 4;
                                }
                                imageView4.setVisibility(i2);
                            } else {
                                SharedMediaLayout.this.searchItem.setAlpha(0.0f);
                            }
                        }
                    }
                    SharedMediaLayout.this.invalidateBlur();
                }
            };
            addView(mediaPage, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
            MediaPage[] mediaPageArr2 = this.mediaPages;
            mediaPageArr2[a5] = mediaPage;
            final ExtendedGridLayoutManager layoutManager = mediaPageArr2[a5].layoutManager = new ExtendedGridLayoutManager(context, 100) { // from class: org.telegram.ui.Components.SharedMediaLayout.8
                private Size size = new Size();

                @Override // org.telegram.ui.Components.ExtendedGridLayoutManager, androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                @Override // androidx.recyclerview.widget.LinearLayoutManager
                public void calculateExtraLayoutSpace(RecyclerView.State state, int[] extraLayoutSpace) {
                    super.calculateExtraLayoutSpace(state, extraLayoutSpace);
                    if (mediaPage.selectedType != 0) {
                        if (mediaPage.selectedType == 1) {
                            extraLayoutSpace[1] = Math.max(extraLayoutSpace[1], AndroidUtilities.dp(56.0f) * 2);
                            return;
                        }
                        return;
                    }
                    extraLayoutSpace[1] = Math.max(extraLayoutSpace[1], SharedPhotoVideoCell.getItemSize(1) * 2);
                }

                @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
                protected Size getSizeForItem(int i2) {
                    TLRPC.Document document;
                    if (mediaPage.listView.getAdapter() == SharedMediaLayout.this.gifAdapter && !SharedMediaLayout.this.sharedMediaData[5].messages.isEmpty()) {
                        document = SharedMediaLayout.this.sharedMediaData[5].messages.get(i2).getDocument();
                    } else {
                        document = null;
                    }
                    Size size = this.size;
                    size.height = 100.0f;
                    size.width = 100.0f;
                    if (document != null) {
                        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                        if (thumb != null && thumb.w != 0 && thumb.h != 0) {
                            this.size.width = thumb.w;
                            this.size.height = thumb.h;
                        }
                        ArrayList<TLRPC.DocumentAttribute> attributes = document.attributes;
                        for (int b = 0; b < attributes.size(); b++) {
                            TLRPC.DocumentAttribute attribute = attributes.get(b);
                            if ((attribute instanceof TLRPC.TL_documentAttributeImageSize) || (attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                this.size.width = attribute.w;
                                this.size.height = attribute.h;
                                break;
                            }
                        }
                    }
                    return this.size;
                }

                @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
                public int getFlowItemCount() {
                    if (mediaPage.listView.getAdapter() != SharedMediaLayout.this.gifAdapter) {
                        return 0;
                    }
                    return getItemCount();
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View host, AccessibilityNodeInfoCompat info) {
                    super.onInitializeAccessibilityNodeInfoForItem(recycler, state, host, info);
                    AccessibilityNodeInfoCompat.CollectionItemInfoCompat itemInfo = info.getCollectionItemInfo();
                    if (itemInfo != null && itemInfo.isHeading()) {
                        info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(itemInfo.getRowIndex(), itemInfo.getRowSpan(), itemInfo.getColumnIndex(), itemInfo.getColumnSpan(), false));
                    }
                }
            };
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.SharedMediaLayout.9
                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int position) {
                    if (mediaPage.listView.getAdapter() == SharedMediaLayout.this.photoVideoAdapter) {
                        if (SharedMediaLayout.this.photoVideoAdapter.getItemViewType(position) == 2) {
                            return SharedMediaLayout.this.mediaColumnsCount;
                        }
                        return 1;
                    } else if (mediaPage.listView.getAdapter() != SharedMediaLayout.this.gifAdapter) {
                        return mediaPage.layoutManager.getSpanCount();
                    } else {
                        return (mediaPage.listView.getAdapter() != SharedMediaLayout.this.gifAdapter || !SharedMediaLayout.this.sharedMediaData[5].messages.isEmpty()) ? mediaPage.layoutManager.getSpanSizeForItem(position) : mediaPage.layoutManager.getSpanCount();
                    }
                }
            });
            this.mediaPages[a5].listView = new BlurredRecyclerView(context) { // from class: org.telegram.ui.Components.SharedMediaLayout.10
                HashSet<SharedPhotoVideoCell2> excludeDrawViews = new HashSet<>();
                ArrayList<SharedPhotoVideoCell2> drawingViews = new ArrayList<>();
                ArrayList<SharedPhotoVideoCell2> drawingViews2 = new ArrayList<>();
                ArrayList<SharedPhotoVideoCell2> drawingViews3 = new ArrayList<>();

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    super.onLayout(changed, l, t, r, b);
                    SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                    MediaPage mediaPage2 = mediaPage;
                    sharedMediaLayout.checkLoadMoreScroll(mediaPage2, mediaPage2.listView, layoutManager);
                    if (mediaPage.selectedType == 0) {
                        PhotoViewer.getInstance().checkCurrentImageVisibility();
                    }
                }

                @Override // org.telegram.ui.Components.BlurredRecyclerView, org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
                public void dispatchDraw(Canvas canvas) {
                    int firstVisibleItemPosition2;
                    int firstVisibleItemPosition;
                    int rowsOffset;
                    int columnsOffset;
                    int min;
                    int i2;
                    float size1;
                    int i3;
                    int lastVisibleItemPosition2;
                    int rowsOffset2;
                    RecyclerView.Adapter adapter = getAdapter();
                    SharedPhotoVideoAdapter sharedPhotoVideoAdapter = SharedMediaLayout.this.photoVideoAdapter;
                    Float valueOf = Float.valueOf(1.0f);
                    if (adapter == sharedPhotoVideoAdapter) {
                        int columnsOffset2 = 0;
                        float minY = getMeasuredHeight();
                        if (!SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                            rowsOffset = 0;
                            firstVisibleItemPosition = 0;
                            firstVisibleItemPosition2 = 0;
                            min = 0;
                            columnsOffset = 0;
                        } else {
                            int max = -1;
                            int min2 = -1;
                            for (int i4 = 0; i4 < mediaPage.listView.getChildCount(); i4++) {
                                int p = mediaPage.listView.getChildAdapterPosition(mediaPage.listView.getChildAt(i4));
                                if (p >= 0 && (p > max || max == -1)) {
                                    max = p;
                                }
                                if (p >= 0 && (p < min2 || min2 == -1)) {
                                    min2 = p;
                                }
                            }
                            int firstVisibleItemPosition3 = min2;
                            int lastVisibleItemPosition = max;
                            int max2 = -1;
                            int min3 = -1;
                            for (int i5 = 0; i5 < mediaPage.animationSupportingListView.getChildCount(); i5++) {
                                int p2 = mediaPage.animationSupportingListView.getChildAdapterPosition(mediaPage.animationSupportingListView.getChildAt(i5));
                                if (p2 >= 0 && (p2 > max2 || max2 == -1)) {
                                    max2 = p2;
                                }
                                if (p2 >= 0 && (p2 < min3 || min3 == -1)) {
                                    min3 = p2;
                                }
                            }
                            int firstVisibleItemPosition22 = min3;
                            int lastVisibleItemPosition22 = max2;
                            if (firstVisibleItemPosition3 >= 0 && firstVisibleItemPosition22 >= 0 && SharedMediaLayout.this.pinchCenterPosition >= 0) {
                                int rowsCount1 = (int) Math.ceil(SharedMediaLayout.this.photoVideoAdapter.getItemCount() / SharedMediaLayout.this.mediaColumnsCount);
                                int rowsCount2 = (int) Math.ceil(SharedMediaLayout.this.photoVideoAdapter.getItemCount() / SharedMediaLayout.this.animateToColumnsCount);
                                int rowsOffset3 = ((SharedMediaLayout.this.pinchCenterPosition / SharedMediaLayout.this.animateToColumnsCount) - (firstVisibleItemPosition22 / SharedMediaLayout.this.animateToColumnsCount)) - ((SharedMediaLayout.this.pinchCenterPosition / SharedMediaLayout.this.mediaColumnsCount) - (firstVisibleItemPosition3 / SharedMediaLayout.this.mediaColumnsCount));
                                if (((firstVisibleItemPosition3 / SharedMediaLayout.this.mediaColumnsCount) - rowsOffset3 < 0 && SharedMediaLayout.this.animateToColumnsCount < SharedMediaLayout.this.mediaColumnsCount) || ((firstVisibleItemPosition22 / SharedMediaLayout.this.animateToColumnsCount) + rowsOffset3 < 0 && SharedMediaLayout.this.animateToColumnsCount > SharedMediaLayout.this.mediaColumnsCount)) {
                                    rowsOffset3 = 0;
                                }
                                if (((lastVisibleItemPosition22 / SharedMediaLayout.this.mediaColumnsCount) + rowsOffset3 >= rowsCount1 && SharedMediaLayout.this.animateToColumnsCount > SharedMediaLayout.this.mediaColumnsCount) || ((lastVisibleItemPosition / SharedMediaLayout.this.animateToColumnsCount) - rowsOffset3 >= rowsCount2 && SharedMediaLayout.this.animateToColumnsCount < SharedMediaLayout.this.mediaColumnsCount)) {
                                    rowsOffset2 = 0;
                                } else {
                                    rowsOffset2 = rowsOffset3;
                                }
                                float k = (SharedMediaLayout.this.pinchCenterPosition % SharedMediaLayout.this.mediaColumnsCount) / (SharedMediaLayout.this.mediaColumnsCount - 1);
                                firstVisibleItemPosition = firstVisibleItemPosition3;
                                columnsOffset2 = (int) ((SharedMediaLayout.this.animateToColumnsCount - SharedMediaLayout.this.mediaColumnsCount) * k);
                            } else {
                                firstVisibleItemPosition = firstVisibleItemPosition3;
                                rowsOffset2 = 0;
                            }
                            SharedMediaLayout.this.animationSupportingSortedCells.clear();
                            this.excludeDrawViews.clear();
                            this.drawingViews.clear();
                            this.drawingViews2.clear();
                            this.drawingViews3.clear();
                            for (int i6 = 0; i6 < mediaPage.animationSupportingListView.getChildCount(); i6++) {
                                View child = mediaPage.animationSupportingListView.getChildAt(i6);
                                if (child.getTop() <= getMeasuredHeight() && child.getBottom() >= 0 && (child instanceof SharedPhotoVideoCell2)) {
                                    SharedMediaLayout.this.animationSupportingSortedCells.add((SharedPhotoVideoCell2) child);
                                }
                            }
                            this.drawingViews.addAll(SharedMediaLayout.this.animationSupportingSortedCells);
                            RecyclerListView.FastScroll fastScroll = getFastScroll();
                            if (fastScroll != null && fastScroll.getTag() != null) {
                                float p1 = SharedMediaLayout.this.photoVideoAdapter.getScrollProgress(mediaPage.listView);
                                float p22 = SharedMediaLayout.this.animationSupportingPhotoVideoAdapter.getScrollProgress(mediaPage.animationSupportingListView);
                                float a1 = SharedMediaLayout.this.photoVideoAdapter.fastScrollIsVisible(mediaPage.listView) ? 1.0f : 0.0f;
                                firstVisibleItemPosition2 = firstVisibleItemPosition22;
                                float a22 = SharedMediaLayout.this.animationSupportingPhotoVideoAdapter.fastScrollIsVisible(mediaPage.animationSupportingListView) ? 1.0f : 0.0f;
                                fastScroll.setProgress(((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * p1) + (SharedMediaLayout.this.photoVideoChangeColumnsProgress * p22));
                                fastScroll.setVisibilityAlpha(((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * a1) + (SharedMediaLayout.this.photoVideoChangeColumnsProgress * a22));
                            } else {
                                firstVisibleItemPosition2 = firstVisibleItemPosition22;
                            }
                            min = lastVisibleItemPosition22;
                            rowsOffset = rowsOffset2;
                            columnsOffset = columnsOffset2;
                        }
                        int i7 = 0;
                        while (i7 < getChildCount()) {
                            View child2 = getChildAt(i7);
                            if (child2.getTop() > getMeasuredHeight()) {
                                lastVisibleItemPosition2 = min;
                            } else if (child2.getBottom() < 0) {
                                lastVisibleItemPosition2 = min;
                            } else {
                                if (!(child2 instanceof SharedPhotoVideoCell2)) {
                                    lastVisibleItemPosition2 = min;
                                } else {
                                    SharedPhotoVideoCell2 cell2 = (SharedPhotoVideoCell2) getChildAt(i7);
                                    if (cell2.getMessageId() == mediaPage.highlightMessageId && cell2.imageReceiver.hasBitmapImage()) {
                                        if (!mediaPage.highlightAnimation) {
                                            mediaPage.highlightProgress = 0.0f;
                                            mediaPage.highlightAnimation = true;
                                        }
                                        float p3 = 1.0f;
                                        if (mediaPage.highlightProgress < 0.3f) {
                                            p3 = mediaPage.highlightProgress / 0.3f;
                                        } else if (mediaPage.highlightProgress > 0.7f) {
                                            p3 = (1.0f - mediaPage.highlightProgress) / 0.3f;
                                        }
                                        cell2.setHighlightProgress(p3);
                                    } else {
                                        cell2.setHighlightProgress(0.0f);
                                    }
                                    MessageObject messageObject = cell2.getMessageObject();
                                    float alpha = 1.0f;
                                    if (messageObject != null && SharedMediaLayout.this.messageAlphaEnter.get(messageObject.getId(), null) != null) {
                                        alpha = SharedMediaLayout.this.messageAlphaEnter.get(messageObject.getId(), valueOf).floatValue();
                                    }
                                    cell2.setImageAlpha(alpha, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                                    boolean inAnimation = false;
                                    if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                                        int currentColumn = (((GridLayoutManager.LayoutParams) cell2.getLayoutParams()).getViewAdapterPosition() % SharedMediaLayout.this.mediaColumnsCount) + columnsOffset;
                                        int currentRow = ((((GridLayoutManager.LayoutParams) cell2.getLayoutParams()).getViewAdapterPosition() - firstVisibleItemPosition) / SharedMediaLayout.this.mediaColumnsCount) + rowsOffset;
                                        int toIndex = (SharedMediaLayout.this.animateToColumnsCount * currentRow) + currentColumn;
                                        if (currentColumn < 0) {
                                            lastVisibleItemPosition2 = min;
                                        } else if (currentColumn >= SharedMediaLayout.this.animateToColumnsCount || toIndex < 0 || toIndex >= SharedMediaLayout.this.animationSupportingSortedCells.size()) {
                                            lastVisibleItemPosition2 = min;
                                        } else {
                                            float toScale = (((SharedPhotoVideoCell2) SharedMediaLayout.this.animationSupportingSortedCells.get(toIndex)).getMeasuredWidth() - AndroidUtilities.dpf2(2.0f)) / (cell2.getMeasuredWidth() - AndroidUtilities.dpf2(2.0f));
                                            float scale = ((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * 1.0f) + (SharedMediaLayout.this.photoVideoChangeColumnsProgress * toScale);
                                            float fromX = cell2.getLeft();
                                            float fromY = cell2.getTop();
                                            lastVisibleItemPosition2 = min;
                                            float toX = ((SharedPhotoVideoCell2) SharedMediaLayout.this.animationSupportingSortedCells.get(toIndex)).getLeft();
                                            float toY = ((SharedPhotoVideoCell2) SharedMediaLayout.this.animationSupportingSortedCells.get(toIndex)).getTop();
                                            cell2.setPivotX(0.0f);
                                            cell2.setPivotY(0.0f);
                                            cell2.setImageScale(scale, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                                            cell2.setTranslationX((toX - fromX) * SharedMediaLayout.this.photoVideoChangeColumnsProgress);
                                            cell2.setTranslationY((toY - fromY) * SharedMediaLayout.this.photoVideoChangeColumnsProgress);
                                            cell2.setCrossfadeView((SharedPhotoVideoCell2) SharedMediaLayout.this.animationSupportingSortedCells.get(toIndex), SharedMediaLayout.this.photoVideoChangeColumnsProgress, SharedMediaLayout.this.animateToColumnsCount);
                                            this.excludeDrawViews.add((SharedPhotoVideoCell2) SharedMediaLayout.this.animationSupportingSortedCells.get(toIndex));
                                            this.drawingViews3.add(cell2);
                                            canvas.save();
                                            canvas.translate(cell2.getX(), cell2.getY());
                                            cell2.draw(canvas);
                                            canvas.restore();
                                            if (cell2.getY() >= minY) {
                                                inAnimation = true;
                                            } else {
                                                minY = cell2.getY();
                                                inAnimation = true;
                                            }
                                        }
                                    } else {
                                        lastVisibleItemPosition2 = min;
                                    }
                                    if (!inAnimation) {
                                        if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                                            this.drawingViews2.add(cell2);
                                        }
                                        cell2.setCrossfadeView(null, 0.0f, 0);
                                        cell2.setTranslationX(0.0f);
                                        cell2.setTranslationY(0.0f);
                                        cell2.setImageScale(1.0f, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                                    }
                                }
                                i7++;
                                min = lastVisibleItemPosition2;
                            }
                            if (child2 instanceof SharedPhotoVideoCell2) {
                                SharedPhotoVideoCell2 cell3 = (SharedPhotoVideoCell2) getChildAt(i7);
                                cell3.setCrossfadeView(null, 0.0f, 0);
                                cell3.setTranslationX(0.0f);
                                cell3.setTranslationY(0.0f);
                                cell3.setImageScale(1.0f, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                            }
                            i7++;
                            min = lastVisibleItemPosition2;
                        }
                        if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation && !this.drawingViews.isEmpty()) {
                            float toScale2 = SharedMediaLayout.this.animateToColumnsCount / SharedMediaLayout.this.mediaColumnsCount;
                            float scale2 = ((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * toScale2) + SharedMediaLayout.this.photoVideoChangeColumnsProgress;
                            float sizeToScale = ((getMeasuredWidth() / SharedMediaLayout.this.mediaColumnsCount) - AndroidUtilities.dpf2(2.0f)) / ((getMeasuredWidth() / SharedMediaLayout.this.animateToColumnsCount) - AndroidUtilities.dpf2(2.0f));
                            float scaleSize = ((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * sizeToScale) + SharedMediaLayout.this.photoVideoChangeColumnsProgress;
                            float fromSize = getMeasuredWidth() / SharedMediaLayout.this.mediaColumnsCount;
                            float toSize = getMeasuredWidth() / SharedMediaLayout.this.animateToColumnsCount;
                            double ceil = Math.ceil(getMeasuredWidth() / SharedMediaLayout.this.animateToColumnsCount);
                            double dpf2 = AndroidUtilities.dpf2(2.0f);
                            Double.isNaN(dpf2);
                            double d = ceil - dpf2;
                            double d2 = scaleSize;
                            Double.isNaN(d2);
                            double dpf22 = AndroidUtilities.dpf2(2.0f);
                            Double.isNaN(dpf22);
                            float size12 = (float) ((d * d2) + dpf22);
                            int i8 = 0;
                            while (i8 < this.drawingViews.size()) {
                                SharedPhotoVideoCell2 view = this.drawingViews.get(i8);
                                if (this.excludeDrawViews.contains(view)) {
                                    i3 = i8;
                                    size1 = size12;
                                } else {
                                    view.setCrossfadeView(null, 0.0f, 0);
                                    int fromColumn = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getViewAdapterPosition() % SharedMediaLayout.this.animateToColumnsCount;
                                    int toColumn = fromColumn - columnsOffset;
                                    int currentRow2 = (((GridLayoutManager.LayoutParams) view.getLayoutParams()).getViewAdapterPosition() - firstVisibleItemPosition2) / SharedMediaLayout.this.animateToColumnsCount;
                                    canvas.save();
                                    canvas.translate((toColumn * fromSize * (1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress)) + (fromColumn * toSize * SharedMediaLayout.this.photoVideoChangeColumnsProgress), ((currentRow2 - rowsOffset) * size12) + minY);
                                    view.setImageScale(scaleSize, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                                    if (toColumn < SharedMediaLayout.this.mediaColumnsCount) {
                                        float size13 = view.getMeasuredWidth() * scale2;
                                        i3 = i8;
                                        size1 = size12;
                                        canvas.saveLayerAlpha(0.0f, 0.0f, size13, view.getMeasuredWidth() * scale2, (int) (SharedMediaLayout.this.photoVideoChangeColumnsProgress * 255.0f), 31);
                                        view.draw(canvas);
                                        canvas.restore();
                                    } else {
                                        size1 = size12;
                                        i3 = i8;
                                        view.draw(canvas);
                                    }
                                    canvas.restore();
                                }
                                i8 = i3 + 1;
                                size12 = size1;
                            }
                        }
                        super.dispatchDraw(canvas);
                        if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                            float toScale3 = SharedMediaLayout.this.mediaColumnsCount / SharedMediaLayout.this.animateToColumnsCount;
                            float scale3 = (SharedMediaLayout.this.photoVideoChangeColumnsProgress * toScale3) + (1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress);
                            float sizeToScale2 = ((getMeasuredWidth() / SharedMediaLayout.this.animateToColumnsCount) - AndroidUtilities.dpf2(2.0f)) / ((getMeasuredWidth() / SharedMediaLayout.this.mediaColumnsCount) - AndroidUtilities.dpf2(2.0f));
                            float scaleSize2 = (SharedMediaLayout.this.photoVideoChangeColumnsProgress * sizeToScale2) + (1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress);
                            double ceil2 = Math.ceil(getMeasuredWidth() / SharedMediaLayout.this.mediaColumnsCount);
                            double dpf23 = AndroidUtilities.dpf2(2.0f);
                            Double.isNaN(dpf23);
                            double d3 = ceil2 - dpf23;
                            double d4 = scaleSize2;
                            Double.isNaN(d4);
                            double dpf24 = AndroidUtilities.dpf2(2.0f);
                            Double.isNaN(dpf24);
                            float size14 = (float) ((d3 * d4) + dpf24);
                            float fromSize2 = getMeasuredWidth() / SharedMediaLayout.this.mediaColumnsCount;
                            float toSize2 = getMeasuredWidth() / SharedMediaLayout.this.animateToColumnsCount;
                            int i9 = 0;
                            while (i9 < this.drawingViews2.size()) {
                                SharedPhotoVideoCell2 view2 = this.drawingViews2.get(i9);
                                int fromColumn2 = ((GridLayoutManager.LayoutParams) view2.getLayoutParams()).getViewAdapterPosition() % SharedMediaLayout.this.mediaColumnsCount;
                                int currentRow3 = (((GridLayoutManager.LayoutParams) view2.getLayoutParams()).getViewAdapterPosition() - firstVisibleItemPosition) / SharedMediaLayout.this.mediaColumnsCount;
                                int toColumn2 = fromColumn2 + columnsOffset;
                                canvas.save();
                                view2.setImageScale(scaleSize2, !SharedMediaLayout.this.photoVideoChangeColumnsAnimation);
                                canvas.translate((fromColumn2 * fromSize2 * (1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress)) + (toColumn2 * toSize2 * SharedMediaLayout.this.photoVideoChangeColumnsProgress), ((currentRow3 + rowsOffset) * size14) + minY);
                                if (toColumn2 < SharedMediaLayout.this.animateToColumnsCount) {
                                    i2 = i9;
                                    canvas.saveLayerAlpha(0.0f, 0.0f, view2.getMeasuredWidth() * scale3, view2.getMeasuredWidth() * scale3, (int) ((1.0f - SharedMediaLayout.this.photoVideoChangeColumnsProgress) * 255.0f), 31);
                                    view2.draw(canvas);
                                    canvas.restore();
                                } else {
                                    i2 = i9;
                                    view2.draw(canvas);
                                }
                                canvas.restore();
                                i9 = i2 + 1;
                            }
                            if (!this.drawingViews3.isEmpty()) {
                                canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), (int) (SharedMediaLayout.this.photoVideoChangeColumnsProgress * 255.0f), 31);
                                for (int i10 = 0; i10 < this.drawingViews3.size(); i10++) {
                                    this.drawingViews3.get(i10).drawCrossafadeImage(canvas);
                                }
                                canvas.restore();
                            }
                        }
                    } else {
                        for (int i11 = 0; i11 < getChildCount(); i11++) {
                            View child3 = getChildAt(i11);
                            int messageId = SharedMediaLayout.this.getMessageId(child3);
                            float alpha2 = 1.0f;
                            if (messageId != 0 && SharedMediaLayout.this.messageAlphaEnter.get(messageId, null) != null) {
                                alpha2 = SharedMediaLayout.this.messageAlphaEnter.get(messageId, valueOf).floatValue();
                            }
                            if (child3 instanceof SharedDocumentCell) {
                                SharedDocumentCell cell4 = (SharedDocumentCell) child3;
                                cell4.setEnterAnimationAlpha(alpha2);
                            } else if (child3 instanceof SharedAudioCell) {
                                SharedAudioCell cell5 = (SharedAudioCell) child3;
                                cell5.setEnterAnimationAlpha(alpha2);
                            }
                        }
                        super.dispatchDraw(canvas);
                    }
                    if (mediaPage.highlightAnimation) {
                        mediaPage.highlightProgress += 0.010666667f;
                        if (mediaPage.highlightProgress >= 1.0f) {
                            mediaPage.highlightProgress = 0.0f;
                            mediaPage.highlightAnimation = false;
                            mediaPage.highlightMessageId = 0;
                        }
                        invalidate();
                    }
                }

                @Override // org.telegram.ui.Components.BlurredRecyclerView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    if (getAdapter() == SharedMediaLayout.this.photoVideoAdapter && SharedMediaLayout.this.photoVideoChangeColumnsAnimation && (child instanceof SharedPhotoVideoCell2)) {
                        return true;
                    }
                    return super.drawChild(canvas, child, drawingTime);
                }
            };
            this.mediaPages[a5].listView.setFastScrollEnabled(1);
            this.mediaPages[a5].listView.setScrollingTouchSlop(1);
            this.mediaPages[a5].listView.setPinnedSectionOffsetY(-AndroidUtilities.dp(2.0f));
            this.mediaPages[a5].listView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
            this.mediaPages[a5].listView.setItemAnimator(null);
            this.mediaPages[a5].listView.setClipToPadding(false);
            this.mediaPages[a5].listView.setSectionsType(2);
            this.mediaPages[a5].listView.setLayoutManager(layoutManager);
            MediaPage[] mediaPageArr3 = this.mediaPages;
            mediaPageArr3[a5].addView(mediaPageArr3[a5].listView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a5].animationSupportingListView = new BlurredRecyclerView(context);
            this.mediaPages[a5].animationSupportingListView.setLayoutManager(this.mediaPages[a5].animationSupportingLayoutManager = new GridLayoutManager(context, 3) { // from class: org.telegram.ui.Components.SharedMediaLayout.11
                @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                        dy = 0;
                    }
                    return super.scrollVerticallyBy(dy, recycler, state);
                }
            });
            MediaPage[] mediaPageArr4 = this.mediaPages;
            mediaPageArr4[a5].addView(mediaPageArr4[a5].animationSupportingListView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a5].animationSupportingListView.setVisibility(8);
            this.mediaPages[a5].listView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.SharedMediaLayout.12
                @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
                public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent2, RecyclerView.State state) {
                    int i2 = 0;
                    if (mediaPage.listView.getAdapter() == SharedMediaLayout.this.gifAdapter) {
                        int position = parent2.getChildAdapterPosition(view);
                        outRect.left = 0;
                        outRect.bottom = 0;
                        if (!mediaPage.layoutManager.isFirstRow(position)) {
                            outRect.top = AndroidUtilities.dp(2.0f);
                        } else {
                            outRect.top = 0;
                        }
                        if (!mediaPage.layoutManager.isLastInRow(position)) {
                            i2 = AndroidUtilities.dp(2.0f);
                        }
                        outRect.right = i2;
                        return;
                    }
                    outRect.left = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                    outRect.right = 0;
                }
            });
            this.mediaPages[a5].listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda6
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view, int i2) {
                    SharedMediaLayout.this.m3034lambda$new$6$orgtelegramuiComponentsSharedMediaLayout(mediaPage, view, i2);
                }
            });
            this.mediaPages[a5].listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.SharedMediaLayout.13
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    SharedMediaLayout.this.scrolling = newState != 0;
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    SharedMediaLayout.this.checkLoadMoreScroll(mediaPage, (RecyclerListView) recyclerView, layoutManager);
                    if (dy != 0 && ((SharedMediaLayout.this.mediaPages[0].selectedType == 0 || SharedMediaLayout.this.mediaPages[0].selectedType == 5) && !SharedMediaLayout.this.sharedMediaData[0].messages.isEmpty())) {
                        SharedMediaLayout.this.showFloatingDateView();
                    }
                    if (dy != 0 && mediaPage.selectedType == 0) {
                        SharedMediaLayout.showFastScrollHint(mediaPage, SharedMediaLayout.this.sharedMediaData, true);
                    }
                    mediaPage.listView.checkSection(true);
                    if (mediaPage.fastScrollHintView != null) {
                        mediaPage.invalidate();
                    }
                    SharedMediaLayout.this.invalidateBlur();
                }
            });
            this.mediaPages[a5].listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda7
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
                public final boolean onItemClick(View view, int i2) {
                    return SharedMediaLayout.this.m3035lambda$new$7$orgtelegramuiComponentsSharedMediaLayout(mediaPage, view, i2);
                }
            });
            if (a5 == 0 && scrollToPositionOnRecreate != -1) {
                layoutManager.scrollToPositionWithOffset(scrollToPositionOnRecreate, scrollToOffsetOnRecreate);
            }
            final RecyclerListView listView = this.mediaPages[a5].listView;
            this.mediaPages[a5].animatingImageView = new ClippingImageView(context) { // from class: org.telegram.ui.Components.SharedMediaLayout.14
                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    listView.invalidate();
                }
            };
            this.mediaPages[a5].animatingImageView.setVisibility(8);
            this.mediaPages[a5].listView.addOverlayView(this.mediaPages[a5].animatingImageView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a5].progressView = new FlickerLoadingView(context) { // from class: org.telegram.ui.Components.SharedMediaLayout.15
                @Override // org.telegram.ui.Components.FlickerLoadingView
                public int getColumnsCount() {
                    return SharedMediaLayout.this.mediaColumnsCount;
                }

                @Override // org.telegram.ui.Components.FlickerLoadingView
                public int getViewType() {
                    setIsSingleCell(false);
                    if (mediaPage.selectedType == 0 || mediaPage.selectedType == 5) {
                        return 2;
                    }
                    if (mediaPage.selectedType == 1) {
                        return 3;
                    }
                    if (mediaPage.selectedType == 2 || mediaPage.selectedType == 4) {
                        return 4;
                    }
                    if (mediaPage.selectedType == 3) {
                        return 5;
                    }
                    if (mediaPage.selectedType == 7) {
                        return 6;
                    }
                    if (mediaPage.selectedType == 6 && SharedMediaLayout.this.scrollSlidingTextTabStrip.getTabsCount() == 1) {
                        setIsSingleCell(true);
                    }
                    return 1;
                }

                @Override // org.telegram.ui.Components.FlickerLoadingView, android.view.View
                public void onDraw(Canvas canvas) {
                    SharedMediaLayout.this.backgroundPaint.setColor(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), SharedMediaLayout.this.backgroundPaint);
                    super.onDraw(canvas);
                }
            };
            this.mediaPages[a5].progressView.showDate(false);
            if (a5 != 0) {
                this.mediaPages[a5].setVisibility(8);
            }
            this.mediaPages[a5].emptyView = new StickerEmptyView(context, this.mediaPages[a5].progressView, 1);
            this.mediaPages[a5].emptyView.setVisibility(8);
            this.mediaPages[a5].emptyView.setAnimateLayoutChange(true);
            MediaPage[] mediaPageArr5 = this.mediaPages;
            mediaPageArr5[a5].addView(mediaPageArr5[a5].emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a5].emptyView.setOnTouchListener(SharedMediaLayout$$ExternalSyntheticLambda12.INSTANCE);
            this.mediaPages[a5].emptyView.showProgress(true, false);
            this.mediaPages[a5].emptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
            this.mediaPages[a5].emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
            this.mediaPages[a5].emptyView.addView(this.mediaPages[a5].progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.mediaPages[a5].listView.setEmptyView(this.mediaPages[a5].emptyView);
            this.mediaPages[a5].listView.setAnimateEmptyView(true, 0);
            this.mediaPages[a5].scrollHelper = new RecyclerAnimationScrollHelper(this.mediaPages[a5].listView, this.mediaPages[a5].layoutManager);
            a5++;
        }
        ChatActionCell chatActionCell = new ChatActionCell(context);
        this.floatingDateView = chatActionCell;
        chatActionCell.setCustomDate((int) (System.currentTimeMillis() / 1000), false, false);
        this.floatingDateView.setAlpha(0.0f);
        this.floatingDateView.setOverrideColor(Theme.key_chat_mediaTimeBackground, Theme.key_chat_mediaTimeText);
        this.floatingDateView.setTranslationY(-AndroidUtilities.dp(48.0f));
        addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 52.0f, 0.0f, 0.0f));
        FragmentContextView fragmentContextView = new FragmentContextView(context, parent, this, false, resourcesProvider);
        this.fragmentContextView = fragmentContextView;
        addView(fragmentContextView, LayoutHelper.createFrame(-1, 38.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
        this.fragmentContextView.setDelegate(new FragmentContextView.FragmentContextViewDelegate() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.FragmentContextView.FragmentContextViewDelegate
            public final void onAnimation(boolean z, boolean z2) {
                SharedMediaLayout.this.m3036lambda$new$9$orgtelegramuiComponentsSharedMediaLayout(z, z2);
            }
        });
        addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 48, 51));
        addView(this.actionModeLayout, LayoutHelper.createFrame(-1, 48, 51));
        View view = new View(context);
        this.shadowLine = view;
        view.setBackgroundColor(getThemedColor(Theme.key_divider));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, 1);
        layoutParams.topMargin = AndroidUtilities.dp(48.0f) - 1;
        addView(this.shadowLine, layoutParams);
        updateTabs(false);
        switchToCurrentSelectedMode(false);
        if (this.hasMedia[0] >= 0) {
            loadFastScrollData(false);
        }
    }

    /* renamed from: org.telegram.ui.Components.SharedMediaLayout$5 */
    /* loaded from: classes5.dex */
    public class AnonymousClass5 implements View.OnClickListener {
        final /* synthetic */ Context val$context;

        AnonymousClass5(Context context) {
            SharedMediaLayout.this = this$0;
            this.val$context = context;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            final View dividerView = new DividerCell(this.val$context);
            ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.val$context) { // from class: org.telegram.ui.Components.SharedMediaLayout.5.1
                @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout, android.widget.FrameLayout, android.view.View
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    if (dividerView.getParent() != null) {
                        dividerView.setVisibility(8);
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        dividerView.getLayoutParams().width = getMeasuredWidth() - AndroidUtilities.dp(16.0f);
                        dividerView.setVisibility(0);
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        return;
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            };
            boolean z = true;
            final ActionBarMenuSubItem mediaZoomInItem = new ActionBarMenuSubItem(this.val$context, true, false);
            final ActionBarMenuSubItem mediaZoomOutItem = new ActionBarMenuSubItem(this.val$context, false, false);
            mediaZoomInItem.setTextAndIcon(LocaleController.getString("MediaZoomIn", R.string.MediaZoomIn), R.drawable.msg_zoomin);
            mediaZoomInItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout$5$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    SharedMediaLayout.AnonymousClass5.this.m3041lambda$onClick$0$orgtelegramuiComponentsSharedMediaLayout$5(mediaZoomInItem, mediaZoomOutItem, view2);
                }
            });
            popupLayout.addView(mediaZoomInItem);
            mediaZoomOutItem.setTextAndIcon(LocaleController.getString("MediaZoomOut", R.string.MediaZoomOut), R.drawable.msg_zoomout);
            mediaZoomOutItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout.5.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    if (!SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                        int newColumnsCount = SharedMediaLayout.this.getNextMediaColumnsCount(SharedMediaLayout.this.mediaColumnsCount, false);
                        if (newColumnsCount == SharedMediaLayout.this.getNextMediaColumnsCount(newColumnsCount, false)) {
                            mediaZoomOutItem.setEnabled(false);
                            mediaZoomOutItem.animate().alpha(0.5f).start();
                        }
                        if (SharedMediaLayout.this.mediaColumnsCount != newColumnsCount) {
                            if (!mediaZoomInItem.isEnabled()) {
                                mediaZoomInItem.setEnabled(true);
                                mediaZoomInItem.animate().alpha(1.0f).start();
                            }
                            SharedConfig.setMediaColumnsCount(newColumnsCount);
                            SharedMediaLayout.this.animateToMediaColumnsCount(newColumnsCount);
                        }
                    }
                }
            });
            if (SharedMediaLayout.this.mediaColumnsCount != 2) {
                if (SharedMediaLayout.this.mediaColumnsCount == 9) {
                    mediaZoomOutItem.setEnabled(false);
                    mediaZoomOutItem.setAlpha(0.5f);
                }
            } else {
                mediaZoomInItem.setEnabled(false);
                mediaZoomInItem.setAlpha(0.5f);
            }
            popupLayout.addView(mediaZoomOutItem);
            boolean hasDifferentTypes = (SharedMediaLayout.this.sharedMediaData[0].hasPhotos && SharedMediaLayout.this.sharedMediaData[0].hasVideos) || !SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1] || !SharedMediaLayout.this.sharedMediaData[0].startReached;
            if (!DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id)) {
                ActionBarMenuSubItem calendarItem = new ActionBarMenuSubItem(this.val$context, false, false);
                calendarItem.setTextAndIcon(LocaleController.getString("Calendar", R.string.Calendar), R.drawable.msg_calendar2);
                popupLayout.addView(calendarItem);
                calendarItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout.5.3
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        SharedMediaLayout.this.showMediaCalendar(false);
                        if (SharedMediaLayout.this.optionsWindow != null) {
                            SharedMediaLayout.this.optionsWindow.dismiss();
                        }
                    }
                });
                if (hasDifferentTypes) {
                    popupLayout.addView(dividerView);
                    final ActionBarMenuSubItem showPhotosItem = new ActionBarMenuSubItem(this.val$context, true, false, false);
                    final ActionBarMenuSubItem showVideosItem = new ActionBarMenuSubItem(this.val$context, true, false, true);
                    showPhotosItem.setTextAndIcon(LocaleController.getString("MediaShowPhotos", R.string.MediaShowPhotos), 0);
                    showPhotosItem.setChecked(SharedMediaLayout.this.sharedMediaData[0].filterType == 0 || SharedMediaLayout.this.sharedMediaData[0].filterType == 1);
                    showPhotosItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout.5.4
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view2) {
                            if (SharedMediaLayout.this.changeTypeAnimation) {
                                return;
                            }
                            if (!showVideosItem.getCheckView().isChecked() && showPhotosItem.getCheckView().isChecked()) {
                                return;
                            }
                            ActionBarMenuSubItem actionBarMenuSubItem = showPhotosItem;
                            actionBarMenuSubItem.setChecked(!actionBarMenuSubItem.getCheckView().isChecked());
                            if (!showPhotosItem.getCheckView().isChecked() || !showVideosItem.getCheckView().isChecked()) {
                                SharedMediaLayout.this.sharedMediaData[0].filterType = 2;
                            } else {
                                SharedMediaLayout.this.sharedMediaData[0].filterType = 0;
                            }
                            SharedMediaLayout.this.changeMediaFilterType();
                        }
                    });
                    popupLayout.addView(showPhotosItem);
                    showVideosItem.setTextAndIcon(LocaleController.getString("MediaShowVideos", R.string.MediaShowVideos), 0);
                    if (SharedMediaLayout.this.sharedMediaData[0].filterType != 0 && SharedMediaLayout.this.sharedMediaData[0].filterType != 2) {
                        z = false;
                    }
                    showVideosItem.setChecked(z);
                    showVideosItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout.5.5
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view2) {
                            if (SharedMediaLayout.this.changeTypeAnimation) {
                                return;
                            }
                            if (!showPhotosItem.getCheckView().isChecked() && showVideosItem.getCheckView().isChecked()) {
                                return;
                            }
                            ActionBarMenuSubItem actionBarMenuSubItem = showVideosItem;
                            actionBarMenuSubItem.setChecked(!actionBarMenuSubItem.getCheckView().isChecked());
                            if (!showPhotosItem.getCheckView().isChecked() || !showVideosItem.getCheckView().isChecked()) {
                                SharedMediaLayout.this.sharedMediaData[0].filterType = 1;
                            } else {
                                SharedMediaLayout.this.sharedMediaData[0].filterType = 0;
                            }
                            SharedMediaLayout.this.changeMediaFilterType();
                        }
                    });
                    popupLayout.addView(showVideosItem);
                }
            }
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            sharedMediaLayout.optionsWindow = AlertsCreator.showPopupMenu(popupLayout, sharedMediaLayout.photoVideoOptionsItem, 0, -AndroidUtilities.dp(56.0f));
        }

        /* renamed from: lambda$onClick$0$org-telegram-ui-Components-SharedMediaLayout$5 */
        public /* synthetic */ void m3041lambda$onClick$0$orgtelegramuiComponentsSharedMediaLayout$5(ActionBarMenuSubItem mediaZoomInItem, ActionBarMenuSubItem mediaZoomOutItem, View view1) {
            if (SharedMediaLayout.this.photoVideoChangeColumnsAnimation) {
                return;
            }
            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
            int newColumnsCount = sharedMediaLayout.getNextMediaColumnsCount(sharedMediaLayout.mediaColumnsCount, true);
            if (newColumnsCount == SharedMediaLayout.this.getNextMediaColumnsCount(newColumnsCount, true)) {
                mediaZoomInItem.setEnabled(false);
                mediaZoomInItem.animate().alpha(0.5f).start();
            }
            if (SharedMediaLayout.this.mediaColumnsCount != newColumnsCount) {
                if (!mediaZoomOutItem.isEnabled()) {
                    mediaZoomOutItem.setEnabled(true);
                    mediaZoomOutItem.animate().alpha(1.0f).start();
                }
                SharedConfig.setMediaColumnsCount(newColumnsCount);
                SharedMediaLayout.this.animateToMediaColumnsCount(newColumnsCount);
            }
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3030lambda$new$2$orgtelegramuiComponentsSharedMediaLayout(View v) {
        closeActionMode();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3031lambda$new$3$orgtelegramuiComponentsSharedMediaLayout(View v) {
        onActionBarItemClick(v, 102);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3032lambda$new$4$orgtelegramuiComponentsSharedMediaLayout(View v) {
        onActionBarItemClick(v, 100);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3033lambda$new$5$orgtelegramuiComponentsSharedMediaLayout(View v) {
        onActionBarItemClick(v, 101);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3034lambda$new$6$orgtelegramuiComponentsSharedMediaLayout(MediaPage mediaPage, View view, int position) {
        MessageObject messageObject;
        long user_id;
        if (mediaPage.selectedType != 7) {
            if (mediaPage.selectedType != 6 || !(view instanceof ProfileSearchCell)) {
                if (mediaPage.selectedType != 1 || !(view instanceof SharedDocumentCell)) {
                    if (mediaPage.selectedType != 3 || !(view instanceof SharedLinkCell)) {
                        if ((mediaPage.selectedType == 2 || mediaPage.selectedType == 4) && (view instanceof SharedAudioCell)) {
                            onItemClick(position, view, ((SharedAudioCell) view).getMessage(), 0, mediaPage.selectedType);
                            return;
                        } else if (mediaPage.selectedType != 5 || !(view instanceof ContextLinkCell)) {
                            if (mediaPage.selectedType == 0 && (view instanceof SharedPhotoVideoCell2) && (messageObject = ((SharedPhotoVideoCell2) view).getMessageObject()) != null) {
                                onItemClick(position, view, messageObject, 0, mediaPage.selectedType);
                                return;
                            }
                            return;
                        } else {
                            onItemClick(position, view, (MessageObject) ((ContextLinkCell) view).getParentObject(), 0, mediaPage.selectedType);
                            return;
                        }
                    }
                    onItemClick(position, view, ((SharedLinkCell) view).getMessage(), 0, mediaPage.selectedType);
                    return;
                }
                onItemClick(position, view, ((SharedDocumentCell) view).getMessage(), 0, mediaPage.selectedType);
                return;
            }
            TLRPC.Chat chat = ((ProfileSearchCell) view).getChat();
            Bundle args = new Bundle();
            args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, chat.id);
            if (!this.profileActivity.getMessagesController().checkCanOpenChat(args, this.profileActivity)) {
                return;
            }
            this.profileActivity.presentFragment(new ChatActivity(args));
        } else if (!(view instanceof UserCell)) {
            RecyclerView.Adapter adapter = mediaPage.listView.getAdapter();
            GroupUsersSearchAdapter groupUsersSearchAdapter = this.groupUsersSearchAdapter;
            if (adapter == groupUsersSearchAdapter) {
                TLObject object = groupUsersSearchAdapter.getItem(position);
                if (object instanceof TLRPC.ChannelParticipant) {
                    TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) object;
                    user_id = MessageObject.getPeerId(channelParticipant.peer);
                } else if (object instanceof TLRPC.ChatParticipant) {
                    TLRPC.ChatParticipant chatParticipant = (TLRPC.ChatParticipant) object;
                    user_id = chatParticipant.user_id;
                } else {
                    return;
                }
                if (user_id == 0 || user_id == this.profileActivity.getUserConfig().getClientUserId()) {
                    return;
                }
                Bundle args2 = new Bundle();
                args2.putLong("user_id", user_id);
                this.profileActivity.presentFragment(new ProfileActivity(args2));
            }
        } else {
            TLRPC.ChatParticipant participant = !this.chatUsersAdapter.sortedUsers.isEmpty() ? this.chatUsersAdapter.chatInfo.participants.participants.get(((Integer) this.chatUsersAdapter.sortedUsers.get(position)).intValue()) : this.chatUsersAdapter.chatInfo.participants.participants.get(position);
            onMemberClick(participant, false);
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ boolean m3035lambda$new$7$orgtelegramuiComponentsSharedMediaLayout(MediaPage mediaPage, View view, int position) {
        MessageObject messageObject;
        if (this.photoVideoChangeColumnsAnimation) {
            return false;
        }
        if (this.isActionModeShowed) {
            mediaPage.listView.getOnItemClickListener().onItemClick(view, position);
            return true;
        } else if (mediaPage.selectedType != 7 || !(view instanceof UserCell)) {
            if (mediaPage.selectedType != 1 || !(view instanceof SharedDocumentCell)) {
                if (mediaPage.selectedType != 3 || !(view instanceof SharedLinkCell)) {
                    if ((mediaPage.selectedType != 2 && mediaPage.selectedType != 4) || !(view instanceof SharedAudioCell)) {
                        if (mediaPage.selectedType != 5 || !(view instanceof ContextLinkCell)) {
                            if (mediaPage.selectedType == 0 && (view instanceof SharedPhotoVideoCell2) && (messageObject = ((SharedPhotoVideoCell2) view).getMessageObject()) != null) {
                                return onItemLongClick(messageObject, view, 0);
                            }
                            return false;
                        }
                        return onItemLongClick((MessageObject) ((ContextLinkCell) view).getParentObject(), view, 0);
                    }
                    return onItemLongClick(((SharedAudioCell) view).getMessage(), view, 0);
                }
                return onItemLongClick(((SharedLinkCell) view).getMessage(), view, 0);
            }
            return onItemLongClick(((SharedDocumentCell) view).getMessage(), view, 0);
        } else {
            TLRPC.ChatParticipant participant = !this.chatUsersAdapter.sortedUsers.isEmpty() ? this.chatUsersAdapter.chatInfo.participants.participants.get(((Integer) this.chatUsersAdapter.sortedUsers.get(position)).intValue()) : this.chatUsersAdapter.chatInfo.participants.participants.get(position);
            return onMemberClick(participant, true);
        }
    }

    public static /* synthetic */ boolean lambda$new$8(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3036lambda$new$9$orgtelegramuiComponentsSharedMediaLayout(boolean start, boolean show) {
        if (!start) {
            requestLayout();
        }
    }

    protected void invalidateBlur() {
    }

    public void setForwardRestrictedHint(HintView hintView) {
        this.fwdRestrictedHint = hintView;
    }

    public int getMessageId(View child) {
        if (child instanceof SharedPhotoVideoCell2) {
            return ((SharedPhotoVideoCell2) child).getMessageId();
        }
        if (child instanceof SharedDocumentCell) {
            SharedDocumentCell cell = (SharedDocumentCell) child;
            return cell.getMessage().getId();
        } else if (child instanceof SharedAudioCell) {
            SharedAudioCell cell2 = (SharedAudioCell) child;
            return cell2.getMessage().getId();
        } else {
            return 0;
        }
    }

    private void updateForwardItem() {
        if (this.forwardItem == null) {
            return;
        }
        boolean noforwards = this.profileActivity.getMessagesController().isChatNoForwards(-this.dialog_id) || hasNoforwardsMessage();
        this.forwardItem.setAlpha(noforwards ? 0.5f : 1.0f);
        if (noforwards && this.forwardItem.getBackground() != null) {
            this.forwardItem.setBackground(null);
        } else if (!noforwards && this.forwardItem.getBackground() == null) {
            this.forwardItem.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), 5));
        }
    }

    private boolean hasNoforwardsMessage() {
        MessageObject msg;
        boolean hasNoforwardsMessage = false;
        for (int a = 1; a >= 0; a--) {
            ArrayList<Integer> ids = new ArrayList<>();
            for (int b = 0; b < this.selectedFiles[a].size(); b++) {
                ids.add(Integer.valueOf(this.selectedFiles[a].keyAt(b)));
            }
            Iterator<Integer> it = ids.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Integer id1 = it.next();
                if (id1.intValue() > 0 && (msg = this.selectedFiles[a].get(id1.intValue())) != null && msg.messageOwner != null && msg.messageOwner.noforwards) {
                    hasNoforwardsMessage = true;
                    break;
                }
            }
            if (hasNoforwardsMessage) {
                break;
            }
        }
        return hasNoforwardsMessage;
    }

    public void changeMediaFilterType() {
        final MediaPage mediaPage = getMediaPage(0);
        if (mediaPage != null && mediaPage.getMeasuredHeight() > 0 && mediaPage.getMeasuredWidth() > 0) {
            Bitmap bitmap = null;
            try {
                bitmap = Bitmap.createBitmap(mediaPage.getMeasuredWidth(), mediaPage.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (bitmap != null) {
                this.changeTypeAnimation = true;
                Canvas canvas = new Canvas(bitmap);
                mediaPage.listView.draw(canvas);
                final View view = new View(mediaPage.getContext());
                view.setBackground(new BitmapDrawable(bitmap));
                mediaPage.addView(view);
                final Bitmap finalBitmap = bitmap;
                view.animate().alpha(0.0f).setDuration(200L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SharedMediaLayout.16
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        SharedMediaLayout.this.changeTypeAnimation = false;
                        if (view.getParent() != null) {
                            mediaPage.removeView(view);
                            finalBitmap.recycle();
                        }
                    }
                }).start();
                mediaPage.listView.setAlpha(0.0f);
                mediaPage.listView.animate().alpha(1.0f).setDuration(200L).start();
            }
        }
        int[] counts = this.sharedMediaPreloader.getLastMediaCount();
        ArrayList<MessageObject> messages = this.sharedMediaPreloader.getSharedMediaData()[0].messages;
        if (this.sharedMediaData[0].filterType == 0) {
            this.sharedMediaData[0].setTotalCount(counts[0]);
        } else if (this.sharedMediaData[0].filterType == 1) {
            this.sharedMediaData[0].setTotalCount(counts[6]);
        } else {
            this.sharedMediaData[0].setTotalCount(counts[7]);
        }
        this.sharedMediaData[0].fastScrollDataLoaded = false;
        jumpToDate(0, DialogObject.isEncryptedDialog(this.dialog_id) ? Integer.MIN_VALUE : Integer.MAX_VALUE, 0, true);
        loadFastScrollData(false);
        this.delegate.updateSelectedMediaTabText();
        boolean enc = DialogObject.isEncryptedDialog(this.dialog_id);
        for (int i = 0; i < messages.size(); i++) {
            MessageObject messageObject = messages.get(i);
            if (this.sharedMediaData[0].filterType == 0) {
                this.sharedMediaData[0].addMessage(messageObject, 0, false, enc);
            } else if (this.sharedMediaData[0].filterType == 1) {
                if (messageObject.isPhoto()) {
                    this.sharedMediaData[0].addMessage(messageObject, 0, false, enc);
                }
            } else if (!messageObject.isPhoto()) {
                this.sharedMediaData[0].addMessage(messageObject, 0, false, enc);
            }
        }
    }

    public MediaPage getMediaPage(int type) {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                if (mediaPageArr[i].selectedType != 0) {
                    i++;
                } else {
                    return this.mediaPages[i];
                }
            } else {
                return null;
            }
        }
    }

    public void showMediaCalendar(boolean fromFastScroll) {
        MediaPage mediaPage;
        if (fromFastScroll && getY() != 0.0f && this.viewType == 1) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("dialog_id", this.dialog_id);
        int date = 0;
        if (fromFastScroll && (mediaPage = getMediaPage(0)) != null) {
            ArrayList<Period> periods = this.sharedMediaData[0].fastScrollPeriods;
            Period period = null;
            int position = mediaPage.layoutManager.findFirstVisibleItemPosition();
            if (position >= 0) {
                if (periods != null) {
                    int i = 0;
                    while (true) {
                        if (i >= periods.size()) {
                            break;
                        } else if (position > periods.get(i).startOffset) {
                            i++;
                        } else {
                            period = periods.get(i);
                            break;
                        }
                    }
                    if (period == null) {
                        period = periods.get(periods.size() - 1);
                    }
                }
                if (period != null) {
                    date = period.date;
                }
            }
        }
        bundle.putInt(CommonProperties.TYPE, 1);
        CalendarActivity calendarActivity = new CalendarActivity(bundle, this.sharedMediaData[0].filterType, date);
        calendarActivity.setCallback(new CalendarActivity.Callback() { // from class: org.telegram.ui.Components.SharedMediaLayout.17
            @Override // org.telegram.ui.CalendarActivity.Callback
            public void onDateSelected(int messageId, int startOffset) {
                int index = -1;
                for (int i2 = 0; i2 < SharedMediaLayout.this.sharedMediaData[0].messages.size(); i2++) {
                    if (SharedMediaLayout.this.sharedMediaData[0].messages.get(i2).getId() == messageId) {
                        index = i2;
                    }
                }
                MediaPage mediaPage2 = SharedMediaLayout.this.getMediaPage(0);
                if (index < 0 || mediaPage2 == null) {
                    SharedMediaLayout.this.jumpToDate(0, messageId, startOffset, true);
                } else {
                    mediaPage2.layoutManager.scrollToPositionWithOffset(index, 0);
                }
                if (mediaPage2 != null) {
                    mediaPage2.highlightMessageId = messageId;
                    mediaPage2.highlightAnimation = false;
                }
            }
        });
        this.profileActivity.presentFragment(calendarActivity);
    }

    private void startPinchToMediaColumnsCount(boolean pinchScaleUp) {
        if (this.photoVideoChangeColumnsAnimation) {
            return;
        }
        MediaPage mediaPage = null;
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i >= mediaPageArr.length) {
                break;
            } else if (mediaPageArr[i].selectedType != 0) {
                i++;
            } else {
                mediaPage = this.mediaPages[i];
                break;
            }
        }
        if (mediaPage != null) {
            int newColumnsCount = getNextMediaColumnsCount(this.mediaColumnsCount, pinchScaleUp);
            this.animateToColumnsCount = newColumnsCount;
            if (newColumnsCount != this.mediaColumnsCount) {
                mediaPage.animationSupportingListView.setVisibility(0);
                mediaPage.animationSupportingListView.setAdapter(this.animationSupportingPhotoVideoAdapter);
                mediaPage.animationSupportingLayoutManager.setSpanCount(newColumnsCount);
                AndroidUtilities.updateVisibleRows(mediaPage.listView);
                this.photoVideoChangeColumnsAnimation = true;
                this.sharedMediaData[0].setListFrozen(true);
                this.photoVideoChangeColumnsProgress = 0.0f;
                if (this.pinchCenterPosition >= 0) {
                    int k = 0;
                    while (true) {
                        MediaPage[] mediaPageArr2 = this.mediaPages;
                        if (k < mediaPageArr2.length) {
                            if (mediaPageArr2[k].selectedType == 0) {
                                this.mediaPages[k].animationSupportingLayoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, this.pinchCenterOffset - this.mediaPages[k].animationSupportingListView.getPaddingTop());
                            }
                            k++;
                        } else {
                            return;
                        }
                    }
                } else {
                    saveScrollPosition();
                }
            }
        }
    }

    private void finishPinchToMediaColumnsCount() {
        if (this.photoVideoChangeColumnsAnimation) {
            MediaPage mediaPage = null;
            int i = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i >= mediaPageArr.length) {
                    break;
                } else if (mediaPageArr[i].selectedType != 0) {
                    i++;
                } else {
                    mediaPage = this.mediaPages[i];
                    break;
                }
            }
            if (mediaPage != null) {
                float f = this.photoVideoChangeColumnsProgress;
                float f2 = 1.0f;
                if (f == 1.0f) {
                    int oldItemCount = this.photoVideoAdapter.getItemCount();
                    this.photoVideoChangeColumnsAnimation = false;
                    this.sharedMediaData[0].setListFrozen(false);
                    mediaPage.animationSupportingListView.setVisibility(8);
                    int i2 = this.animateToColumnsCount;
                    this.mediaColumnsCount = i2;
                    SharedConfig.setMediaColumnsCount(i2);
                    mediaPage.layoutManager.setSpanCount(this.mediaColumnsCount);
                    mediaPage.listView.invalidate();
                    if (this.photoVideoAdapter.getItemCount() == oldItemCount) {
                        AndroidUtilities.updateVisibleRows(mediaPage.listView);
                    } else {
                        this.photoVideoAdapter.notifyDataSetChanged();
                    }
                    if (this.pinchCenterPosition >= 0) {
                        int k = 0;
                        while (true) {
                            MediaPage[] mediaPageArr2 = this.mediaPages;
                            if (k < mediaPageArr2.length) {
                                if (mediaPageArr2[k].selectedType == 0) {
                                    View view = this.mediaPages[k].animationSupportingLayoutManager.findViewByPosition(this.pinchCenterPosition);
                                    if (view != null) {
                                        this.pinchCenterOffset = view.getTop();
                                    }
                                    this.mediaPages[k].layoutManager.scrollToPositionWithOffset(this.pinchCenterPosition, (-this.mediaPages[k].listView.getPaddingTop()) + this.pinchCenterOffset);
                                }
                                k++;
                            } else {
                                return;
                            }
                        }
                    } else {
                        saveScrollPosition();
                    }
                } else if (f == 0.0f) {
                    this.photoVideoChangeColumnsAnimation = false;
                    this.sharedMediaData[0].setListFrozen(false);
                    mediaPage.animationSupportingListView.setVisibility(8);
                    mediaPage.listView.invalidate();
                } else {
                    final boolean forward2 = f > 0.2f;
                    float[] fArr = new float[2];
                    fArr[0] = f;
                    if (!forward2) {
                        f2 = 0.0f;
                    }
                    fArr[1] = f2;
                    ValueAnimator animator = ValueAnimator.ofFloat(fArr);
                    final MediaPage finalMediaPage = mediaPage;
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.SharedMediaLayout.18
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            SharedMediaLayout.this.photoVideoChangeColumnsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                            finalMediaPage.listView.invalidate();
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SharedMediaLayout.19
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            View view2;
                            int oldItemCount2 = SharedMediaLayout.this.photoVideoAdapter.getItemCount();
                            SharedMediaLayout.this.photoVideoChangeColumnsAnimation = false;
                            SharedMediaLayout.this.sharedMediaData[0].setListFrozen(false);
                            if (forward2) {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.mediaColumnsCount = sharedMediaLayout.animateToColumnsCount;
                                SharedConfig.setMediaColumnsCount(SharedMediaLayout.this.animateToColumnsCount);
                                finalMediaPage.layoutManager.setSpanCount(SharedMediaLayout.this.mediaColumnsCount);
                            }
                            if (forward2) {
                                if (SharedMediaLayout.this.photoVideoAdapter.getItemCount() != oldItemCount2) {
                                    SharedMediaLayout.this.photoVideoAdapter.notifyDataSetChanged();
                                } else {
                                    AndroidUtilities.updateVisibleRows(finalMediaPage.listView);
                                }
                            }
                            finalMediaPage.animationSupportingListView.setVisibility(8);
                            if (SharedMediaLayout.this.pinchCenterPosition < 0) {
                                SharedMediaLayout.this.saveScrollPosition();
                            } else {
                                for (int k2 = 0; k2 < SharedMediaLayout.this.mediaPages.length; k2++) {
                                    if (SharedMediaLayout.this.mediaPages[k2].selectedType == 0) {
                                        if (forward2 && (view2 = SharedMediaLayout.this.mediaPages[k2].animationSupportingLayoutManager.findViewByPosition(SharedMediaLayout.this.pinchCenterPosition)) != null) {
                                            SharedMediaLayout.this.pinchCenterOffset = view2.getTop();
                                        }
                                        SharedMediaLayout.this.mediaPages[k2].layoutManager.scrollToPositionWithOffset(SharedMediaLayout.this.pinchCenterPosition, (-SharedMediaLayout.this.mediaPages[k2].listView.getPaddingTop()) + SharedMediaLayout.this.pinchCenterOffset);
                                    }
                                }
                            }
                            super.onAnimationEnd(animation);
                        }
                    });
                    animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    animator.setDuration(200L);
                    animator.start();
                }
            }
        }
    }

    public void animateToMediaColumnsCount(final int newColumnsCount) {
        final MediaPage mediaPage = getMediaPage(0);
        this.pinchCenterPosition = -1;
        if (mediaPage != null) {
            mediaPage.listView.stopScroll();
            this.animateToColumnsCount = newColumnsCount;
            mediaPage.animationSupportingListView.setVisibility(0);
            mediaPage.animationSupportingListView.setAdapter(this.animationSupportingPhotoVideoAdapter);
            mediaPage.animationSupportingLayoutManager.setSpanCount(newColumnsCount);
            AndroidUtilities.updateVisibleRows(mediaPage.listView);
            this.photoVideoChangeColumnsAnimation = true;
            this.sharedMediaData[0].setListFrozen(true);
            this.photoVideoChangeColumnsProgress = 0.0f;
            saveScrollPosition();
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animationIndex = NotificationCenter.getInstance(this.profileActivity.getCurrentAccount()).setAnimationInProgress(this.animationIndex, null);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.SharedMediaLayout.20
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SharedMediaLayout.this.photoVideoChangeColumnsProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    mediaPage.listView.invalidate();
                }
            });
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SharedMediaLayout.21
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    NotificationCenter.getInstance(SharedMediaLayout.this.profileActivity.getCurrentAccount()).onAnimationFinish(SharedMediaLayout.this.animationIndex);
                    int oldItemCount = SharedMediaLayout.this.photoVideoAdapter.getItemCount();
                    SharedMediaLayout.this.photoVideoChangeColumnsAnimation = false;
                    SharedMediaLayout.this.sharedMediaData[0].setListFrozen(false);
                    SharedMediaLayout.this.mediaColumnsCount = newColumnsCount;
                    mediaPage.layoutManager.setSpanCount(SharedMediaLayout.this.mediaColumnsCount);
                    if (SharedMediaLayout.this.photoVideoAdapter.getItemCount() != oldItemCount) {
                        SharedMediaLayout.this.photoVideoAdapter.notifyDataSetChanged();
                    } else {
                        AndroidUtilities.updateVisibleRows(mediaPage.listView);
                    }
                    mediaPage.animationSupportingListView.setVisibility(8);
                    SharedMediaLayout.this.saveScrollPosition();
                }
            });
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animator.setStartDelay(100L);
            animator.setDuration(350L);
            animator.start();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.scrollSlidingTextTabStrip != null) {
            canvas.save();
            canvas.translate(this.scrollSlidingTextTabStrip.getX(), this.scrollSlidingTextTabStrip.getY());
            this.scrollSlidingTextTabStrip.drawBackground(canvas);
            canvas.restore();
        }
        super.dispatchDraw(canvas);
        FragmentContextView fragmentContextView = this.fragmentContextView;
        if (fragmentContextView != null && fragmentContextView.isCallStyle()) {
            canvas.save();
            canvas.translate(this.fragmentContextView.getX(), this.fragmentContextView.getY());
            this.fragmentContextView.setDrawOverlay(true);
            this.fragmentContextView.draw(canvas);
            this.fragmentContextView.setDrawOverlay(false);
            canvas.restore();
        }
    }

    private ScrollSlidingTextTabStripInner createScrollingTextTabStrip(Context context) {
        ScrollSlidingTextTabStripInner scrollSlidingTextTabStrip = new ScrollSlidingTextTabStripInner(context, this.resourcesProvider);
        int i = this.initialTab;
        if (i != -1) {
            scrollSlidingTextTabStrip.setInitialTabId(i);
            this.initialTab = -1;
        }
        scrollSlidingTextTabStrip.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        scrollSlidingTextTabStrip.setColors(Theme.key_profile_tabSelectedLine, Theme.key_profile_tabSelectedText, Theme.key_profile_tabText, Theme.key_profile_tabSelector);
        scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.SharedMediaLayout.22
            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public void onPageSelected(int id, boolean forward2) {
                if (SharedMediaLayout.this.mediaPages[0].selectedType != id) {
                    SharedMediaLayout.this.mediaPages[1].selectedType = id;
                    SharedMediaLayout.this.mediaPages[1].setVisibility(0);
                    SharedMediaLayout.this.hideFloatingDateView(true);
                    SharedMediaLayout.this.switchToCurrentSelectedMode(true);
                    SharedMediaLayout.this.animatingForward = forward2;
                    SharedMediaLayout.this.onSelectedTabChanged();
                }
            }

            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public void onSamePageSelected() {
                SharedMediaLayout.this.scrollToTop();
            }

            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public void onPageScrolled(float progress) {
                if (progress != 1.0f || SharedMediaLayout.this.mediaPages[1].getVisibility() == 0) {
                    if (SharedMediaLayout.this.animatingForward) {
                        SharedMediaLayout.this.mediaPages[0].setTranslationX((-progress) * SharedMediaLayout.this.mediaPages[0].getMeasuredWidth());
                        SharedMediaLayout.this.mediaPages[1].setTranslationX(SharedMediaLayout.this.mediaPages[0].getMeasuredWidth() - (SharedMediaLayout.this.mediaPages[0].getMeasuredWidth() * progress));
                    } else {
                        SharedMediaLayout.this.mediaPages[0].setTranslationX(SharedMediaLayout.this.mediaPages[0].getMeasuredWidth() * progress);
                        SharedMediaLayout.this.mediaPages[1].setTranslationX((SharedMediaLayout.this.mediaPages[0].getMeasuredWidth() * progress) - SharedMediaLayout.this.mediaPages[0].getMeasuredWidth());
                    }
                    float photoVideoOptionsAlpha = 0.0f;
                    if (SharedMediaLayout.this.mediaPages[0].selectedType == 0) {
                        photoVideoOptionsAlpha = 1.0f - progress;
                    }
                    if (SharedMediaLayout.this.mediaPages[1].selectedType == 0) {
                        photoVideoOptionsAlpha = progress;
                    }
                    SharedMediaLayout.this.photoVideoOptionsItem.setAlpha(photoVideoOptionsAlpha);
                    SharedMediaLayout.this.photoVideoOptionsItem.setVisibility((photoVideoOptionsAlpha == 0.0f || !SharedMediaLayout.this.canShowSearchItem()) ? 4 : 0);
                    if (SharedMediaLayout.this.canShowSearchItem()) {
                        if (SharedMediaLayout.this.searchItemState == 1) {
                            SharedMediaLayout.this.searchItem.setAlpha(progress);
                        } else if (SharedMediaLayout.this.searchItemState == 2) {
                            SharedMediaLayout.this.searchItem.setAlpha(1.0f - progress);
                        }
                    } else {
                        SharedMediaLayout.this.searchItem.setVisibility(4);
                        SharedMediaLayout.this.searchItem.setAlpha(0.0f);
                    }
                    if (progress == 1.0f) {
                        MediaPage tempPage = SharedMediaLayout.this.mediaPages[0];
                        SharedMediaLayout.this.mediaPages[0] = SharedMediaLayout.this.mediaPages[1];
                        SharedMediaLayout.this.mediaPages[1] = tempPage;
                        SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                        if (SharedMediaLayout.this.searchItemState == 2) {
                            SharedMediaLayout.this.searchItem.setVisibility(4);
                        }
                        SharedMediaLayout.this.searchItemState = 0;
                        SharedMediaLayout.this.startStopVisibleGifs();
                    }
                }
            }
        });
        return scrollSlidingTextTabStrip;
    }

    protected void drawBackgroundWithBlur(Canvas canvas, float y, android.graphics.Rect rectTmp2, Paint backgroundPaint) {
        canvas.drawRect(rectTmp2, backgroundPaint);
    }

    private boolean fillMediaData(int type) {
        SharedMediaData[] mediaData = this.sharedMediaPreloader.getSharedMediaData();
        if (mediaData == null) {
            return false;
        }
        if (type == 0) {
            if (!this.sharedMediaData[type].fastScrollDataLoaded) {
                this.sharedMediaData[type].totalCount = mediaData[type].totalCount;
            }
        } else {
            this.sharedMediaData[type].totalCount = mediaData[type].totalCount;
        }
        this.sharedMediaData[type].messages.addAll(mediaData[type].messages);
        this.sharedMediaData[type].sections.addAll(mediaData[type].sections);
        for (Map.Entry<String, ArrayList<MessageObject>> entry : mediaData[type].sectionArrays.entrySet()) {
            this.sharedMediaData[type].sectionArrays.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        for (int i = 0; i < 2; i++) {
            this.sharedMediaData[type].messagesDict[i] = mediaData[type].messagesDict[i].clone();
            this.sharedMediaData[type].max_id[i] = mediaData[type].max_id[i];
            this.sharedMediaData[type].endReached[i] = mediaData[type].endReached[i];
        }
        this.sharedMediaData[type].fastScrollPeriods.addAll(mediaData[type].fastScrollPeriods);
        return !mediaData[type].messages.isEmpty();
    }

    public void showFloatingDateView() {
    }

    public void hideFloatingDateView(boolean animated) {
        AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
        if (this.floatingDateView.getTag() == null) {
            return;
        }
        this.floatingDateView.setTag(null);
        AnimatorSet animatorSet = this.floatingDateAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.floatingDateAnimation = null;
        }
        if (!animated) {
            this.floatingDateView.setAlpha(0.0f);
            return;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.floatingDateAnimation = animatorSet2;
        animatorSet2.setDuration(180L);
        this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.floatingDateView, View.TRANSLATION_Y, (-AndroidUtilities.dp(48.0f)) + this.additionalFloatingTranslation));
        this.floatingDateAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SharedMediaLayout.23
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                SharedMediaLayout.this.floatingDateAnimation = null;
            }
        });
        this.floatingDateAnimation.start();
    }

    public void scrollToTop() {
        int height;
        switch (this.mediaPages[0].selectedType) {
            case 0:
                height = SharedPhotoVideoCell.getItemSize(1);
                break;
            case 1:
            case 2:
            case 4:
                height = AndroidUtilities.dp(56.0f);
                break;
            case 3:
                height = AndroidUtilities.dp(100.0f);
                break;
            case 5:
                height = AndroidUtilities.dp(60.0f);
                break;
            default:
                height = AndroidUtilities.dp(58.0f);
                break;
        }
        int scrollDistance = this.mediaPages[0].selectedType == 0 ? (this.mediaPages[0].layoutManager.findFirstVisibleItemPosition() / this.mediaColumnsCount) * height : this.mediaPages[0].layoutManager.findFirstVisibleItemPosition() * height;
        if (scrollDistance >= this.mediaPages[0].listView.getMeasuredHeight() * 1.2f) {
            this.mediaPages[0].scrollHelper.setScrollDirection(1);
            this.mediaPages[0].scrollHelper.scrollToPosition(0, 0, false, true);
            return;
        }
        this.mediaPages[0].listView.smoothScrollToPosition(0);
    }

    public void checkLoadMoreScroll(MediaPage mediaPage, final RecyclerListView recyclerView, LinearLayoutManager layoutManager) {
        int threshold;
        RecyclerView.ViewHolder holder;
        int type;
        if (this.photoVideoChangeColumnsAnimation || this.jumpToRunnable != null) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (recyclerView.getFastScroll() != null && recyclerView.getFastScroll().isPressed() && currentTime - mediaPage.lastCheckScrollTime < 300) {
            return;
        }
        mediaPage.lastCheckScrollTime = currentTime;
        if ((this.searching && this.searchWas) || mediaPage.selectedType == 7) {
            return;
        }
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
        int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
        int totalItemCount = recyclerView.getAdapter().getItemCount();
        if (mediaPage.selectedType == 0 || mediaPage.selectedType == 1 || mediaPage.selectedType == 2 || mediaPage.selectedType == 4) {
            final int type2 = mediaPage.selectedType;
            totalItemCount = this.sharedMediaData[type2].getStartOffset() + this.sharedMediaData[type2].messages.size();
            if (this.sharedMediaData[type2].fastScrollDataLoaded && this.sharedMediaData[type2].fastScrollPeriods.size() > 2 && mediaPage.selectedType == 0 && this.sharedMediaData[type2].messages.size() != 0) {
                int columnsCount = 1;
                if (type2 == 0) {
                    columnsCount = this.mediaColumnsCount;
                }
                int jumpToTreshold = (int) ((recyclerView.getMeasuredHeight() / (recyclerView.getMeasuredWidth() / columnsCount)) * columnsCount * 1.5f);
                if (jumpToTreshold < 100) {
                    jumpToTreshold = 100;
                }
                if (jumpToTreshold < this.sharedMediaData[type2].fastScrollPeriods.get(1).startOffset) {
                    jumpToTreshold = this.sharedMediaData[type2].fastScrollPeriods.get(1).startOffset;
                }
                if ((firstVisibleItem > totalItemCount && firstVisibleItem - totalItemCount > jumpToTreshold) || (firstVisibleItem + visibleItemCount < this.sharedMediaData[type2].startOffset && this.sharedMediaData[0].startOffset - (firstVisibleItem + visibleItemCount) > jumpToTreshold)) {
                    Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda17
                        @Override // java.lang.Runnable
                        public final void run() {
                            SharedMediaLayout.this.m3025xe0c6abc(type2, recyclerView);
                        }
                    };
                    this.jumpToRunnable = runnable;
                    AndroidUtilities.runOnUIThread(runnable);
                    return;
                }
            }
        }
        if (mediaPage.selectedType != 7) {
            if (mediaPage.selectedType != 6) {
                if (mediaPage.selectedType != 0) {
                    if (mediaPage.selectedType == 5) {
                        threshold = 10;
                    } else {
                        threshold = 6;
                    }
                } else {
                    threshold = 3;
                }
                if ((firstVisibleItem + visibleItemCount > totalItemCount - threshold || this.sharedMediaData[mediaPage.selectedType].loadingAfterFastScroll) && !this.sharedMediaData[mediaPage.selectedType].loading) {
                    if (mediaPage.selectedType != 0) {
                        if (mediaPage.selectedType != 1) {
                            if (mediaPage.selectedType != 2) {
                                if (mediaPage.selectedType != 4) {
                                    if (mediaPage.selectedType == 5) {
                                        type = 5;
                                    } else {
                                        type = 3;
                                    }
                                } else {
                                    type = 4;
                                }
                            } else {
                                type = 2;
                            }
                        } else {
                            type = 1;
                        }
                    } else {
                        type = 0;
                        if (this.sharedMediaData[0].filterType == 1) {
                            type = 6;
                        } else if (this.sharedMediaData[0].filterType == 2) {
                            type = 7;
                        }
                    }
                    if (!this.sharedMediaData[mediaPage.selectedType].endReached[0]) {
                        this.sharedMediaData[mediaPage.selectedType].loading = true;
                        this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, this.sharedMediaData[mediaPage.selectedType].max_id[0], 0, type, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[mediaPage.selectedType].requestIndex);
                    } else if (this.mergeDialogId != 0 && !this.sharedMediaData[mediaPage.selectedType].endReached[1]) {
                        this.sharedMediaData[mediaPage.selectedType].loading = true;
                        this.profileActivity.getMediaDataController().loadMedia(this.mergeDialogId, 50, this.sharedMediaData[mediaPage.selectedType].max_id[1], 0, type, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[mediaPage.selectedType].requestIndex);
                    }
                }
                int startOffset = this.sharedMediaData[mediaPage.selectedType].startOffset;
                if (mediaPage.selectedType == 0) {
                    startOffset = this.photoVideoAdapter.getPositionForIndex(0);
                }
                if (firstVisibleItem - startOffset < threshold + 1 && !this.sharedMediaData[mediaPage.selectedType].loading && !this.sharedMediaData[mediaPage.selectedType].startReached && !this.sharedMediaData[mediaPage.selectedType].loadingAfterFastScroll) {
                    loadFromStart(mediaPage.selectedType);
                }
                if (this.mediaPages[0].listView == recyclerView) {
                    if ((this.mediaPages[0].selectedType == 0 || this.mediaPages[0].selectedType == 5) && firstVisibleItem != -1 && (holder = recyclerView.findViewHolderForAdapterPosition(firstVisibleItem)) != null && holder.getItemViewType() == 0) {
                        if (holder.itemView instanceof SharedPhotoVideoCell) {
                            SharedPhotoVideoCell cell = (SharedPhotoVideoCell) holder.itemView;
                            MessageObject messageObject = cell.getMessageObject(0);
                            if (messageObject != null) {
                                this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
                            }
                        } else if (holder.itemView instanceof ContextLinkCell) {
                            ContextLinkCell cell2 = (ContextLinkCell) holder.itemView;
                            this.floatingDateView.setCustomDate(cell2.getDate(), false, true);
                        }
                    }
                }
            } else if (visibleItemCount > 0 && !this.commonGroupsAdapter.endReached && !this.commonGroupsAdapter.loading && !this.commonGroupsAdapter.chats.isEmpty() && firstVisibleItem + visibleItemCount >= totalItemCount - 5) {
                CommonGroupsAdapter commonGroupsAdapter = this.commonGroupsAdapter;
                commonGroupsAdapter.getChats(((TLRPC.Chat) commonGroupsAdapter.chats.get(this.commonGroupsAdapter.chats.size() - 1)).id, 100);
            }
        }
    }

    /* renamed from: lambda$checkLoadMoreScroll$10$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3025xe0c6abc(int type, RecyclerListView recyclerView) {
        findPeriodAndJumpToDate(type, recyclerView, false);
        this.jumpToRunnable = null;
    }

    private void loadFromStart(int selectedType) {
        int type;
        if (selectedType == 0) {
            type = 0;
            if (this.sharedMediaData[0].filterType != 1) {
                if (this.sharedMediaData[0].filterType == 2) {
                    type = 7;
                }
            } else {
                type = 6;
            }
        } else if (selectedType == 1) {
            type = 1;
        } else if (selectedType == 2) {
            type = 2;
        } else if (selectedType == 4) {
            type = 4;
        } else if (selectedType == 5) {
            type = 5;
        } else {
            type = 3;
        }
        this.sharedMediaData[selectedType].loading = true;
        this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, 0, this.sharedMediaData[selectedType].min_id, type, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[selectedType].requestIndex);
    }

    public ActionBarMenuItem getSearchItem() {
        return this.searchItem;
    }

    public boolean isSearchItemVisible() {
        if (this.mediaPages[0].selectedType == 7) {
            return this.delegate.canSearchMembers();
        }
        return (this.mediaPages[0].selectedType == 0 || this.mediaPages[0].selectedType == 2 || this.mediaPages[0].selectedType == 5 || this.mediaPages[0].selectedType == 6) ? false : true;
    }

    public boolean isCalendarItemVisible() {
        return this.mediaPages[0].selectedType == 0;
    }

    public int getSelectedTab() {
        return this.scrollSlidingTextTabStrip.getCurrentTabId();
    }

    public int getClosestTab() {
        MediaPage[] mediaPageArr = this.mediaPages;
        if (mediaPageArr[1] == null || mediaPageArr[1].getVisibility() != 0 || ((!this.tabsAnimationInProgress || this.backAnimation) && Math.abs(this.mediaPages[1].getTranslationX()) >= this.mediaPages[1].getMeasuredWidth() / 2.0f)) {
            return this.scrollSlidingTextTabStrip.getCurrentTabId();
        }
        return this.mediaPages[1].selectedType;
    }

    protected void onSelectedTabChanged() {
    }

    protected boolean canShowSearchItem() {
        return true;
    }

    protected void onSearchStateChanged(boolean expanded) {
    }

    protected boolean onMemberClick(TLRPC.ChatParticipant participant, boolean isLong) {
        return false;
    }

    public void onDestroy() {
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.mediaDidLoad);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messageReceivedByServer);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingDidReset);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        this.profileActivity.getNotificationCenter().removeObserver(this, NotificationCenter.messagePlayingDidStart);
    }

    private void checkCurrentTabValid() {
        if (!this.scrollSlidingTextTabStrip.hasTab(this.scrollSlidingTextTabStrip.getCurrentTabId())) {
            int id = this.scrollSlidingTextTabStrip.getFirstTabId();
            this.scrollSlidingTextTabStrip.setInitialTabId(id);
            this.mediaPages[0].selectedType = id;
            switchToCurrentSelectedMode(false);
        }
    }

    public void setNewMediaCounts(int[] mediaCounts) {
        boolean hadMedia = false;
        int a = 0;
        while (true) {
            if (a >= 6) {
                break;
            } else if (this.hasMedia[a] < 0) {
                a++;
            } else {
                hadMedia = true;
                break;
            }
        }
        System.arraycopy(mediaCounts, 0, this.hasMedia, 0, 6);
        updateTabs(true);
        if (!hadMedia && this.scrollSlidingTextTabStrip.getCurrentTabId() == 6) {
            this.scrollSlidingTextTabStrip.resetTab();
        }
        checkCurrentTabValid();
        if (this.hasMedia[0] >= 0) {
            loadFastScrollData(false);
        }
    }

    private void loadFastScrollData(boolean force) {
        int k = 0;
        while (true) {
            int[] iArr = supportedFastScrollTypes;
            if (k < iArr.length) {
                final int type = iArr[k];
                if ((this.sharedMediaData[type].fastScrollDataLoaded && !force) || DialogObject.isEncryptedDialog(this.dialog_id)) {
                    return;
                }
                this.sharedMediaData[type].fastScrollDataLoaded = false;
                TLRPC.TL_messages_getSearchResultsPositions req = new TLRPC.TL_messages_getSearchResultsPositions();
                if (type == 0) {
                    if (this.sharedMediaData[type].filterType != 1) {
                        if (this.sharedMediaData[type].filterType == 2) {
                            req.filter = new TLRPC.TL_inputMessagesFilterVideo();
                        } else {
                            req.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
                        }
                    } else {
                        req.filter = new TLRPC.TL_inputMessagesFilterPhotos();
                    }
                } else if (type == 1) {
                    req.filter = new TLRPC.TL_inputMessagesFilterDocument();
                } else if (type == 2) {
                    req.filter = new TLRPC.TL_inputMessagesFilterRoundVoice();
                } else {
                    req.filter = new TLRPC.TL_inputMessagesFilterMusic();
                }
                req.limit = 100;
                req.peer = MessagesController.getInstance(this.profileActivity.getCurrentAccount()).getInputPeer(this.dialog_id);
                final int reqIndex = this.sharedMediaData[type].requestIndex;
                int reqId = ConnectionsManager.getInstance(this.profileActivity.getCurrentAccount()).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda3
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SharedMediaLayout.this.m3028x343dedc(reqIndex, type, tLObject, tL_error);
                    }
                });
                ConnectionsManager.getInstance(this.profileActivity.getCurrentAccount()).bindRequestToGuid(reqId, this.profileActivity.getClassGuid());
                k++;
            } else {
                return;
            }
        }
    }

    /* renamed from: lambda$loadFastScrollData$13$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3028x343dedc(final int reqIndex, final int type, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SharedMediaLayout.this.m3027x49cc513d(error, reqIndex, type, response);
            }
        });
    }

    /* renamed from: lambda$loadFastScrollData$12$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3027x49cc513d(TLRPC.TL_error error, int reqIndex, int type, TLObject response) {
        if (error != null || reqIndex != this.sharedMediaData[type].requestIndex) {
            return;
        }
        TLRPC.TL_messages_searchResultsPositions res = (TLRPC.TL_messages_searchResultsPositions) response;
        this.sharedMediaData[type].fastScrollPeriods.clear();
        int n = res.positions.size();
        for (int i = 0; i < n; i++) {
            TLRPC.TL_searchResultPosition serverPeriod = res.positions.get(i);
            if (serverPeriod.date != 0) {
                Period period = new Period(serverPeriod);
                this.sharedMediaData[type].fastScrollPeriods.add(period);
            }
        }
        Collections.sort(this.sharedMediaData[type].fastScrollPeriods, SharedMediaLayout$$ExternalSyntheticLambda2.INSTANCE);
        this.sharedMediaData[type].setTotalCount(res.count);
        this.sharedMediaData[type].fastScrollDataLoaded = true;
        if (!this.sharedMediaData[type].fastScrollPeriods.isEmpty()) {
            int i2 = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i2 >= mediaPageArr.length) {
                    break;
                }
                if (mediaPageArr[i2].selectedType == type) {
                    this.mediaPages[i2].fastScrollEnabled = true;
                    updateFastScrollVisibility(this.mediaPages[i2], true);
                }
                i2++;
            }
        }
        this.photoVideoAdapter.notifyDataSetChanged();
    }

    public static /* synthetic */ int lambda$loadFastScrollData$11(Period period, Period period2) {
        return period2.date - period.date;
    }

    public static void showFastScrollHint(final MediaPage mediaPage, SharedMediaData[] sharedMediaData, boolean show) {
        if (show) {
            if (SharedConfig.fastScrollHintCount <= 0 || mediaPage.fastScrollHintView != null || mediaPage.fastScrollHinWasShown || mediaPage.listView.getFastScroll() == null || !mediaPage.listView.getFastScroll().isVisible || mediaPage.listView.getFastScroll().getVisibility() != 0 || sharedMediaData[0].totalCount < 50) {
                return;
            }
            SharedConfig.setFastScrollHintCount(SharedConfig.fastScrollHintCount - 1);
            mediaPage.fastScrollHinWasShown = true;
            final SharedMediaFastScrollTooltip tooltip = new SharedMediaFastScrollTooltip(mediaPage.getContext());
            mediaPage.fastScrollHintView = tooltip;
            mediaPage.addView(mediaPage.fastScrollHintView, LayoutHelper.createFrame(-2, -2.0f));
            mediaPage.fastScrollHintView.setAlpha(0.0f);
            mediaPage.fastScrollHintView.setScaleX(0.8f);
            mediaPage.fastScrollHintView.setScaleY(0.8f);
            mediaPage.fastScrollHintView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150L).start();
            mediaPage.invalidate();
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    SharedMediaLayout.lambda$showFastScrollHint$14(SharedMediaLayout.MediaPage.this, tooltip);
                }
            };
            mediaPage.fastScrollHideHintRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 4000L);
        } else if (mediaPage.fastScrollHintView == null || mediaPage.fastScrollHideHintRunnable == null) {
        } else {
            AndroidUtilities.cancelRunOnUIThread(mediaPage.fastScrollHideHintRunnable);
            mediaPage.fastScrollHideHintRunnable.run();
            mediaPage.fastScrollHideHintRunnable = null;
            mediaPage.fastScrollHintView = null;
        }
    }

    public static /* synthetic */ void lambda$showFastScrollHint$14(MediaPage mediaPage, final SharedMediaFastScrollTooltip tooltip) {
        mediaPage.fastScrollHintView = null;
        mediaPage.fastScrollHideHintRunnable = null;
        tooltip.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(220L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SharedMediaLayout.24
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (tooltip.getParent() != null) {
                    ((ViewGroup) tooltip.getParent()).removeView(tooltip);
                }
            }
        }).start();
    }

    public void setCommonGroupsCount(int count) {
        this.hasMedia[6] = count;
        updateTabs(true);
        checkCurrentTabValid();
    }

    public void onActionBarItemClick(View v, int id) {
        if (id == 101) {
            TLRPC.Chat currentChat = null;
            TLRPC.User currentUser = null;
            TLRPC.EncryptedChat currentEncryptedChat = null;
            if (DialogObject.isEncryptedDialog(this.dialog_id)) {
                currentEncryptedChat = this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(this.dialog_id)));
            } else if (DialogObject.isUserDialog(this.dialog_id)) {
                currentUser = this.profileActivity.getMessagesController().getUser(Long.valueOf(this.dialog_id));
            } else {
                currentChat = this.profileActivity.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
            }
            AlertsCreator.createDeleteMessagesAlert(this.profileActivity, currentUser, currentChat, currentEncryptedChat, null, this.mergeDialogId, null, this.selectedFiles, null, false, 1, new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    SharedMediaLayout.this.m3037xeb929fb8();
                }
            }, null, this.resourcesProvider);
            return;
        }
        char c = 1;
        if (id == 100) {
            if (this.info != null) {
                TLRPC.Chat chat = this.profileActivity.getMessagesController().getChat(Long.valueOf(this.info.id));
                if (this.profileActivity.getMessagesController().isChatNoForwards(chat)) {
                    HintView hintView = this.fwdRestrictedHint;
                    if (hintView != null) {
                        hintView.setText((!ChatObject.isChannel(chat) || chat.megagroup) ? LocaleController.getString("ForwardsRestrictedInfoGroup", R.string.ForwardsRestrictedInfoGroup) : LocaleController.getString("ForwardsRestrictedInfoChannel", R.string.ForwardsRestrictedInfoChannel));
                        this.fwdRestrictedHint.showForView(v, true);
                        return;
                    }
                    return;
                }
            }
            if (hasNoforwardsMessage()) {
                HintView hintView2 = this.fwdRestrictedHint;
                if (hintView2 != null) {
                    hintView2.setText(LocaleController.getString("ForwardsRestrictedInfoBot", R.string.ForwardsRestrictedInfoBot));
                    this.fwdRestrictedHint.showForView(v, true);
                    return;
                }
                return;
            }
            Bundle args = new Bundle();
            args.putBoolean("onlySelect", true);
            args.putInt("dialogsType", 3);
            DialogsActivity fragment = new DialogsActivity(args);
            fragment.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda8
                @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
                public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                    SharedMediaLayout.this.m3038xa50a2d57(dialogsActivity, arrayList, charSequence, z);
                }
            });
            this.profileActivity.presentFragment(fragment);
        } else if (id != 102 || this.selectedFiles[0].size() + this.selectedFiles[1].size() != 1) {
        } else {
            SparseArray<MessageObject>[] sparseArrayArr = this.selectedFiles;
            if (sparseArrayArr[0].size() == 1) {
                c = 0;
            }
            MessageObject messageObject = sparseArrayArr[c].valueAt(0);
            Bundle args2 = new Bundle();
            long dialogId = messageObject.getDialogId();
            if (DialogObject.isEncryptedDialog(dialogId)) {
                args2.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
            } else if (DialogObject.isUserDialog(dialogId)) {
                args2.putLong("user_id", dialogId);
            } else {
                TLRPC.Chat chat2 = this.profileActivity.getMessagesController().getChat(Long.valueOf(-dialogId));
                if (chat2 != null && chat2.migrated_to != null) {
                    args2.putLong("migrated_to", dialogId);
                    dialogId = -chat2.migrated_to.channel_id;
                }
                args2.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -dialogId);
            }
            args2.putInt(Constants.MessagePayloadKeys.MSGID_SERVER, messageObject.getId());
            args2.putBoolean("need_remove_previous_same_chat_activity", false);
            this.profileActivity.presentFragment(new ChatActivity(args2), false);
        }
    }

    /* renamed from: lambda$onActionBarItemClick$15$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3037xeb929fb8() {
        showActionMode(false);
        this.actionBar.closeSearchField();
        this.cantDeleteMessagesCount = 0;
    }

    /* renamed from: lambda$onActionBarItemClick$16$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3038xa50a2d57(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        ArrayList<MessageObject> fmessages = new ArrayList<>();
        for (int a = 1; a >= 0; a--) {
            ArrayList<Integer> ids = new ArrayList<>();
            for (int b = 0; b < this.selectedFiles[a].size(); b++) {
                ids.add(Integer.valueOf(this.selectedFiles[a].keyAt(b)));
            }
            Collections.sort(ids);
            Iterator<Integer> it = ids.iterator();
            while (it.hasNext()) {
                Integer id1 = it.next();
                if (id1.intValue() > 0) {
                    fmessages.add(this.selectedFiles[a].get(id1.intValue()));
                }
            }
            this.selectedFiles[a].clear();
        }
        this.cantDeleteMessagesCount = 0;
        showActionMode(false);
        if (dids.size() > 1 || ((Long) dids.get(0)).longValue() == this.profileActivity.getUserConfig().getClientUserId() || message != null) {
            updateRowsSelection();
            for (int a2 = 0; a2 < dids.size(); a2++) {
                long did = ((Long) dids.get(a2)).longValue();
                if (message != null) {
                    this.profileActivity.getSendMessagesHelper().sendMessage(message.toString(), did, null, null, null, true, null, null, null, true, 0, null);
                }
                this.profileActivity.getSendMessagesHelper().sendMessage(fmessages, did, false, false, true, 0);
            }
            fragment1.finishFragment();
            UndoView undoView = null;
            BaseFragment baseFragment = this.profileActivity;
            if (baseFragment instanceof ProfileActivity) {
                undoView = ((ProfileActivity) baseFragment).getUndoView();
            }
            if (undoView != null) {
                if (dids.size() == 1) {
                    undoView.showWithAction(((Long) dids.get(0)).longValue(), 53, Integer.valueOf(fmessages.size()));
                    return;
                }
                undoView.showWithAction(0L, 53, Integer.valueOf(fmessages.size()), Integer.valueOf(dids.size()), (Runnable) null, (Runnable) null);
                return;
            }
            return;
        }
        long did2 = ((Long) dids.get(0)).longValue();
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(did2)) {
            args1.putInt("enc_id", DialogObject.getEncryptedChatId(did2));
        } else {
            if (DialogObject.isUserDialog(did2)) {
                args1.putLong("user_id", did2);
            } else {
                args1.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did2);
            }
            if (!this.profileActivity.getMessagesController().checkCanOpenChat(args1, fragment1)) {
                return;
            }
        }
        this.profileActivity.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        ChatActivity chatActivity = new ChatActivity(args1);
        fragment1.presentFragment(chatActivity, true);
        chatActivity.showFieldPanelForForward(true, fmessages);
    }

    private boolean prepareForMoving(MotionEvent ev, boolean forward2) {
        int id = this.scrollSlidingTextTabStrip.getNextPageId(forward2);
        if (id < 0) {
            return false;
        }
        if (canShowSearchItem()) {
            int i = this.searchItemState;
            if (i != 0) {
                if (i == 2) {
                    this.searchItem.setAlpha(1.0f);
                } else if (i == 1) {
                    this.searchItem.setAlpha(0.0f);
                    this.searchItem.setVisibility(4);
                }
                this.searchItemState = 0;
            }
        } else {
            this.searchItem.setVisibility(4);
            this.searchItem.setAlpha(0.0f);
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        hideFloatingDateView(true);
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.startedTrackingX = (int) ev.getX();
        this.actionBar.setEnabled(false);
        this.scrollSlidingTextTabStrip.setEnabled(false);
        this.mediaPages[1].selectedType = id;
        this.mediaPages[1].setVisibility(0);
        this.animatingForward = forward2;
        switchToCurrentSelectedMode(true);
        if (forward2) {
            MediaPage[] mediaPageArr = this.mediaPages;
            mediaPageArr[1].setTranslationX(mediaPageArr[0].getMeasuredWidth());
        } else {
            MediaPage[] mediaPageArr2 = this.mediaPages;
            mediaPageArr2[1].setTranslationX(-mediaPageArr2[0].getMeasuredWidth());
        }
        return true;
    }

    @Override // android.view.View
    public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
        super.forceHasOverlappingRendering(hasOverlappingRendering);
    }

    @Override // android.view.View
    public void setPadding(int left, int top, int right, int bottom) {
        this.topPadding = top;
        int a = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (a >= mediaPageArr.length) {
                break;
            }
            mediaPageArr[a].setTranslationY(this.topPadding - this.lastMeasuredTopPadding);
            a++;
        }
        this.fragmentContextView.setTranslationY(AndroidUtilities.dp(48.0f) + top);
        this.additionalFloatingTranslation = top;
        ChatActionCell chatActionCell = this.floatingDateView;
        chatActionCell.setTranslationY((chatActionCell.getTag() == null ? -AndroidUtilities.dp(48.0f) : 0) + this.additionalFloatingTranslation);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = this.delegate.getListView() != null ? this.delegate.getListView().getHeight() : 0;
        if (heightSize == 0) {
            heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(widthSize, heightSize);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != 8) {
                if (child instanceof MediaPage) {
                    measureChildWithMargins(child, widthMeasureSpec, 0, View.MeasureSpec.makeMeasureSpec(heightSize, C.BUFFER_FLAG_ENCRYPTED), 0);
                    ((MediaPage) child).listView.setPadding(0, 0, 0, this.topPadding);
                } else {
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                }
            }
        }
    }

    public boolean checkTabsAnimationInProgress() {
        if (this.tabsAnimationInProgress) {
            boolean cancel = false;
            int i = -1;
            if (this.backAnimation) {
                if (Math.abs(this.mediaPages[0].getTranslationX()) < 1.0f) {
                    this.mediaPages[0].setTranslationX(0.0f);
                    MediaPage[] mediaPageArr = this.mediaPages;
                    MediaPage mediaPage = mediaPageArr[1];
                    int measuredWidth = mediaPageArr[0].getMeasuredWidth();
                    if (this.animatingForward) {
                        i = 1;
                    }
                    mediaPage.setTranslationX(measuredWidth * i);
                    cancel = true;
                }
            } else if (Math.abs(this.mediaPages[1].getTranslationX()) < 1.0f) {
                MediaPage[] mediaPageArr2 = this.mediaPages;
                MediaPage mediaPage2 = mediaPageArr2[0];
                int measuredWidth2 = mediaPageArr2[0].getMeasuredWidth();
                if (!this.animatingForward) {
                    i = 1;
                }
                mediaPage2.setTranslationX(measuredWidth2 * i);
                this.mediaPages[1].setTranslationX(0.0f);
                cancel = true;
            }
            if (cancel) {
                AnimatorSet animatorSet = this.tabsAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.tabsAnimation = null;
                }
                this.tabsAnimationInProgress = false;
            }
            return this.tabsAnimationInProgress;
        }
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return checkTabsAnimationInProgress() || this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(ev);
    }

    public boolean isCurrentTabFirst() {
        return this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId();
    }

    public RecyclerListView getCurrentListView() {
        return this.mediaPages[0].listView;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        float velY;
        float velX;
        float dx;
        int duration;
        boolean z;
        int i = 0;
        boolean z2 = false;
        if (this.profileActivity.getParentLayout() == null || this.profileActivity.getParentLayout().checkTransitionAnimation() || checkTabsAnimationInProgress() || this.isInPinchToZoomTouchMode) {
            return false;
        }
        if (ev != null) {
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.velocityTracker.addMovement(ev);
            HintView hintView = this.fwdRestrictedHint;
            if (hintView != null) {
                hintView.hide();
            }
        }
        if (ev == null || ev.getAction() != 0 || this.startedTracking || this.maybeStartTracking || ev.getY() < AndroidUtilities.dp(48.0f)) {
            if (ev == null || ev.getAction() != 2 || ev.getPointerId(0) != this.startedTrackingPointerId) {
                if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                    this.velocityTracker.computeCurrentVelocity(1000, this.maximumVelocity);
                    if (ev != null && ev.getAction() != 3) {
                        velX = this.velocityTracker.getXVelocity();
                        velY = this.velocityTracker.getYVelocity();
                        if (!this.startedTracking && Math.abs(velX) >= 3000.0f && Math.abs(velX) > Math.abs(velY)) {
                            prepareForMoving(ev, velX < 0.0f);
                        }
                    } else {
                        velX = 0.0f;
                        velY = 0.0f;
                    }
                    if (this.startedTracking) {
                        float x = this.mediaPages[0].getX();
                        this.tabsAnimation = new AnimatorSet();
                        boolean z3 = Math.abs(x) < ((float) this.mediaPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                        this.backAnimation = z3;
                        if (z3) {
                            dx = Math.abs(x);
                            if (this.animatingForward) {
                                this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(this.mediaPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.mediaPages[1], View.TRANSLATION_X, this.mediaPages[1].getMeasuredWidth()));
                            } else {
                                this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(this.mediaPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(this.mediaPages[1], View.TRANSLATION_X, -this.mediaPages[1].getMeasuredWidth()));
                            }
                        } else {
                            dx = this.mediaPages[0].getMeasuredWidth() - Math.abs(x);
                            if (this.animatingForward) {
                                this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(this.mediaPages[0], View.TRANSLATION_X, -this.mediaPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(this.mediaPages[1], View.TRANSLATION_X, 0.0f));
                            } else {
                                this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(this.mediaPages[0], View.TRANSLATION_X, this.mediaPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(this.mediaPages[1], View.TRANSLATION_X, 0.0f));
                            }
                        }
                        this.tabsAnimation.setInterpolator(interpolator);
                        int width = getMeasuredWidth();
                        int halfWidth = width / 2;
                        float distanceRatio = Math.min(1.0f, (dx * 1.0f) / width);
                        float distance = halfWidth + (halfWidth * AndroidUtilities.distanceInfluenceForSnapDuration(distanceRatio));
                        float velX2 = Math.abs(velX);
                        if (velX2 > 0.0f) {
                            duration = Math.round(Math.abs(distance / velX2) * 1000.0f) * 4;
                        } else {
                            float pageDelta = dx / getMeasuredWidth();
                            duration = (int) ((1.0f + pageDelta) * 100.0f);
                        }
                        this.tabsAnimation.setDuration(Math.max(150, Math.min(duration, 600)));
                        this.tabsAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SharedMediaLayout.25
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                SharedMediaLayout.this.tabsAnimation = null;
                                if (SharedMediaLayout.this.backAnimation) {
                                    SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                                    if (SharedMediaLayout.this.canShowSearchItem()) {
                                        if (SharedMediaLayout.this.searchItemState == 2) {
                                            SharedMediaLayout.this.searchItem.setAlpha(1.0f);
                                        } else if (SharedMediaLayout.this.searchItemState == 1) {
                                            SharedMediaLayout.this.searchItem.setAlpha(0.0f);
                                            SharedMediaLayout.this.searchItem.setVisibility(4);
                                        }
                                    } else {
                                        SharedMediaLayout.this.searchItem.setVisibility(4);
                                        SharedMediaLayout.this.searchItem.setAlpha(0.0f);
                                    }
                                    SharedMediaLayout.this.searchItemState = 0;
                                } else {
                                    MediaPage tempPage = SharedMediaLayout.this.mediaPages[0];
                                    SharedMediaLayout.this.mediaPages[0] = SharedMediaLayout.this.mediaPages[1];
                                    SharedMediaLayout.this.mediaPages[1] = tempPage;
                                    SharedMediaLayout.this.mediaPages[1].setVisibility(8);
                                    if (SharedMediaLayout.this.searchItemState == 2) {
                                        SharedMediaLayout.this.searchItem.setVisibility(4);
                                    }
                                    SharedMediaLayout.this.searchItemState = 0;
                                    SharedMediaLayout.this.scrollSlidingTextTabStrip.selectTabWithId(SharedMediaLayout.this.mediaPages[0].selectedType, 1.0f);
                                    SharedMediaLayout.this.onSelectedTabChanged();
                                    SharedMediaLayout.this.startStopVisibleGifs();
                                }
                                SharedMediaLayout.this.tabsAnimationInProgress = false;
                                SharedMediaLayout.this.maybeStartTracking = false;
                                SharedMediaLayout.this.startedTracking = false;
                                SharedMediaLayout.this.actionBar.setEnabled(true);
                                SharedMediaLayout.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        this.tabsAnimation.start();
                        this.tabsAnimationInProgress = true;
                        this.startedTracking = false;
                        onSelectedTabChanged();
                    } else {
                        this.maybeStartTracking = false;
                        this.actionBar.setEnabled(true);
                        this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    VelocityTracker velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
            } else {
                int dx2 = (int) (ev.getX() - this.startedTrackingX);
                int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                if (this.startedTracking && (((z = this.animatingForward) && dx2 > 0) || (!z && dx2 < 0))) {
                    if (!prepareForMoving(ev, dx2 < 0)) {
                        this.maybeStartTracking = true;
                        this.startedTracking = false;
                        this.mediaPages[0].setTranslationX(0.0f);
                        MediaPage[] mediaPageArr = this.mediaPages;
                        mediaPageArr[1].setTranslationX(this.animatingForward ? mediaPageArr[0].getMeasuredWidth() : -mediaPageArr[0].getMeasuredWidth());
                        this.scrollSlidingTextTabStrip.selectTabWithId(this.mediaPages[1].selectedType, 0.0f);
                    }
                }
                if (this.maybeStartTracking && !this.startedTracking) {
                    float touchSlop = AndroidUtilities.getPixelsInCM(0.3f, true);
                    if (Math.abs(dx2) >= touchSlop && Math.abs(dx2) > dy) {
                        if (dx2 < 0) {
                            z2 = true;
                        }
                        prepareForMoving(ev, z2);
                    }
                } else if (this.startedTracking) {
                    this.mediaPages[0].setTranslationX(dx2);
                    if (this.animatingForward) {
                        MediaPage[] mediaPageArr2 = this.mediaPages;
                        mediaPageArr2[1].setTranslationX(mediaPageArr2[0].getMeasuredWidth() + dx2);
                    } else {
                        MediaPage[] mediaPageArr3 = this.mediaPages;
                        mediaPageArr3[1].setTranslationX(dx2 - mediaPageArr3[0].getMeasuredWidth());
                    }
                    float scrollProgress = Math.abs(dx2) / this.mediaPages[0].getMeasuredWidth();
                    if (canShowSearchItem()) {
                        int i2 = this.searchItemState;
                        if (i2 == 2) {
                            this.searchItem.setAlpha(1.0f - scrollProgress);
                        } else if (i2 == 1) {
                            this.searchItem.setAlpha(scrollProgress);
                        }
                        float photoVideoOptionsAlpha = 0.0f;
                        MediaPage[] mediaPageArr4 = this.mediaPages;
                        if (mediaPageArr4[1] != null && mediaPageArr4[1].selectedType == 0) {
                            photoVideoOptionsAlpha = scrollProgress;
                        }
                        if (this.mediaPages[0].selectedType == 0) {
                            photoVideoOptionsAlpha = 1.0f - scrollProgress;
                        }
                        this.photoVideoOptionsItem.setAlpha(photoVideoOptionsAlpha);
                        ImageView imageView = this.photoVideoOptionsItem;
                        if (photoVideoOptionsAlpha == 0.0f || !canShowSearchItem()) {
                            i = 4;
                        }
                        imageView.setVisibility(i);
                    } else {
                        this.searchItem.setAlpha(0.0f);
                    }
                    this.scrollSlidingTextTabStrip.selectTabWithId(this.mediaPages[1].selectedType, scrollProgress);
                    onSelectedTabChanged();
                }
            }
        } else {
            this.startedTrackingPointerId = ev.getPointerId(0);
            this.maybeStartTracking = true;
            this.startedTrackingX = (int) ev.getX();
            this.startedTrackingY = (int) ev.getY();
            this.velocityTracker.clear();
        }
        return this.startedTracking;
    }

    public boolean closeActionMode() {
        if (this.isActionModeShowed) {
            for (int a = 1; a >= 0; a--) {
                this.selectedFiles[a].clear();
            }
            this.cantDeleteMessagesCount = 0;
            showActionMode(false);
            updateRowsSelection();
            return true;
        }
        return false;
    }

    public void setVisibleHeight(int height) {
        int height2 = Math.max(height, AndroidUtilities.dp(120.0f));
        for (int a = 0; a < this.mediaPages.length; a++) {
            float t = (-(getMeasuredHeight() - height2)) / 2.0f;
            this.mediaPages[a].emptyView.setTranslationY(t);
            this.mediaPages[a].progressView.setTranslationY(-t);
        }
    }

    private void showActionMode(final boolean show) {
        if (this.isActionModeShowed == show) {
            return;
        }
        this.isActionModeShowed = show;
        AnimatorSet animatorSet = this.actionModeAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (show) {
            this.actionModeLayout.setVisibility(0);
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.actionModeAnimation = animatorSet2;
        Animator[] animatorArr = new Animator[1];
        LinearLayout linearLayout = this.actionModeLayout;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        fArr[0] = show ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(linearLayout, property, fArr);
        animatorSet2.playTogether(animatorArr);
        this.actionModeAnimation.setDuration(180L);
        this.actionModeAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SharedMediaLayout.26
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                SharedMediaLayout.this.actionModeAnimation = null;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (SharedMediaLayout.this.actionModeAnimation != null) {
                    SharedMediaLayout.this.actionModeAnimation = null;
                    if (!show) {
                        SharedMediaLayout.this.actionModeLayout.setVisibility(4);
                    }
                }
            }
        });
        this.actionModeAnimation.start();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        int oldItemCount;
        RecyclerListView listView;
        if (id == NotificationCenter.mediaDidLoad) {
            long uid = ((Long) args[0]).longValue();
            int guid = ((Integer) args[3]).intValue();
            int requestIndex = ((Integer) args[7]).intValue();
            int type = ((Integer) args[4]).intValue();
            boolean fromStart = ((Boolean) args[6]).booleanValue();
            if (type == 6 || type == 7) {
                type = 0;
            }
            if (guid == this.profileActivity.getClassGuid() && requestIndex == this.sharedMediaData[type].requestIndex) {
                if (type != 0 && type != 1 && type != 2 && type != 4) {
                    this.sharedMediaData[type].totalCount = ((Integer) args[1]).intValue();
                }
                ArrayList<MessageObject> arr = (ArrayList) args[2];
                boolean enc = DialogObject.isEncryptedDialog(this.dialog_id);
                int requestIndex2 = requestIndex;
                int loadIndex = uid == this.dialog_id ? 0 : 1;
                RecyclerView.Adapter adapter = null;
                if (type == 0) {
                    adapter = this.photoVideoAdapter;
                } else if (type == 1) {
                    adapter = this.documentsAdapter;
                } else if (type == 2) {
                    adapter = this.voiceAdapter;
                } else if (type == 3) {
                    adapter = this.linksAdapter;
                } else if (type == 4) {
                    adapter = this.audioAdapter;
                } else if (type == 5) {
                    adapter = this.gifAdapter;
                }
                int oldMessagesCount = this.sharedMediaData[type].messages.size();
                if (adapter != null) {
                    oldItemCount = adapter.getItemCount();
                    if (adapter instanceof RecyclerListView.SectionsAdapter) {
                        RecyclerListView.SectionsAdapter sectionsAdapter = (RecyclerListView.SectionsAdapter) adapter;
                        sectionsAdapter.notifySectionsChanged();
                    }
                } else {
                    oldItemCount = 0;
                }
                this.sharedMediaData[type].loading = false;
                SparseBooleanArray addedMesages = new SparseBooleanArray();
                if (fromStart) {
                    int a = arr.size() - 1;
                    while (a >= 0) {
                        int guid2 = guid;
                        MessageObject message = arr.get(a);
                        int requestIndex3 = requestIndex2;
                        long uid2 = uid;
                        boolean added = this.sharedMediaData[type].addMessage(message, loadIndex, true, enc);
                        if (added) {
                            addedMesages.put(message.getId(), true);
                            SharedMediaData.access$710(this.sharedMediaData[type]);
                            if (this.sharedMediaData[type].startOffset < 0) {
                                this.sharedMediaData[type].startOffset = 0;
                            }
                        }
                        a--;
                        guid = guid2;
                        requestIndex2 = requestIndex3;
                        uid = uid2;
                    }
                    this.sharedMediaData[type].startReached = ((Boolean) args[5]).booleanValue();
                    if (this.sharedMediaData[type].startReached) {
                        this.sharedMediaData[type].startOffset = 0;
                    }
                } else {
                    for (int a2 = 0; a2 < arr.size(); a2++) {
                        MessageObject message2 = arr.get(a2);
                        if (this.sharedMediaData[type].addMessage(message2, loadIndex, false, enc)) {
                            addedMesages.put(message2.getId(), true);
                            SharedMediaData.access$7010(this.sharedMediaData[type]);
                            if (this.sharedMediaData[type].endLoadingStubs < 0) {
                                this.sharedMediaData[type].endLoadingStubs = 0;
                            }
                        }
                    }
                    if (this.sharedMediaData[type].loadingAfterFastScroll && this.sharedMediaData[type].messages.size() > 0) {
                        SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                        sharedMediaDataArr[type].min_id = sharedMediaDataArr[type].messages.get(0).getId();
                    }
                    this.sharedMediaData[type].endReached[loadIndex] = ((Boolean) args[5]).booleanValue();
                    if (this.sharedMediaData[type].endReached[loadIndex]) {
                        SharedMediaData[] sharedMediaDataArr2 = this.sharedMediaData;
                        sharedMediaDataArr2[type].totalCount = sharedMediaDataArr2[type].messages.size() + this.sharedMediaData[type].startOffset;
                    }
                }
                if (!fromStart && loadIndex == 0 && this.sharedMediaData[type].endReached[loadIndex] && this.mergeDialogId != 0) {
                    this.sharedMediaData[type].loading = true;
                    this.profileActivity.getMediaDataController().loadMedia(this.mergeDialogId, 50, this.sharedMediaData[type].max_id[1], 0, type, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[type].requestIndex);
                }
                if (adapter != null) {
                    RecyclerListView listView2 = null;
                    int a3 = 0;
                    while (true) {
                        MediaPage[] mediaPageArr = this.mediaPages;
                        if (a3 >= mediaPageArr.length) {
                            break;
                        }
                        if (mediaPageArr[a3].listView.getAdapter() == adapter) {
                            listView2 = this.mediaPages[a3].listView;
                            this.mediaPages[a3].listView.stopScroll();
                        }
                        a3++;
                    }
                    int newItemCount = adapter.getItemCount();
                    SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
                    if (adapter == sharedPhotoVideoAdapter) {
                        if (sharedPhotoVideoAdapter.getItemCount() == oldItemCount) {
                            AndroidUtilities.updateVisibleRows(listView2);
                        } else {
                            this.photoVideoAdapter.notifyDataSetChanged();
                        }
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    if (this.sharedMediaData[type].messages.isEmpty() && !this.sharedMediaData[type].loading) {
                        if (listView2 != null) {
                            animateItemsEnter(listView2, oldItemCount, addedMesages);
                        }
                    } else if (listView2 != null && (adapter == this.photoVideoAdapter || newItemCount >= oldItemCount)) {
                        animateItemsEnter(listView2, oldItemCount, addedMesages);
                    }
                    if (listView2 != null && !this.sharedMediaData[type].loadingAfterFastScroll) {
                        if (oldMessagesCount == 0) {
                            int k = 0;
                            while (k < 2) {
                                if (this.mediaPages[k].selectedType != 0) {
                                    listView = listView2;
                                } else {
                                    int position = this.photoVideoAdapter.getPositionForIndex(0);
                                    listView = listView2;
                                    ((LinearLayoutManager) listView2.getLayoutManager()).scrollToPositionWithOffset(position, 0);
                                }
                                k++;
                                listView2 = listView;
                            }
                        } else {
                            saveScrollPosition();
                        }
                    }
                }
                if (this.sharedMediaData[type].loadingAfterFastScroll) {
                    if (this.sharedMediaData[type].messages.size() == 0) {
                        loadFromStart(type);
                    } else {
                        this.sharedMediaData[type].loadingAfterFastScroll = false;
                    }
                }
                this.scrolling = true;
            } else if (this.sharedMediaPreloader != null && this.sharedMediaData[type].messages.isEmpty() && !this.sharedMediaData[type].loadingAfterFastScroll && fillMediaData(type)) {
                RecyclerView.Adapter adapter2 = null;
                if (type == 0) {
                    adapter2 = this.photoVideoAdapter;
                } else if (type == 1) {
                    adapter2 = this.documentsAdapter;
                } else if (type == 2) {
                    adapter2 = this.voiceAdapter;
                } else if (type == 3) {
                    adapter2 = this.linksAdapter;
                } else if (type == 4) {
                    adapter2 = this.audioAdapter;
                } else if (type == 5) {
                    adapter2 = this.gifAdapter;
                }
                if (adapter2 != null) {
                    int a4 = 0;
                    while (true) {
                        MediaPage[] mediaPageArr2 = this.mediaPages;
                        if (a4 >= mediaPageArr2.length) {
                            break;
                        }
                        if (mediaPageArr2[a4].listView.getAdapter() == adapter2) {
                            this.mediaPages[a4].listView.stopScroll();
                        }
                        a4++;
                    }
                    adapter2.notifyDataSetChanged();
                }
                this.scrolling = true;
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            boolean scheduled = ((Boolean) args[2]).booleanValue();
            if (scheduled) {
                return;
            }
            TLRPC.Chat currentChat = null;
            if (DialogObject.isChatDialog(this.dialog_id)) {
                currentChat = this.profileActivity.getMessagesController().getChat(Long.valueOf(-this.dialog_id));
            }
            long channelId = ((Long) args[1]).longValue();
            int loadIndex2 = 0;
            if (ChatObject.isChannel(currentChat)) {
                if (channelId == 0 && this.mergeDialogId != 0) {
                    loadIndex2 = 1;
                } else if (channelId == currentChat.id) {
                    loadIndex2 = 0;
                } else {
                    return;
                }
            } else if (channelId != 0) {
                return;
            }
            ArrayList<Integer> markAsDeletedMessages = (ArrayList) args[0];
            boolean updated = false;
            int type2 = -1;
            int N = markAsDeletedMessages.size();
            for (int a5 = 0; a5 < N; a5++) {
                int b = 0;
                while (true) {
                    SharedMediaData[] sharedMediaDataArr3 = this.sharedMediaData;
                    if (b < sharedMediaDataArr3.length) {
                        if (sharedMediaDataArr3[b].deleteMessage(markAsDeletedMessages.get(a5).intValue(), loadIndex2) != null) {
                            type2 = b;
                            updated = true;
                        }
                        b++;
                    }
                }
            }
            if (updated) {
                this.scrolling = true;
                SharedPhotoVideoAdapter sharedPhotoVideoAdapter2 = this.photoVideoAdapter;
                if (sharedPhotoVideoAdapter2 != null) {
                    sharedPhotoVideoAdapter2.notifyDataSetChanged();
                }
                SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
                if (sharedDocumentsAdapter != null) {
                    sharedDocumentsAdapter.notifyDataSetChanged();
                }
                SharedDocumentsAdapter sharedDocumentsAdapter2 = this.voiceAdapter;
                if (sharedDocumentsAdapter2 != null) {
                    sharedDocumentsAdapter2.notifyDataSetChanged();
                }
                SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
                if (sharedLinksAdapter != null) {
                    sharedLinksAdapter.notifyDataSetChanged();
                }
                SharedDocumentsAdapter sharedDocumentsAdapter3 = this.audioAdapter;
                if (sharedDocumentsAdapter3 != null) {
                    sharedDocumentsAdapter3.notifyDataSetChanged();
                }
                GifAdapter gifAdapter = this.gifAdapter;
                if (gifAdapter != null) {
                    gifAdapter.notifyDataSetChanged();
                }
                if (type2 == 0 || type2 == 1 || type2 == 2 || type2 == 4) {
                    loadFastScrollData(true);
                }
            }
            getMediaPage(type2);
        } else if (id == NotificationCenter.didReceiveNewMessages) {
            boolean scheduled2 = ((Boolean) args[2]).booleanValue();
            if (scheduled2) {
                return;
            }
            long uid3 = ((Long) args[0]).longValue();
            long j = this.dialog_id;
            if (uid3 == j) {
                ArrayList<MessageObject> arr2 = (ArrayList) args[1];
                boolean enc2 = DialogObject.isEncryptedDialog(j);
                boolean updated2 = false;
                for (int a6 = 0; a6 < arr2.size(); a6++) {
                    MessageObject obj = arr2.get(a6);
                    if (obj.messageOwner.media != null && !obj.needDrawBluredPreview()) {
                        int type3 = MediaDataController.getMediaType(obj.messageOwner);
                        if (type3 == -1) {
                            return;
                        }
                        if (this.sharedMediaData[type3].startReached) {
                            if (this.sharedMediaData[type3].addMessage(obj, obj.getDialogId() == this.dialog_id ? 0 : 1, true, enc2)) {
                                updated2 = true;
                                this.hasMedia[type3] = 1;
                            }
                        }
                    }
                }
                if (updated2) {
                    this.scrolling = true;
                    int a7 = 0;
                    while (true) {
                        MediaPage[] mediaPageArr3 = this.mediaPages;
                        if (a7 >= mediaPageArr3.length) {
                            updateTabs(true);
                            return;
                        }
                        RecyclerView.Adapter adapter3 = null;
                        if (mediaPageArr3[a7].selectedType != 0) {
                            if (this.mediaPages[a7].selectedType != 1) {
                                if (this.mediaPages[a7].selectedType != 2) {
                                    if (this.mediaPages[a7].selectedType != 3) {
                                        if (this.mediaPages[a7].selectedType != 4) {
                                            if (this.mediaPages[a7].selectedType == 5) {
                                                adapter3 = this.gifAdapter;
                                            }
                                        } else {
                                            adapter3 = this.audioAdapter;
                                        }
                                    } else {
                                        adapter3 = this.linksAdapter;
                                    }
                                } else {
                                    adapter3 = this.voiceAdapter;
                                }
                            } else {
                                adapter3 = this.documentsAdapter;
                            }
                        } else {
                            adapter3 = this.photoVideoAdapter;
                        }
                        if (adapter3 != null) {
                            adapter3.getItemCount();
                            this.photoVideoAdapter.notifyDataSetChanged();
                            this.documentsAdapter.notifyDataSetChanged();
                            this.voiceAdapter.notifyDataSetChanged();
                            this.linksAdapter.notifyDataSetChanged();
                            this.audioAdapter.notifyDataSetChanged();
                            this.gifAdapter.notifyDataSetChanged();
                        }
                        a7++;
                    }
                }
            }
        } else if (id == NotificationCenter.messageReceivedByServer) {
            Boolean scheduled3 = (Boolean) args[6];
            if (scheduled3.booleanValue()) {
                return;
            }
            Integer msgId = (Integer) args[0];
            Integer newMsgId = (Integer) args[1];
            int a8 = 0;
            while (true) {
                SharedMediaData[] sharedMediaDataArr4 = this.sharedMediaData;
                if (a8 < sharedMediaDataArr4.length) {
                    sharedMediaDataArr4[a8].replaceMid(msgId.intValue(), newMsgId.intValue());
                    a8++;
                } else {
                    return;
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidStart || id == NotificationCenter.messagePlayingPlayStateChanged || id == NotificationCenter.messagePlayingDidReset) {
            if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
                int b2 = 0;
                while (true) {
                    MediaPage[] mediaPageArr4 = this.mediaPages;
                    if (b2 < mediaPageArr4.length) {
                        int count = mediaPageArr4[b2].listView.getChildCount();
                        for (int a9 = 0; a9 < count; a9++) {
                            View view = this.mediaPages[b2].listView.getChildAt(a9);
                            if (view instanceof SharedAudioCell) {
                                SharedAudioCell cell = (SharedAudioCell) view;
                                MessageObject messageObject = cell.getMessage();
                                if (messageObject != null) {
                                    cell.updateButtonState(false, true);
                                }
                            }
                        }
                        b2++;
                    } else {
                        return;
                    }
                }
            } else {
                MessageObject messageObject2 = (MessageObject) args[0];
                if (messageObject2.eventId != 0) {
                    return;
                }
                int b3 = 0;
                while (true) {
                    MediaPage[] mediaPageArr5 = this.mediaPages;
                    if (b3 < mediaPageArr5.length) {
                        int count2 = mediaPageArr5[b3].listView.getChildCount();
                        for (int a10 = 0; a10 < count2; a10++) {
                            View view2 = this.mediaPages[b3].listView.getChildAt(a10);
                            if (view2 instanceof SharedAudioCell) {
                                SharedAudioCell cell2 = (SharedAudioCell) view2;
                                MessageObject messageObject1 = cell2.getMessage();
                                if (messageObject1 != null) {
                                    cell2.updateButtonState(false, true);
                                }
                            }
                        }
                        b3++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void saveScrollPosition() {
        int k = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (k < mediaPageArr.length) {
                RecyclerListView listView = mediaPageArr[k].listView;
                if (listView != null) {
                    int messageId = 0;
                    int offset = 0;
                    for (int i = 0; i < listView.getChildCount(); i++) {
                        View child = listView.getChildAt(i);
                        if (child instanceof SharedPhotoVideoCell2) {
                            SharedPhotoVideoCell2 cell = (SharedPhotoVideoCell2) child;
                            messageId = cell.getMessageId();
                            offset = cell.getTop();
                        }
                        if (child instanceof SharedDocumentCell) {
                            SharedDocumentCell cell2 = (SharedDocumentCell) child;
                            messageId = cell2.getMessage().getId();
                            offset = cell2.getTop();
                        }
                        if (child instanceof SharedAudioCell) {
                            SharedAudioCell cell3 = (SharedAudioCell) child;
                            messageId = cell3.getMessage().getId();
                            offset = cell3.getTop();
                        }
                        if (messageId != 0) {
                            break;
                        }
                    }
                    if (messageId != 0) {
                        int index = -1;
                        if (this.mediaPages[k].selectedType >= 0 && this.mediaPages[k].selectedType < this.sharedMediaData.length) {
                            int i2 = 0;
                            while (true) {
                                if (i2 < this.sharedMediaData[this.mediaPages[k].selectedType].messages.size()) {
                                    if (messageId != this.sharedMediaData[this.mediaPages[k].selectedType].messages.get(i2).getId()) {
                                        i2++;
                                    } else {
                                        index = i2;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            int position = this.sharedMediaData[this.mediaPages[k].selectedType].startOffset + index;
                            if (index >= 0) {
                                ((LinearLayoutManager) listView.getLayoutManager()).scrollToPositionWithOffset(position, (-this.mediaPages[k].listView.getPaddingTop()) + offset);
                                if (this.photoVideoChangeColumnsAnimation) {
                                    this.mediaPages[k].animationSupportingLayoutManager.scrollToPositionWithOffset(position, (-this.mediaPages[k].listView.getPaddingTop()) + offset);
                                }
                            }
                        }
                    }
                }
                k++;
            } else {
                return;
            }
        }
    }

    public void animateItemsEnter(RecyclerListView finalListView, int oldItemCount, SparseBooleanArray addedMesages) {
        int n = finalListView.getChildCount();
        View progressView = null;
        for (int i = 0; i < n; i++) {
            View child = finalListView.getChildAt(i);
            if (child instanceof FlickerLoadingView) {
                progressView = child;
            }
        }
        View finalProgressView = progressView;
        if (progressView != null) {
            finalListView.removeView(progressView);
        }
        getViewTreeObserver().addOnPreDrawListener(new AnonymousClass27(finalListView, addedMesages, finalProgressView, oldItemCount));
    }

    /* renamed from: org.telegram.ui.Components.SharedMediaLayout$27 */
    /* loaded from: classes5.dex */
    public class AnonymousClass27 implements ViewTreeObserver.OnPreDrawListener {
        final /* synthetic */ SparseBooleanArray val$addedMesages;
        final /* synthetic */ RecyclerListView val$finalListView;
        final /* synthetic */ View val$finalProgressView;
        final /* synthetic */ int val$oldItemCount;

        AnonymousClass27(RecyclerListView recyclerListView, SparseBooleanArray sparseBooleanArray, View view, int i) {
            SharedMediaLayout.this = this$0;
            this.val$finalListView = recyclerListView;
            this.val$addedMesages = sparseBooleanArray;
            this.val$finalProgressView = view;
            this.val$oldItemCount = i;
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            SharedMediaLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
            RecyclerView.Adapter adapter = this.val$finalListView.getAdapter();
            if (adapter == SharedMediaLayout.this.photoVideoAdapter || adapter == SharedMediaLayout.this.documentsAdapter || adapter == SharedMediaLayout.this.audioAdapter || adapter == SharedMediaLayout.this.voiceAdapter) {
                if (this.val$addedMesages != null) {
                    int n = this.val$finalListView.getChildCount();
                    for (int i = 0; i < n; i++) {
                        View child = this.val$finalListView.getChildAt(i);
                        final int messageId = SharedMediaLayout.this.getMessageId(child);
                        if (messageId != 0 && this.val$addedMesages.get(messageId, false)) {
                            SharedMediaLayout.this.messageAlphaEnter.put(messageId, Float.valueOf(0.0f));
                            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                            final RecyclerListView recyclerListView = this.val$finalListView;
                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.SharedMediaLayout$27$$ExternalSyntheticLambda0
                                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                                    SharedMediaLayout.AnonymousClass27.this.m3039xf7884674(messageId, recyclerListView, valueAnimator2);
                                }
                            });
                            valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SharedMediaLayout.27.1
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    SharedMediaLayout.this.messageAlphaEnter.remove(messageId);
                                    AnonymousClass27.this.val$finalListView.invalidate();
                                }
                            });
                            int s = Math.min(this.val$finalListView.getMeasuredHeight(), Math.max(0, child.getTop()));
                            int delay = (int) ((s / this.val$finalListView.getMeasuredHeight()) * 100.0f);
                            valueAnimator.setStartDelay(delay);
                            valueAnimator.setDuration(250L);
                            valueAnimator.start();
                        }
                        this.val$finalListView.invalidate();
                    }
                }
            } else {
                int n2 = this.val$finalListView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i2 = 0; i2 < n2; i2++) {
                    View child2 = this.val$finalListView.getChildAt(i2);
                    if (child2 != this.val$finalProgressView && this.val$finalListView.getChildAdapterPosition(child2) >= this.val$oldItemCount - 1) {
                        child2.setAlpha(0.0f);
                        int s2 = Math.min(this.val$finalListView.getMeasuredHeight(), Math.max(0, child2.getTop()));
                        int delay2 = (int) ((s2 / this.val$finalListView.getMeasuredHeight()) * 100.0f);
                        ObjectAnimator a = ObjectAnimator.ofFloat(child2, View.ALPHA, 0.0f, 1.0f);
                        a.setStartDelay(delay2);
                        a.setDuration(200L);
                        animatorSet.playTogether(a);
                    }
                    View view = this.val$finalProgressView;
                    if (view != null && view.getParent() == null) {
                        this.val$finalListView.addView(this.val$finalProgressView);
                        final RecyclerView.LayoutManager layoutManager = this.val$finalListView.getLayoutManager();
                        if (layoutManager != null) {
                            layoutManager.ignoreView(this.val$finalProgressView);
                            Animator animator = ObjectAnimator.ofFloat(this.val$finalProgressView, View.ALPHA, this.val$finalProgressView.getAlpha(), 0.0f);
                            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SharedMediaLayout.27.2
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    AnonymousClass27.this.val$finalProgressView.setAlpha(1.0f);
                                    layoutManager.stopIgnoringView(AnonymousClass27.this.val$finalProgressView);
                                    AnonymousClass27.this.val$finalListView.removeView(AnonymousClass27.this.val$finalProgressView);
                                }
                            });
                            animator.start();
                        }
                    }
                }
                animatorSet.start();
            }
            return true;
        }

        /* renamed from: lambda$onPreDraw$0$org-telegram-ui-Components-SharedMediaLayout$27 */
        public /* synthetic */ void m3039xf7884674(int messageId, RecyclerListView finalListView, ValueAnimator valueAnimator1) {
            SharedMediaLayout.this.messageAlphaEnter.put(messageId, (Float) valueAnimator1.getAnimatedValue());
            finalListView.invalidate();
        }
    }

    public void onResume() {
        this.scrolling = true;
        SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
        if (sharedPhotoVideoAdapter != null) {
            sharedPhotoVideoAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        for (int a = 0; a < this.mediaPages.length; a++) {
            fixLayoutInternal(a);
        }
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int a = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (a < mediaPageArr.length) {
                if (mediaPageArr[a].listView != null) {
                    final int num = a;
                    ViewTreeObserver obs = this.mediaPages[a].listView.getViewTreeObserver();
                    obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.SharedMediaLayout.28
                        @Override // android.view.ViewTreeObserver.OnPreDrawListener
                        public boolean onPreDraw() {
                            SharedMediaLayout.this.mediaPages[num].getViewTreeObserver().removeOnPreDrawListener(this);
                            SharedMediaLayout.this.fixLayoutInternal(num);
                            return true;
                        }
                    });
                }
                a++;
            } else {
                return;
            }
        }
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo) {
        this.info = chatInfo;
        if (chatInfo != null && chatInfo.migrated_from_chat_id != 0 && this.mergeDialogId == 0) {
            this.mergeDialogId = -this.info.migrated_from_chat_id;
            int a = 0;
            while (true) {
                SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                if (a < sharedMediaDataArr.length) {
                    sharedMediaDataArr[a].max_id[1] = this.info.migrated_from_max_id;
                    this.sharedMediaData[a].endReached[1] = false;
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    public void setChatUsers(ArrayList<Integer> sortedUsers, TLRPC.ChatFull chatInfo) {
        this.chatUsersAdapter.chatInfo = chatInfo;
        this.chatUsersAdapter.sortedUsers = sortedUsers;
        updateTabs(true);
        int a = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (a < mediaPageArr.length) {
                if (mediaPageArr[a].selectedType == 7) {
                    this.mediaPages[a].listView.getAdapter().notifyDataSetChanged();
                }
                a++;
            } else {
                return;
            }
        }
    }

    public void updateAdapters() {
        SharedPhotoVideoAdapter sharedPhotoVideoAdapter = this.photoVideoAdapter;
        if (sharedPhotoVideoAdapter != null) {
            sharedPhotoVideoAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter = this.documentsAdapter;
        if (sharedDocumentsAdapter != null) {
            sharedDocumentsAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter2 = this.voiceAdapter;
        if (sharedDocumentsAdapter2 != null) {
            sharedDocumentsAdapter2.notifyDataSetChanged();
        }
        SharedLinksAdapter sharedLinksAdapter = this.linksAdapter;
        if (sharedLinksAdapter != null) {
            sharedLinksAdapter.notifyDataSetChanged();
        }
        SharedDocumentsAdapter sharedDocumentsAdapter3 = this.audioAdapter;
        if (sharedDocumentsAdapter3 != null) {
            sharedDocumentsAdapter3.notifyDataSetChanged();
        }
        GifAdapter gifAdapter = this.gifAdapter;
        if (gifAdapter != null) {
            gifAdapter.notifyDataSetChanged();
        }
    }

    private void updateRowsSelection() {
        int i = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (i < mediaPageArr.length) {
                int count = mediaPageArr[i].listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.mediaPages[i].listView.getChildAt(a);
                    if (child instanceof SharedDocumentCell) {
                        ((SharedDocumentCell) child).setChecked(false, true);
                    } else if (child instanceof SharedPhotoVideoCell2) {
                        ((SharedPhotoVideoCell2) child).setChecked(false, true);
                    } else if (child instanceof SharedLinkCell) {
                        ((SharedLinkCell) child).setChecked(false, true);
                    } else if (child instanceof SharedAudioCell) {
                        ((SharedAudioCell) child).setChecked(false, true);
                    } else if (child instanceof ContextLinkCell) {
                        ((ContextLinkCell) child).setChecked(false, true);
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void setMergeDialogId(long did) {
        this.mergeDialogId = did;
    }

    private void updateTabs(boolean animated) {
        if (this.scrollSlidingTextTabStrip == null) {
            return;
        }
        if (!this.delegate.isFragmentOpened()) {
            animated = false;
        }
        int changed = 0;
        if ((this.chatUsersAdapter.chatInfo == null) == this.scrollSlidingTextTabStrip.hasTab(7)) {
            changed = 0 + 1;
        }
        if ((this.hasMedia[0] <= 0) == this.scrollSlidingTextTabStrip.hasTab(0)) {
            changed++;
        }
        if ((this.hasMedia[1] <= 0) == this.scrollSlidingTextTabStrip.hasTab(1)) {
            changed++;
        }
        if (!DialogObject.isEncryptedDialog(this.dialog_id)) {
            if ((this.hasMedia[3] <= 0) == this.scrollSlidingTextTabStrip.hasTab(3)) {
                changed++;
            }
            if ((this.hasMedia[4] <= 0) == this.scrollSlidingTextTabStrip.hasTab(4)) {
                changed++;
            }
        } else {
            if ((this.hasMedia[4] <= 0) == this.scrollSlidingTextTabStrip.hasTab(4)) {
                changed++;
            }
        }
        if ((this.hasMedia[2] <= 0) == this.scrollSlidingTextTabStrip.hasTab(2)) {
            changed++;
        }
        if ((this.hasMedia[5] <= 0) == this.scrollSlidingTextTabStrip.hasTab(5)) {
            changed++;
        }
        if ((this.hasMedia[6] <= 0) == this.scrollSlidingTextTabStrip.hasTab(6)) {
            changed++;
        }
        if (changed > 0) {
            if (animated && Build.VERSION.SDK_INT >= 19) {
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.setOrdering(0);
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.addTransition(new Visibility() { // from class: org.telegram.ui.Components.SharedMediaLayout.29
                    @Override // android.transition.Visibility
                    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(view, View.SCALE_X, 0.5f, 1.0f), ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.5f, 1.0f));
                        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        return set;
                    }

                    @Override // android.transition.Visibility
                    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, view.getAlpha(), 0.0f), ObjectAnimator.ofFloat(view, View.SCALE_X, view.getScaleX(), 0.5f), ObjectAnimator.ofFloat(view, View.SCALE_Y, view.getScaleX(), 0.5f));
                        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        return set;
                    }
                });
                transitionSet.setDuration(200L);
                TransitionManager.beginDelayedTransition(this.scrollSlidingTextTabStrip.getTabsContainer(), transitionSet);
                this.scrollSlidingTextTabStrip.recordIndicatorParams();
            }
            SparseArray<View> idToView = this.scrollSlidingTextTabStrip.removeTabs();
            if (changed > 3) {
                idToView = null;
            }
            if (this.chatUsersAdapter.chatInfo != null && !this.scrollSlidingTextTabStrip.hasTab(7)) {
                this.scrollSlidingTextTabStrip.addTextTab(7, LocaleController.getString("GroupMembers", R.string.GroupMembers), idToView);
            }
            if (this.hasMedia[0] > 0 && !this.scrollSlidingTextTabStrip.hasTab(0)) {
                int[] iArr = this.hasMedia;
                if (iArr[1] == 0 && iArr[2] == 0 && iArr[3] == 0 && iArr[4] == 0 && iArr[5] == 0 && iArr[6] == 0 && this.chatUsersAdapter.chatInfo == null) {
                    this.scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("SharedMediaTabFull2", R.string.SharedMediaTabFull2), idToView);
                } else {
                    this.scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("SharedMediaTab2", R.string.SharedMediaTab2), idToView);
                }
            }
            if (this.hasMedia[1] > 0 && !this.scrollSlidingTextTabStrip.hasTab(1)) {
                this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("SharedFilesTab2", R.string.SharedFilesTab2), idToView);
            }
            if (!DialogObject.isEncryptedDialog(this.dialog_id)) {
                if (this.hasMedia[3] > 0 && !this.scrollSlidingTextTabStrip.hasTab(3)) {
                    this.scrollSlidingTextTabStrip.addTextTab(3, LocaleController.getString("SharedLinksTab2", R.string.SharedLinksTab2), idToView);
                }
                if (this.hasMedia[4] > 0 && !this.scrollSlidingTextTabStrip.hasTab(4)) {
                    this.scrollSlidingTextTabStrip.addTextTab(4, LocaleController.getString("SharedMusicTab2", R.string.SharedMusicTab2), idToView);
                }
            } else if (this.hasMedia[4] > 0 && !this.scrollSlidingTextTabStrip.hasTab(4)) {
                this.scrollSlidingTextTabStrip.addTextTab(4, LocaleController.getString("SharedMusicTab2", R.string.SharedMusicTab2), idToView);
            }
            if (this.hasMedia[2] > 0 && !this.scrollSlidingTextTabStrip.hasTab(2)) {
                this.scrollSlidingTextTabStrip.addTextTab(2, LocaleController.getString("SharedVoiceTab2", R.string.SharedVoiceTab2), idToView);
            }
            if (this.hasMedia[5] > 0 && !this.scrollSlidingTextTabStrip.hasTab(5)) {
                this.scrollSlidingTextTabStrip.addTextTab(5, LocaleController.getString("SharedGIFsTab2", R.string.SharedGIFsTab2), idToView);
            }
            if (this.hasMedia[6] > 0 && !this.scrollSlidingTextTabStrip.hasTab(6)) {
                this.scrollSlidingTextTabStrip.addTextTab(6, LocaleController.getString("SharedGroupsTab2", R.string.SharedGroupsTab2), idToView);
            }
        }
        int id = this.scrollSlidingTextTabStrip.getCurrentTabId();
        if (id >= 0) {
            this.mediaPages[0].selectedType = id;
        }
        this.scrollSlidingTextTabStrip.finishAddingTabs();
    }

    public void startStopVisibleGifs() {
        int b = 0;
        while (true) {
            MediaPage[] mediaPageArr = this.mediaPages;
            if (b < mediaPageArr.length) {
                int count = mediaPageArr[b].listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.mediaPages[b].listView.getChildAt(a);
                    if (child instanceof ContextLinkCell) {
                        ContextLinkCell cell = (ContextLinkCell) child;
                        ImageReceiver imageReceiver = cell.getPhotoImage();
                        if (b == 0) {
                            imageReceiver.setAllowStartAnimation(true);
                            imageReceiver.startAnimation();
                        } else {
                            imageReceiver.setAllowStartAnimation(false);
                            imageReceiver.stopAnimation();
                        }
                    }
                }
                b++;
            } else {
                return;
            }
        }
    }

    public void switchToCurrentSelectedMode(boolean animated) {
        MediaPage[] mediaPageArr;
        GroupUsersSearchAdapter groupUsersSearchAdapter;
        int a = 0;
        while (true) {
            mediaPageArr = this.mediaPages;
            if (a >= mediaPageArr.length) {
                break;
            }
            mediaPageArr[a].listView.stopScroll();
            a++;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mediaPageArr[animated ? 1 : 0].getLayoutParams();
        boolean fastScrollVisible = false;
        int spanCount = 100;
        RecyclerView.Adapter currentAdapter = this.mediaPages[animated].listView.getAdapter();
        RecyclerView.RecycledViewPool viewPool = null;
        if (!this.searching || !this.searchWas) {
            this.mediaPages[animated].listView.setPinnedHeaderShadowDrawable(null);
            if (this.mediaPages[animated].selectedType != 0) {
                if (this.mediaPages[animated].selectedType != 1) {
                    if (this.mediaPages[animated].selectedType != 2) {
                        if (this.mediaPages[animated].selectedType != 3) {
                            if (this.mediaPages[animated].selectedType != 4) {
                                if (this.mediaPages[animated].selectedType != 5) {
                                    if (this.mediaPages[animated].selectedType != 6) {
                                        if (this.mediaPages[animated].selectedType == 7 && currentAdapter != this.chatUsersAdapter) {
                                            recycleAdapter(currentAdapter);
                                            this.mediaPages[animated].listView.setAdapter(this.chatUsersAdapter);
                                        }
                                    } else if (currentAdapter != this.commonGroupsAdapter) {
                                        recycleAdapter(currentAdapter);
                                        this.mediaPages[animated].listView.setAdapter(this.commonGroupsAdapter);
                                    }
                                } else if (currentAdapter != this.gifAdapter) {
                                    recycleAdapter(currentAdapter);
                                    this.mediaPages[animated].listView.setAdapter(this.gifAdapter);
                                }
                            } else {
                                if (this.sharedMediaData[4].fastScrollDataLoaded && !this.sharedMediaData[4].fastScrollPeriods.isEmpty()) {
                                    fastScrollVisible = true;
                                }
                                if (currentAdapter != this.audioAdapter) {
                                    recycleAdapter(currentAdapter);
                                    this.mediaPages[animated].listView.setAdapter(this.audioAdapter);
                                }
                            }
                        } else if (currentAdapter != this.linksAdapter) {
                            recycleAdapter(currentAdapter);
                            this.mediaPages[animated].listView.setAdapter(this.linksAdapter);
                        }
                    } else {
                        if (this.sharedMediaData[2].fastScrollDataLoaded && !this.sharedMediaData[2].fastScrollPeriods.isEmpty()) {
                            fastScrollVisible = true;
                        }
                        if (currentAdapter != this.voiceAdapter) {
                            recycleAdapter(currentAdapter);
                            this.mediaPages[animated].listView.setAdapter(this.voiceAdapter);
                        }
                    }
                } else {
                    if (this.sharedMediaData[1].fastScrollDataLoaded && !this.sharedMediaData[1].fastScrollPeriods.isEmpty()) {
                        fastScrollVisible = true;
                    }
                    if (currentAdapter != this.documentsAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[animated].listView.setAdapter(this.documentsAdapter);
                    }
                }
            } else {
                if (currentAdapter != this.photoVideoAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[animated].listView.setAdapter(this.photoVideoAdapter);
                }
                int i = -AndroidUtilities.dp(1.0f);
                layoutParams.rightMargin = i;
                layoutParams.leftMargin = i;
                if (this.sharedMediaData[0].fastScrollDataLoaded && !this.sharedMediaData[0].fastScrollPeriods.isEmpty()) {
                    fastScrollVisible = true;
                }
                spanCount = this.mediaColumnsCount;
                this.mediaPages[animated].listView.setPinnedHeaderShadowDrawable(this.pinnedHeaderShadowDrawable);
                if (this.sharedMediaData[0].recycledViewPool == null) {
                    this.sharedMediaData[0].recycledViewPool = new RecyclerView.RecycledViewPool();
                }
                viewPool = this.sharedMediaData[0].recycledViewPool;
            }
            if (this.mediaPages[animated].selectedType == 0 || this.mediaPages[animated].selectedType == 2 || this.mediaPages[animated].selectedType == 5 || this.mediaPages[animated].selectedType == 6 || (this.mediaPages[animated].selectedType == 7 && !this.delegate.canSearchMembers())) {
                if (animated != 0) {
                    this.searchItemState = 2;
                } else {
                    this.searchItemState = 0;
                    this.searchItem.setVisibility(4);
                }
            } else if (animated != 0) {
                if (this.searchItem.getVisibility() == 4 && !this.actionBar.isSearchFieldVisible()) {
                    if (canShowSearchItem()) {
                        this.searchItemState = 1;
                        this.searchItem.setVisibility(0);
                    } else {
                        this.searchItem.setVisibility(4);
                    }
                    this.searchItem.setAlpha(0.0f);
                } else {
                    this.searchItemState = 0;
                }
            } else if (this.searchItem.getVisibility() == 4) {
                if (canShowSearchItem()) {
                    this.searchItemState = 0;
                    this.searchItem.setAlpha(1.0f);
                    this.searchItem.setVisibility(0);
                } else {
                    this.searchItem.setVisibility(4);
                    this.searchItem.setAlpha(0.0f);
                }
            }
            if (this.mediaPages[animated].selectedType != 6) {
                if (this.mediaPages[animated].selectedType != 7 && !this.sharedMediaData[this.mediaPages[animated].selectedType].loading && !this.sharedMediaData[this.mediaPages[animated].selectedType].endReached[0] && this.sharedMediaData[this.mediaPages[animated].selectedType].messages.isEmpty()) {
                    this.sharedMediaData[this.mediaPages[animated].selectedType].loading = true;
                    this.documentsAdapter.notifyDataSetChanged();
                    int type = this.mediaPages[animated].selectedType;
                    if (type == 0) {
                        if (this.sharedMediaData[0].filterType == 1) {
                            type = 6;
                        } else if (this.sharedMediaData[0].filterType == 2) {
                            type = 7;
                        }
                    }
                    this.profileActivity.getMediaDataController().loadMedia(this.dialog_id, 50, 0, 0, type, 1, this.profileActivity.getClassGuid(), this.sharedMediaData[this.mediaPages[animated].selectedType].requestIndex);
                }
            } else if (!this.commonGroupsAdapter.loading && !this.commonGroupsAdapter.endReached && this.commonGroupsAdapter.chats.isEmpty()) {
                this.commonGroupsAdapter.getChats(0L, 100);
            }
            this.mediaPages[animated].listView.setVisibility(0);
        } else if (animated != 0) {
            if (this.mediaPages[animated].selectedType == 0 || this.mediaPages[animated].selectedType == 2 || this.mediaPages[animated].selectedType == 5 || this.mediaPages[animated].selectedType == 6 || (this.mediaPages[animated].selectedType == 7 && !this.delegate.canSearchMembers())) {
                this.searching = false;
                this.searchWas = false;
                switchToCurrentSelectedMode(true);
                return;
            }
            String text = this.searchItem.getSearchField().getText().toString();
            if (this.mediaPages[animated].selectedType != 1) {
                if (this.mediaPages[animated].selectedType != 3) {
                    if (this.mediaPages[animated].selectedType != 4) {
                        if (this.mediaPages[animated].selectedType == 7 && (groupUsersSearchAdapter = this.groupUsersSearchAdapter) != null) {
                            groupUsersSearchAdapter.search(text, false);
                            if (currentAdapter != this.groupUsersSearchAdapter) {
                                recycleAdapter(currentAdapter);
                                this.mediaPages[animated].listView.setAdapter(this.groupUsersSearchAdapter);
                            }
                        }
                    } else {
                        MediaSearchAdapter mediaSearchAdapter = this.audioSearchAdapter;
                        if (mediaSearchAdapter != null) {
                            mediaSearchAdapter.search(text, false);
                            if (currentAdapter != this.audioSearchAdapter) {
                                recycleAdapter(currentAdapter);
                                this.mediaPages[animated].listView.setAdapter(this.audioSearchAdapter);
                            }
                        }
                    }
                } else {
                    MediaSearchAdapter mediaSearchAdapter2 = this.linksSearchAdapter;
                    if (mediaSearchAdapter2 != null) {
                        mediaSearchAdapter2.search(text, false);
                        if (currentAdapter != this.linksSearchAdapter) {
                            recycleAdapter(currentAdapter);
                            this.mediaPages[animated].listView.setAdapter(this.linksSearchAdapter);
                        }
                    }
                }
            } else {
                MediaSearchAdapter mediaSearchAdapter3 = this.documentsSearchAdapter;
                if (mediaSearchAdapter3 != null) {
                    mediaSearchAdapter3.search(text, false);
                    if (currentAdapter != this.documentsSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[animated].listView.setAdapter(this.documentsSearchAdapter);
                    }
                }
            }
        } else if (this.mediaPages[animated].listView != null) {
            if (this.mediaPages[animated].selectedType != 1) {
                if (this.mediaPages[animated].selectedType != 3) {
                    if (this.mediaPages[animated].selectedType != 4) {
                        if (this.mediaPages[animated].selectedType == 7) {
                            if (currentAdapter != this.groupUsersSearchAdapter) {
                                recycleAdapter(currentAdapter);
                                this.mediaPages[animated].listView.setAdapter(this.groupUsersSearchAdapter);
                            }
                            this.groupUsersSearchAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (currentAdapter != this.audioSearchAdapter) {
                            recycleAdapter(currentAdapter);
                            this.mediaPages[animated].listView.setAdapter(this.audioSearchAdapter);
                        }
                        this.audioSearchAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (currentAdapter != this.linksSearchAdapter) {
                        recycleAdapter(currentAdapter);
                        this.mediaPages[animated].listView.setAdapter(this.linksSearchAdapter);
                    }
                    this.linksSearchAdapter.notifyDataSetChanged();
                }
            } else {
                if (currentAdapter != this.documentsSearchAdapter) {
                    recycleAdapter(currentAdapter);
                    this.mediaPages[animated].listView.setAdapter(this.documentsSearchAdapter);
                }
                this.documentsSearchAdapter.notifyDataSetChanged();
            }
        }
        this.mediaPages[animated].fastScrollEnabled = fastScrollVisible;
        updateFastScrollVisibility(this.mediaPages[animated], false);
        this.mediaPages[animated].layoutManager.setSpanCount(spanCount);
        this.mediaPages[animated].listView.setRecycledViewPool(viewPool);
        this.mediaPages[animated].animationSupportingListView.setRecycledViewPool(viewPool);
        if (this.searchItemState == 2 && this.actionBar.isSearchFieldVisible()) {
            this.ignoreSearchCollapse = true;
            this.actionBar.closeSearchField();
            this.searchItemState = 0;
            this.searchItem.setAlpha(0.0f);
            this.searchItem.setVisibility(4);
        }
    }

    private boolean onItemLongClick(MessageObject item, View view, int a) {
        if (this.isActionModeShowed || this.profileActivity.getParentActivity() == null || item == null) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.profileActivity.getParentActivity().getCurrentFocus());
        this.selectedFiles[item.getDialogId() == this.dialog_id ? (char) 0 : (char) 1].put(item.getId(), item);
        if (!item.canDeleteMessage(false, null)) {
            this.cantDeleteMessagesCount++;
        }
        this.deleteItem.setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
        ActionBarMenuItem actionBarMenuItem = this.gotoItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(0);
        }
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int i = 0; i < this.actionModeViews.size(); i++) {
            View view2 = this.actionModeViews.get(i);
            AndroidUtilities.clearDrawableAnimation(view2);
            animators.add(ObjectAnimator.ofFloat(view2, View.SCALE_Y, 0.1f, 1.0f));
        }
        animatorSet.playTogether(animators);
        animatorSet.setDuration(250L);
        animatorSet.start();
        this.scrolling = false;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell) {
            ((SharedPhotoVideoCell) view).setChecked(a, true, true);
        } else if (view instanceof SharedLinkCell) {
            ((SharedLinkCell) view).setChecked(true, true);
        } else if (view instanceof SharedAudioCell) {
            ((SharedAudioCell) view).setChecked(true, true);
        } else if (view instanceof ContextLinkCell) {
            ((ContextLinkCell) view).setChecked(true, true);
        } else if (view instanceof SharedPhotoVideoCell2) {
            ((SharedPhotoVideoCell2) view).setChecked(true, true);
        }
        if (!this.isActionModeShowed) {
            showActionMode(true);
        }
        updateForwardItem();
        return true;
    }

    private void onItemClick(int index, View view, MessageObject message, int a, int selectedMode) {
        if (message != null && !this.photoVideoChangeColumnsAnimation) {
            TLRPC.WebPage webPage = null;
            boolean z = false;
            if (this.isActionModeShowed) {
                int loadIndex = message.getDialogId() == this.dialog_id ? 0 : 1;
                if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) < 0) {
                    if (this.selectedFiles[0].size() + this.selectedFiles[1].size() >= 100) {
                        return;
                    }
                    this.selectedFiles[loadIndex].put(message.getId(), message);
                    if (!message.canDeleteMessage(false, null)) {
                        this.cantDeleteMessagesCount++;
                    }
                } else {
                    this.selectedFiles[loadIndex].remove(message.getId());
                    if (!message.canDeleteMessage(false, null)) {
                        this.cantDeleteMessagesCount--;
                    }
                }
                if (this.selectedFiles[0].size() != 0 || this.selectedFiles[1].size() != 0) {
                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles[0].size() + this.selectedFiles[1].size(), true);
                    int i = 8;
                    this.deleteItem.setVisibility(this.cantDeleteMessagesCount == 0 ? 0 : 8);
                    ActionBarMenuItem actionBarMenuItem = this.gotoItem;
                    if (actionBarMenuItem != null) {
                        if (this.selectedFiles[0].size() == 1) {
                            i = 0;
                        }
                        actionBarMenuItem.setVisibility(i);
                    }
                } else {
                    showActionMode(false);
                }
                this.scrolling = false;
                if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) view;
                    if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, true);
                } else if (view instanceof SharedPhotoVideoCell) {
                    SharedPhotoVideoCell sharedPhotoVideoCell = (SharedPhotoVideoCell) view;
                    if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    sharedPhotoVideoCell.setChecked(a, z, true);
                } else if (view instanceof SharedLinkCell) {
                    SharedLinkCell sharedLinkCell = (SharedLinkCell) view;
                    if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    sharedLinkCell.setChecked(z, true);
                } else if (view instanceof SharedAudioCell) {
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) view;
                    if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    sharedAudioCell.setChecked(z, true);
                } else if (view instanceof ContextLinkCell) {
                    ContextLinkCell contextLinkCell = (ContextLinkCell) view;
                    if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    contextLinkCell.setChecked(z, true);
                } else if (view instanceof SharedPhotoVideoCell2) {
                    SharedPhotoVideoCell2 sharedPhotoVideoCell2 = (SharedPhotoVideoCell2) view;
                    if (this.selectedFiles[loadIndex].indexOfKey(message.getId()) >= 0) {
                        z = true;
                    }
                    sharedPhotoVideoCell2.setChecked(z, true);
                }
            } else if (selectedMode == 0) {
                int i2 = index - this.sharedMediaData[selectedMode].startOffset;
                if (i2 >= 0 && i2 < this.sharedMediaData[selectedMode].messages.size()) {
                    PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                    PhotoViewer.getInstance().openPhoto(this.sharedMediaData[selectedMode].messages, i2, this.dialog_id, this.mergeDialogId, this.provider);
                }
            } else if (selectedMode == 2 || selectedMode == 4) {
                if (view instanceof SharedAudioCell) {
                    ((SharedAudioCell) view).didPressedButton();
                }
            } else if (selectedMode == 5) {
                PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                int index2 = this.sharedMediaData[selectedMode].messages.indexOf(message);
                if (index2 >= 0) {
                    PhotoViewer.getInstance().openPhoto(this.sharedMediaData[selectedMode].messages, index2, this.dialog_id, this.mergeDialogId, this.provider);
                } else {
                    ArrayList<MessageObject> documents = new ArrayList<>();
                    documents.add(message);
                    PhotoViewer.getInstance().openPhoto(documents, 0, 0L, 0L, this.provider);
                }
                updateForwardItem();
            } else if (selectedMode == 1) {
                if (view instanceof SharedDocumentCell) {
                    SharedDocumentCell cell = (SharedDocumentCell) view;
                    TLRPC.Document document = message.getDocument();
                    if (cell.isLoaded()) {
                        if (message.canPreviewDocument()) {
                            PhotoViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity());
                            int index3 = this.sharedMediaData[selectedMode].messages.indexOf(message);
                            if (index3 >= 0) {
                                PhotoViewer.getInstance().openPhoto(this.sharedMediaData[selectedMode].messages, index3, this.dialog_id, this.mergeDialogId, this.provider);
                                return;
                            }
                            ArrayList<MessageObject> documents2 = new ArrayList<>();
                            documents2.add(message);
                            PhotoViewer.getInstance().openPhoto(documents2, 0, 0L, 0L, this.provider);
                            return;
                        }
                        AndroidUtilities.openDocument(message, this.profileActivity.getParentActivity(), this.profileActivity);
                    } else if (!cell.isLoading()) {
                        MessageObject messageObject = cell.getMessage();
                        messageObject.putInDownloadsStore = true;
                        this.profileActivity.getFileLoader().loadFile(document, messageObject, 0, 0);
                        cell.updateFileExistIcon(true);
                    } else {
                        this.profileActivity.getFileLoader().cancelLoadFile(document);
                        cell.updateFileExistIcon(true);
                    }
                }
            } else if (selectedMode == 3) {
                try {
                    if (message.messageOwner.media != null) {
                        webPage = message.messageOwner.media.webpage;
                    }
                    TLRPC.WebPage webPage2 = webPage;
                    String link = null;
                    if (webPage2 != null && !(webPage2 instanceof TLRPC.TL_webPageEmpty)) {
                        if (webPage2.cached_page != null) {
                            ArticleViewer.getInstance().setParentActivity(this.profileActivity.getParentActivity(), this.profileActivity);
                            ArticleViewer.getInstance().open(message);
                            return;
                        } else if (webPage2.embed_url != null && webPage2.embed_url.length() != 0) {
                            openWebView(webPage2, message);
                            return;
                        } else {
                            link = webPage2.url;
                        }
                    }
                    if (link == null) {
                        link = ((SharedLinkCell) view).getLink(0);
                    }
                    if (link != null) {
                        openUrl(link);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            updateForwardItem();
        }
    }

    public void openUrl(String link) {
        if (AndroidUtilities.shouldShowUrlInAlert(link)) {
            AlertsCreator.showOpenUrlAlert(this.profileActivity, link, true, true);
        } else {
            Browser.openUrl(this.profileActivity.getParentActivity(), link);
        }
    }

    public void openWebView(TLRPC.WebPage webPage, MessageObject message) {
        EmbedBottomSheet.show(this.profileActivity.getParentActivity(), message, this.provider, webPage.site_name, webPage.description, webPage.url, webPage.embed_url, webPage.embed_width, webPage.embed_height, false);
    }

    private void recycleAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof SharedPhotoVideoAdapter) {
            this.cellCache.addAll(this.cache);
            this.cache.clear();
        } else if (adapter == this.audioAdapter) {
            this.audioCellCache.addAll(this.audioCache);
            this.audioCache.clear();
        }
    }

    public void fixLayoutInternal(int num) {
        WindowManager manager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        manager.getDefaultDisplay().getRotation();
        if (num == 0) {
            if (!AndroidUtilities.isTablet() && ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 2) {
                this.selectedMessagesCountTextView.setTextSize(18);
            } else {
                this.selectedMessagesCountTextView.setTextSize(20);
            }
        }
        if (num == 0) {
            this.photoVideoAdapter.notifyDataSetChanged();
        }
    }

    /* renamed from: org.telegram.ui.Components.SharedMediaLayout$30 */
    /* loaded from: classes5.dex */
    public class AnonymousClass30 implements SharedLinkCell.SharedLinkCellDelegate {
        AnonymousClass30() {
            SharedMediaLayout.this = this$0;
        }

        @Override // org.telegram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate
        public void needOpenWebView(TLRPC.WebPage webPage, MessageObject message) {
            SharedMediaLayout.this.openWebView(webPage, message);
        }

        @Override // org.telegram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate
        public boolean canPerformActions() {
            return !SharedMediaLayout.this.isActionModeShowed;
        }

        @Override // org.telegram.ui.Cells.SharedLinkCell.SharedLinkCellDelegate
        public void onLinkPress(final String urlFinal, boolean longPress) {
            if (!longPress) {
                SharedMediaLayout.this.openUrl(urlFinal);
                return;
            }
            BottomSheet.Builder builder = new BottomSheet.Builder(SharedMediaLayout.this.profileActivity.getParentActivity());
            builder.setTitle(urlFinal);
            builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.SharedMediaLayout$30$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    SharedMediaLayout.AnonymousClass30.this.m3040xe9a1eee(urlFinal, dialogInterface, i);
                }
            });
            SharedMediaLayout.this.profileActivity.showDialog(builder.create());
        }

        /* renamed from: lambda$onLinkPress$0$org-telegram-ui-Components-SharedMediaLayout$30 */
        public /* synthetic */ void m3040xe9a1eee(String urlFinal, DialogInterface dialog, int which) {
            if (which == 0) {
                SharedMediaLayout.this.openUrl(urlFinal);
            } else if (which == 1) {
                String url = urlFinal;
                if (url.startsWith(MailTo.MAILTO_SCHEME)) {
                    url = url.substring(7);
                } else if (url.startsWith("tel:")) {
                    url = url.substring(4);
                }
                AndroidUtilities.addToClipboard(url);
            }
        }
    }

    /* loaded from: classes5.dex */
    public class SharedLinksAdapter extends RecyclerListView.SectionsAdapter {
        private Context mContext;

        public SharedLinksAdapter(Context context) {
            SharedMediaLayout.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public Object getItem(int section, int position) {
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() != 0 || SharedMediaLayout.this.sharedMediaData[3].loading) {
                return section == 0 || row != 0;
            }
            return false;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getSectionCount() {
            int i = 1;
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() != 0 || SharedMediaLayout.this.sharedMediaData[3].loading) {
                int size = SharedMediaLayout.this.sharedMediaData[3].sections.size();
                if (SharedMediaLayout.this.sharedMediaData[3].sections.isEmpty() || (SharedMediaLayout.this.sharedMediaData[3].endReached[0] && SharedMediaLayout.this.sharedMediaData[3].endReached[1])) {
                    i = 0;
                }
                return size + i;
            }
            return 1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getCountForSection(int section) {
            int i = 1;
            if ((SharedMediaLayout.this.sharedMediaData[3].sections.size() != 0 || SharedMediaLayout.this.sharedMediaData[3].loading) && section < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                int size = SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(SharedMediaLayout.this.sharedMediaData[3].sections.get(section)).size();
                if (section == 0) {
                    i = 0;
                }
                return size + i;
            }
            return 1;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new GraySectionCell(this.mContext);
                view.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_graySection) & (-218103809));
            }
            if (section != 0) {
                if (section < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                    view.setAlpha(1.0f);
                    String name = SharedMediaLayout.this.sharedMediaData[3].sections.get(section);
                    ArrayList<MessageObject> messageObjects = SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(name);
                    MessageObject messageObject = messageObjects.get(0);
                    ((GraySectionCell) view).setText(LocaleController.formatSectionDate(messageObject.messageOwner.date));
                }
            } else {
                view.setAlpha(0.0f);
            }
            return view;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GraySectionCell(this.mContext, SharedMediaLayout.this.resourcesProvider);
                    break;
                case 1:
                    view = new SharedLinkCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
                    ((SharedLinkCell) view).setDelegate(SharedMediaLayout.this.sharedLinkCellDelegate);
                    break;
                case 2:
                default:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext, SharedMediaLayout.this.resourcesProvider);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.showDate(false);
                    flickerLoadingView.setViewType(5);
                    view = flickerLoadingView;
                    break;
                case 3:
                    View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 3, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(emptyStubView);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() != 2 && holder.getItemViewType() != 3) {
                String name = SharedMediaLayout.this.sharedMediaData[3].sections.get(section);
                ArrayList<MessageObject> messageObjects = SharedMediaLayout.this.sharedMediaData[3].sectionArrays.get(name);
                boolean z = false;
                switch (holder.getItemViewType()) {
                    case 0:
                        ((GraySectionCell) holder.itemView).setText(LocaleController.formatSectionDate(messageObjects.get(0).messageOwner.date));
                        return;
                    case 1:
                        if (section != 0) {
                            position--;
                        }
                        SharedLinkCell sharedLinkCell = (SharedLinkCell) holder.itemView;
                        MessageObject messageObject = messageObjects.get(position);
                        sharedLinkCell.setLink(messageObject, position != messageObjects.size() - 1 || (section == SharedMediaLayout.this.sharedMediaData[3].sections.size() - 1 && SharedMediaLayout.this.sharedMediaData[3].loading));
                        if (SharedMediaLayout.this.isActionModeShowed) {
                            if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0) {
                                z = true;
                            }
                            sharedLinkCell.setChecked(z, !SharedMediaLayout.this.scrolling);
                            return;
                        }
                        sharedLinkCell.setChecked(false, !SharedMediaLayout.this.scrolling);
                        return;
                    default:
                        return;
                }
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
        public int getItemViewType(int section, int position) {
            if (SharedMediaLayout.this.sharedMediaData[3].sections.size() != 0 || SharedMediaLayout.this.sharedMediaData[3].loading) {
                if (section < SharedMediaLayout.this.sharedMediaData[3].sections.size()) {
                    if (section != 0 && position == 0) {
                        return 0;
                    }
                    return 1;
                }
                return 2;
            }
            return 3;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public String getLetter(int position) {
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = 0;
            position[1] = 0;
        }
    }

    /* loaded from: classes5.dex */
    public class SharedDocumentsAdapter extends RecyclerListView.FastScrollAdapter {
        private int currentType;
        private boolean inFastScrollMode;
        private Context mContext;

        public SharedDocumentsAdapter(Context context, int type) {
            SharedMediaLayout.this = r1;
            this.mContext = context;
            this.currentType = type;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].loadingAfterFastScroll) {
                return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[this.currentType].loading) {
                return 1;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() == 0 && ((!SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[0] || !SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[1]) && SharedMediaLayout.this.sharedMediaData[this.currentType].startReached)) {
                return 0;
            }
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount == 0) {
                int count = SharedMediaLayout.this.sharedMediaData[this.currentType].getStartOffset() + SharedMediaLayout.this.sharedMediaData[this.currentType].getMessages().size();
                if (count != 0) {
                    if (!SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[0] || !SharedMediaLayout.this.sharedMediaData[this.currentType].endReached[1]) {
                        if (SharedMediaLayout.this.sharedMediaData[this.currentType].getEndLoadingStubs() != 0) {
                            return count + SharedMediaLayout.this.sharedMediaData[this.currentType].getEndLoadingStubs();
                        }
                        return count + 1;
                    }
                    return count;
                }
                return count;
            }
            return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            View view2;
            switch (viewType) {
                case 1:
                    SharedDocumentCell cell = new SharedDocumentCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
                    cell.setGlobalGradientView(SharedMediaLayout.this.globalGradientView);
                    view = cell;
                    break;
                case 2:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext, SharedMediaLayout.this.resourcesProvider);
                    view = flickerLoadingView;
                    if (this.currentType == 2) {
                        flickerLoadingView.setViewType(4);
                    } else {
                        flickerLoadingView.setViewType(3);
                    }
                    flickerLoadingView.showDate(false);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setGlobalGradientView(SharedMediaLayout.this.globalGradientView);
                    break;
                case 3:
                default:
                    if (this.currentType == 4 && !SharedMediaLayout.this.audioCellCache.isEmpty()) {
                        View view3 = (View) SharedMediaLayout.this.audioCellCache.get(0);
                        SharedMediaLayout.this.audioCellCache.remove(0);
                        ViewGroup p = (ViewGroup) view3.getParent();
                        if (p != null) {
                            p.removeView(view3);
                        }
                        view2 = view3;
                    } else {
                        view2 = new SharedAudioCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider) { // from class: org.telegram.ui.Components.SharedMediaLayout.SharedDocumentsAdapter.1
                            @Override // org.telegram.ui.Cells.SharedAudioCell
                            public boolean needPlayMessage(MessageObject messageObject) {
                                if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                                    boolean result = MediaController.getInstance().playMessage(messageObject);
                                    MediaController.getInstance().setVoiceMessagesPlaylist(result ? SharedMediaLayout.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages : null, false);
                                    return result;
                                } else if (!messageObject.isMusic()) {
                                    return false;
                                } else {
                                    return MediaController.getInstance().setPlaylist(SharedMediaLayout.this.sharedMediaData[SharedDocumentsAdapter.this.currentType].messages, messageObject, SharedMediaLayout.this.mergeDialogId);
                                }
                            }
                        };
                    }
                    SharedAudioCell audioCell = (SharedAudioCell) view2;
                    audioCell.setGlobalGradientView(SharedMediaLayout.this.globalGradientView);
                    view = view2;
                    if (this.currentType == 4) {
                        SharedMediaLayout.this.audioCache.add((SharedAudioCell) view2);
                        view = view2;
                        break;
                    }
                    break;
                case 4:
                    View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, this.currentType, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(emptyStubView);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<MessageObject> messageObjects = SharedMediaLayout.this.sharedMediaData[this.currentType].messages;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 1:
                    SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) holder.itemView;
                    MessageObject messageObject = messageObjects.get(position - SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset);
                    sharedDocumentCell.setDocument(messageObject, position != messageObjects.size() - 1);
                    if (SharedMediaLayout.this.isActionModeShowed) {
                        if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        sharedDocumentCell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                        return;
                    }
                    sharedDocumentCell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
                    return;
                case 2:
                default:
                    return;
                case 3:
                    SharedAudioCell sharedAudioCell = (SharedAudioCell) holder.itemView;
                    MessageObject messageObject2 = messageObjects.get(position - SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset);
                    sharedAudioCell.setMessageObject(messageObject2, position != messageObjects.size() - 1);
                    if (SharedMediaLayout.this.isActionModeShowed) {
                        if (SharedMediaLayout.this.selectedFiles[messageObject2.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1].indexOfKey(messageObject2.getId()) >= 0) {
                            z = true;
                        }
                        sharedAudioCell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                        return;
                    }
                    sharedAudioCell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].sections.size() != 0 || SharedMediaLayout.this.sharedMediaData[this.currentType].loading) {
                if (position < SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset || position >= SharedMediaLayout.this.sharedMediaData[this.currentType].startOffset + SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size()) {
                    return 2;
                }
                int i = this.currentType;
                if (i == 2 || i == 4) {
                    return 3;
                }
                return 1;
            }
            return 4;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public String getLetter(int position) {
            if (SharedMediaLayout.this.sharedMediaData[this.currentType].fastScrollPeriods == null) {
                return "";
            }
            ArrayList<Period> periods = SharedMediaLayout.this.sharedMediaData[this.currentType].fastScrollPeriods;
            if (periods.isEmpty()) {
                return "";
            }
            for (int i = 0; i < periods.size(); i++) {
                if (position <= periods.get(i).startOffset) {
                    return periods.get(i).formatedDate;
                }
            }
            int i2 = periods.size();
            return periods.get(i2 - 1).formatedDate;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            int viewHeight = listView.getChildAt(0).getMeasuredHeight();
            int totalHeight = getTotalItemsCount() * viewHeight;
            int listViewHeight = listView.getMeasuredHeight() - listView.getPaddingTop();
            position[0] = (int) (((totalHeight - listViewHeight) * progress) / viewHeight);
            position[1] = ((int) ((totalHeight - listViewHeight) * progress)) % viewHeight;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void onStartFastScroll() {
            this.inFastScrollMode = true;
            MediaPage mediaPage = SharedMediaLayout.this.getMediaPage(this.currentType);
            if (mediaPage != null) {
                SharedMediaLayout.showFastScrollHint(mediaPage, null, false);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void onFinishFastScroll(RecyclerListView listView) {
            if (this.inFastScrollMode) {
                this.inFastScrollMode = false;
                if (listView != null) {
                    int messageId = 0;
                    for (int i = 0; i < listView.getChildCount(); i++) {
                        View child = listView.getChildAt(i);
                        messageId = SharedMediaLayout.this.getMessageId(child);
                        if (messageId != 0) {
                            break;
                        }
                    }
                    if (messageId == 0) {
                        SharedMediaLayout.this.findPeriodAndJumpToDate(this.currentType, listView, true);
                    }
                }
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public int getTotalItemsCount() {
            return SharedMediaLayout.this.sharedMediaData[this.currentType].totalCount;
        }
    }

    public static View createEmptyStubView(Context context, int currentType, long dialog_id, Theme.ResourcesProvider resourcesProvider) {
        EmptyStubView emptyStubView = new EmptyStubView(context, resourcesProvider);
        if (currentType == 0) {
            if (DialogObject.isEncryptedDialog(dialog_id)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoMediaSecret", R.string.NoMediaSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoMedia", R.string.NoMedia));
            }
        } else if (currentType == 1) {
            if (DialogObject.isEncryptedDialog(dialog_id)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedFilesSecret", R.string.NoSharedFilesSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedFiles", R.string.NoSharedFiles));
            }
        } else if (currentType == 2) {
            if (DialogObject.isEncryptedDialog(dialog_id)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedVoiceSecret", R.string.NoSharedVoiceSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedVoice", R.string.NoSharedVoice));
            }
        } else if (currentType == 3) {
            if (DialogObject.isEncryptedDialog(dialog_id)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedLinksSecret", R.string.NoSharedLinksSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedLinks", R.string.NoSharedLinks));
            }
        } else if (currentType == 4) {
            if (DialogObject.isEncryptedDialog(dialog_id)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedAudioSecret", R.string.NoSharedAudioSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedAudio", R.string.NoSharedAudio));
            }
        } else if (currentType == 5) {
            if (DialogObject.isEncryptedDialog(dialog_id)) {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoSharedGifSecret", R.string.NoSharedGifSecret));
            } else {
                emptyStubView.emptyTextView.setText(LocaleController.getString("NoGIFs", R.string.NoGIFs));
            }
        } else if (currentType == 6) {
            emptyStubView.emptyImageView.setImageDrawable(null);
            emptyStubView.emptyTextView.setText(LocaleController.getString("NoGroupsInCommon", R.string.NoGroupsInCommon));
        } else if (currentType == 7) {
            emptyStubView.emptyImageView.setImageDrawable(null);
            emptyStubView.emptyTextView.setText("");
        }
        return emptyStubView;
    }

    /* loaded from: classes5.dex */
    public static class EmptyStubView extends LinearLayout {
        final ImageView emptyImageView;
        final TextView emptyTextView;
        boolean ignoreRequestLayout;

        public EmptyStubView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            TextView textView = new TextView(context);
            this.emptyTextView = textView;
            ImageView imageView = new ImageView(context);
            this.emptyImageView = imageView;
            setOrientation(1);
            setGravity(17);
            addView(imageView, LayoutHelper.createLinear(-2, -2));
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
            textView.setGravity(17);
            textView.setTextSize(1, 17.0f);
            textView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            addView(textView, LayoutHelper.createLinear(-2, -2, 17, 0, 24, 0, 0));
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            WindowManager manager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
            int rotation = manager.getDefaultDisplay().getRotation();
            this.ignoreRequestLayout = true;
            if (AndroidUtilities.isTablet()) {
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            } else if (rotation == 3 || rotation == 1) {
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
            } else {
                this.emptyTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
            }
            this.ignoreRequestLayout = false;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreRequestLayout) {
                return;
            }
            super.requestLayout();
        }
    }

    /* loaded from: classes5.dex */
    public class SharedPhotoVideoAdapter extends RecyclerListView.FastScrollAdapter {
        private boolean inFastScrollMode;
        private Context mContext;
        SharedPhotoVideoCell2.SharedResources sharedResources;

        public SharedPhotoVideoAdapter(Context context) {
            SharedMediaLayout.this = r1;
            this.mContext = context;
        }

        public int getPositionForIndex(int i) {
            return SharedMediaLayout.this.sharedMediaData[0].startOffset + i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id)) {
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading) {
                    return 1;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && (!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1])) {
                    return 0;
                }
                int count = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
                if (count != 0) {
                    if (!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1]) {
                        return count + 1;
                    }
                    return count;
                }
                return count;
            } else if (SharedMediaLayout.this.sharedMediaData[0].loadingAfterFastScroll) {
                return SharedMediaLayout.this.sharedMediaData[0].totalCount;
            } else {
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[0].loading) {
                    return 1;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].messages.size() == 0 && ((!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1]) && SharedMediaLayout.this.sharedMediaData[0].startReached)) {
                    return 0;
                }
                if (SharedMediaLayout.this.sharedMediaData[0].totalCount == 0) {
                    int count2 = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
                    if (count2 != 0) {
                        if (!SharedMediaLayout.this.sharedMediaData[0].endReached[0] || !SharedMediaLayout.this.sharedMediaData[0].endReached[1]) {
                            if (SharedMediaLayout.this.sharedMediaData[0].getEndLoadingStubs() != 0) {
                                return count2 + SharedMediaLayout.this.sharedMediaData[0].getEndLoadingStubs();
                            }
                            return count2 + 1;
                        }
                        return count2;
                    }
                    return count2;
                }
                return SharedMediaLayout.this.sharedMediaData[0].totalCount;
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    if (this.sharedResources == null) {
                        this.sharedResources = new SharedPhotoVideoCell2.SharedResources(parent.getContext(), SharedMediaLayout.this.resourcesProvider);
                    }
                    View view = new SharedPhotoVideoCell2(this.mContext, this.sharedResources, SharedMediaLayout.this.profileActivity.getCurrentAccount());
                    SharedPhotoVideoCell2 cell = (SharedPhotoVideoCell2) view;
                    cell.setGradientView(SharedMediaLayout.this.globalGradientView);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(view);
                default:
                    View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 0, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(emptyStubView);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                boolean z = false;
                ArrayList<MessageObject> messageObjects = SharedMediaLayout.this.sharedMediaData[0].getMessages();
                int index = position - SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
                SharedPhotoVideoCell2 cell = (SharedPhotoVideoCell2) holder.itemView;
                int oldMessageId = cell.getMessageId();
                int parentCount = this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount : SharedMediaLayout.this.animateToColumnsCount;
                if (index >= 0 && index < messageObjects.size()) {
                    MessageObject messageObject = messageObjects.get(index);
                    boolean animated = messageObject.getId() == oldMessageId;
                    if (SharedMediaLayout.this.isActionModeShowed) {
                        if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0) {
                            z = true;
                        }
                        cell.setChecked(z, animated);
                    } else {
                        cell.setChecked(false, animated);
                    }
                    cell.setMessageObject(messageObject, parentCount);
                    return;
                }
                cell.setMessageObject(null, parentCount);
                cell.setChecked(false, false);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (this.inFastScrollMode || SharedMediaLayout.this.sharedMediaData[0].getMessages().size() != 0 || SharedMediaLayout.this.sharedMediaData[0].loading || !SharedMediaLayout.this.sharedMediaData[0].startReached) {
                int startOffset = SharedMediaLayout.this.sharedMediaData[0].getStartOffset() + SharedMediaLayout.this.sharedMediaData[0].getMessages().size();
                SharedMediaLayout.this.sharedMediaData[0].getStartOffset();
                return 0;
            }
            return 2;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public String getLetter(int position) {
            if (SharedMediaLayout.this.sharedMediaData[0].fastScrollPeriods == null) {
                return "";
            }
            ArrayList<Period> periods = SharedMediaLayout.this.sharedMediaData[0].fastScrollPeriods;
            if (periods.isEmpty()) {
                return "";
            }
            for (int i = 0; i < periods.size(); i++) {
                if (position <= periods.get(i).startOffset) {
                    return periods.get(i).formatedDate;
                }
            }
            int i2 = periods.size();
            return periods.get(i2 - 1).formatedDate;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            int viewHeight = listView.getChildAt(0).getMeasuredHeight();
            double ceil = Math.ceil(getTotalItemsCount() / SharedMediaLayout.this.mediaColumnsCount);
            double d = viewHeight;
            Double.isNaN(d);
            int totalHeight = (int) (ceil * d);
            int listHeight = listView.getMeasuredHeight() - listView.getPaddingTop();
            position[0] = ((int) (((totalHeight - listHeight) * progress) / viewHeight)) * SharedMediaLayout.this.mediaColumnsCount;
            position[1] = ((int) ((totalHeight - listHeight) * progress)) % viewHeight;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void onStartFastScroll() {
            this.inFastScrollMode = true;
            MediaPage mediaPage = SharedMediaLayout.this.getMediaPage(0);
            if (mediaPage != null) {
                SharedMediaLayout.showFastScrollHint(mediaPage, null, false);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void onFinishFastScroll(RecyclerListView listView) {
            if (this.inFastScrollMode) {
                this.inFastScrollMode = false;
                if (listView != null) {
                    int messageId = 0;
                    for (int i = 0; i < listView.getChildCount(); i++) {
                        View child = listView.getChildAt(i);
                        if (child instanceof SharedPhotoVideoCell2) {
                            SharedPhotoVideoCell2 cell = (SharedPhotoVideoCell2) child;
                            messageId = cell.getMessageId();
                        }
                        if (messageId != 0) {
                            break;
                        }
                    }
                    if (messageId == 0) {
                        SharedMediaLayout.this.findPeriodAndJumpToDate(0, listView, true);
                    }
                }
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public int getTotalItemsCount() {
            return SharedMediaLayout.this.sharedMediaData[0].totalCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public float getScrollProgress(RecyclerListView listView) {
            int parentCount = this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount : SharedMediaLayout.this.animateToColumnsCount;
            int cellCount = (int) Math.ceil(getTotalItemsCount() / parentCount);
            if (listView.getChildCount() == 0) {
                return 0.0f;
            }
            int cellHeight = listView.getChildAt(0).getMeasuredHeight();
            View firstChild = listView.getChildAt(0);
            int firstPosition = listView.getChildAdapterPosition(firstChild);
            if (firstPosition < 0) {
                return 0.0f;
            }
            float childTop = firstChild.getTop() - listView.getPaddingTop();
            float listH = listView.getMeasuredHeight() - listView.getPaddingTop();
            float scrollY = ((firstPosition / parentCount) * cellHeight) - childTop;
            return scrollY / ((cellCount * cellHeight) - listH);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public boolean fastScrollIsVisible(RecyclerListView listView) {
            int parentCount = this == SharedMediaLayout.this.photoVideoAdapter ? SharedMediaLayout.this.mediaColumnsCount : SharedMediaLayout.this.animateToColumnsCount;
            int cellCount = (int) Math.ceil(getTotalItemsCount() / parentCount);
            if (listView.getChildCount() == 0) {
                return false;
            }
            int cellHeight = listView.getChildAt(0).getMeasuredHeight();
            return cellCount * cellHeight > listView.getMeasuredHeight();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void onFastScrollSingleTap() {
            SharedMediaLayout.this.showMediaCalendar(true);
        }
    }

    public void findPeriodAndJumpToDate(int type, RecyclerListView listView, boolean scrollToPosition) {
        ArrayList<Period> periods = this.sharedMediaData[type].fastScrollPeriods;
        Period period = null;
        int position = ((LinearLayoutManager) listView.getLayoutManager()).findFirstVisibleItemPosition();
        if (position >= 0) {
            if (periods != null) {
                int i = 0;
                while (true) {
                    if (i >= periods.size()) {
                        break;
                    } else if (position > periods.get(i).startOffset) {
                        i++;
                    } else {
                        period = periods.get(i);
                        break;
                    }
                }
                if (period == null) {
                    period = periods.get(periods.size() - 1);
                }
            }
            if (period != null) {
                jumpToDate(type, period.maxId, period.startOffset + 1, scrollToPosition);
            }
        }
    }

    public void jumpToDate(int type, int messageId, int startOffset, boolean scrollToPosition) {
        this.sharedMediaData[type].messages.clear();
        this.sharedMediaData[type].messagesDict[0].clear();
        this.sharedMediaData[type].messagesDict[1].clear();
        this.sharedMediaData[type].setMaxId(0, messageId);
        this.sharedMediaData[type].setEndReached(0, false);
        this.sharedMediaData[type].startReached = false;
        this.sharedMediaData[type].startOffset = startOffset;
        SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
        sharedMediaDataArr[type].endLoadingStubs = (sharedMediaDataArr[type].totalCount - startOffset) - 1;
        if (this.sharedMediaData[type].endLoadingStubs < 0) {
            this.sharedMediaData[type].endLoadingStubs = 0;
        }
        this.sharedMediaData[type].min_id = messageId;
        this.sharedMediaData[type].loadingAfterFastScroll = true;
        this.sharedMediaData[type].loading = false;
        this.sharedMediaData[type].requestIndex++;
        MediaPage mediaPage = getMediaPage(type);
        if (mediaPage != null && mediaPage.listView.getAdapter() != null) {
            mediaPage.listView.getAdapter().notifyDataSetChanged();
        }
        if (scrollToPosition) {
            int i = 0;
            while (true) {
                MediaPage[] mediaPageArr = this.mediaPages;
                if (i < mediaPageArr.length) {
                    if (mediaPageArr[i].selectedType == type) {
                        this.mediaPages[i].layoutManager.scrollToPositionWithOffset(Math.min(this.sharedMediaData[type].totalCount - 1, this.sharedMediaData[type].startOffset), 0);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    /* loaded from: classes5.dex */
    public class MediaSearchAdapter extends RecyclerListView.SelectionAdapter {
        private int currentType;
        private int lastReqId;
        private Context mContext;
        private Runnable searchRunnable;
        private int searchesInProgress;
        private ArrayList<MessageObject> searchResult = new ArrayList<>();
        protected ArrayList<MessageObject> globalSearch = new ArrayList<>();
        private int reqId = 0;

        public MediaSearchAdapter(Context context, int type) {
            SharedMediaLayout.this = this$0;
            this.mContext = context;
            this.currentType = type;
        }

        public void queryServerSearch(final String query, final int max_id, long did) {
            if (DialogObject.isEncryptedDialog(did)) {
                return;
            }
            if (this.reqId != 0) {
                SharedMediaLayout.this.profileActivity.getConnectionsManager().cancelRequest(this.reqId, true);
                this.reqId = 0;
                this.searchesInProgress--;
            }
            if (query == null || query.length() == 0) {
                this.globalSearch.clear();
                this.lastReqId = 0;
                notifyDataSetChanged();
                return;
            }
            TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
            req.limit = 50;
            req.offset_id = max_id;
            int i = this.currentType;
            if (i == 1) {
                req.filter = new TLRPC.TL_inputMessagesFilterDocument();
            } else if (i == 3) {
                req.filter = new TLRPC.TL_inputMessagesFilterUrl();
            } else if (i == 4) {
                req.filter = new TLRPC.TL_inputMessagesFilterMusic();
            }
            req.q = query;
            req.peer = SharedMediaLayout.this.profileActivity.getMessagesController().getInputPeer(did);
            if (req.peer == null) {
                return;
            }
            final int currentReqId = this.lastReqId + 1;
            this.lastReqId = currentReqId;
            this.searchesInProgress++;
            this.reqId = SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SharedMediaLayout.MediaSearchAdapter.this.m3051xc89d879e(max_id, currentReqId, query, tLObject, tL_error);
                }
            }, 2);
            SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(this.reqId, SharedMediaLayout.this.profileActivity.getClassGuid());
        }

        /* renamed from: lambda$queryServerSearch$1$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter */
        public /* synthetic */ void m3051xc89d879e(int max_id, final int currentReqId, final String query, TLObject response, TLRPC.TL_error error) {
            final ArrayList<MessageObject> messageObjects = new ArrayList<>();
            if (error == null) {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                for (int a = 0; a < res.messages.size(); a++) {
                    TLRPC.Message message = res.messages.get(a);
                    if (max_id == 0 || message.id <= max_id) {
                        messageObjects.add(new MessageObject(SharedMediaLayout.this.profileActivity.getCurrentAccount(), message, false, true));
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.m3050x7ade0f9d(currentReqId, messageObjects, query);
                }
            });
        }

        /* renamed from: lambda$queryServerSearch$0$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter */
        public /* synthetic */ void m3050x7ade0f9d(int currentReqId, ArrayList messageObjects, String query) {
            if (this.reqId != 0) {
                if (currentReqId == this.lastReqId) {
                    int oldItemCounts = getItemCount();
                    this.globalSearch = messageObjects;
                    this.searchesInProgress--;
                    int count = getItemCount();
                    if (this.searchesInProgress == 0 || count != 0) {
                        SharedMediaLayout.this.switchToCurrentSelectedMode(false);
                    }
                    for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                        if (SharedMediaLayout.this.mediaPages[a].selectedType == this.currentType) {
                            if (this.searchesInProgress == 0 && count == 0) {
                                SharedMediaLayout.this.mediaPages[a].emptyView.title.setText(LocaleController.formatString(R.string.NoResultFoundFor, "NoResultFoundFor", query));
                                SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(false, true);
                            } else if (oldItemCounts == 0) {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[a].listView, 0, null);
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
                this.reqId = 0;
            }
        }

        public void search(final String query, boolean animated) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (!this.searchResult.isEmpty() || !this.globalSearch.isEmpty()) {
                this.searchResult.clear();
                this.globalSearch.clear();
                notifyDataSetChanged();
            }
            if (TextUtils.isEmpty(query)) {
                if (!this.searchResult.isEmpty() || !this.globalSearch.isEmpty() || this.searchesInProgress != 0) {
                    this.searchResult.clear();
                    this.globalSearch.clear();
                    if (this.reqId != 0) {
                        SharedMediaLayout.this.profileActivity.getConnectionsManager().cancelRequest(this.reqId, true);
                        this.reqId = 0;
                        this.searchesInProgress--;
                        return;
                    }
                    return;
                }
                return;
            }
            for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                if (SharedMediaLayout.this.mediaPages[a].selectedType == this.currentType) {
                    SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(true, animated);
                }
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.m3053x1d44c0ef(query);
                }
            };
            this.searchRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 300L);
        }

        /* renamed from: lambda$search$3$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter */
        public /* synthetic */ void m3053x1d44c0ef(final String query) {
            int i;
            if (!SharedMediaLayout.this.sharedMediaData[this.currentType].messages.isEmpty() && ((i = this.currentType) == 1 || i == 4)) {
                MessageObject messageObject = SharedMediaLayout.this.sharedMediaData[this.currentType].messages.get(SharedMediaLayout.this.sharedMediaData[this.currentType].messages.size() - 1);
                queryServerSearch(query, messageObject.getId(), messageObject.getDialogId());
            } else if (this.currentType == 3) {
                queryServerSearch(query, 0, SharedMediaLayout.this.dialog_id);
            }
            int i2 = this.currentType;
            if (i2 == 1 || i2 == 4) {
                final ArrayList<MessageObject> copy = new ArrayList<>(SharedMediaLayout.this.sharedMediaData[this.currentType].messages);
                this.searchesInProgress++;
                Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        SharedMediaLayout.MediaSearchAdapter.this.m3052xcf8548ee(query, copy);
                    }
                });
            }
        }

        /* renamed from: lambda$search$2$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter */
        public /* synthetic */ void m3052xcf8548ee(String query, ArrayList copy) {
            TLRPC.Document document;
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
                updateSearchResults(new ArrayList<>());
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search1);
            if (search1.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[(search2 != null ? 1 : 0) + 1];
            search[0] = search1;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<MessageObject> resultArray = new ArrayList<>();
            for (int a = 0; a < copy.size(); a++) {
                MessageObject messageObject = (MessageObject) copy.get(a);
                int b = 0;
                while (true) {
                    if (b < search.length) {
                        String q = search[b];
                        String name = messageObject.getDocumentName();
                        if (name != null && name.length() != 0) {
                            if (name.toLowerCase().contains(q)) {
                                resultArray.add(messageObject);
                                break;
                            } else if (this.currentType == 4) {
                                if (messageObject.type == 0) {
                                    document = messageObject.messageOwner.media.webpage.document;
                                } else {
                                    document = messageObject.messageOwner.media.document;
                                }
                                boolean ok = false;
                                int c = 0;
                                while (true) {
                                    if (c >= document.attributes.size()) {
                                        break;
                                    }
                                    TLRPC.DocumentAttribute attribute = document.attributes.get(c);
                                    if (!(attribute instanceof TLRPC.TL_documentAttributeAudio)) {
                                        c++;
                                    } else {
                                        if (attribute.performer != null) {
                                            ok = attribute.performer.toLowerCase().contains(q);
                                        }
                                        if (!ok && attribute.title != null) {
                                            ok = attribute.title.toLowerCase().contains(q);
                                        }
                                    }
                                }
                                if (ok) {
                                    resultArray.add(messageObject);
                                    break;
                                }
                            } else {
                                continue;
                            }
                        }
                        b++;
                    }
                }
            }
            updateSearchResults(resultArray);
        }

        private void updateSearchResults(final ArrayList<MessageObject> documents) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    SharedMediaLayout.MediaSearchAdapter.this.m3054x34a34193(documents);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-Components-SharedMediaLayout$MediaSearchAdapter */
        public /* synthetic */ void m3054x34a34193(ArrayList documents) {
            if (!SharedMediaLayout.this.searching) {
                return;
            }
            this.searchesInProgress--;
            int oldItemCount = getItemCount();
            this.searchResult = documents;
            int count = getItemCount();
            if (this.searchesInProgress == 0 || count != 0) {
                SharedMediaLayout.this.switchToCurrentSelectedMode(false);
            }
            for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                if (SharedMediaLayout.this.mediaPages[a].selectedType == this.currentType) {
                    if (this.searchesInProgress == 0 && count == 0) {
                        SharedMediaLayout.this.mediaPages[a].emptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
                        SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(false, true);
                    } else if (oldItemCount == 0) {
                        SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                        sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[a].listView, 0, null);
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != this.searchResult.size() + this.globalSearch.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int count = this.searchResult.size();
            int globalCount = this.globalSearch.size();
            if (globalCount != 0) {
                return count + globalCount;
            }
            return count;
        }

        public boolean isGlobalSearch(int i) {
            int localCount = this.searchResult.size();
            int globalCount = this.globalSearch.size();
            if ((i >= 0 && i < localCount) || i <= localCount || i > globalCount + localCount) {
                return false;
            }
            return true;
        }

        public MessageObject getItem(int i) {
            if (i < this.searchResult.size()) {
                return this.searchResult.get(i);
            }
            return this.globalSearch.get(i - this.searchResult.size());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            int i = this.currentType;
            if (i == 1) {
                view = new SharedDocumentCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
            } else if (i == 4) {
                view = new SharedAudioCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider) { // from class: org.telegram.ui.Components.SharedMediaLayout.MediaSearchAdapter.1
                    @Override // org.telegram.ui.Cells.SharedAudioCell
                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean result = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(result ? MediaSearchAdapter.this.searchResult : null, false);
                            if (messageObject.isRoundVideo()) {
                                MediaController.getInstance().setCurrentVideoVisible(false);
                            }
                            return result;
                        } else if (!messageObject.isMusic()) {
                            return false;
                        } else {
                            return MediaController.getInstance().setPlaylist(MediaSearchAdapter.this.searchResult, messageObject, SharedMediaLayout.this.mergeDialogId);
                        }
                    }
                };
            } else {
                view = new SharedLinkCell(this.mContext, 0, SharedMediaLayout.this.resourcesProvider);
                ((SharedLinkCell) view).setDelegate(SharedMediaLayout.this.sharedLinkCellDelegate);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int i = this.currentType;
            boolean z = false;
            if (i == 1) {
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) holder.itemView;
                MessageObject messageObject = getItem(position);
                sharedDocumentCell.setDocument(messageObject, position != getItemCount() - 1);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    sharedDocumentCell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                    return;
                }
                sharedDocumentCell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
            } else if (i == 3) {
                SharedLinkCell sharedLinkCell = (SharedLinkCell) holder.itemView;
                MessageObject messageObject2 = getItem(position);
                sharedLinkCell.setLink(messageObject2, position != getItemCount() - 1);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject2.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1].indexOfKey(messageObject2.getId()) >= 0) {
                        z = true;
                    }
                    sharedLinkCell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                    return;
                }
                sharedLinkCell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
            } else if (i == 4) {
                SharedAudioCell sharedAudioCell = (SharedAudioCell) holder.itemView;
                MessageObject messageObject3 = getItem(position);
                sharedAudioCell.setMessageObject(messageObject3, position != getItemCount() - 1);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject3.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1].indexOfKey(messageObject3.getId()) >= 0) {
                        z = true;
                    }
                    sharedAudioCell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                    return;
                }
                sharedAudioCell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }
    }

    /* loaded from: classes5.dex */
    public class GifAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public GifAdapter(Context context) {
            SharedMediaLayout.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (SharedMediaLayout.this.sharedMediaData[5].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[5].loading) {
                return false;
            }
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (SharedMediaLayout.this.sharedMediaData[5].messages.size() != 0 || SharedMediaLayout.this.sharedMediaData[5].loading) {
                return SharedMediaLayout.this.sharedMediaData[5].messages.size();
            }
            return 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (SharedMediaLayout.this.sharedMediaData[5].messages.size() == 0 && !SharedMediaLayout.this.sharedMediaData[5].loading) {
                return 1;
            }
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 5, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(emptyStubView);
            }
            ContextLinkCell cell = new ContextLinkCell(this.mContext, true, SharedMediaLayout.this.resourcesProvider);
            cell.setCanPreviewGif(true);
            return new RecyclerListView.Holder(cell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MessageObject messageObject;
            TLRPC.Document document;
            if (holder.getItemViewType() != 1 && (document = (messageObject = SharedMediaLayout.this.sharedMediaData[5].messages.get(position)).getDocument()) != null) {
                ContextLinkCell cell = (ContextLinkCell) holder.itemView;
                boolean z = false;
                cell.setGif(document, messageObject, messageObject.messageOwner.date, false);
                if (SharedMediaLayout.this.isActionModeShowed) {
                    if (SharedMediaLayout.this.selectedFiles[messageObject.getDialogId() == SharedMediaLayout.this.dialog_id ? (char) 0 : (char) 1].indexOfKey(messageObject.getId()) >= 0) {
                        z = true;
                    }
                    cell.setChecked(z, true ^ SharedMediaLayout.this.scrolling);
                    return;
                }
                cell.setChecked(false, true ^ SharedMediaLayout.this.scrolling);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ContextLinkCell) {
                ContextLinkCell cell = (ContextLinkCell) holder.itemView;
                ImageReceiver imageReceiver = cell.getPhotoImage();
                if (SharedMediaLayout.this.mediaPages[0].selectedType == 5) {
                    imageReceiver.setAllowStartAnimation(true);
                    imageReceiver.startAnimation();
                    return;
                }
                imageReceiver.setAllowStartAnimation(false);
                imageReceiver.stopAnimation();
            }
        }
    }

    /* loaded from: classes5.dex */
    public class CommonGroupsAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        private boolean endReached;
        private boolean firstLoaded;
        private boolean loading;
        private Context mContext;

        public CommonGroupsAdapter(Context context) {
            SharedMediaLayout.this = r1;
            this.mContext = context;
        }

        public void getChats(long max_id, final int count) {
            long uid;
            if (this.loading) {
                return;
            }
            TLRPC.TL_messages_getCommonChats req = new TLRPC.TL_messages_getCommonChats();
            if (DialogObject.isEncryptedDialog(SharedMediaLayout.this.dialog_id)) {
                TLRPC.EncryptedChat encryptedChat = SharedMediaLayout.this.profileActivity.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(SharedMediaLayout.this.dialog_id)));
                uid = encryptedChat.user_id;
            } else {
                uid = SharedMediaLayout.this.dialog_id;
            }
            req.user_id = SharedMediaLayout.this.profileActivity.getMessagesController().getInputUser(uid);
            if (req.user_id instanceof TLRPC.TL_inputUserEmpty) {
                return;
            }
            req.limit = count;
            req.max_id = max_id;
            this.loading = true;
            notifyDataSetChanged();
            int reqId = SharedMediaLayout.this.profileActivity.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda1
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SharedMediaLayout.CommonGroupsAdapter.this.m3043xfa1b5b69(count, tLObject, tL_error);
                }
            });
            SharedMediaLayout.this.profileActivity.getConnectionsManager().bindRequestToGuid(reqId, SharedMediaLayout.this.profileActivity.getClassGuid());
        }

        /* renamed from: lambda$getChats$1$org-telegram-ui-Components-SharedMediaLayout$CommonGroupsAdapter */
        public /* synthetic */ void m3043xfa1b5b69(final int count, final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SharedMediaLayout.CommonGroupsAdapter.this.m3042x8febd34a(error, response, count);
                }
            });
        }

        /* renamed from: lambda$getChats$0$org-telegram-ui-Components-SharedMediaLayout$CommonGroupsAdapter */
        public /* synthetic */ void m3042x8febd34a(TLRPC.TL_error error, TLObject response, int count) {
            int oldCount = getItemCount();
            if (error == null) {
                TLRPC.messages_Chats res = (TLRPC.messages_Chats) response;
                SharedMediaLayout.this.profileActivity.getMessagesController().putChats(res.chats, false);
                this.endReached = res.chats.isEmpty() || res.chats.size() != count;
                this.chats.addAll(res.chats);
            } else {
                this.endReached = true;
            }
            for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                if (SharedMediaLayout.this.mediaPages[a].selectedType == 6 && SharedMediaLayout.this.mediaPages[a].listView != null) {
                    RecyclerListView listView = SharedMediaLayout.this.mediaPages[a].listView;
                    if (this.firstLoaded || oldCount == 0) {
                        SharedMediaLayout.this.animateItemsEnter(listView, 0, null);
                    }
                }
            }
            this.loading = false;
            this.firstLoaded = true;
            notifyDataSetChanged();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getAdapterPosition() != this.chats.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (this.chats.isEmpty() && !this.loading) {
                return 1;
            }
            int count = this.chats.size();
            if (!this.chats.isEmpty() && !this.endReached) {
                return count + 1;
            }
            return count;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ProfileSearchCell(this.mContext, SharedMediaLayout.this.resourcesProvider);
                    break;
                case 1:
                default:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext, SharedMediaLayout.this.resourcesProvider);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.showDate(false);
                    flickerLoadingView.setViewType(1);
                    view = flickerLoadingView;
                    break;
                case 2:
                    View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 6, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                    emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    return new RecyclerListView.Holder(emptyStubView);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                ProfileSearchCell cell = (ProfileSearchCell) holder.itemView;
                TLRPC.Chat chat = this.chats.get(position);
                cell.setData(chat, null, null, null, false, false);
                boolean z = true;
                if (position == this.chats.size() - 1 && this.endReached) {
                    z = false;
                }
                cell.useSeparator = z;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (this.chats.isEmpty() && !this.loading) {
                return 2;
            }
            if (i < this.chats.size()) {
                return 0;
            }
            return 1;
        }
    }

    /* loaded from: classes5.dex */
    public class ChatUsersAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC.ChatFull chatInfo;
        private Context mContext;
        private ArrayList<Integer> sortedUsers;

        public ChatUsersAdapter(Context context) {
            SharedMediaLayout.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            TLRPC.ChatFull chatFull = this.chatInfo;
            if (chatFull != null && chatFull.participants.participants.isEmpty()) {
                return 1;
            }
            TLRPC.ChatFull chatFull2 = this.chatInfo;
            if (chatFull2 == null) {
                return 0;
            }
            return chatFull2.participants.participants.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View emptyStubView = SharedMediaLayout.createEmptyStubView(this.mContext, 7, SharedMediaLayout.this.dialog_id, SharedMediaLayout.this.resourcesProvider);
                emptyStubView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                return new RecyclerListView.Holder(emptyStubView);
            }
            View view = new UserCell(this.mContext, 9, 0, true, false, SharedMediaLayout.this.resourcesProvider);
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.ChatParticipant part;
            String role;
            String role2;
            UserCell userCell = (UserCell) holder.itemView;
            if (!this.sortedUsers.isEmpty()) {
                part = this.chatInfo.participants.participants.get(this.sortedUsers.get(position).intValue());
            } else {
                part = this.chatInfo.participants.participants.get(position);
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
                TLRPC.User user = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(part.user_id));
                boolean z = true;
                if (position == this.chatInfo.participants.participants.size() - 1) {
                    z = false;
                }
                userCell.setData(user, null, null, 0, z);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            TLRPC.ChatFull chatFull = this.chatInfo;
            if (chatFull != null && chatFull.participants.participants.isEmpty()) {
                return 1;
            }
            return 0;
        }
    }

    /* loaded from: classes5.dex */
    public class GroupUsersSearchAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC.Chat currentChat;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private Runnable searchRunnable;
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private int totalCount = 0;
        int searchCount = 0;

        public GroupUsersSearchAdapter(Context context) {
            SharedMediaLayout.this = r3;
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda4
                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public final void onDataSetChanged(int i) {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.m3044x8d979017(i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
            this.currentChat = r3.delegate.getCurrentChat();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter */
        public /* synthetic */ void m3044x8d979017(int searchId) {
            notifyDataSetChanged();
            if (searchId == 1) {
                int i = this.searchCount - 1;
                this.searchCount = i;
                if (i == 0) {
                    for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                        if (SharedMediaLayout.this.mediaPages[a].selectedType == 7) {
                            if (getItemCount() == 0) {
                                SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(false, true);
                            } else {
                                SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                                sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[a].listView, 0, null);
                            }
                        }
                    }
                }
            }
        }

        private boolean createMenuForParticipant(TLObject participant, boolean resultOnly) {
            if (participant instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) participant;
                TLRPC.TL_chatChannelParticipant p = new TLRPC.TL_chatChannelParticipant();
                p.channelParticipant = channelParticipant;
                p.user_id = MessageObject.getPeerId(channelParticipant.peer);
                p.inviter_id = channelParticipant.inviter_id;
                p.date = channelParticipant.date;
                participant = p;
            }
            return SharedMediaLayout.this.delegate.onMemberClick((TLRPC.ChatParticipant) participant, true, resultOnly);
        }

        public void search(final String query, boolean animated) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults(null);
            this.searchAdapterHelper.queryServerSearch(null, true, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0L, false, 2, 0);
            notifyDataSetChanged();
            for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                if (SharedMediaLayout.this.mediaPages[a].selectedType == 7 && !TextUtils.isEmpty(query)) {
                    SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(true, animated);
                }
            }
            if (!TextUtils.isEmpty(query)) {
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SharedMediaLayout.GroupUsersSearchAdapter.this.m3048x8ccdb494(query);
                    }
                };
                this.searchRunnable = runnable;
                dispatchQueue.postRunnable(runnable, 300L);
            }
        }

        /* renamed from: processSearch */
        public void m3048x8ccdb494(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.m3047x8f8bfa7d(query);
                }
            });
        }

        /* renamed from: lambda$processSearch$3$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter */
        public /* synthetic */ void m3047x8f8bfa7d(final String query) {
            final ArrayList<TLObject> participantsCopy = null;
            this.searchRunnable = null;
            if (!ChatObject.isChannel(this.currentChat) && SharedMediaLayout.this.info != null) {
                participantsCopy = new ArrayList<>(SharedMediaLayout.this.info.participants.participants);
            }
            this.searchCount = 2;
            if (participantsCopy == null) {
                this.searchCount = 2 - 1;
            } else {
                Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        SharedMediaLayout.GroupUsersSearchAdapter.this.m3046x56ab99de(query, participantsCopy);
                    }
                });
            }
            this.searchAdapterHelper.queryServerSearch(query, false, false, true, false, false, ChatObject.isChannel(this.currentChat) ? this.currentChat.id : 0L, false, 2, 1);
        }

        /* JADX WARN: Code restructure failed: missing block: B:42:0x0103, code lost:
            if (r4.contains(" " + r1) != false) goto L49;
         */
        /* JADX WARN: Removed duplicated region for block: B:56:0x015a A[LOOP:1: B:33:0x00c1->B:56:0x015a, LOOP_END] */
        /* JADX WARN: Removed duplicated region for block: B:68:0x011d A[SYNTHETIC] */
        /* renamed from: lambda$processSearch$2$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void m3046x56ab99de(java.lang.String r22, java.util.ArrayList r23) {
            /*
                Method dump skipped, instructions count: 387
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SharedMediaLayout.GroupUsersSearchAdapter.m3046x56ab99de(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(final ArrayList<CharSequence> names, final ArrayList<TLObject> participants) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    SharedMediaLayout.GroupUsersSearchAdapter.this.m3049x561abbae(names, participants);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter */
        public /* synthetic */ void m3049x561abbae(ArrayList names, ArrayList participants) {
            if (!SharedMediaLayout.this.searching) {
                return;
            }
            this.searchResultNames = names;
            this.searchCount--;
            if (!ChatObject.isChannel(this.currentChat)) {
                ArrayList<TLObject> search = this.searchAdapterHelper.getGroupSearch();
                search.clear();
                search.addAll(participants);
            }
            if (this.searchCount == 0) {
                for (int a = 0; a < SharedMediaLayout.this.mediaPages.length; a++) {
                    if (SharedMediaLayout.this.mediaPages[a].selectedType == 7) {
                        if (getItemCount() == 0) {
                            SharedMediaLayout.this.mediaPages[a].emptyView.showProgress(false, true);
                        } else {
                            SharedMediaLayout sharedMediaLayout = SharedMediaLayout.this;
                            sharedMediaLayout.animateItemsEnter(sharedMediaLayout.mediaPages[a].listView, 0, null);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.totalCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            int size = this.searchAdapterHelper.getGroupSearch().size();
            this.totalCount = size;
            if (size > 0 && SharedMediaLayout.this.searching && SharedMediaLayout.this.mediaPages[0].selectedType == 7 && SharedMediaLayout.this.mediaPages[0].listView.getAdapter() != this) {
                SharedMediaLayout.this.switchToCurrentSelectedMode(false);
            }
            super.notifyDataSetChanged();
        }

        public void removeUserId(long userId) {
            this.searchAdapterHelper.removeUserId(userId);
            notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (i < 0 || i >= count) {
                return null;
            }
            return this.searchAdapterHelper.getGroupSearch().get(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ManageChatUserCell view = new ManageChatUserCell(this.mContext, 9, 5, true, SharedMediaLayout.this.resourcesProvider);
            view.setBackgroundColor(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhite));
            view.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() { // from class: org.telegram.ui.Components.SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda5
                @Override // org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate
                public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                    return SharedMediaLayout.GroupUsersSearchAdapter.this.m3045xfc74e4ec(manageChatUserCell, z);
                }
            });
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-Components-SharedMediaLayout$GroupUsersSearchAdapter */
        public /* synthetic */ boolean m3045xfc74e4ec(ManageChatUserCell cell, boolean click) {
            TLObject object = getItem(((Integer) cell.getTag()).intValue());
            if (object instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant participant = (TLRPC.ChannelParticipant) object;
                return createMenuForParticipant(participant, !click);
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.User user;
            TLObject object = getItem(position);
            if (object instanceof TLRPC.ChannelParticipant) {
                user = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(((TLRPC.ChannelParticipant) object).peer)));
            } else if (object instanceof TLRPC.ChatParticipant) {
                user = SharedMediaLayout.this.profileActivity.getMessagesController().getUser(Long.valueOf(((TLRPC.ChatParticipant) object).user_id));
            } else {
                return;
            }
            String str = user.username;
            SpannableStringBuilder name = null;
            this.searchAdapterHelper.getGroupSearch().size();
            String nameSearch = this.searchAdapterHelper.getLastFoundChannel();
            if (nameSearch != null) {
                String u = UserObject.getUserName(user);
                name = new SpannableStringBuilder(u);
                int idx = AndroidUtilities.indexOfIgnoreCase(u, nameSearch);
                if (idx != -1) {
                    name.setSpan(new ForegroundColorSpan(SharedMediaLayout.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueText4)), idx, nameSearch.length() + idx, 33);
                }
            }
            ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
            userCell.setTag(Integer.valueOf(position));
            userCell.setData(user, name, null, false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.shadowLine, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.deleteItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.deleteItem, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector));
        if (this.gotoItem != null) {
            arrayList.add(new ThemeDescription(this.gotoItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription(this.gotoItem, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector));
        }
        if (this.forwardItem != null) {
            arrayList.add(new ThemeDescription(this.forwardItem.getIconView(), ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription(this.forwardItem, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector));
        }
        arrayList.add(new ThemeDescription(this.closeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, new Drawable[]{this.backDrawable}, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(this.closeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionModeLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, null, null, null, null, Theme.key_chat_mediaTimeBackground));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, null, null, null, null, Theme.key_chat_mediaTimeText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip, 0, new Class[]{ScrollSlidingTextTabStrip.class}, new String[]{"selectorDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_tabSelectedLine));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_profile_tabSelectedText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_profile_tabText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, Theme.key_profile_tabSelector));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerBackground));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPlayPause));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerTitle));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPerformer));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerClose));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallBackground));
        arrayList.add(new ThemeDescription(this.fragmentContextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallText));
        for (int a = 0; a < this.mediaPages.length; a++) {
            final int num = a;
            ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.SharedMediaLayout$$ExternalSyntheticLambda4
                @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
                public final void didSetColor() {
                    SharedMediaLayout.this.m3026xf9b297a9(num);
                }

                @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
                public /* synthetic */ void onAnimationProgress(float f) {
                    ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
                }
            };
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
            arrayList.add(new ThemeDescription(this.mediaPages[a].progressView, 0, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_creatorIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_name));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretName));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{ProfileSearchCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyStubView.class}, new String[]{"emptyTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{SharedDocumentCell.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_startStopLoadIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"statusImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_startStopLoadIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_folderIcon));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_files_iconText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedAudioCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_titleTextPaint, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedAudioCell.class}, Theme.chat_contextResult_descriptionTextPaint, null, null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedLinkCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"titleTextPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholderText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedLinkCell.class}, new String[]{"letterDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_linkPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_SECTIONS, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedMediaSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{SharedPhotoVideoCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_photoPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedPhotoVideoCell.class}, null, null, cellDelegate, Theme.key_checkbox));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedPhotoVideoCell.class}, null, null, cellDelegate, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, new Class[]{ContextLinkCell.class}, new String[]{"backgroundPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_sharedMedia_photoPlaceholder));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{ContextLinkCell.class}, null, null, cellDelegate, Theme.key_checkbox));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{ContextLinkCell.class}, null, null, cellDelegate, Theme.key_checkboxCheck));
            arrayList.add(new ThemeDescription(this.mediaPages[a].listView, 0, null, null, new Drawable[]{this.pinnedHeaderShadowDrawable}, null, Theme.key_windowBackgroundGrayShadow));
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.mediaPages[a].emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        }
        return arrayList;
    }

    /* renamed from: lambda$getThemeDescriptions$17$org-telegram-ui-Components-SharedMediaLayout */
    public /* synthetic */ void m3026xf9b297a9(int num) {
        if (this.mediaPages[num].listView != null) {
            int count = this.mediaPages[num].listView.getChildCount();
            for (int a1 = 0; a1 < count; a1++) {
                View child = this.mediaPages[num].listView.getChildAt(a1);
                if (child instanceof SharedPhotoVideoCell) {
                    ((SharedPhotoVideoCell) child).updateCheckboxColor();
                } else if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                } else if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }

    public int getNextMediaColumnsCount(int mediaColumnsCount, boolean up) {
        if (!up) {
            if (mediaColumnsCount == 2) {
                return 3;
            }
            if (mediaColumnsCount == 3) {
                return 4;
            }
            if (mediaColumnsCount == 4) {
                return 5;
            }
            if (mediaColumnsCount == 5) {
                return 6;
            }
            if (mediaColumnsCount != 6) {
                return mediaColumnsCount;
            }
            return 9;
        } else if (mediaColumnsCount == 9) {
            return 6;
        } else {
            if (mediaColumnsCount == 6) {
                return 5;
            }
            if (mediaColumnsCount == 5) {
                return 4;
            }
            if (mediaColumnsCount == 4) {
                return 3;
            }
            if (mediaColumnsCount != 3) {
                return mediaColumnsCount;
            }
            return 2;
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child == this.fragmentContextView) {
            canvas.save();
            canvas.clipRect(0, this.mediaPages[0].getTop(), child.getMeasuredWidth(), this.mediaPages[0].getTop() + child.getMeasuredHeight() + AndroidUtilities.dp(12.0f));
            boolean b = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return b;
        }
        boolean b2 = super.drawChild(canvas, child, drawingTime);
        return b2;
    }

    /* loaded from: classes5.dex */
    public class ScrollSlidingTextTabStripInner extends ScrollSlidingTextTabStrip {
        public int backgroundColor = 0;
        protected Paint backgroundPaint;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ScrollSlidingTextTabStripInner(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            SharedMediaLayout.this = r1;
        }

        protected void drawBackground(Canvas canvas) {
            if (SharedConfig.chatBlurEnabled() && this.backgroundColor != 0) {
                if (this.backgroundPaint == null) {
                    this.backgroundPaint = new Paint();
                }
                this.backgroundPaint.setColor(this.backgroundColor);
                AndroidUtilities.rectTmp2.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                SharedMediaLayout.this.drawBackgroundWithBlur(canvas, getY(), AndroidUtilities.rectTmp2, this.backgroundPaint);
            }
        }

        @Override // android.view.View
        public void setBackgroundColor(int color) {
            this.backgroundColor = color;
            invalidate();
        }
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
