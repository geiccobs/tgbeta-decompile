package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.GroupCallTextCell;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UsersAlertBase;
/* loaded from: classes5.dex */
public class UsersAlertBase extends BottomSheet {
    public static final Property<UsersAlertBase, Float> COLOR_PROGRESS = new AnimationProperties.FloatProperty<UsersAlertBase>("colorProgress") { // from class: org.telegram.ui.Components.UsersAlertBase.3
        public void setValue(UsersAlertBase object, float value) {
            object.setColorProgress(value);
        }

        public Float get(UsersAlertBase object) {
            return Float.valueOf(object.getColorProgress());
        }
    };
    private int backgroundColor;
    private float colorProgress;
    protected StickerEmptyView emptyView;
    protected FlickerLoadingView flickerLoadingView;
    protected FrameLayout frameLayout;
    protected final FillLastLinearLayoutManager layoutManager;
    protected RecyclerListView listView;
    protected RecyclerView.Adapter listViewAdapter;
    protected int scrollOffsetY;
    protected RecyclerView.Adapter searchListViewAdapter;
    protected SearchField searchView;
    protected View shadow;
    protected AnimatorSet shadowAnimation;
    protected Drawable shadowDrawable;
    private RectF rect = new RectF();
    protected boolean needSnapToTop = true;
    protected boolean isEmptyViewVisible = true;
    protected String keyScrollUp = Theme.key_sheet_scrollUp;
    protected String keyListSelector = Theme.key_listSelector;
    protected String keySearchBackground = Theme.key_dialogSearchBackground;
    protected String keyInviteMembersBackground = Theme.key_windowBackgroundWhite;
    protected String keyListViewBackground = Theme.key_windowBackgroundWhite;
    protected String keyActionBarUnscrolled = Theme.key_windowBackgroundWhite;
    protected String keyNameText = Theme.key_windowBackgroundWhiteBlackText;
    protected String keyLastSeenText = Theme.key_windowBackgroundWhiteGrayText;
    protected String keyLastSeenTextUnscrolled = Theme.key_windowBackgroundWhiteGrayText;
    protected String keySearchPlaceholder = Theme.key_dialogSearchHint;
    protected String keySearchText = Theme.key_dialogSearchText;
    protected String keySearchIcon = Theme.key_dialogSearchIcon;
    protected String keySearchIconUnscrolled = Theme.key_dialogSearchIcon;

