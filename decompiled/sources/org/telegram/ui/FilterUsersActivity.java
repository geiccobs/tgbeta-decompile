package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.ScrollView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilterUsersActivity;
/* loaded from: classes4.dex */
public class FilterUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private int containerHeight;
    private GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private FilterUsersActivityDelegate delegate;
    private EditTextBoldCursor editText;
    private EmptyTextProgressView emptyView;
    private int fieldY;
    private int filterFlags;
    private ImageView floatingButton;
    private boolean ignoreScrollEvent;
    private ArrayList<Long> initialIds;
    private boolean isInclude;
    private RecyclerListView listView;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private int selectedCount;
    private SpansContainer spansContainer;
    private LongSparseArray<GroupCreateSpan> selectedContacts = new LongSparseArray<>();
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList<>();

    /* loaded from: classes4.dex */
    public interface FilterUsersActivityDelegate {
        void didSelectChats(ArrayList<Long> arrayList, int i);
    }

    static /* synthetic */ int access$1972(FilterUsersActivity x0, int x1) {
        int i = x0.filterFlags & x1;
        x0.filterFlags = i;
        return i;
    }

    static /* synthetic */ int access$508(FilterUsersActivity x0) {
        int i = x0.selectedCount;
        x0.selectedCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$510(FilterUsersActivity x0) {
        int i = x0.selectedCount;
        x0.selectedCount = i - 1;
        return i;
    }

    /* loaded from: classes4.dex */
    private static class ItemDecoration extends RecyclerView.ItemDecoration {
        private boolean single;
        private int skipRows;

        private ItemDecoration() {
        }

        public void setSingle(boolean value) {
            this.single = value;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            int width = parent.getWidth();
            int childCount = parent.getChildCount() - (!this.single ? 1 : 0);
            int i = 0;
            while (i < childCount) {
                View child = parent.getChildAt(i);
                View nextChild = i < childCount + (-1) ? parent.getChildAt(i + 1) : null;
                int position = parent.getChildAdapterPosition(child);
                if (position >= this.skipRows && !(child instanceof GraySectionCell) && !(nextChild instanceof GraySectionCell)) {
                    int top = child.getBottom();
                    canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(72.0f), top, width - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0), top, Theme.dividerPaint);
                }
                i++;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top = 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList<>();
        private AnimatorSet currentAnimation;
        private View removingSpan;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SpansContainer(Context context) {
            super(context);
            FilterUsersActivity.this = r1;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int minWidth;
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
                                this.animators.add(ObjectAnimator.ofFloat(child, View.TRANSLATION_X, x));
                            }
                            if (child.getTranslationY() != y) {
                                this.animators.add(ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, y));
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
            FilterUsersActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(maxWidth - currentLineWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C.BUFFER_FLAG_ENCRYPTED));
            if (this.animationStarted) {
                if (this.currentAnimation != null && !FilterUsersActivity.this.ignoreScrollEvent && this.removingSpan == null) {
                    FilterUsersActivity.this.editText.bringPointIntoView(FilterUsersActivity.this.editText.getSelectionStart());
                }
            } else {
                int currentHeight = AndroidUtilities.dp(42.0f) + allY;
                int fieldX = AndroidUtilities.dp(16.0f) + currentLineWidth;
                FilterUsersActivity.this.fieldY = y;
                if (this.currentAnimation == null) {
                    FilterUsersActivity.this.containerHeight = currentHeight;
                    FilterUsersActivity.this.editText.setTranslationX(fieldX);
                    FilterUsersActivity.this.editText.setTranslationY(FilterUsersActivity.this.fieldY);
                } else {
                    int resultHeight = AndroidUtilities.dp(42.0f) + y;
                    if (FilterUsersActivity.this.containerHeight != resultHeight) {
                        this.animators.add(ObjectAnimator.ofInt(FilterUsersActivity.this, "containerHeight", resultHeight));
                    }
                    if (FilterUsersActivity.this.editText.getTranslationX() != fieldX) {
                        this.animators.add(ObjectAnimator.ofFloat(FilterUsersActivity.this.editText, View.TRANSLATION_X, fieldX));
                    }
                    if (FilterUsersActivity.this.editText.getTranslationY() != FilterUsersActivity.this.fieldY) {
                        this.animators.add(ObjectAnimator.ofFloat(FilterUsersActivity.this.editText, View.TRANSLATION_Y, FilterUsersActivity.this.fieldY));
                    }
                    FilterUsersActivity.this.editText.setAllowDrawCursor(false);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                }
            }
            setMeasuredDimension(width, FilterUsersActivity.this.containerHeight);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan span, boolean animated) {
            FilterUsersActivity.this.allSpans.add(span);
            long uid = span.getUid();
            if (uid > -2147483641) {
                FilterUsersActivity.access$508(FilterUsersActivity.this);
            }
            FilterUsersActivity.this.selectedContacts.put(uid, span);
            FilterUsersActivity.this.editText.setHintVisible(false);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.FilterUsersActivity.SpansContainer.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        SpansContainer.this.addingSpan = null;
                        SpansContainer.this.currentAnimation = null;
                        SpansContainer.this.animationStarted = false;
                        FilterUsersActivity.this.editText.setAllowDrawCursor(true);
                    }
                });
                this.currentAnimation.setDuration(150L);
                this.addingSpan = span;
                this.animators.clear();
                this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_X, 0.01f, 1.0f));
                this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.SCALE_Y, 0.01f, 1.0f));
                this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, View.ALPHA, 0.0f, 1.0f));
            }
            addView(span);
        }

        public void removeSpan(final GroupCreateSpan span) {
            FilterUsersActivity.this.ignoreScrollEvent = true;
            long uid = span.getUid();
            if (uid > -2147483641) {
                FilterUsersActivity.access$510(FilterUsersActivity.this);
            }
            FilterUsersActivity.this.selectedContacts.remove(uid);
            FilterUsersActivity.this.allSpans.remove(span);
            span.setOnClickListener(null);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.FilterUsersActivity.SpansContainer.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(span);
                    SpansContainer.this.removingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    FilterUsersActivity.this.editText.setAllowDrawCursor(true);
                    if (FilterUsersActivity.this.allSpans.isEmpty()) {
                        FilterUsersActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150L);
            this.removingSpan = span;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_X, 1.0f, 0.01f));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.SCALE_Y, 1.0f, 0.01f));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, View.ALPHA, 1.0f, 0.0f));
            requestLayout();
        }
    }

    public FilterUsersActivity(boolean include, ArrayList<Long> arrayList, int flags) {
        this.isInclude = include;
        this.filterFlags = flags;
        this.initialIds = arrayList;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        GroupCreateSpan span = (GroupCreateSpan) v;
        if (span.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(span);
            if (span.getUid() == -2147483648L) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ (-1);
            } else if (span.getUid() == -2147483647L) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ (-1);
            } else if (span.getUid() == -2147483646) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ (-1);
            } else if (span.getUid() == -2147483645) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ (-1);
            } else if (span.getUid() == -2147483644) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_BOTS ^ (-1);
            } else if (span.getUid() == -2147483643) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED ^ (-1);
            } else if (span.getUid() == -2147483642) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ ^ (-1);
            } else if (span.getUid() == -2147483641) {
                this.filterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED ^ (-1);
            }
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
        TLObject object;
        int flag;
        Object object2;
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.isInclude) {
            this.actionBar.setTitle(LocaleController.getString("FilterAlwaysShow", R.string.FilterAlwaysShow));
        } else {
            this.actionBar.setTitle(LocaleController.getString("FilterNeverShow", R.string.FilterNeverShow));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.FilterUsersActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    FilterUsersActivity.this.finishFragment();
                } else if (id == 1) {
                    FilterUsersActivity.this.onDonePressed(true);
                }
            }
        });
        this.fragmentView = new ViewGroup(context) { // from class: org.telegram.ui.FilterUsersActivity.2
            @Override // android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int maxSize;
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                float f = 56.0f;
                if (AndroidUtilities.isTablet() || height > width) {
                    maxSize = AndroidUtilities.dp(144.0f);
                } else {
                    maxSize = AndroidUtilities.dp(56.0f);
                }
                FilterUsersActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE));
                FilterUsersActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height - FilterUsersActivity.this.scrollView.getMeasuredHeight(), C.BUFFER_FLAG_ENCRYPTED));
                FilterUsersActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height - FilterUsersActivity.this.scrollView.getMeasuredHeight(), C.BUFFER_FLAG_ENCRYPTED));
                if (FilterUsersActivity.this.floatingButton != null) {
                    if (Build.VERSION.SDK_INT < 21) {
                        f = 60.0f;
                    }
                    int w = AndroidUtilities.dp(f);
                    FilterUsersActivity.this.floatingButton.measure(View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED));
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                FilterUsersActivity.this.scrollView.layout(0, 0, FilterUsersActivity.this.scrollView.getMeasuredWidth(), FilterUsersActivity.this.scrollView.getMeasuredHeight());
                FilterUsersActivity.this.listView.layout(0, FilterUsersActivity.this.scrollView.getMeasuredHeight(), FilterUsersActivity.this.listView.getMeasuredWidth(), FilterUsersActivity.this.scrollView.getMeasuredHeight() + FilterUsersActivity.this.listView.getMeasuredHeight());
                FilterUsersActivity.this.emptyView.layout(0, FilterUsersActivity.this.scrollView.getMeasuredHeight(), FilterUsersActivity.this.emptyView.getMeasuredWidth(), FilterUsersActivity.this.scrollView.getMeasuredHeight() + FilterUsersActivity.this.emptyView.getMeasuredHeight());
                if (FilterUsersActivity.this.floatingButton != null) {
                    int l = LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : ((right - left) - AndroidUtilities.dp(14.0f)) - FilterUsersActivity.this.floatingButton.getMeasuredWidth();
                    int t = ((bottom - top) - AndroidUtilities.dp(14.0f)) - FilterUsersActivity.this.floatingButton.getMeasuredHeight();
                    FilterUsersActivity.this.floatingButton.layout(l, t, FilterUsersActivity.this.floatingButton.getMeasuredWidth() + l, FilterUsersActivity.this.floatingButton.getMeasuredHeight() + t);
                }
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == FilterUsersActivity.this.listView || child == FilterUsersActivity.this.emptyView) {
                    FilterUsersActivity.this.parentLayout.drawHeaderShadow(canvas, FilterUsersActivity.this.scrollView.getMeasuredHeight());
                }
                return result;
            }
        };
        ViewGroup frameLayout = (ViewGroup) this.fragmentView;
        ScrollView scrollView = new ScrollView(context) { // from class: org.telegram.ui.FilterUsersActivity.3
            @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.ViewParent
            public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                if (FilterUsersActivity.this.ignoreScrollEvent) {
                    FilterUsersActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
                rectangle.top += FilterUsersActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rectangle.bottom += FilterUsersActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(child, rectangle, immediate);
            }
        };
        this.scrollView = scrollView;
        scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_windowBackgroundWhite));
        frameLayout.addView(this.scrollView);
        SpansContainer spansContainer = new SpansContainer(context);
        this.spansContainer = spansContainer;
        this.scrollView.addView(spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        this.spansContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FilterUsersActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FilterUsersActivity.this.m3435lambda$createView$0$orgtelegramuiFilterUsersActivity(view);
            }
        });
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) { // from class: org.telegram.ui.FilterUsersActivity.4
            @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (FilterUsersActivity.this.currentDeletingSpan != null) {
                    FilterUsersActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    FilterUsersActivity.this.currentDeletingSpan = null;
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
        this.editText.setHintText(LocaleController.getString("SearchForPeopleAndGroups", R.string.SearchForPeopleAndGroups));
        this.editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() { // from class: org.telegram.ui.FilterUsersActivity.5
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
        this.editText.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.FilterUsersActivity.6
            private boolean wasEmpty;

            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 67) {
                    boolean z = true;
                    if (event.getAction() == 0) {
                        if (FilterUsersActivity.this.editText.length() != 0) {
                            z = false;
                        }
                        this.wasEmpty = z;
                    } else if (event.getAction() == 1 && this.wasEmpty && !FilterUsersActivity.this.allSpans.isEmpty()) {
                        GroupCreateSpan span = (GroupCreateSpan) FilterUsersActivity.this.allSpans.get(FilterUsersActivity.this.allSpans.size() - 1);
                        FilterUsersActivity.this.spansContainer.removeSpan(span);
                        if (span.getUid() == -2147483648L) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ (-1));
                        } else if (span.getUid() == -2147483647L) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ (-1));
                        } else if (span.getUid() == -2147483646) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ (-1));
                        } else if (span.getUid() == -2147483645) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ (-1));
                        } else if (span.getUid() == -2147483644) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_BOTS ^ (-1));
                        } else if (span.getUid() == -2147483643) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED ^ (-1));
                        } else if (span.getUid() == -2147483642) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ ^ (-1));
                        } else if (span.getUid() == -2147483641) {
                            FilterUsersActivity.access$1972(FilterUsersActivity.this, MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED ^ (-1));
                        }
                        FilterUsersActivity.this.updateHint();
                        FilterUsersActivity.this.checkVisibleRows();
                        return true;
                    }
                }
                return false;
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.FilterUsersActivity.7
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (FilterUsersActivity.this.editText.length() == 0) {
                    FilterUsersActivity.this.closeSearch();
                    return;
                }
                if (!FilterUsersActivity.this.adapter.searching) {
                    FilterUsersActivity.this.searching = true;
                    FilterUsersActivity.this.searchWas = true;
                    FilterUsersActivity.this.adapter.setSearching(true);
                    FilterUsersActivity.this.listView.setFastScrollVisible(false);
                    FilterUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
                    FilterUsersActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                    FilterUsersActivity.this.emptyView.showProgress();
                }
                FilterUsersActivity.this.adapter.searchDialogs(FilterUsersActivity.this.editText.getText().toString());
            }
        });
        this.emptyView = new EmptyTextProgressView(context);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
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
        this.listView.addItemDecoration(new ItemDecoration());
        frameLayout.addView(this.listView);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.FilterUsersActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                FilterUsersActivity.this.m3436lambda$createView$1$orgtelegramuiFilterUsersActivity(context, view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.FilterUsersActivity.8
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(FilterUsersActivity.this.editText);
                }
            }
        });
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
        this.floatingButton.setImageResource(R.drawable.floating_check);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.floatingButton.setStateListAnimator(animator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.FilterUsersActivity.9
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        frameLayout.addView(this.floatingButton);
        this.floatingButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FilterUsersActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                FilterUsersActivity.this.m3437lambda$createView$2$orgtelegramuiFilterUsersActivity(view);
            }
        });
        this.floatingButton.setContentDescription(LocaleController.getString("Next", R.string.Next));
        int N = this.isInclude ? 5 : 3;
        for (int position = 1; position <= N; position++) {
            if (this.isInclude) {
                if (position == 1) {
                    flag = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                    object2 = "contacts";
                } else if (position == 2) {
                    flag = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                    object2 = "non_contacts";
                } else if (position == 3) {
                    object2 = "groups";
                    flag = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                } else if (position == 4) {
                    object2 = "channels";
                    flag = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                } else {
                    object2 = "bots";
                    flag = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                }
            } else if (position == 1) {
                object2 = "muted";
                flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
            } else if (position == 2) {
                object2 = "read";
                flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
            } else {
                object2 = "archived";
                flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
            }
            if ((this.filterFlags & flag) != 0) {
                GroupCreateSpan span = new GroupCreateSpan(this.editText.getContext(), object2);
                this.spansContainer.addSpan(span, false);
                span.setOnClickListener(this);
            }
        }
        ArrayList<Long> arrayList = this.initialIds;
        if (arrayList != null && !arrayList.isEmpty()) {
            int N2 = this.initialIds.size();
            for (int a = 0; a < N2; a++) {
                Long id = this.initialIds.get(a);
                if (id.longValue() > 0) {
                    object = getMessagesController().getUser(id);
                } else {
                    object = getMessagesController().getChat(Long.valueOf(-id.longValue()));
                }
                if (object != null) {
                    GroupCreateSpan span2 = new GroupCreateSpan(this.editText.getContext(), object);
                    this.spansContainer.addSpan(span2, false);
                    span2.setOnClickListener(this);
                }
            }
        }
        updateHint();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-FilterUsersActivity */
    public /* synthetic */ void m3435lambda$createView$0$orgtelegramuiFilterUsersActivity(View v) {
        this.editText.clearFocus();
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-FilterUsersActivity */
    public /* synthetic */ void m3436lambda$createView$1$orgtelegramuiFilterUsersActivity(Context context, View view, int position) {
        long id;
        int flag;
        if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell cell = (GroupCreateUserCell) view;
            Object object = cell.getObject();
            if (object instanceof String) {
                if (this.isInclude) {
                    if (position == 1) {
                        flag = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                        id = -2147483648L;
                    } else if (position == 2) {
                        flag = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                        id = -2147483647L;
                    } else if (position == 3) {
                        flag = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                        id = -2147483646;
                    } else if (position == 4) {
                        flag = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                        id = -2147483645;
                    } else {
                        flag = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                        id = -2147483644;
                    }
                } else if (position == 1) {
                    flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
                    id = -2147483643;
                } else if (position == 2) {
                    flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
                    id = -2147483642;
                } else {
                    flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
                    id = -2147483641;
                }
                if (cell.isChecked()) {
                    this.filterFlags &= flag ^ (-1);
                } else {
                    this.filterFlags |= flag;
                }
            } else if (object instanceof TLRPC.User) {
                id = ((TLRPC.User) object).id;
            } else if (object instanceof TLRPC.Chat) {
                id = -((TLRPC.Chat) object).id;
            } else {
                return;
            }
            boolean z = false;
            boolean z2 = this.selectedContacts.indexOfKey(id) >= 0;
            boolean exists = z2;
            if (z2) {
                this.spansContainer.removeSpan(this.selectedContacts.get(id));
            } else if ((!(object instanceof String) && !getUserConfig().isPremium() && this.selectedCount >= MessagesController.getInstance(this.currentAccount).dialogFiltersChatsLimitDefault) || this.selectedCount >= MessagesController.getInstance(this.currentAccount).dialogFiltersChatsLimitPremium) {
                LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(this, context, 4, this.currentAccount);
                limitReachedBottomSheet.setCurrentValue(this.selectedCount);
                showDialog(limitReachedBottomSheet);
                return;
            } else {
                if (object instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) object;
                    MessagesController.getInstance(this.currentAccount).putUser(user, !this.searching);
                } else if (object instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) object;
                    MessagesController.getInstance(this.currentAccount).putChat(chat, !this.searching);
                }
                GroupCreateSpan span = new GroupCreateSpan(this.editText.getContext(), object);
                this.spansContainer.addSpan(span, true);
                span.setOnClickListener(this);
            }
            updateHint();
            if (this.searching || this.searchWas) {
                AndroidUtilities.showKeyboard(this.editText);
            } else {
                if (!exists) {
                    z = true;
                }
                cell.setChecked(z, true);
            }
            if (this.editText.length() > 0) {
                this.editText.setText((CharSequence) null);
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-FilterUsersActivity */
    public /* synthetic */ void m3437lambda$createView$2$orgtelegramuiFilterUsersActivity(View v) {
        onDonePressed(true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsDidLoad) {
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
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

    public void setContainerHeight(int value) {
        this.containerHeight = value;
        SpansContainer spansContainer = this.spansContainer;
        if (spansContainer != null) {
            spansContainer.requestLayout();
        }
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
                boolean z = false;
                if (object instanceof String) {
                    String str = (String) object;
                    char c = 65535;
                    switch (str.hashCode()) {
                        case -1716307998:
                            if (str.equals("archived")) {
                                c = 7;
                                break;
                            }
                            break;
                        case -1237460524:
                            if (str.equals("groups")) {
                                c = 2;
                                break;
                            }
                            break;
                        case -1197490811:
                            if (str.equals("non_contacts")) {
                                c = 1;
                                break;
                            }
                            break;
                        case -567451565:
                            if (str.equals("contacts")) {
                                c = 0;
                                break;
                            }
                            break;
                        case 3029900:
                            if (str.equals("bots")) {
                                c = 4;
                                break;
                            }
                            break;
                        case 3496342:
                            if (str.equals("read")) {
                                c = 6;
                                break;
                            }
                            break;
                        case 104264043:
                            if (str.equals("muted")) {
                                c = 5;
                                break;
                            }
                            break;
                        case 1432626128:
                            if (str.equals("channels")) {
                                c = 3;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            id = -2147483648L;
                            break;
                        case 1:
                            id = -2147483647L;
                            break;
                        case 2:
                            id = -2147483646;
                            break;
                        case 3:
                            id = -2147483645;
                            break;
                        case 4:
                            id = -2147483644;
                            break;
                        case 5:
                            id = -2147483643;
                            break;
                        case 6:
                            id = -2147483642;
                            break;
                        default:
                            id = -2147483641;
                            break;
                    }
                } else if (object instanceof TLRPC.User) {
                    id = ((TLRPC.User) object).id;
                } else if (object instanceof TLRPC.Chat) {
                    id = -((TLRPC.Chat) object).id;
                } else {
                    id = 0;
                }
                if (id != 0) {
                    if (this.selectedContacts.indexOfKey(id) >= 0) {
                        z = true;
                    }
                    cell.setChecked(z, true);
                    cell.setCheckBoxEnabled(true);
                }
            }
        }
    }

    public boolean onDonePressed(boolean alert) {
        ArrayList<Long> result = new ArrayList<>();
        for (int a = 0; a < this.selectedContacts.size(); a++) {
            long uid = this.selectedContacts.keyAt(a);
            if (uid > -2147483641) {
                result.add(Long.valueOf(this.selectedContacts.keyAt(a)));
            }
        }
        FilterUsersActivityDelegate filterUsersActivityDelegate = this.delegate;
        if (filterUsersActivityDelegate != null) {
            filterUsersActivityDelegate.didSelectChats(result, this.filterFlags);
        }
        finishFragment();
        return true;
    }

    public void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
    }

    public void updateHint() {
        int limit = getUserConfig().isPremium() ? getMessagesController().dialogFiltersChatsLimitPremium : getMessagesController().dialogFiltersChatsLimitDefault;
        if (this.selectedCount == 0) {
            this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", R.string.MembersCountZero, LocaleController.formatPluralString("Chats", limit, new Object[0])));
        } else {
            this.actionBar.setSubtitle(String.format(LocaleController.getPluralString("MembersCountSelected", this.selectedCount), Integer.valueOf(this.selectedCount), Integer.valueOf(limit)));
        }
    }

    public void setDelegate(FilterUsersActivityDelegate filterUsersActivityDelegate) {
        this.delegate = filterUsersActivityDelegate;
    }

    /* loaded from: classes4.dex */
    public class GroupCreateAdapter extends RecyclerListView.FastScrollAdapter {
        private Context context;
        private SearchAdapterHelper searchAdapterHelper;
        private Runnable searchRunnable;
        private boolean searching;
        private final int usersStartRow;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private ArrayList<TLObject> contacts = new ArrayList<>();

        public GroupCreateAdapter(Context ctx) {
            FilterUsersActivity.this = this$0;
            this.usersStartRow = this$0.isInclude ? 7 : 5;
            this.context = ctx;
            boolean hasSelf = false;
            ArrayList<TLRPC.Dialog> dialogs = this$0.getMessagesController().getAllDialogs();
            int N = dialogs.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Dialog dialog = dialogs.get(a);
                if (!DialogObject.isEncryptedDialog(dialog.id)) {
                    if (DialogObject.isUserDialog(dialog.id)) {
                        TLRPC.User user = this$0.getMessagesController().getUser(Long.valueOf(dialog.id));
                        if (user != null) {
                            this.contacts.add(user);
                            if (UserObject.isUserSelf(user)) {
                                hasSelf = true;
                            }
                        }
                    } else {
                        TLRPC.Chat chat = this$0.getMessagesController().getChat(Long.valueOf(-dialog.id));
                        if (chat != null) {
                            this.contacts.add(chat);
                        }
                    }
                }
            }
            if (!hasSelf) {
                this.contacts.add(0, this$0.getMessagesController().getUser(Long.valueOf(this$0.getUserConfig().clientUserId)));
            }
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(false);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setAllowGlobalResults(false);
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda4
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
                    FilterUsersActivity.GroupCreateAdapter.this.m3439xa4656924(i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-FilterUsersActivity$GroupCreateAdapter */
        public /* synthetic */ void m3439xa4656924(int searchId) {
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress()) {
                FilterUsersActivity.this.emptyView.showTextView();
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
            return null;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int count;
            if (!this.searching) {
                if (FilterUsersActivity.this.isInclude) {
                    count = 7;
                } else {
                    count = 5;
                }
                return count + this.contacts.size();
            }
            int count2 = this.searchResult.size();
            int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            return count2 + localServerCount + globalCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 1:
                    view = new GroupCreateUserCell(this.context, 1, 0, true);
                    break;
                default:
                    view = new GraySectionCell(this.context);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Object object;
            long id;
            int flag;
            Object object2;
            CharSequence name;
            String objectUserName;
            int index;
            switch (holder.getItemViewType()) {
                case 1:
                    GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
                    CharSequence username = null;
                    CharSequence name2 = null;
                    if (this.searching) {
                        int localCount = this.searchResult.size();
                        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
                        int localServerCount = this.searchAdapterHelper.getLocalServerSearch().size();
                        if (position >= 0 && position < localCount) {
                            object = this.searchResult.get(position);
                        } else if (position >= localCount && position < localServerCount + localCount) {
                            object = this.searchAdapterHelper.getLocalServerSearch().get(position - localCount);
                        } else if (position > localCount + localServerCount && position < globalCount + localCount + localServerCount) {
                            object = this.searchAdapterHelper.getGlobalSearch().get((position - localCount) - localServerCount);
                        } else {
                            object = null;
                        }
                        if (object != null) {
                            if (object instanceof TLRPC.User) {
                                objectUserName = ((TLRPC.User) object).username;
                            } else {
                                objectUserName = ((TLRPC.Chat) object).username;
                            }
                            if (position < localCount) {
                                name2 = this.searchResultNames.get(position);
                                if (name2 != null && !TextUtils.isEmpty(objectUserName)) {
                                    if (name2.toString().startsWith("@" + objectUserName)) {
                                        name2 = null;
                                        username = name2;
                                    }
                                }
                            } else if (position > localCount && !TextUtils.isEmpty(objectUserName)) {
                                String foundUserName = this.searchAdapterHelper.getLastFoundUsername();
                                if (foundUserName.startsWith("@")) {
                                    foundUserName = foundUserName.substring(1);
                                }
                                try {
                                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                    spannableStringBuilder.append((CharSequence) "@");
                                    spannableStringBuilder.append((CharSequence) objectUserName);
                                    int index2 = AndroidUtilities.indexOfIgnoreCase(objectUserName, foundUserName);
                                    if (index2 != -1) {
                                        int len = foundUserName.length();
                                        if (index2 == 0) {
                                            len++;
                                            index = index2;
                                        } else {
                                            index = index2 + 1;
                                        }
                                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), index, index + len, 33);
                                    }
                                    username = spannableStringBuilder;
                                } catch (Exception e) {
                                    username = objectUserName;
                                }
                            }
                        }
                    } else {
                        int i = this.usersStartRow;
                        if (position < i) {
                            if (FilterUsersActivity.this.isInclude) {
                                if (position == 1) {
                                    name = LocaleController.getString("FilterContacts", R.string.FilterContacts);
                                    object2 = "contacts";
                                    flag = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                                } else if (position == 2) {
                                    name = LocaleController.getString("FilterNonContacts", R.string.FilterNonContacts);
                                    object2 = "non_contacts";
                                    flag = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                                } else if (position == 3) {
                                    name = LocaleController.getString("FilterGroups", R.string.FilterGroups);
                                    object2 = "groups";
                                    flag = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                                } else if (position == 4) {
                                    name = LocaleController.getString("FilterChannels", R.string.FilterChannels);
                                    object2 = "channels";
                                    flag = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                                } else {
                                    name = LocaleController.getString("FilterBots", R.string.FilterBots);
                                    object2 = "bots";
                                    flag = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                                }
                            } else if (position == 1) {
                                name = LocaleController.getString("FilterMuted", R.string.FilterMuted);
                                object2 = "muted";
                                flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
                            } else if (position == 2) {
                                name = LocaleController.getString("FilterRead", R.string.FilterRead);
                                object2 = "read";
                                flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
                            } else {
                                name = LocaleController.getString("FilterArchived", R.string.FilterArchived);
                                object2 = "archived";
                                flag = MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
                            }
                            cell.setObject(object2, name, null);
                            cell.setChecked((FilterUsersActivity.this.filterFlags & flag) == flag, false);
                            cell.setCheckBoxEnabled(true);
                            return;
                        }
                        object = this.contacts.get(position - i);
                    }
                    if (object instanceof TLRPC.User) {
                        id = ((TLRPC.User) object).id;
                    } else if (object instanceof TLRPC.Chat) {
                        id = -((TLRPC.Chat) object).id;
                    } else {
                        id = 0;
                    }
                    if (!this.searching) {
                        StringBuilder builder = new StringBuilder();
                        ArrayList<MessagesController.DialogFilter> filters = FilterUsersActivity.this.getMessagesController().dialogFilters;
                        int N = filters.size();
                        for (int a = 0; a < N; a++) {
                            MessagesController.DialogFilter filter = filters.get(a);
                            if (filter.includesDialog(FilterUsersActivity.this.getAccountInstance(), id)) {
                                if (builder.length() > 0) {
                                    builder.append(", ");
                                }
                                builder.append(filter.name);
                            }
                        }
                        username = builder;
                    }
                    cell.setObject(object, name2, username);
                    if (id != 0) {
                        cell.setChecked(FilterUsersActivity.this.selectedContacts.indexOfKey(id) >= 0, false);
                        cell.setCheckBoxEnabled(true);
                        return;
                    }
                    return;
                case 2:
                    GraySectionCell cell2 = (GraySectionCell) holder.itemView;
                    if (position == 0) {
                        cell2.setText(LocaleController.getString("FilterChatTypes", R.string.FilterChatTypes));
                        return;
                    } else {
                        cell2.setText(LocaleController.getString("FilterChats", R.string.FilterChats));
                        return;
                    }
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (this.searching) {
                return 1;
            }
            if (FilterUsersActivity.this.isInclude) {
                if (position == 0 || position == 6) {
                    return 2;
                }
            } else if (position == 0 || position == 4) {
                return 2;
            }
            return 1;
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
            return holder.getItemViewType() == 1;
        }

        public void searchDialogs(final String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults(null);
                this.searchAdapterHelper.queryServerSearch(null, true, true, false, false, false, 0L, false, 0, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    FilterUsersActivity.GroupCreateAdapter.this.m3442x107f0f1e(query);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }

        /* renamed from: lambda$searchDialogs$3$org-telegram-ui-FilterUsersActivity$GroupCreateAdapter */
        public /* synthetic */ void m3442x107f0f1e(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    FilterUsersActivity.GroupCreateAdapter.this.m3441xf48bc3f(query);
                }
            });
        }

        /* renamed from: lambda$searchDialogs$2$org-telegram-ui-FilterUsersActivity$GroupCreateAdapter */
        public /* synthetic */ void m3441xf48bc3f(final String query) {
            this.searchAdapterHelper.queryServerSearch(query, true, true, true, true, false, 0L, false, 0, 0);
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FilterUsersActivity.GroupCreateAdapter.this.m3440xe126960(query);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable);
        }

        /* renamed from: lambda$searchDialogs$1$org-telegram-ui-FilterUsersActivity$GroupCreateAdapter */
        public /* synthetic */ void m3440xe126960(String query) {
            String username;
            String search1;
            String search2;
            String search12 = query.trim().toLowerCase();
            if (search12.length() == 0) {
                updateSearchResults(new ArrayList<>(), new ArrayList<>());
                return;
            }
            String search22 = LocaleController.getInstance().getTranslitString(search12);
            if (search12.equals(search22) || search22.length() == 0) {
                search22 = null;
            }
            char c = 0;
            char c2 = 1;
            String[] search = new String[(search22 != null ? 1 : 0) + 1];
            search[0] = search12;
            if (search22 != null) {
                search[1] = search22;
            }
            ArrayList<Object> resultArray = new ArrayList<>();
            ArrayList<CharSequence> resultArrayNames = new ArrayList<>();
            int a = 0;
            while (a < this.contacts.size()) {
                TLObject object = this.contacts.get(a);
                String[] names = new String[3];
                if (object instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) object;
                    names[c] = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                    username = user.username;
                    if (UserObject.isReplyUser(user)) {
                        names[2] = LocaleController.getString("RepliesTitle", R.string.RepliesTitle).toLowerCase();
                    } else if (user.self) {
                        names[2] = LocaleController.getString("SavedMessages", R.string.SavedMessages).toLowerCase();
                    }
                } else {
                    TLRPC.Chat chat = (TLRPC.Chat) object;
                    names[c] = chat.title.toLowerCase();
                    username = chat.username;
                }
                names[c2] = LocaleController.getInstance().getTranslitString(names[c]);
                if (names[c].equals(names[c2])) {
                    names[c2] = null;
                }
                int found = 0;
                int length = search.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        search1 = search12;
                        search2 = search22;
                        break;
                    }
                    String q = search[i];
                    int i2 = 0;
                    while (i2 < names.length) {
                        String name = names[i2];
                        if (name == null) {
                            search1 = search12;
                            search2 = search22;
                        } else {
                            if (!name.startsWith(q)) {
                                search1 = search12;
                                StringBuilder sb = new StringBuilder();
                                search2 = search22;
                                sb.append(" ");
                                sb.append(q);
                                if (name.contains(sb.toString())) {
                                }
                            } else {
                                search1 = search12;
                                search2 = search22;
                            }
                            found = 1;
                            break;
                        }
                        i2++;
                        search12 = search1;
                        search22 = search2;
                    }
                    search1 = search12;
                    search2 = search22;
                    if (found == 0 && username != null && username.toLowerCase().startsWith(q)) {
                        found = 2;
                    }
                    if (found == 0) {
                        i++;
                        search12 = search1;
                        search22 = search2;
                    } else {
                        if (found == 1) {
                            if (object instanceof TLRPC.User) {
                                TLRPC.User user2 = (TLRPC.User) object;
                                resultArrayNames.add(AndroidUtilities.generateSearchName(user2.first_name, user2.last_name, q));
                            } else {
                                resultArrayNames.add(AndroidUtilities.generateSearchName(((TLRPC.Chat) object).title, null, q));
                            }
                        } else {
                            resultArrayNames.add(AndroidUtilities.generateSearchName("@" + username, null, "@" + q));
                        }
                        resultArray.add(object);
                    }
                }
                a++;
                search12 = search1;
                search22 = search2;
                c = 0;
                c2 = 1;
            }
            updateSearchResults(resultArray, resultArrayNames);
        }

        private void updateSearchResults(final ArrayList<Object> users, final ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FilterUsersActivity$GroupCreateAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    FilterUsersActivity.GroupCreateAdapter.this.m3443xc06098fb(users, names);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-FilterUsersActivity$GroupCreateAdapter */
        public /* synthetic */ void m3443xc06098fb(ArrayList users, ArrayList names) {
            if (!this.searching) {
                return;
            }
            this.searchRunnable = null;
            this.searchResult = users;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(users);
            if (this.searching && !this.searchAdapterHelper.isSearchInProgress()) {
                FilterUsersActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.FilterUsersActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                FilterUsersActivity.this.m3438xc74b04bb();
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
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
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
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$3$org-telegram-ui-FilterUsersActivity */
    public /* synthetic */ void m3438xc74b04bb() {
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
