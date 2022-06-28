package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PopupSwipeBackLayout;
/* loaded from: classes4.dex */
public class ActionBarPopupWindow extends PopupWindow {
    private static final ViewTreeObserver.OnScrollChangedListener NOP;
    private static final boolean allowAnimation;
    private static DecelerateInterpolator decelerateInterpolator;
    private static Method layoutInScreenMethod;
    private static final Field superListenerField;
    private boolean isClosingAnimated;
    private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
    private ViewTreeObserver mViewTreeObserver;
    private boolean pauseNotifications;
    private boolean scaleOut;
    private AnimatorSet windowAnimatorSet;
    private boolean animationEnabled = allowAnimation;
    private int dismissAnimationDuration = 150;
    private int currentAccount = UserConfig.selectedAccount;
    private long outEmptyTime = -1;
    private int popupAnimationIndex = -1;

    /* loaded from: classes4.dex */
    public interface OnDispatchKeyEventListener {
        void onDispatchKeyEvent(KeyEvent keyEvent);
    }

    /* loaded from: classes4.dex */
    public interface onSizeChangedListener {
        void onSizeChanged();
    }

    static {
        allowAnimation = Build.VERSION.SDK_INT >= 18;
        decelerateInterpolator = new DecelerateInterpolator();
        Field f = null;
        try {
            f = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        superListenerField = f;
        NOP = ActionBarPopupWindow$$ExternalSyntheticLambda0.INSTANCE;
    }

    public static /* synthetic */ void lambda$static$0() {
    }

    public void setScaleOut(boolean b) {
        this.scaleOut = b;
    }

    /* loaded from: classes4.dex */
    public static class ActionBarPopupWindowLayout extends FrameLayout {
        public static final int FLAG_SHOWN_FROM_BOTTOM = 2;
        public static final int FLAG_USE_SWIPEBACK = 1;
        private boolean animationEnabled;
        private int backAlpha;
        private float backScaleX;
        private float backScaleY;
        private int backgroundColor;
        protected Drawable backgroundDrawable;
        private Rect bgPaddings;
        private boolean fitItems;
        private int gapEndY;
        private int gapStartY;
        private ArrayList<AnimatorSet> itemAnimators;
        private int lastStartedChild;
        protected LinearLayout linearLayout;
        private OnDispatchKeyEventListener mOnDispatchKeyEventListener;
        private onSizeChangedListener onSizeChangedListener;
        private HashMap<View, Integer> positions;
        private final Theme.ResourcesProvider resourcesProvider;
        private ScrollView scrollView;
        private boolean shownFromBottom;
        private boolean startAnimationPending;
        public int subtractBackgroundHeight;
        public boolean swipeBackGravityRight;
        private PopupSwipeBackLayout swipeBackLayout;
        private View topView;
        public boolean updateAnimation;

        public ActionBarPopupWindowLayout(Context context) {
            this(context, null);
        }

        public ActionBarPopupWindowLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            this(context, R.drawable.popup_fixed_alert2, resourcesProvider);
        }

        public ActionBarPopupWindowLayout(Context context, int resId, Theme.ResourcesProvider resourcesProvider) {
            this(context, resId, resourcesProvider, 0);
        }

        public ActionBarPopupWindowLayout(Context context, int resId, Theme.ResourcesProvider resourcesProvider, int flags) {
            super(context);
            this.backScaleX = 1.0f;
            this.backScaleY = 1.0f;
            this.startAnimationPending = false;
            this.backAlpha = 255;
            this.lastStartedChild = 0;
            this.animationEnabled = ActionBarPopupWindow.allowAnimation;
            this.positions = new HashMap<>();
            this.gapStartY = -1000000;
            this.gapEndY = -1000000;
            this.bgPaddings = new Rect();
            this.backgroundColor = -1;
            this.resourcesProvider = resourcesProvider;
            if (resId != 0) {
                this.backgroundDrawable = getResources().getDrawable(resId).mutate();
                setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            }
            Drawable drawable = this.backgroundDrawable;
            if (drawable != null) {
                drawable.getPadding(this.bgPaddings);
                setBackgroundColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
            }
            setWillNotDraw(false);
            if ((flags & 2) > 0) {
                this.shownFromBottom = true;
            }
            if ((flags & 1) > 0) {
                PopupSwipeBackLayout popupSwipeBackLayout = new PopupSwipeBackLayout(context, resourcesProvider);
                this.swipeBackLayout = popupSwipeBackLayout;
                addView(popupSwipeBackLayout, LayoutHelper.createFrame(-2, -2.0f));
            }
            int i = 80;
            try {
                ScrollView scrollView = new ScrollView(context);
                this.scrollView = scrollView;
                scrollView.setVerticalScrollBarEnabled(false);
                PopupSwipeBackLayout popupSwipeBackLayout2 = this.swipeBackLayout;
                if (popupSwipeBackLayout2 != null) {
                    popupSwipeBackLayout2.addView(this.scrollView, LayoutHelper.createFrame(-2, -2, this.shownFromBottom ? 80 : 48));
                } else {
                    addView(this.scrollView, LayoutHelper.createFrame(-2, -2.0f));
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout.1
                @Override // android.widget.LinearLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    if (ActionBarPopupWindowLayout.this.fitItems) {
                        ActionBarPopupWindowLayout.this.gapStartY = -1000000;
                        ActionBarPopupWindowLayout.this.gapEndY = -1000000;
                        int N = getChildCount();
                        int maxWidth = 0;
                        int fixWidth = 0;
                        ArrayList<View> viewsToFix = null;
                        for (int a = 0; a < N; a++) {
                            View view = getChildAt(a);
                            if (view.getVisibility() != 8) {
                                Object tag = view.getTag(R.id.width_tag);
                                Object tag2 = view.getTag(R.id.object_tag);
                                Object fitToWidth = view.getTag(R.id.fit_width_tag);
                                if (tag != null) {
                                    view.getLayoutParams().width = -2;
                                }
                                measureChildWithMargins(view, widthMeasureSpec, 0, heightMeasureSpec, 0);
                                if (fitToWidth == null) {
                                    if (!(tag instanceof Integer) && tag2 == null) {
                                        maxWidth = Math.max(maxWidth, view.getMeasuredWidth());
                                    } else if (tag instanceof Integer) {
                                        fixWidth = Math.max(((Integer) tag).intValue(), view.getMeasuredWidth());
                                        ActionBarPopupWindowLayout.this.gapStartY = view.getMeasuredHeight();
                                        ActionBarPopupWindowLayout actionBarPopupWindowLayout = ActionBarPopupWindowLayout.this;
                                        actionBarPopupWindowLayout.gapEndY = actionBarPopupWindowLayout.gapStartY + AndroidUtilities.dp(6.0f);
                                    }
                                }
                                if (viewsToFix == null) {
                                    viewsToFix = new ArrayList<>();
                                }
                                viewsToFix.add(view);
                            }
                        }
                        if (viewsToFix != null) {
                            int N2 = viewsToFix.size();
                            for (int a2 = 0; a2 < N2; a2++) {
                                viewsToFix.get(a2).getLayoutParams().width = Math.max(maxWidth, fixWidth);
                            }
                        }
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                @Override // android.view.ViewGroup
                protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    if (child instanceof GapView) {
                        return false;
                    }
                    return super.drawChild(canvas, child, drawingTime);
                }
            };
            this.linearLayout = linearLayout;
            linearLayout.setOrientation(1);
            ScrollView scrollView2 = this.scrollView;
            if (scrollView2 != null) {
                scrollView2.addView(this.linearLayout, new FrameLayout.LayoutParams(-2, -2));
                return;
            }
            PopupSwipeBackLayout popupSwipeBackLayout3 = this.swipeBackLayout;
            if (popupSwipeBackLayout3 != null) {
                popupSwipeBackLayout3.addView(this.linearLayout, LayoutHelper.createFrame(-2, -2, !this.shownFromBottom ? 48 : i));
            } else {
                addView(this.linearLayout, LayoutHelper.createFrame(-2, -2.0f));
            }
        }

        public PopupSwipeBackLayout getSwipeBack() {
            return this.swipeBackLayout;
        }

        public int addViewToSwipeBack(View v) {
            this.swipeBackLayout.addView(v, LayoutHelper.createFrame(-2, -2, this.shownFromBottom ? 80 : 48));
            return this.swipeBackLayout.getChildCount() - 1;
        }

        public void setFitItems(boolean value) {
            this.fitItems = value;
        }

        public void setShownFromBottom(boolean value) {
            this.shownFromBottom = value;
        }

        public void setDispatchKeyEventListener(OnDispatchKeyEventListener listener) {
            this.mOnDispatchKeyEventListener = listener;
        }

        public int getBackgroundColor() {
            return this.backgroundColor;
        }

        @Override // android.view.View
        public void setBackgroundColor(int color) {
            Drawable drawable;
            if (this.backgroundColor != color && (drawable = this.backgroundDrawable) != null) {
                this.backgroundColor = color;
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            }
        }

        public void setBackAlpha(int value) {
            this.backAlpha = value;
        }

        public int getBackAlpha() {
            return this.backAlpha;
        }

        public void setBackScaleX(float value) {
            if (this.backScaleX != value) {
                this.backScaleX = value;
                invalidate();
                onSizeChangedListener onsizechangedlistener = this.onSizeChangedListener;
                if (onsizechangedlistener != null) {
                    onsizechangedlistener.onSizeChanged();
                }
            }
        }

        public void translateChildrenAfter(int index, float ty) {
            this.subtractBackgroundHeight = (int) (-ty);
            for (int i = index + 1; i < this.linearLayout.getChildCount(); i++) {
                View child = this.linearLayout.getChildAt(i);
                if (child != null) {
                    child.setTranslationY(ty);
                }
            }
        }

        public void setBackScaleY(float value) {
            if (this.backScaleY != value) {
                this.backScaleY = value;
                if (this.animationEnabled && this.updateAnimation) {
                    int height = getMeasuredHeight() - AndroidUtilities.dp(16.0f);
                    if (this.shownFromBottom) {
                        for (int a = this.lastStartedChild; a >= 0; a--) {
                            View child = getItemAt(a);
                            if (child.getVisibility() == 0 && !(child instanceof GapView)) {
                                Integer position = this.positions.get(child);
                                if (position != null && height - ((position.intValue() * AndroidUtilities.dp(48.0f)) + AndroidUtilities.dp(32.0f)) > height * value) {
                                    break;
                                }
                                this.lastStartedChild = a - 1;
                                startChildAnimation(child);
                            }
                        }
                    } else {
                        int count = getItemsCount();
                        int h = 0;
                        for (int a2 = 0; a2 < count; a2++) {
                            View child2 = getItemAt(a2);
                            if (child2.getVisibility() == 0) {
                                h += child2.getMeasuredHeight();
                                if (a2 < this.lastStartedChild) {
                                    continue;
                                } else if (this.positions.get(child2) != null && h - AndroidUtilities.dp(24.0f) > height * value) {
                                    break;
                                } else {
                                    this.lastStartedChild = a2 + 1;
                                    startChildAnimation(child2);
                                }
                            }
                        }
                    }
                }
                invalidate();
                onSizeChangedListener onsizechangedlistener = this.onSizeChangedListener;
                if (onsizechangedlistener != null) {
                    onsizechangedlistener.onSizeChanged();
                }
            }
        }

        @Override // android.view.View
        public void setBackgroundDrawable(Drawable drawable) {
            this.backgroundColor = -1;
            this.backgroundDrawable = drawable;
            if (drawable != null) {
                drawable.getPadding(this.bgPaddings);
            }
        }

        private void startChildAnimation(View child) {
            if (this.animationEnabled) {
                final AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                Property property = View.ALPHA;
                float[] fArr = new float[2];
                fArr[0] = 0.0f;
                fArr[1] = child.isEnabled() ? 1.0f : 0.5f;
                animatorArr[0] = ObjectAnimator.ofFloat(child, property, fArr);
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[2];
                fArr2[0] = AndroidUtilities.dp(this.shownFromBottom ? 6.0f : -6.0f);
                fArr2[1] = 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(child, property2, fArr2);
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(180L);
                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        ActionBarPopupWindowLayout.this.itemAnimators.remove(animatorSet);
                    }
                });
                animatorSet.setInterpolator(ActionBarPopupWindow.decelerateInterpolator);
                animatorSet.start();
                if (this.itemAnimators == null) {
                    this.itemAnimators = new ArrayList<>();
                }
                this.itemAnimators.add(animatorSet);
            }
        }

        public void setAnimationEnabled(boolean value) {
            this.animationEnabled = value;
        }

        @Override // android.view.ViewGroup
        public void addView(View child) {
            this.linearLayout.addView(child);
        }

        public void addView(View child, LinearLayout.LayoutParams layoutParams) {
            this.linearLayout.addView(child, layoutParams);
        }

        public int getViewsCount() {
            return this.linearLayout.getChildCount();
        }

        public int precalculateHeight() {
            int MOST_SPEC = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
            this.linearLayout.measure(MOST_SPEC, MOST_SPEC);
            return this.linearLayout.getMeasuredHeight();
        }

        public void removeInnerViews() {
            this.linearLayout.removeAllViews();
        }

        public float getBackScaleX() {
            return this.backScaleX;
        }

        public float getBackScaleY() {
            return this.backScaleY;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent event) {
            OnDispatchKeyEventListener onDispatchKeyEventListener = this.mOnDispatchKeyEventListener;
            if (onDispatchKeyEventListener != null) {
                onDispatchKeyEventListener.onDispatchKeyEvent(event);
            }
            return super.dispatchKeyEvent(event);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            if (this.swipeBackGravityRight) {
                setTranslationX(getMeasuredWidth() * (1.0f - this.backScaleX));
                View view = this.topView;
                if (view != null) {
                    view.setTranslationX(getMeasuredWidth() * (1.0f - this.backScaleX));
                    this.topView.setAlpha(1.0f - this.swipeBackLayout.transitionProgress);
                    float h = this.topView.getMeasuredHeight() - AndroidUtilities.dp(16.0f);
                    float yOffset = (-h) * this.swipeBackLayout.transitionProgress;
                    this.topView.setTranslationY(yOffset);
                    setTranslationY(yOffset);
                }
            }
            super.dispatchDraw(canvas);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            boolean hasGap;
            int i;
            if (this.backgroundDrawable != null) {
                int start = this.gapStartY - this.scrollView.getScrollY();
                int end = this.gapEndY - this.scrollView.getScrollY();
                int i2 = 0;
                while (true) {
                    if (i2 >= this.linearLayout.getChildCount()) {
                        hasGap = false;
                        break;
                    } else if (!(this.linearLayout.getChildAt(i2) instanceof GapView) || this.linearLayout.getChildAt(i2).getVisibility() != 0) {
                        i2++;
                    } else {
                        hasGap = true;
                        break;
                    }
                }
                for (int a = 0; a < 2; a++) {
                    if (a != 1 || start >= (-AndroidUtilities.dp(16.0f))) {
                        boolean needRestore = false;
                        boolean applyAlpha = true;
                        if (!hasGap || this.backAlpha == 255) {
                            i = -1000000;
                            if (this.gapStartY != -1000000) {
                                needRestore = true;
                                canvas.save();
                                canvas.clipRect(0, this.bgPaddings.top, getMeasuredWidth(), getMeasuredHeight());
                            }
                        } else {
                            i = -1000000;
                            canvas.saveLayerAlpha(0.0f, this.bgPaddings.top, getMeasuredWidth(), getMeasuredHeight(), this.backAlpha, 31);
                            needRestore = true;
                            applyAlpha = false;
                        }
                        this.backgroundDrawable.setAlpha(applyAlpha ? this.backAlpha : 255);
                        if (this.shownFromBottom) {
                            int height = getMeasuredHeight();
                            this.backgroundDrawable.setBounds(0, (int) (height * (1.0f - this.backScaleY)), (int) (getMeasuredWidth() * this.backScaleX), height);
                        } else if (start > (-AndroidUtilities.dp(16.0f))) {
                            int h = (int) (getMeasuredHeight() * this.backScaleY);
                            if (a == 0) {
                                this.backgroundDrawable.setBounds(0, (-this.scrollView.getScrollY()) + (this.gapStartY != i ? AndroidUtilities.dp(1.0f) : 0), (int) (getMeasuredWidth() * this.backScaleX), (this.gapStartY != i ? Math.min(h, AndroidUtilities.dp(16.0f) + start) : h) - this.subtractBackgroundHeight);
                            } else if (h >= end) {
                                this.backgroundDrawable.setBounds(0, end, (int) (getMeasuredWidth() * this.backScaleX), h - this.subtractBackgroundHeight);
                            } else {
                                if (this.gapStartY != i) {
                                    canvas.restore();
                                }
                            }
                        } else {
                            this.backgroundDrawable.setBounds(0, this.gapStartY < 0 ? 0 : -AndroidUtilities.dp(16.0f), (int) (getMeasuredWidth() * this.backScaleX), ((int) (getMeasuredHeight() * this.backScaleY)) - this.subtractBackgroundHeight);
                        }
                        this.backgroundDrawable.draw(canvas);
                        if (hasGap) {
                            canvas.save();
                            AndroidUtilities.rectTmp2.set(this.backgroundDrawable.getBounds());
                            AndroidUtilities.rectTmp2.inset(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                            canvas.clipRect(AndroidUtilities.rectTmp2);
                            for (int i3 = 0; i3 < this.linearLayout.getChildCount(); i3++) {
                                if ((this.linearLayout.getChildAt(i3) instanceof GapView) && this.linearLayout.getChildAt(i3).getVisibility() == 0) {
                                    canvas.save();
                                    float x = 0.0f;
                                    float y = 0.0f;
                                    GapView child = (GapView) this.linearLayout.getChildAt(i3);
                                    View view = child;
                                    while (view != this) {
                                        x += view.getX();
                                        y += view.getY();
                                        view = (View) view.getParent();
                                        if (view == null) {
                                            break;
                                        }
                                    }
                                    canvas.translate(x, this.scrollView.getScaleY() * y);
                                    child.draw(canvas);
                                    canvas.restore();
                                }
                            }
                            canvas.restore();
                        }
                        if (needRestore) {
                            canvas.restore();
                        }
                    } else {
                        return;
                    }
                }
            }
        }

        public Drawable getBackgroundDrawable() {
            return this.backgroundDrawable;
        }

        public int getItemsCount() {
            return this.linearLayout.getChildCount();
        }

        public View getItemAt(int index) {
            return this.linearLayout.getChildAt(index);
        }

        public void scrollToTop() {
            ScrollView scrollView = this.scrollView;
            if (scrollView != null) {
                scrollView.scrollTo(0, 0);
            }
        }

        public void setupRadialSelectors(int color) {
            int count = this.linearLayout.getChildCount();
            int a = 0;
            while (a < count) {
                View child = this.linearLayout.getChildAt(a);
                int i = 6;
                int i2 = a == 0 ? 6 : 0;
                if (a != count - 1) {
                    i = 0;
                }
                child.setBackground(Theme.createRadSelectorDrawable(color, i2, i));
                a++;
            }
        }

        public void updateRadialSelectors() {
            int count = this.linearLayout.getChildCount();
            View firstVisible = null;
            View lastVisible = null;
            for (int a = 0; a < count; a++) {
                View child = this.linearLayout.getChildAt(a);
                if (child.getVisibility() == 0) {
                    if (firstVisible == null) {
                        firstVisible = child;
                    }
                    lastVisible = child;
                }
            }
            boolean prevGap = false;
            for (int a2 = 0; a2 < count; a2++) {
                View child2 = this.linearLayout.getChildAt(a2);
                if (child2.getVisibility() == 0) {
                    Object tag = child2.getTag(R.id.object_tag);
                    if (child2 instanceof ActionBarMenuSubItem) {
                        ActionBarMenuSubItem actionBarMenuSubItem = (ActionBarMenuSubItem) child2;
                        boolean z = false;
                        boolean z2 = child2 == firstVisible || prevGap;
                        if (child2 == lastVisible) {
                            z = true;
                        }
                        actionBarMenuSubItem.updateSelectorBackground(z2, z);
                    }
                    if (tag != null) {
                        prevGap = true;
                    } else {
                        prevGap = false;
                    }
                }
            }
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }

        public void setOnSizeChangedListener(onSizeChangedListener onSizeChangedListener) {
            this.onSizeChangedListener = onSizeChangedListener;
        }

        public int getVisibleHeight() {
            return (int) (getMeasuredHeight() * this.backScaleY);
        }

        public void setTopView(View topView) {
            this.topView = topView;
        }

        public void setSwipeBackForegroundColor(int color) {
            getSwipeBack().setForegroundColor(color);
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            PopupSwipeBackLayout popupSwipeBackLayout = this.swipeBackLayout;
            if (popupSwipeBackLayout != null) {
                popupSwipeBackLayout.invalidateTransforms(!this.startAnimationPending);
            }
        }
    }

    public ActionBarPopupWindow() {
        init();
    }

    public ActionBarPopupWindow(Context context) {
        super(context);
        init();
    }

    public ActionBarPopupWindow(int width, int height) {
        super(width, height);
        init();
    }

    public ActionBarPopupWindow(View contentView) {
        super(contentView);
        init();
    }

    public ActionBarPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        init();
    }

    public ActionBarPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
        init();
    }

    public void setAnimationEnabled(boolean value) {
        this.animationEnabled = value;
    }

    public void setLayoutInScreen(boolean value) {
        try {
            if (layoutInScreenMethod == null) {
                Method declaredMethod = PopupWindow.class.getDeclaredMethod("setLayoutInScreenEnabled", Boolean.TYPE);
                layoutInScreenMethod = declaredMethod;
                declaredMethod.setAccessible(true);
            }
            layoutInScreenMethod.invoke(this, true);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void init() {
        Field field = superListenerField;
        if (field != null) {
            try {
                this.mSuperScrollListener = (ViewTreeObserver.OnScrollChangedListener) field.get(this);
                field.set(this, NOP);
            } catch (Exception e) {
                this.mSuperScrollListener = null;
            }
        }
    }

    public void setDismissAnimationDuration(int value) {
        this.dismissAnimationDuration = value;
    }

    public void unregisterListener() {
        ViewTreeObserver viewTreeObserver;
        if (this.mSuperScrollListener != null && (viewTreeObserver = this.mViewTreeObserver) != null) {
            if (viewTreeObserver.isAlive()) {
                this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
            }
            this.mViewTreeObserver = null;
        }
    }

    private void registerListener(View anchor) {
        if (this.mSuperScrollListener != null) {
            ViewTreeObserver vto = anchor.getWindowToken() != null ? anchor.getViewTreeObserver() : null;
            ViewTreeObserver viewTreeObserver = this.mViewTreeObserver;
            if (vto != viewTreeObserver) {
                if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = vto;
                if (vto != null) {
                    vto.addOnScrollChangedListener(this.mSuperScrollListener);
                }
            }
        }
    }

    public void dimBehind() {
        View container = getContentView().getRootView();
        Context context = getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService("window");
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= 2;
        p.dimAmount = 0.2f;
        wm.updateViewLayout(container, p);
    }

    private void dismissDim() {
        View container = getContentView().getRootView();
        Context context = getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService("window");
        if (container.getLayoutParams() == null || !(container.getLayoutParams() instanceof WindowManager.LayoutParams)) {
            return;
        }
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        try {
            if ((p.flags & 2) != 0) {
                p.flags &= -3;
                p.dimAmount = 0.0f;
                wm.updateViewLayout(container, p);
            }
        } catch (Exception e) {
        }
    }

    @Override // android.widget.PopupWindow
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        try {
            super.showAsDropDown(anchor, xoff, yoff);
            registerListener(anchor);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void startAnimation() {
        if (!this.animationEnabled || this.windowAnimatorSet != null) {
            return;
        }
        ViewGroup viewGroup = (ViewGroup) getContentView();
        ActionBarPopupWindowLayout content = null;
        if (viewGroup instanceof ActionBarPopupWindowLayout) {
            content = (ActionBarPopupWindowLayout) viewGroup;
            content.startAnimationPending = true;
        } else {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof ActionBarPopupWindowLayout) {
                    content = (ActionBarPopupWindowLayout) viewGroup.getChildAt(i);
                    content.startAnimationPending = true;
                }
            }
        }
        content.setTranslationY(0.0f);
        content.setAlpha(1.0f);
        content.setPivotX(content.getMeasuredWidth());
        content.setPivotY(0.0f);
        int count = content.getItemsCount();
        content.positions.clear();
        int visibleCount = 0;
        for (int a = 0; a < count; a++) {
            View child = content.getItemAt(a);
            child.setAlpha(0.0f);
            if (child.getVisibility() == 0) {
                content.positions.put(child, Integer.valueOf(visibleCount));
                visibleCount++;
            }
        }
        if (content.shownFromBottom) {
            content.lastStartedChild = count - 1;
        } else {
            content.lastStartedChild = 0;
        }
        float finalScaleY = 1.0f;
        if (content.getSwipeBack() != null) {
            content.getSwipeBack().invalidateTransforms();
            finalScaleY = content.backScaleY;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        this.windowAnimatorSet = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(content, "backScaleY", 0.0f, finalScaleY), ObjectAnimator.ofInt(content, "backAlpha", 0, 255));
        this.windowAnimatorSet.setDuration((visibleCount * 16) + 150);
        this.windowAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ActionBarPopupWindow.this.windowAnimatorSet = null;
                ViewGroup viewGroup2 = (ViewGroup) ActionBarPopupWindow.this.getContentView();
                ActionBarPopupWindowLayout content2 = null;
                if (viewGroup2 instanceof ActionBarPopupWindowLayout) {
                    content2 = (ActionBarPopupWindowLayout) viewGroup2;
                    content2.startAnimationPending = false;
                } else {
                    for (int i2 = 0; i2 < viewGroup2.getChildCount(); i2++) {
                        if (viewGroup2.getChildAt(i2) instanceof ActionBarPopupWindowLayout) {
                            content2 = (ActionBarPopupWindowLayout) viewGroup2.getChildAt(i2);
                            content2.startAnimationPending = false;
                        }
                    }
                }
                int count2 = content2.getItemsCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View child2 = content2.getItemAt(a2);
                    if (!(child2 instanceof GapView)) {
                        child2.setAlpha(child2.isEnabled() ? 1.0f : 0.5f);
                    }
                }
            }
        });
        this.windowAnimatorSet.start();
    }

    @Override // android.widget.PopupWindow
    public void update(View anchor, int xoff, int yoff, int width, int height) {
        super.update(anchor, xoff, yoff, width, height);
        registerListener(anchor);
    }

    @Override // android.widget.PopupWindow
    public void update(View anchor, int width, int height) {
        super.update(anchor, width, height);
        registerListener(anchor);
    }

    @Override // android.widget.PopupWindow
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        unregisterListener();
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        dismiss(true);
    }

    public void setPauseNotifications(boolean value) {
        this.pauseNotifications = value;
    }

    public void dismiss(boolean animated) {
        setFocusable(false);
        dismissDim();
        AnimatorSet animatorSet = this.windowAnimatorSet;
        if (animatorSet != null) {
            if (animated && this.isClosingAnimated) {
                return;
            }
            animatorSet.cancel();
            this.windowAnimatorSet = null;
        }
        this.isClosingAnimated = false;
        if (this.animationEnabled && animated) {
            this.isClosingAnimated = true;
            ViewGroup viewGroup = (ViewGroup) getContentView();
            ActionBarPopupWindowLayout content = null;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof ActionBarPopupWindowLayout) {
                    content = (ActionBarPopupWindowLayout) viewGroup.getChildAt(i);
                }
            }
            if (content != null && content.itemAnimators != null && !content.itemAnimators.isEmpty()) {
                int N = content.itemAnimators.size();
                for (int a = 0; a < N; a++) {
                    AnimatorSet animatorSet2 = (AnimatorSet) content.itemAnimators.get(a);
                    animatorSet2.removeAllListeners();
                    animatorSet2.cancel();
                }
                content.itemAnimators.clear();
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.windowAnimatorSet = animatorSet3;
            if (this.outEmptyTime > 0) {
                animatorSet3.playTogether(ValueAnimator.ofFloat(0.0f, 1.0f));
                this.windowAnimatorSet.setDuration(this.outEmptyTime);
            } else if (this.scaleOut) {
                animatorSet3.playTogether(ObjectAnimator.ofFloat(viewGroup, View.SCALE_Y, 0.8f), ObjectAnimator.ofFloat(viewGroup, View.SCALE_X, 0.8f), ObjectAnimator.ofFloat(viewGroup, View.ALPHA, 0.0f));
                this.windowAnimatorSet.setDuration(this.dismissAnimationDuration);
            } else {
                Animator[] animatorArr = new Animator[2];
                Property property = View.TRANSLATION_Y;
                float[] fArr = new float[1];
                fArr[0] = AndroidUtilities.dp((content == null || !content.shownFromBottom) ? -5.0f : 5.0f);
                animatorArr[0] = ObjectAnimator.ofFloat(viewGroup, property, fArr);
                animatorArr[1] = ObjectAnimator.ofFloat(viewGroup, View.ALPHA, 0.0f);
                animatorSet3.playTogether(animatorArr);
                this.windowAnimatorSet.setDuration(this.dismissAnimationDuration);
            }
            this.windowAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ActionBarPopupWindow.this.windowAnimatorSet = null;
                    ActionBarPopupWindow.this.isClosingAnimated = false;
                    ActionBarPopupWindow.this.setFocusable(false);
                    try {
                        ActionBarPopupWindow.super.dismiss();
                    } catch (Exception e) {
                    }
                    ActionBarPopupWindow.this.unregisterListener();
                    if (ActionBarPopupWindow.this.pauseNotifications) {
                        NotificationCenter.getInstance(ActionBarPopupWindow.this.currentAccount).onAnimationFinish(ActionBarPopupWindow.this.popupAnimationIndex);
                    }
                }
            });
            if (this.pauseNotifications) {
                this.popupAnimationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.popupAnimationIndex, null);
            }
            this.windowAnimatorSet.start();
            return;
        }
        try {
            super.dismiss();
        } catch (Exception e) {
        }
        unregisterListener();
    }

    public void setEmptyOutAnimation(long time) {
        this.outEmptyTime = time;
    }

    /* loaded from: classes4.dex */
    public static class GapView extends FrameLayout {
        String colorKey;
        Theme.ResourcesProvider resourcesProvider;

        public GapView(Context context, Theme.ResourcesProvider resourcesProvider, String colorKey) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            this.colorKey = colorKey;
            setBackgroundColor(getThemedColor(colorKey));
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }

        public void setColor(int color) {
            setBackgroundColor(color);
        }
    }
}
