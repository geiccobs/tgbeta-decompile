package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PermanentLinkBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.GroupCreateActivity;
/* loaded from: classes4.dex */
public class GroupCreateActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private boolean addToGroup;
    private ArrayList<GroupCreateSpan> allSpans;
    private long channelId;
    private int chatAddType;
    private long chatId;
    private int chatType;
    private int containerHeight;
    private AnimatorSet currentAnimation;
    private GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private GroupCreateActivityDelegate delegate;
    private ContactsAddActivityDelegate delegate2;
    private boolean doneButtonVisible;
    private EditTextBoldCursor editText;
    private StickerEmptyView emptyView;
    private int fieldY;
    private ImageView floatingButton;
    private boolean forImport;
    private boolean ignoreScrollEvent;
    private LongSparseArray<TLObject> ignoreUsers;
    private TLRPC.ChatFull info;
    private boolean isAlwaysShare;
    private boolean isNeverShare;
    private GroupCreateDividerItemDecoration itemDecoration;
    private RecyclerListView listView;
    private int maxCount;
    int maxSize;
    private int measuredContainerHeight;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private LongSparseArray<GroupCreateSpan> selectedContacts;
    private PermanentLinkBottomSheet sharedLinkBottomSheet;
    private SpansContainer spansContainer;

    /* loaded from: classes4.dex */
    public interface GroupCreateActivityDelegate {
        void didSelectUsers(ArrayList<Long> arrayList);
    }

    /* loaded from: classes4.dex */
    public interface GroupCreateActivityImportDelegate {
        void didCreateChat(int i);
    }

    /* loaded from: classes4.dex */
    public interface ContactsAddActivityDelegate {
        void didSelectUsers(ArrayList<TLRPC.User> arrayList, int i);

        void needAddBot(TLRPC.User user);

        /* renamed from: org.telegram.ui.GroupCreateActivity$ContactsAddActivityDelegate$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static void $default$needAddBot(ContactsAddActivityDelegate _this, TLRPC.User user) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private View removingSpan;
        private ArrayList<Animator> animators = new ArrayList<>();
        private int animationIndex = -1;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SpansContainer(Context context) {
            super(context);
            GroupCreateActivity.this = r1;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int minWidth;
            boolean z;
            int count = getChildCount();
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int maxWidth = width - AndroidUtilities.dp(26.0f);
            int currentLineWidth = 0;
            int y = AndroidUtilities.dp(10.0f);
            int allCurrentLineWidth = 0;
            int allY = AndroidUtilities.dp(10.0f);
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof GroupCreateSpan) {
                    child.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C.BUFFER_FLAG_ENCRYPTED));
                    if (child != this.removingSpan && child.getMeasuredWidth() + currentLineWidth > maxWidth) {
                        y += child.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        currentLineWidth = 0;
                    }
                    if (child.getMeasuredWidth() + allCurrentLineWidth > maxWidth) {
                        allY += child.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        allCurrentLineWidth = 0;
                    }
                    int x = AndroidUtilities.dp(13.0f) + currentLineWidth;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (child == view) {
                            child.setTranslationX(AndroidUtilities.dp(13.0f) + allCurrentLineWidth);
                            child.setTranslationY(allY);
                        } else if (view != null) {
                            if (child.getTranslationX() != x) {
                                this.animators.add(ObjectAnimator.ofFloat(child, "translationX", x));
                            }
                            if (child.getTranslationY() != y) {
                                this.animators.add(ObjectAnimator.ofFloat(child, "translationY", y));
                            }
                        } else {
                            child.setTranslationX(x);
                            child.setTranslationY(y);
                        }
                    }
                    if (child != this.removingSpan) {
                        currentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    allCurrentLineWidth += child.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
            }
            if (AndroidUtilities.isTablet()) {
                minWidth = AndroidUtilities.dp(372.0f) / 3;
            } else {
                minWidth = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(158.0f)) / 3;
            }
            if (maxWidth - currentLineWidth < minWidth) {
                currentLineWidth = 0;
                y += AndroidUtilities.dp(40.0f);
            }
            if (maxWidth - allCurrentLineWidth < minWidth) {
                allY += AndroidUtilities.dp(40.0f);
            }
            GroupCreateActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(maxWidth - currentLineWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C.BUFFER_FLAG_ENCRYPTED));
            if (this.animationStarted) {
                if (GroupCreateActivity.this.currentAnimation != null && !GroupCreateActivity.this.ignoreScrollEvent && this.removingSpan == null) {
                    GroupCreateActivity.this.editText.bringPointIntoView(GroupCreateActivity.this.editText.getSelectionStart());
                }
            } else {
                int currentHeight = AndroidUtilities.dp(42.0f) + allY;
                int fieldX = AndroidUtilities.dp(16.0f) + currentLineWidth;
                GroupCreateActivity.this.fieldY = y;
                if (GroupCreateActivity.this.currentAnimation == null) {
                    GroupCreateActivity groupCreateActivity = GroupCreateActivity.this;
                    groupCreateActivity.measuredContainerHeight = groupCreateActivity.containerHeight = currentHeight;
                    GroupCreateActivity.this.editText.setTranslationX(fieldX);
                    GroupCreateActivity.this.editText.setTranslationY(GroupCreateActivity.this.fieldY);
                } else {
                    int resultHeight = AndroidUtilities.dp(42.0f) + y;
                    if (GroupCreateActivity.this.containerHeight != resultHeight) {
                        this.animators.add(ObjectAnimator.ofInt(GroupCreateActivity.this, "containerHeight", resultHeight));
                    }
                    GroupCreateActivity groupCreateActivity2 = GroupCreateActivity.this;
                    groupCreateActivity2.measuredContainerHeight = Math.max(groupCreateActivity2.containerHeight, resultHeight);
                    if (GroupCreateActivity.this.editText.getTranslationX() != fieldX) {
                        this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationX", fieldX));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationY() == GroupCreateActivity.this.fieldY) {
                        z = false;
                    } else {
                        z = false;
                        this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationY", GroupCreateActivity.this.fieldY));
                    }
                    GroupCreateActivity.this.editText.setAllowDrawCursor(z);
                    GroupCreateActivity.this.currentAnimation.playTogether(this.animators);
                    GroupCreateActivity.this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCreateActivity.SpansContainer.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            GroupCreateActivity.this.getNotificationCenter().onAnimationFinish(SpansContainer.this.animationIndex);
                            SpansContainer.this.requestLayout();
                        }
                    });
                    this.animationIndex = GroupCreateActivity.this.getNotificationCenter().setAnimationInProgress(this.animationIndex, null);
                    GroupCreateActivity.this.currentAnimation.start();
                    this.animationStarted = true;
                }
            }
            setMeasuredDimension(width, GroupCreateActivity.this.measuredContainerHeight);
            GroupCreateActivity.this.listView.setTranslationY(0.0f);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan span) {
            GroupCreateActivity.this.allSpans.add(span);
            GroupCreateActivity.this.selectedContacts.put(span.getUid(), span);
            GroupCreateActivity.this.editText.setHintVisible(false);
            if (GroupCreateActivity.this.currentAnimation != null) {
                GroupCreateActivity.this.currentAnimation.setupEndValues();
                GroupCreateActivity.this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            GroupCreateActivity.this.currentAnimation = new AnimatorSet();
            GroupCreateActivity.this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCreateActivity.SpansContainer.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.addingSpan = null;
                    GroupCreateActivity.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                }
            });
            GroupCreateActivity.this.currentAnimation.setDuration(150L);
            this.addingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_X, 0.01f, 1.0f));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_Y, 0.01f, 1.0f));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.ALPHA, 0.0f, 1.0f));
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            GroupCreateActivity.this.ignoreScrollEvent = true;
            GroupCreateActivity.this.selectedContacts.remove(span.getUid());
            GroupCreateActivity.this.allSpans.remove(span);
            span.setOnClickListener(null);
            if (GroupCreateActivity.this.currentAnimation != null) {
                GroupCreateActivity.this.currentAnimation.setupEndValues();
                GroupCreateActivity.this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            GroupCreateActivity.this.currentAnimation = new AnimatorSet();
            GroupCreateActivity.this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCreateActivity.SpansContainer.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(span);
                    SpansContainer.this.removingSpan = null;
                    GroupCreateActivity.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                    if (GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            GroupCreateActivity.this.currentAnimation.setDuration(150L);
            this.removingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_X, 1.0f, 0.01f));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_Y, 1.0f, 0.01f));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.ALPHA, 1.0f, 0.0f));
            requestLayout();
        }
    }

    public GroupCreateActivity() {
        this.maxCount = getMessagesController().maxMegagroupCount;
        this.chatType = 0;
        this.selectedContacts = new LongSparseArray<>();
        this.allSpans = new ArrayList<>();
    }

    public GroupCreateActivity(Bundle args) {
        super(args);
        this.maxCount = getMessagesController().maxMegagroupCount;
        this.chatType = 0;
        this.selectedContacts = new LongSparseArray<>();
        this.allSpans = new ArrayList<>();
        this.chatType = args.getInt("chatType", 0);
        this.forImport = args.getBoolean("forImport", false);
        this.isAlwaysShare = args.getBoolean("isAlwaysShare", false);
        this.isNeverShare = args.getBoolean("isNeverShare", false);
        this.addToGroup = args.getBoolean("addToGroup", false);
        this.chatAddType = args.getInt("chatAddType", 0);
        this.chatId = args.getLong("chatId");
        this.channelId = args.getLong("channelId");
        if (this.isAlwaysShare || this.isNeverShare || this.addToGroup) {
            this.maxCount = 0;
        } else {
            this.maxCount = this.chatType == 0 ? getMessagesController().maxMegagroupCount : getMessagesController().maxBroadcastCount;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().removeObserver(this, NotificationCenter.chatDidCreated);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        GroupCreateSpan span = (GroupCreateSpan) v;
        if (span.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(span);
            updateHint();
            checkVisibleRows();
            return;
        }
        GroupCreateSpan groupCreateSpan = this.currentDeletingSpan;
        if (groupCreateSpan != null) {
            groupCreateSpan.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = span;
        span.startDeleteAnimation();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        String str;
        int i;
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.doneButtonVisible = this.chatType == 2;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.chatType == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAddSubscribers", R.string.ChannelAddSubscribers));
        } else if (this.addToGroup) {
            if (this.channelId != 0) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddSubscribers", R.string.ChannelAddSubscribers));
            } else {
                this.actionBar.setTitle(LocaleController.getString("GroupAddMembers", R.string.GroupAddMembers));
            }
        } else if (this.isAlwaysShare) {
            int i2 = this.chatAddType;
            if (i2 == 2) {
                this.actionBar.setTitle(LocaleController.getString("FilterAlwaysShow", R.string.FilterAlwaysShow));
            } else if (i2 == 1) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", R.string.AlwaysShareWithTitle));
            }
        } else if (this.isNeverShare) {
            int i3 = this.chatAddType;
            if (i3 == 2) {
                this.actionBar.setTitle(LocaleController.getString("FilterNeverShow", R.string.FilterNeverShow));
            } else if (i3 == 1) {
                this.actionBar.setTitle(LocaleController.getString("NeverAllow", R.string.NeverAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", R.string.NeverShareWithTitle));
            }
        } else {
            ActionBar actionBar = this.actionBar;
            if (this.chatType == 0) {
                i = R.string.NewGroup;
                str = "NewGroup";
            } else {
                i = R.string.NewBroadcastList;
                str = "NewBroadcastList";
            }
            actionBar.setTitle(LocaleController.getString(str, i));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.GroupCreateActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupCreateActivity.this.finishFragment();
                } else if (id == 1) {
                    GroupCreateActivity.this.onDonePressed(true);
                }
            }
        });
        this.fragmentView = new ViewGroup(context) { // from class: org.telegram.ui.GroupCreateActivity.2
            private VerticalPositionAutoAnimator verticalPositionAutoAnimator;

            @Override // android.view.ViewGroup
            public void onViewAdded(View child) {
                if (child == GroupCreateActivity.this.floatingButton && this.verticalPositionAutoAnimator == null) {
                    this.verticalPositionAutoAnimator = VerticalPositionAutoAnimator.attach(child);
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                VerticalPositionAutoAnimator verticalPositionAutoAnimator = this.verticalPositionAutoAnimator;
                if (verticalPositionAutoAnimator != null) {
                    verticalPositionAutoAnimator.ignoreNextLayout();
                }
            }

            @Override // android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                float f = 56.0f;
                if (AndroidUtilities.isTablet() || height > width) {
                    GroupCreateActivity.this.maxSize = AndroidUtilities.dp(144.0f);
                } else {
                    GroupCreateActivity.this.maxSize = AndroidUtilities.dp(56.0f);
                }
                GroupCreateActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(GroupCreateActivity.this.maxSize, Integer.MIN_VALUE));
                GroupCreateActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), C.BUFFER_FLAG_ENCRYPTED));
                GroupCreateActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height - GroupCreateActivity.this.scrollView.getMeasuredHeight(), C.BUFFER_FLAG_ENCRYPTED));
                if (GroupCreateActivity.this.floatingButton != null) {
                    if (Build.VERSION.SDK_INT < 21) {
                        f = 60.0f;
                    }
                    int w = AndroidUtilities.dp(f);
                    GroupCreateActivity.this.floatingButton.measure(View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED));
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                GroupCreateActivity.this.scrollView.layout(0, 0, GroupCreateActivity.this.scrollView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight());
                GroupCreateActivity.this.listView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.listView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.listView.getMeasuredHeight());
                GroupCreateActivity.this.emptyView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.emptyView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.emptyView.getMeasuredHeight());
                if (GroupCreateActivity.this.floatingButton != null) {
                    int l = LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : ((right - left) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredWidth();
                    int t = ((bottom - top) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredHeight();
                    GroupCreateActivity.this.floatingButton.layout(l, t, GroupCreateActivity.this.floatingButton.getMeasuredWidth() + l, GroupCreateActivity.this.floatingButton.getMeasuredHeight() + t);
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                GroupCreateActivity.this.parentLayout.drawHeaderShadow(canvas, Math.min(GroupCreateActivity.this.maxSize, (GroupCreateActivity.this.measuredContainerHeight + GroupCreateActivity.this.containerHeight) - GroupCreateActivity.this.measuredContainerHeight));
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child != GroupCreateActivity.this.listView) {
                    if (child == GroupCreateActivity.this.scrollView) {
                        canvas.save();
                        canvas.clipRect(child.getLeft(), child.getTop(), child.getRight(), Math.min(GroupCreateActivity.this.maxSize, (GroupCreateActivity.this.measuredContainerHeight + GroupCreateActivity.this.containerHeight) - GroupCreateActivity.this.measuredContainerHeight));
                        boolean result = super.drawChild(canvas, child, drawingTime);
                        canvas.restore();
                        return result;
                    }
                    boolean result2 = super.drawChild(canvas, child, drawingTime);
                    return result2;
                }
                canvas.save();
                canvas.clipRect(child.getLeft(), Math.min(GroupCreateActivity.this.maxSize, (GroupCreateActivity.this.measuredContainerHeight + GroupCreateActivity.this.containerHeight) - GroupCreateActivity.this.measuredContainerHeight), child.getRight(), child.getBottom());
                boolean result3 = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return result3;
            }
        };
        ViewGroup frameLayout = (ViewGroup) this.fragmentView;
        frameLayout.setFocusableInTouchMode(true);
        frameLayout.setDescendantFocusability(131072);
        ScrollView scrollView = new ScrollView(context) { // from class: org.telegram.ui.GroupCreateActivity.3
            @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.ViewParent
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (GroupCreateActivity.this.ignoreScrollEvent) {
                    GroupCreateActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rectangle.bottom += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.scrollView = scrollView;
        scrollView.setClipChildren(false);
        frameLayout.setClipChildren(false);
        this.scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_windowBackgroundWhite));
        frameLayout.addView(this.scrollView);
        SpansContainer spansContainer = new SpansContainer(context);
        this.spansContainer = spansContainer;
        this.scrollView.addView(spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        this.spansContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCreateActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCreateActivity.this.m3532lambda$createView$0$orgtelegramuiGroupCreateActivity(view);
            }
        });
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) { // from class: org.telegram.ui.GroupCreateActivity.4
            @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (GroupCreateActivity.this.currentDeletingSpan != null) {
                    GroupCreateActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateActivity.this.currentDeletingSpan = null;
                }
                if (event.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(event);
            }
        };
        this.editText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 16.0f);
        this.editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setCursorColor(Theme.getColor(Theme.key_groupcreate_cursor));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(268435462);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        updateEditTextHint();
        this.editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() { // from class: org.telegram.ui.GroupCreateActivity.5
            @Override // android.view.ActionMode.Callback
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override // android.view.ActionMode.Callback
            public void onDestroyActionMode(ActionMode mode) {
            }

            @Override // android.view.ActionMode.Callback
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override // android.view.ActionMode.Callback
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.GroupCreateActivity$$ExternalSyntheticLambda5
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i4, KeyEvent keyEvent) {
                return GroupCreateActivity.this.m3533lambda$createView$1$orgtelegramuiGroupCreateActivity(textView, i4, keyEvent);
            }
        });
        this.editText.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.GroupCreateActivity.6
            private boolean wasEmpty;

            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 67) {
                    boolean z = true;
                    if (event.getAction() == 0) {
                        if (GroupCreateActivity.this.editText.length() != 0) {
                            z = false;
                        }
                        this.wasEmpty = z;
                    } else if (event.getAction() == 1 && this.wasEmpty && !GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan) GroupCreateActivity.this.allSpans.get(GroupCreateActivity.this.allSpans.size() - 1));
                        GroupCreateActivity.this.updateHint();
                        GroupCreateActivity.this.checkVisibleRows();
                        return true;
                    }
                }
                return false;
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.GroupCreateActivity.7
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i4, int i22, int i32) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (GroupCreateActivity.this.editText.length() == 0) {
                    GroupCreateActivity.this.closeSearch();
                    return;
                }
                if (!GroupCreateActivity.this.adapter.searching) {
                    GroupCreateActivity.this.searching = true;
                    GroupCreateActivity.this.searchWas = true;
                    GroupCreateActivity.this.adapter.setSearching(true);
                    GroupCreateActivity.this.itemDecoration.setSearching(true);
                    GroupCreateActivity.this.listView.setFastScrollVisible(false);
                    GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
                }
                GroupCreateActivity.this.adapter.searchDialogs(GroupCreateActivity.this.editText.getText().toString());
                GroupCreateActivity.this.emptyView.showProgress(true, false);
            }
        });
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        flickerLoadingView.setViewType(6);
        flickerLoadingView.showDate(false);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.addView(flickerLoadingView);
        this.emptyView.showProgress(true, false);
        this.emptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setFastScrollEnabled(0);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        GroupCreateAdapter groupCreateAdapter = new GroupCreateAdapter(context);
        this.adapter = groupCreateAdapter;
        recyclerListView2.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView3 = this.listView;
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.itemDecoration = groupCreateDividerItemDecoration;
        recyclerListView3.addItemDecoration(groupCreateDividerItemDecoration);
        frameLayout.addView(this.listView);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.GroupCreateActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i4) {
                GroupCreateActivity.this.m3535lambda$createView$3$orgtelegramuiGroupCreateActivity(context, view, i4);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.GroupCreateActivity.8
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    GroupCreateActivity.this.editText.hideActionMode();
                    AndroidUtilities.hideKeyboard(GroupCreateActivity.this.editText);
                }
            }
        });
        this.listView.setAnimateEmptyView(true, 0);
        ImageView imageView = new ImageView(context);
        this.floatingButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(drawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
        if (this.isNeverShare || this.isAlwaysShare || this.addToGroup) {
            this.floatingButton.setImageResource(R.drawable.floating_check);
        } else {
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setArrowRotation(180);
            this.floatingButton.setImageDrawable(backDrawable);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.floatingButton.setStateListAnimator(animator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.GroupCreateActivity.9
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        frameLayout.addView(this.floatingButton);
        this.floatingButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCreateActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCreateActivity.this.m3536lambda$createView$4$orgtelegramuiGroupCreateActivity(view);
            }
        });
        if (this.chatType != 2) {
            this.floatingButton.setVisibility(4);
            this.floatingButton.setScaleX(0.0f);
            this.floatingButton.setScaleY(0.0f);
            this.floatingButton.setAlpha(0.0f);
        }
        this.floatingButton.setContentDescription(LocaleController.getString("Next", R.string.Next));
        updateHint();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-GroupCreateActivity */
    public /* synthetic */ void m3532lambda$createView$0$orgtelegramuiGroupCreateActivity(View v) {
        this.editText.clearFocus();
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-GroupCreateActivity */
    public /* synthetic */ boolean m3533lambda$createView$1$orgtelegramuiGroupCreateActivity(TextView v, int actionId, KeyEvent event) {
        return actionId == 6 && onDonePressed(true);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-GroupCreateActivity */
    public /* synthetic */ void m3535lambda$createView$3$orgtelegramuiGroupCreateActivity(Context context, View view, int position) {
        long id;
        if (position == 0 && this.adapter.inviteViaLink != 0 && !this.adapter.searching) {
            PermanentLinkBottomSheet permanentLinkBottomSheet = new PermanentLinkBottomSheet(context, false, this, this.info, this.chatId, this.channelId != 0);
            this.sharedLinkBottomSheet = permanentLinkBottomSheet;
            showDialog(permanentLinkBottomSheet);
        } else if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell cell = (GroupCreateUserCell) view;
            Object object = cell.getObject();
            if (object instanceof TLRPC.User) {
                id = ((TLRPC.User) object).id;
            } else if (object instanceof TLRPC.Chat) {
                id = -((TLRPC.Chat) object).id;
            } else {
                return;
            }
            LongSparseArray<TLObject> longSparseArray = this.ignoreUsers;
            if (longSparseArray != null && longSparseArray.indexOfKey(id) >= 0) {
                return;
            }
            boolean z = this.selectedContacts.indexOfKey(id) >= 0;
            boolean exists = z;
            if (z) {
                this.spansContainer.removeSpan(this.selectedContacts.get(id));
            } else if (this.maxCount != 0 && this.selectedContacts.size() == this.maxCount) {
                return;
            } else {
                if (this.chatType == 0 && this.selectedContacts.size() == getMessagesController().maxGroupCount) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.getString("SoftUserLimitAlert", R.string.SoftUserLimitAlert));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    showDialog(builder.create());
                    return;
                }
                if (object instanceof TLRPC.User) {
                    final TLRPC.User user = (TLRPC.User) object;
                    if (this.addToGroup && user.bot) {
                        if (this.channelId == 0 && user.bot_nochats) {
                            try {
                                BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("BotCantJoinGroups", R.string.BotCantJoinGroups)).show();
                                return;
                            } catch (Exception e) {
                                FileLog.e(e);
                                return;
                            }
                        } else if (this.channelId != 0) {
                            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.channelId));
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                            if (ChatObject.canAddAdmins(chat)) {
                                builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder2.setMessage(LocaleController.getString("AddBotAsAdmin", R.string.AddBotAsAdmin));
                                builder2.setPositiveButton(LocaleController.getString("MakeAdmin", R.string.MakeAdmin), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCreateActivity$$ExternalSyntheticLambda0
                                    @Override // android.content.DialogInterface.OnClickListener
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        GroupCreateActivity.this.m3534lambda$createView$2$orgtelegramuiGroupCreateActivity(user, dialogInterface, i);
                                    }
                                });
                                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            } else {
                                builder2.setMessage(LocaleController.getString("CantAddBotAsAdmin", R.string.CantAddBotAsAdmin));
                                builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                            }
                            showDialog(builder2.create());
                            return;
                        }
                    }
                    getMessagesController().putUser(user, !this.searching);
                } else {
                    TLRPC.Chat chat2 = (TLRPC.Chat) object;
                    getMessagesController().putChat(chat2, !this.searching);
                }
                GroupCreateSpan span = new GroupCreateSpan(this.editText.getContext(), object);
                this.spansContainer.addSpan(span);
                span.setOnClickListener(this);
            }
            updateHint();
            if (this.searching || this.searchWas) {
                AndroidUtilities.showKeyboard(this.editText);
            } else {
                cell.setChecked(!exists, true);
            }
            if (this.editText.length() > 0) {
                this.editText.setText((CharSequence) null);
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-GroupCreateActivity */
    public /* synthetic */ void m3534lambda$createView$2$orgtelegramuiGroupCreateActivity(TLRPC.User user, DialogInterface dialogInterface, int i) {
        this.delegate2.needAddBot(user);
        if (this.editText.length() > 0) {
            this.editText.setText((CharSequence) null);
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-GroupCreateActivity */
    public /* synthetic */ void m3536lambda$createView$4$orgtelegramuiGroupCreateActivity(View v) {
        onDonePressed(true);
    }

    public void updateEditTextHint() {
        GroupCreateAdapter groupCreateAdapter;
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor == null) {
            return;
        }
        if (this.chatType == 2) {
            editTextBoldCursor.setHintText(LocaleController.getString("AddMutual", R.string.AddMutual));
        } else if (this.addToGroup || ((groupCreateAdapter = this.adapter) != null && groupCreateAdapter.noContactsStubRow == 0)) {
            this.editText.setHintText(LocaleController.getString("SearchForPeople", R.string.SearchForPeople));
        } else if (this.isAlwaysShare || this.isNeverShare) {
            this.editText.setHintText(LocaleController.getString("SearchForPeopleAndGroups", R.string.SearchForPeopleAndGroups));
        } else {
            this.editText.setHintText(LocaleController.getString("SendMessageTo", R.string.SendMessageTo));
        }
    }

    public void showItemsAnimated(final int from) {
        if (this.isPaused) {
            return;
        }
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.GroupCreateActivity.10
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                GroupCreateActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int n = GroupCreateActivity.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i = 0; i < n; i++) {
                    View child = GroupCreateActivity.this.listView.getChildAt(i);
                    if (GroupCreateActivity.this.listView.getChildAdapterPosition(child) >= from) {
                        child.setAlpha(0.0f);
                        int s = Math.min(GroupCreateActivity.this.listView.getMeasuredHeight(), Math.max(0, child.getTop()));
                        int delay = (int) ((s / GroupCreateActivity.this.listView.getMeasuredHeight()) * 100.0f);
                        ObjectAnimator a = ObjectAnimator.ofFloat(child, View.ALPHA, 0.0f, 1.0f);
                        a.setStartDelay(delay);
                        a.setDuration(200L);
                        animatorSet.playTogether(a);
                    }
                }
                animatorSet.start();
                return true;
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsDidLoad) {
            GroupCreateAdapter groupCreateAdapter = this.adapter;
            if (groupCreateAdapter != null) {
                groupCreateAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                int mask = ((Integer) args[0]).intValue();
                int count = this.listView.getChildCount();
                if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 || (MessagesController.UPDATE_MASK_NAME & mask) != 0 || (MessagesController.UPDATE_MASK_STATUS & mask) != 0) {
                    for (int a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.chatDidCreated) {
            removeSelfFromStack();
        }
    }

    public void setIgnoreUsers(LongSparseArray<TLObject> users) {
        this.ignoreUsers = users;
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
    }

    public void setContainerHeight(int value) {
        int dy = this.containerHeight - value;
        this.containerHeight = value;
        int measuredH = Math.min(this.maxSize, this.measuredContainerHeight);
        int currentH = Math.min(this.maxSize, this.containerHeight);
        ScrollView scrollView = this.scrollView;
        scrollView.scrollTo(0, Math.max(0, scrollView.getScrollY() - dy));
        this.listView.setTranslationY(currentH - measuredH);
        this.fragmentView.invalidate();
    }

    public int getContainerHeight() {
        return this.containerHeight;
    }

    public void checkVisibleRows() {
        long id;
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof GroupCreateUserCell) {
                GroupCreateUserCell cell = (GroupCreateUserCell) child;
                Object object = cell.getObject();
                if (object instanceof TLRPC.User) {
                    id = ((TLRPC.User) object).id;
                } else if (object instanceof TLRPC.Chat) {
                    id = -((TLRPC.Chat) object).id;
                } else {
                    id = 0;
                }
                if (id != 0) {
                    LongSparseArray<TLObject> longSparseArray = this.ignoreUsers;
                    boolean z = false;
                    if (longSparseArray != null && longSparseArray.indexOfKey(id) >= 0) {
                        cell.setChecked(true, false);
                        cell.setCheckBoxEnabled(false);
                    } else {
                        if (this.selectedContacts.indexOfKey(id) >= 0) {
                            z = true;
                        }
                        cell.setChecked(z, true);
                        cell.setCheckBoxEnabled(true);
                    }
                }
            }
        }
    }

    private void onAddToGroupDone(int count) {
        ArrayList<TLRPC.User> result = new ArrayList<>();
        for (int a = 0; a < this.selectedContacts.size(); a++) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.selectedContacts.keyAt(a)));
            result.add(user);
        }
        ContactsAddActivityDelegate contactsAddActivityDelegate = this.delegate2;
        if (contactsAddActivityDelegate != null) {
            contactsAddActivityDelegate.didSelectUsers(result, count);
        }
        finishFragment();
    }

    public boolean onDonePressed(boolean alert) {
        if (this.selectedContacts.size() != 0 || this.chatType == 2) {
            if (alert && this.addToGroup) {
                if (getParentActivity() == null) {
                    return false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                if (this.selectedContacts.size() == 1) {
                    builder.setTitle(LocaleController.getString("AddOneMemberAlertTitle", R.string.AddOneMemberAlertTitle));
                } else {
                    builder.setTitle(LocaleController.formatString("AddMembersAlertTitle", R.string.AddMembersAlertTitle, LocaleController.formatPluralString("Members", this.selectedContacts.size(), new Object[0])));
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (int a = 0; a < this.selectedContacts.size(); a++) {
                    long uid = this.selectedContacts.keyAt(a);
                    TLRPC.User user = getMessagesController().getUser(Long.valueOf(uid));
                    if (user != null) {
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(", ");
                        }
                        stringBuilder.append("**");
                        stringBuilder.append(ContactsController.formatName(user.first_name, user.last_name));
                        stringBuilder.append("**");
                    }
                }
                MessagesController messagesController = getMessagesController();
                long j = this.chatId;
                if (j == 0) {
                    j = this.channelId;
                }
                TLRPC.Chat chat = messagesController.getChat(Long.valueOf(j));
                if (this.selectedContacts.size() > 5) {
                    Object[] objArr = new Object[2];
                    objArr[0] = LocaleController.formatPluralString("Members", this.selectedContacts.size(), new Object[0]);
                    objArr[1] = chat == null ? "" : chat.title;
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, objArr)));
                    String countString = String.format("%d", Integer.valueOf(this.selectedContacts.size()));
                    int index = TextUtils.indexOf(spannableStringBuilder, countString);
                    if (index >= 0) {
                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), index, countString.length() + index, 33);
                    }
                    builder.setMessage(spannableStringBuilder);
                } else {
                    Object[] objArr2 = new Object[2];
                    objArr2[0] = stringBuilder;
                    objArr2[1] = chat == null ? "" : chat.title;
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, objArr2)));
                }
                final CheckBoxCell[] cells = new CheckBoxCell[1];
                if (!ChatObject.isChannel(chat)) {
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    linearLayout.setOrientation(1);
                    cells[0] = new CheckBoxCell(getParentActivity(), 1);
                    cells[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    cells[0].setMultiline(true);
                    if (this.selectedContacts.size() == 1) {
                        cells[0].setText(AndroidUtilities.replaceTags(LocaleController.formatString("AddOneMemberForwardMessages", R.string.AddOneMemberForwardMessages, UserObject.getFirstName(getMessagesController().getUser(Long.valueOf(this.selectedContacts.keyAt(0)))))), "", true, false);
                    } else {
                        cells[0].setText(LocaleController.getString("AddMembersForwardMessages", R.string.AddMembersForwardMessages), "", true, false);
                    }
                    cells[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                    linearLayout.addView(cells[0], LayoutHelper.createLinear(-1, -2));
                    cells[0].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCreateActivity$$ExternalSyntheticLambda4
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            CheckBoxCell[] checkBoxCellArr = cells;
                            checkBoxCellArr[0].setChecked(!checkBoxCellArr[0].isChecked(), true);
                        }
                    });
                    builder.setCustomViewOffset(12);
                    builder.setView(linearLayout);
                }
                builder.setPositiveButton(LocaleController.getString("Add", R.string.Add), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupCreateActivity$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        GroupCreateActivity.this.m3538lambda$onDonePressed$6$orgtelegramuiGroupCreateActivity(cells, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            } else if (this.chatType == 2) {
                ArrayList<TLRPC.InputUser> result = new ArrayList<>();
                for (int a2 = 0; a2 < this.selectedContacts.size(); a2++) {
                    TLRPC.InputUser user2 = getMessagesController().getInputUser(getMessagesController().getUser(Long.valueOf(this.selectedContacts.keyAt(a2))));
                    if (user2 != null) {
                        result.add(user2);
                    }
                }
                getMessagesController().addUsersToChannel(this.chatId, result, null);
                getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                Bundle args2 = new Bundle();
                args2.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
                presentFragment(new ChatActivity(args2), true);
            } else if (!this.doneButtonVisible || this.selectedContacts.size() == 0) {
                return false;
            } else {
                if (this.addToGroup) {
                    onAddToGroupDone(0);
                } else {
                    ArrayList<Long> result2 = new ArrayList<>();
                    for (int a3 = 0; a3 < this.selectedContacts.size(); a3++) {
                        result2.add(Long.valueOf(this.selectedContacts.keyAt(a3)));
                    }
                    if (this.isAlwaysShare || this.isNeverShare) {
                        GroupCreateActivityDelegate groupCreateActivityDelegate = this.delegate;
                        if (groupCreateActivityDelegate != null) {
                            groupCreateActivityDelegate.didSelectUsers(result2);
                        }
                        finishFragment();
                    } else {
                        Bundle args = new Bundle();
                        long[] array = new long[result2.size()];
                        for (int a4 = 0; a4 < array.length; a4++) {
                            array[a4] = result2.get(a4).longValue();
                        }
                        args.putLongArray("result", array);
                        args.putInt("chatType", this.chatType);
                        args.putBoolean("forImport", this.forImport);
                        presentFragment(new GroupCreateFinalActivity(args));
                    }
                }
            }
            return true;
        }
        return false;
    }

    /* renamed from: lambda$onDonePressed$6$org-telegram-ui-GroupCreateActivity */
    public /* synthetic */ void m3538lambda$onDonePressed$6$orgtelegramuiGroupCreateActivity(CheckBoxCell[] cells, DialogInterface dialogInterface, int i) {
        int i2 = 0;
        if (cells[0] != null && cells[0].isChecked()) {
            i2 = 100;
        }
        onAddToGroupDone(i2);
    }

    public void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.itemDecoration.setSearching(false);
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        showItemsAnimated(0);
    }

    public void updateHint() {
        if (!this.isAlwaysShare && !this.isNeverShare && !this.addToGroup) {
            if (this.chatType == 2) {
                this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size(), new Object[0]));
            } else if (this.selectedContacts.size() == 0) {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", R.string.MembersCountZero, LocaleController.formatPluralString("Members", this.maxCount, new Object[0])));
            } else {
                String str = LocaleController.getPluralString("MembersCountSelected", this.selectedContacts.size());
                this.actionBar.setSubtitle(String.format(str, Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount)));
            }
        }
        if (this.chatType != 2) {
            if (this.doneButtonVisible && this.allSpans.isEmpty()) {
                AnimatorSet animatorSet = this.currentDoneButtonAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentDoneButtonAnimation = animatorSet2;
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.floatingButton, View.ALPHA, 0.0f));
                this.currentDoneButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCreateActivity.11
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        GroupCreateActivity.this.floatingButton.setVisibility(4);
                    }
                });
                this.currentDoneButtonAnimation.setDuration(180L);
                this.currentDoneButtonAnimation.start();
                this.doneButtonVisible = false;
            } else if (!this.doneButtonVisible && !this.allSpans.isEmpty()) {
                AnimatorSet animatorSet3 = this.currentDoneButtonAnimation;
                if (animatorSet3 != null) {
                    animatorSet3.cancel();
                }
                this.currentDoneButtonAnimation = new AnimatorSet();
                this.floatingButton.setVisibility(0);
                this.currentDoneButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.floatingButton, View.ALPHA, 1.0f));
                this.currentDoneButtonAnimation.setDuration(180L);
                this.currentDoneButtonAnimation.start();
                this.doneButtonVisible = true;
            }
        }
    }

    public void setDelegate(GroupCreateActivityDelegate groupCreateActivityDelegate) {
        this.delegate = groupCreateActivityDelegate;
    }

    public void setDelegate(ContactsAddActivityDelegate contactsAddActivityDelegate) {
        this.delegate2 = contactsAddActivityDelegate;
    }

    /* loaded from: classes4.dex */
    public class GroupCreateAdapter extends RecyclerListView.FastScrollAdapter {
        private Context context;
        private int currentItemsCount;
        private int inviteViaLink;
        private int noContactsStubRow;
        private SearchAdapterHelper searchAdapterHelper;
        private Runnable searchRunnable;
        private boolean searching;
        private int usersStartRow;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private ArrayList<TLObject> contacts = new ArrayList<>();

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            GroupCreateActivity.this.updateEditTextHint();
        }

        public GroupCreateAdapter(Context ctx) {
            TLRPC.Chat chat;
            GroupCreateActivity.this = this$0;
            this.context = ctx;
            ArrayList<TLRPC.TL_contact> arrayList = this$0.getContactsController().contacts;
            for (int a = 0; a < arrayList.size(); a++) {
                TLRPC.User user = this$0.getMessagesController().getUser(Long.valueOf(arrayList.get(a).user_id));
                if (user != null && !user.self && !user.deleted) {
                    this.contacts.add(user);
                }
            }
            if (this$0.isNeverShare || this$0.isAlwaysShare) {
                ArrayList<TLRPC.Dialog> dialogs = this$0.getMessagesController().getAllDialogs();
                int N = dialogs.size();
                for (int a2 = 0; a2 < N; a2++) {
                    TLRPC.Dialog dialog = dialogs.get(a2);
                    if (DialogObject.isChatDialog(dialog.id) && (chat = this$0.getMessagesController().getChat(Long.valueOf(-dialog.id))) != null && chat.migrated_to == null && (!ChatObject.isChannel(chat) || chat.megagroup)) {
                        this.contacts.add(chat);
                    }
                }
                Collections.sort(this.contacts, new Comparator<TLObject>() { // from class: org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.1
                    private String getName(TLObject object) {
                        if (object instanceof TLRPC.User) {
                            TLRPC.User user2 = (TLRPC.User) object;
                            return ContactsController.formatName(user2.first_name, user2.last_name);
                        }
                        TLRPC.Chat chat2 = (TLRPC.Chat) object;
                        return chat2.title;
                    }

                    public int compare(TLObject o1, TLObject o2) {
                        return getName(o1).compareTo(getName(o2));
                    }
                });
            }
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda4
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
                    GroupCreateActivity.GroupCreateAdapter.this.m3539x6d31c359(i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ void onSetHashtags(ArrayList arrayList2, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList2, hashMap);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-GroupCreateActivity$GroupCreateAdapter */
        public /* synthetic */ void m3539x6d31c359(int searchId) {
            GroupCreateActivity.this.showItemsAnimated(this.currentItemsCount);
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress() && getItemCount() == 0) {
                GroupCreateActivity.this.emptyView.showProgress(false, true);
            }
            notifyDataSetChanged();
        }

        public void setSearching(boolean value) {
            if (this.searching == value) {
                return;
            }
            this.searching = value;
            notifyDataSetChanged();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public String getLetter(int position) {
            String firstName;
            String lastName;
            if (this.searching || position < this.usersStartRow) {
                return null;
            }
            int size = this.contacts.size();
            int i = this.usersStartRow;
            if (position >= size + i) {
                return null;
            }
            TLObject object = this.contacts.get(position - i);
            if (object instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) object;
                firstName = user.first_name;
                lastName = user.last_name;
            } else {
                TLRPC.Chat chat = (TLRPC.Chat) object;
                firstName = chat.title;
                lastName = "";
            }
            if (LocaleController.nameDisplayOrder == 1) {
                if (!TextUtils.isEmpty(firstName)) {
                    return firstName.substring(0, 1).toUpperCase();
                }
                if (!TextUtils.isEmpty(lastName)) {
                    return lastName.substring(0, 1).toUpperCase();
                }
                return "";
            } else if (!TextUtils.isEmpty(lastName)) {
                return lastName.substring(0, 1).toUpperCase();
            } else {
                if (!TextUtils.isEmpty(firstName)) {
                    return firstName.substring(0, 1).toUpperCase();
                }
                return "";
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            this.noContactsStubRow = -1;
            if (this.searching) {
                int count = this.searchResult.size();
                int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
                int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
                int count2 = count + localServerCount;
                if (globalCount != 0) {
                    count2 += globalCount + 1;
                }
                this.currentItemsCount = count2;
                return count2;
            }
            int count3 = this.contacts.size();
            if (GroupCreateActivity.this.addToGroup) {
                if (GroupCreateActivity.this.chatId != 0) {
                    this.inviteViaLink = ChatObject.canUserDoAdminAction(GroupCreateActivity.this.getMessagesController().getChat(Long.valueOf(GroupCreateActivity.this.chatId)), 3) ? 1 : 0;
                } else if (GroupCreateActivity.this.channelId != 0) {
                    TLRPC.Chat chat = GroupCreateActivity.this.getMessagesController().getChat(Long.valueOf(GroupCreateActivity.this.channelId));
                    this.inviteViaLink = (!ChatObject.canUserDoAdminAction(chat, 3) || !TextUtils.isEmpty(chat.username)) ? 0 : 2;
                } else {
                    this.inviteViaLink = 0;
                }
                if (this.inviteViaLink != 0) {
                    this.usersStartRow = 1;
                    count3++;
                }
            }
            if (count3 == 0) {
                this.noContactsStubRow = 0;
                count3++;
            }
            this.currentItemsCount = count3;
            return count3;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GroupCreateSectionCell(this.context);
                    break;
                case 1:
                    view = new GroupCreateUserCell(this.context, 1, 0, false);
                    break;
                case 2:
                default:
                    view = new TextCell(this.context);
                    break;
                case 3:
                    StickerEmptyView stickerEmptyView = new StickerEmptyView(this.context, null, 0) { // from class: org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.2
                        @Override // org.telegram.ui.Components.StickerEmptyView, android.view.ViewGroup, android.view.View
                        public void onAttachedToWindow() {
                            super.onAttachedToWindow();
                            this.stickerView.getImageReceiver().startAnimation();
                        }
                    };
                    stickerEmptyView.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    stickerEmptyView.subtitle.setVisibility(8);
                    stickerEmptyView.title.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
                    stickerEmptyView.setAnimateLayoutChange(true);
                    view = stickerEmptyView;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:66:0x0165  */
        /* JADX WARN: Removed duplicated region for block: B:67:0x016b  */
        /* JADX WARN: Removed duplicated region for block: B:73:0x017e  */
        /* JADX WARN: Removed duplicated region for block: B:93:? A[RETURN, SYNTHETIC] */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r19, int r20) {
            /*
                Method dump skipped, instructions count: 466
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (this.searching) {
                return position == this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size() ? 0 : 1;
            } else if (this.inviteViaLink != 0 && position == 0) {
                return 2;
            } else {
                return this.noContactsStubRow == position ? 3 : 1;
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = (int) (getItemCount() * progress);
            position[1] = 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof GroupCreateUserCell) {
                ((GroupCreateUserCell) holder.itemView).recycle();
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (GroupCreateActivity.this.ignoreUsers != null && (holder.itemView instanceof GroupCreateUserCell)) {
                GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
                Object object = cell.getObject();
                if (object instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) object;
                    return GroupCreateActivity.this.ignoreUsers.indexOfKey(user.id) < 0;
                }
            }
            return true;
        }

        public void searchDialogs(final String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults(null);
            this.searchAdapterHelper.queryServerSearch(null, true, GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare, false, false, false, 0L, false, 0, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(query)) {
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        GroupCreateActivity.GroupCreateAdapter.this.m3542xd94b6953(query);
                    }
                };
                this.searchRunnable = runnable;
                dispatchQueue.postRunnable(runnable, 300L);
            }
        }

        /* renamed from: lambda$searchDialogs$3$org-telegram-ui-GroupCreateActivity$GroupCreateAdapter */
        public /* synthetic */ void m3542xd94b6953(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCreateActivity.GroupCreateAdapter.this.m3541xd8151674(query);
                }
            });
        }

        /* renamed from: lambda$searchDialogs$2$org-telegram-ui-GroupCreateActivity$GroupCreateAdapter */
        public /* synthetic */ void m3541xd8151674(final String query) {
            this.searchAdapterHelper.queryServerSearch(query, true, GroupCreateActivity.this.isAlwaysShare || GroupCreateActivity.this.isNeverShare, true, false, false, 0L, false, 0, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCreateActivity.GroupCreateAdapter.this.m3540xd6dec395(query);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable);
        }

        /* JADX WARN: Code restructure failed: missing block: B:38:0x00cc, code lost:
            if (r12.contains(" " + r3) != false) goto L44;
         */
        /* JADX WARN: Removed duplicated region for block: B:54:0x012f A[LOOP:1: B:29:0x008e->B:54:0x012f, LOOP_END] */
        /* JADX WARN: Removed duplicated region for block: B:63:0x00e0 A[SYNTHETIC] */
        /* renamed from: lambda$searchDialogs$1$org-telegram-ui-GroupCreateActivity$GroupCreateAdapter */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void m3540xd6dec395(java.lang.String r19) {
            /*
                Method dump skipped, instructions count: 325
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.m3540xd6dec395(java.lang.String):void");
        }

        private void updateSearchResults(final ArrayList<Object> users, final ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCreateActivity.GroupCreateAdapter.this.m3543x892cf330(users, names);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-GroupCreateActivity$GroupCreateAdapter */
        public /* synthetic */ void m3543x892cf330(ArrayList users, ArrayList names) {
            if (!this.searching) {
                return;
            }
            this.searchRunnable = null;
            this.searchResult = users;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(users);
            GroupCreateActivity.this.showItemsAnimated(this.currentItemsCount);
            notifyDataSetChanged();
            if (this.searching && !this.searchAdapterHelper.isSearchInProgress() && getItemCount() == 0) {
                GroupCreateActivity.this.emptyView.showProgress(false, true);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.GroupCreateActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                GroupCreateActivity.this.m3537x175d62aa();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_groupcreate_hintText));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_groupcreate_cursor));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, Theme.key_graySection));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_sectionShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_sectionText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_sectionText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxDisabled));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanBackground));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanText));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanDelete));
        themeDescriptions.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        PermanentLinkBottomSheet permanentLinkBottomSheet = this.sharedLinkBottomSheet;
        if (permanentLinkBottomSheet != null) {
            themeDescriptions.addAll(permanentLinkBottomSheet.getThemeDescriptions());
        }
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$7$org-telegram-ui-GroupCreateActivity */
    public /* synthetic */ void m3537x175d62aa() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) child).update(0);
                }
            }
        }
    }
}