    public UsersAlertBase(Context context, boolean needFocus, int account, Theme.ResourcesProvider resourcesProvider) {
        super(context, needFocus, resourcesProvider);
        updateColorKeys();
        setDimBehindAlpha(75);
        this.currentAccount = account;
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.containerView = createContainerView(context);
        this.containerView.setWillNotDraw(false);
        this.containerView.setClipChildren(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        this.frameLayout = new FrameLayout(context);
        SearchField searchField = new SearchField(context);
        this.searchView = searchField;
        this.frameLayout.addView(searchField, LayoutHelper.createFrame(-1, -1, 51));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setViewType(6);
        this.flickerLoadingView.showDate(false);
        this.flickerLoadingView.setUseHeaderOffset(true);
        this.flickerLoadingView.setColors(this.keyInviteMembersBackground, this.keySearchBackground, this.keyActionBarUnscrolled);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, this.flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.addView(this.flickerLoadingView, 0, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 2.0f, 0.0f, 0.0f));
        this.emptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
        this.emptyView.setVisibility(8);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.showProgress(true, false);
        this.emptyView.setColors(this.keyNameText, this.keyLastSeenText, this.keyInviteMembersBackground, this.keySearchBackground);
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 62.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.UsersAlertBase.1
            @Override // org.telegram.ui.Components.RecyclerListView
            protected boolean allowSelectChildAtPosition(float x, float y) {
                return UsersAlertBase.this.isAllowSelectChildAtPosition(x, y);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                int[] ii = new int[2];
                getLocationInWindow(ii);
            }

            @Override // org.telegram.ui.Components.RecyclerListView
            public boolean emptyViewIsVisible() {
                return getAdapter() != null && UsersAlertBase.this.isEmptyViewVisible && getAdapter().getItemCount() <= 2;
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setTag(13);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHideIfEmpty(false);
        this.listView.setSelectorDrawableColor(Theme.getColor(this.keyListSelector));
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(getContext(), 1, false, AndroidUtilities.dp(8.0f), this.listView);
        this.layoutManager = fillLastLinearLayoutManager;
        fillLastLinearLayoutManager.setBind(false);
        this.listView.setLayoutManager(fillLastLinearLayoutManager);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.UsersAlertBase.2
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                UsersAlertBase.this.updateLayout();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                RecyclerListView.Holder holder;
                if (newState == 0 && UsersAlertBase.this.needSnapToTop && UsersAlertBase.this.scrollOffsetY + UsersAlertBase.this.backgroundPaddingTop + AndroidUtilities.dp(13.0f) < AndroidUtilities.statusBarHeight * 2 && UsersAlertBase.this.listView.canScrollVertically(1) && (holder = (RecyclerListView.Holder) UsersAlertBase.this.listView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > 0) {
                    UsersAlertBase.this.listView.smoothScrollBy(0, holder.itemView.getTop());
                }
            }
        });
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        frameLayoutParams.topMargin = AndroidUtilities.dp(58.0f);
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        this.containerView.addView(this.shadow, frameLayoutParams);
        this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        setColorProgress(0.0f);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AndroidUtilities.statusBarHeight = AndroidUtilities.getStatusBarHeight(getContext());
    }

    protected ContainerView createContainerView(Context context) {
        return new ContainerView(context);
    }

    protected boolean isAllowSelectChildAtPosition(float x, float y) {
        return y >= ((float) (AndroidUtilities.dp(58.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
    }

    protected void updateColorKeys() {
    }

    /* loaded from: classes5.dex */
    public class SearchField extends FrameLayout {
        private final ImageView clearSearchImageView;
        private final CloseProgressDrawable2 progressDrawable;
        private final View searchBackground;
        protected EditTextBoldCursor searchEditText;
        private final ImageView searchIconImageView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SearchField(Context context) {
            super(context);
            UsersAlertBase.this = this$0;
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor(this$0.keySearchBackground)));
            addView(view, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(R.drawable.smiles_inputsearch);
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(this$0.keySearchPlaceholder), PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2() { // from class: org.telegram.ui.Components.UsersAlertBase.SearchField.1
                @Override // org.telegram.ui.Components.CloseProgressDrawable2
                protected int getCurrentColor() {
                    return Theme.getColor(UsersAlertBase.this.keySearchPlaceholder);
                }
            };
            this.progressDrawable = closeProgressDrawable2;
            imageView2.setImageDrawable(closeProgressDrawable2);
            closeProgressDrawable2.setSide(AndroidUtilities.dp(7.0f));
            imageView2.setScaleX(0.1f);
            imageView2.setScaleY(0.1f);
            imageView2.setAlpha(0.0f);
            addView(imageView2, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            imageView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.UsersAlertBase$SearchField$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    UsersAlertBase.SearchField.this.m3195x1c6cec7f(view2);
                }
            });
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) { // from class: org.telegram.ui.Components.UsersAlertBase.SearchField.2
                @Override // org.telegram.ui.Components.EditTextEffects, android.view.View
                public boolean dispatchTouchEvent(MotionEvent event) {
                    MotionEvent e = MotionEvent.obtain(event);
                    e.setLocation(e.getRawX(), e.getRawY() - UsersAlertBase.this.containerView.getTranslationY());
                    if (e.getAction() == 1) {
                        e.setAction(3);
                    }
                    UsersAlertBase.this.listView.dispatchTouchEvent(e);
                    e.recycle();
                    return super.dispatchTouchEvent(event);
                }
            };
            this.searchEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor(this$0.keySearchPlaceholder));
            this.searchEditText.setTextColor(Theme.getColor(this$0.keySearchText));
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(268435459);
            this.searchEditText.setHint(LocaleController.getString("VoipGroupSearchMembers", R.string.VoipGroupSearchMembers));
            this.searchEditText.setCursorColor(Theme.getColor(this$0.keySearchText));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.UsersAlertBase.SearchField.3
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    boolean show = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    boolean showed = SearchField.this.clearSearchImageView.getAlpha() != 0.0f;
                    if (show != showed) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (show) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150L).scaleX(show ? 1.0f : 0.1f);
                        if (!show) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    String text = SearchField.this.searchEditText.getText().toString();
                    int oldItemsCount = UsersAlertBase.this.listView.getAdapter() == null ? 0 : UsersAlertBase.this.listView.getAdapter().getItemCount();
                    UsersAlertBase.this.search(text);
                    if (TextUtils.isEmpty(text) && UsersAlertBase.this.listView != null && UsersAlertBase.this.listView.getAdapter() != UsersAlertBase.this.listViewAdapter) {
                        UsersAlertBase.this.listView.setAnimateEmptyView(false, 0);
                        UsersAlertBase.this.listView.setAdapter(UsersAlertBase.this.listViewAdapter);
                        UsersAlertBase.this.listView.setAnimateEmptyView(true, 0);
                        if (oldItemsCount == 0) {
                            UsersAlertBase.this.showItemsAnimated(0);
                        }
                    }
                    UsersAlertBase.this.flickerLoadingView.setVisibility(0);
                }
            });
            this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.UsersAlertBase$SearchField$$ExternalSyntheticLambda1
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return UsersAlertBase.SearchField.this.m3196x45c141c0(textView, i, keyEvent);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-UsersAlertBase$SearchField */
        public /* synthetic */ void m3195x1c6cec7f(View v) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Components-UsersAlertBase$SearchField */
        public /* synthetic */ boolean m3196x45c141c0(TextView v, int actionId, KeyEvent event) {
            if (event != null) {
                if ((event.getAction() == 1 && event.getKeyCode() == 84) || (event.getAction() == 0 && event.getKeyCode() == 66)) {
                    AndroidUtilities.hideKeyboard(this.searchEditText);
                    return false;
                }
                return false;
            }
            return false;
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            UsersAlertBase.this.onSearchViewTouched(ev, this.searchEditText);
            return super.onInterceptTouchEvent(ev);
        }

        public void closeSearch() {
            this.clearSearchImageView.callOnClick();
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }
    }

    protected void onSearchViewTouched(MotionEvent ev, EditTextBoldCursor searchEditText) {
    }

    public void search(String text) {
    }

    public float getColorProgress() {
        return this.colorProgress;
    }

    public void setColorProgress(float progress) {
        this.colorProgress = progress;
        this.backgroundColor = AndroidUtilities.getOffsetColor(Theme.getColor(this.keyInviteMembersBackground), Theme.getColor(this.keyListViewBackground), progress, 1.0f);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
        this.frameLayout.setBackgroundColor(this.backgroundColor);
        this.navBarColor = this.backgroundColor;
        this.listView.setGlowColor(this.backgroundColor);
        int color = AndroidUtilities.getOffsetColor(Theme.getColor(this.keyLastSeenTextUnscrolled), Theme.getColor(this.keyLastSeenText), progress, 1.0f);
        int color2 = AndroidUtilities.getOffsetColor(Theme.getColor(this.keySearchIconUnscrolled), Theme.getColor(this.keySearchIcon), progress, 1.0f);
        int N = this.listView.getChildCount();
        for (int a = 0; a < N; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof GroupCallTextCell) {
                GroupCallTextCell cell = (GroupCallTextCell) child;
                cell.setColors(color, color);
            } else if (child instanceof GroupCallUserCell) {
                GroupCallUserCell cell2 = (GroupCallUserCell) child;
                cell2.setGrayIconColor(this.shadow.getTag() != null ? this.keySearchIcon : this.keySearchIconUnscrolled, color2);
            }
        }
        this.containerView.invalidate();
        this.listView.invalidate();
        this.container.invalidate();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        AndroidUtilities.hideKeyboard(this.searchView.searchEditText);
        super.dismiss();
    }

    public void updateLayout() {
        int top;
        if (this.listView.getChildCount() <= 0) {
            return;
        }
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(0);
        if (holder != null) {
            top = holder.itemView.getTop() - AndroidUtilities.dp(8.0f);
        } else {
            top = 0;
        }
        int newOffset = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
            runShadowAnimation(false);
        } else {
            runShadowAnimation(true);
        }
        if (this.scrollOffsetY != newOffset) {
            this.scrollOffsetY = newOffset;
            setTranslationY(newOffset);
        }
    }

    public void setTranslationY(int newOffset) {
        this.listView.setTopGlowOffset(newOffset);
        this.frameLayout.setTranslationY(newOffset);
        this.emptyView.setTranslationY(newOffset);
        this.containerView.invalidate();
    }

    private void runShadowAnimation(final boolean show) {
        if ((show && this.shadow.getTag() != null) || (!show && this.shadow.getTag() == null)) {
            this.shadow.setTag(show ? null : 1);
            if (show) {
                this.shadow.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.shadowAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150L);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.UsersAlertBase.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (UsersAlertBase.this.shadowAnimation != null && UsersAlertBase.this.shadowAnimation.equals(animation)) {
                        if (!show) {
                            UsersAlertBase.this.shadow.setVisibility(4);
                        }
                        UsersAlertBase.this.shadowAnimation = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (UsersAlertBase.this.shadowAnimation != null && UsersAlertBase.this.shadowAnimation.equals(animation)) {
                        UsersAlertBase.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    public void showItemsAnimated(final int from) {
        if (!isShowing()) {
            return;
        }
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.Components.UsersAlertBase.5
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                UsersAlertBase.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int n = UsersAlertBase.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i = 0; i < n; i++) {
                    View child = UsersAlertBase.this.listView.getChildAt(i);
                    int position = UsersAlertBase.this.listView.getChildAdapterPosition(child);
                    if (position >= from) {
                        if (position == 1 && UsersAlertBase.this.listView.getAdapter() == UsersAlertBase.this.searchListViewAdapter && (child instanceof GraySectionCell)) {
                            child = ((GraySectionCell) child).getTextView();
                        }
                        child.setAlpha(0.0f);
                        int s = Math.min(UsersAlertBase.this.listView.getMeasuredHeight(), Math.max(0, child.getTop()));
                        int delay = (int) ((s / UsersAlertBase.this.listView.getMeasuredHeight()) * 100.0f);
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

    /* loaded from: classes5.dex */
    public class ContainerView extends FrameLayout {
        private boolean ignoreLayout = false;
        float snapToTopOffset;
        ValueAnimator valueAnimator;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ContainerView(Context context) {
            super(context);
            UsersAlertBase.this = this$0;
        }

        @Override // android.view.View
        public void setTranslationY(float translationY) {
            super.setTranslationY(translationY);
            invalidate();
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int padding;
            int totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            if (Build.VERSION.SDK_INT >= 21) {
                this.ignoreLayout = true;
                setPadding(UsersAlertBase.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, UsersAlertBase.this.backgroundPaddingLeft, 0);
                this.ignoreLayout = false;
            }
            int availableHeight = totalHeight - getPaddingTop();
            if (UsersAlertBase.this.keyboardVisible) {
                padding = AndroidUtilities.dp(8.0f);
                UsersAlertBase.this.setAllowNestedScroll(false);
                if (UsersAlertBase.this.scrollOffsetY != 0) {
                    float f = UsersAlertBase.this.scrollOffsetY;
                    this.snapToTopOffset = f;
                    setTranslationY(f);
                    ValueAnimator valueAnimator = this.valueAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllListeners();
                        this.valueAnimator.cancel();
                    }
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(this.snapToTopOffset, 0.0f);
                    this.valueAnimator = ofFloat;
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.UsersAlertBase$ContainerView$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            UsersAlertBase.ContainerView.this.m3194x41a7edb2(valueAnimator2);
                        }
                    });
                    this.valueAnimator.setDuration(250L);
                    this.valueAnimator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                    this.valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.UsersAlertBase.ContainerView.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            ContainerView.this.snapToTopOffset = 0.0f;
                            ContainerView.this.setTranslationY(0.0f);
                            ContainerView.this.valueAnimator = null;
                        }
                    });
                    this.valueAnimator.start();
                } else if (this.valueAnimator != null) {
                    setTranslationY(this.snapToTopOffset);
                }
            } else {
                padding = (availableHeight - ((availableHeight / 5) * 3)) + AndroidUtilities.dp(8.0f);
                UsersAlertBase.this.setAllowNestedScroll(true);
            }
            if (UsersAlertBase.this.listView.getPaddingTop() != padding) {
                this.ignoreLayout = true;
                UsersAlertBase.this.listView.setPadding(0, padding, 0, 0);
                this.ignoreLayout = false;
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, C.BUFFER_FLAG_ENCRYPTED));
        }

        /* renamed from: lambda$onMeasure$0$org-telegram-ui-Components-UsersAlertBase$ContainerView */
        public /* synthetic */ void m3194x41a7edb2(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.snapToTopOffset = floatValue;
            setTranslationY(floatValue);
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            UsersAlertBase.this.updateLayout();
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() == 0 && ev.getY() < UsersAlertBase.this.scrollOffsetY) {
                UsersAlertBase.this.dismiss();
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent e) {
            return !UsersAlertBase.this.isDismissed() && super.onTouchEvent(e);
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
            canvas.save();
            int y = (UsersAlertBase.this.scrollOffsetY - UsersAlertBase.this.backgroundPaddingTop) + AndroidUtilities.dp(6.0f);
            int top = (UsersAlertBase.this.scrollOffsetY - UsersAlertBase.this.backgroundPaddingTop) - AndroidUtilities.dp(13.0f);
            int height = getMeasuredHeight() + AndroidUtilities.dp(50.0f) + UsersAlertBase.this.backgroundPaddingTop;
            int statusBarHeight = 0;
            float radProgress = 1.0f;
            if (Build.VERSION.SDK_INT >= 21) {
                top += AndroidUtilities.statusBarHeight;
                y += AndroidUtilities.statusBarHeight;
                height -= AndroidUtilities.statusBarHeight;
                if (UsersAlertBase.this.backgroundPaddingTop + top + getTranslationY() < AndroidUtilities.statusBarHeight * 2) {
                    int diff = (int) Math.min(AndroidUtilities.statusBarHeight, (((AndroidUtilities.statusBarHeight * 2) - top) - UsersAlertBase.this.backgroundPaddingTop) - getTranslationY());
                    top -= diff;
                    height += diff;
                    radProgress = 1.0f - Math.min(1.0f, (diff * 2) / AndroidUtilities.statusBarHeight);
                }
                if (UsersAlertBase.this.backgroundPaddingTop + top + getTranslationY() < AndroidUtilities.statusBarHeight) {
                    statusBarHeight = (int) Math.min(AndroidUtilities.statusBarHeight, ((AndroidUtilities.statusBarHeight - top) - UsersAlertBase.this.backgroundPaddingTop) - getTranslationY());
                }
            }
            UsersAlertBase.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
            UsersAlertBase.this.shadowDrawable.draw(canvas);
            if (radProgress != 1.0f) {
                Theme.dialogs_onlineCirclePaint.setColor(UsersAlertBase.this.backgroundColor);
                UsersAlertBase.this.rect.set(UsersAlertBase.this.backgroundPaddingLeft, UsersAlertBase.this.backgroundPaddingTop + top, getMeasuredWidth() - UsersAlertBase.this.backgroundPaddingLeft, UsersAlertBase.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                canvas.drawRoundRect(UsersAlertBase.this.rect, AndroidUtilities.dp(12.0f) * radProgress, AndroidUtilities.dp(12.0f) * radProgress, Theme.dialogs_onlineCirclePaint);
            }
            int w = AndroidUtilities.dp(36.0f);
            UsersAlertBase.this.rect.set((getMeasuredWidth() - w) / 2, y, (getMeasuredWidth() + w) / 2, AndroidUtilities.dp(4.0f) + y);
            Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(UsersAlertBase.this.keyScrollUp));
            canvas.drawRoundRect(UsersAlertBase.this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
            if (statusBarHeight > 0) {
                int finalColor = Color.argb(255, (int) (Color.red(UsersAlertBase.this.backgroundColor) * 0.8f), (int) (Color.green(UsersAlertBase.this.backgroundColor) * 0.8f), (int) (Color.blue(UsersAlertBase.this.backgroundColor) * 0.8f));
                Theme.dialogs_onlineCirclePaint.setColor(finalColor);
                canvas.drawRect(UsersAlertBase.this.backgroundPaddingLeft, (AndroidUtilities.statusBarHeight - statusBarHeight) - getTranslationY(), getMeasuredWidth() - UsersAlertBase.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight - getTranslationY(), Theme.dialogs_onlineCirclePaint);
            }
            canvas.restore();
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0, getPaddingTop(), getMeasuredWidth(), getMeasuredHeight());
            super.dispatchDraw(canvas);
            canvas.restore();
        }
    }
}
