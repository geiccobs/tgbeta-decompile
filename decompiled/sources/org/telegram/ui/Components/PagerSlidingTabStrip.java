package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class PagerSlidingTabStrip extends HorizontalScrollView {
    public ViewPager.OnPageChangeListener delegatePageListener;
    private ViewPager pager;
    private Paint rectPaint;
    private Theme.ResourcesProvider resourcesProvider;
    private int tabCount;
    private LinearLayout tabsContainer;
    private final PageListener pageListener = new PageListener();
    private int currentPosition = 0;
    private float currentPositionOffset = 0.0f;
    private int indicatorColor = -10066330;
    private int underlineColor = 436207616;
    private boolean shouldExpand = false;
    private int scrollOffset = AndroidUtilities.dp(52.0f);
    private int indicatorHeight = AndroidUtilities.dp(8.0f);
    private int underlineHeight = AndroidUtilities.dp(2.0f);
    private int dividerPadding = AndroidUtilities.dp(12.0f);
    private int tabPadding = AndroidUtilities.dp(24.0f);
    private int lastScrollX = 0;
    private LinearLayout.LayoutParams defaultTabLayoutParams = new LinearLayout.LayoutParams(-2, -1);

    /* loaded from: classes5.dex */
    public interface IconTabProvider {
        boolean canScrollToTab(int i);

        void customOnDraw(Canvas canvas, int i);

        Drawable getPageIconDrawable(int i);
    }

    public PagerSlidingTabStrip(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setFillViewport(true);
        setWillNotDraw(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.tabsContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        Paint paint = new Paint();
        this.rectPaint = paint;
        paint.setAntiAlias(true);
        this.rectPaint.setStyle(Paint.Style.FILL);
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.setOnPageChangeListener(this.pageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        this.tabsContainer.removeAllViews();
        this.tabCount = this.pager.getAdapter().getCount();
        for (int i = 0; i < this.tabCount; i++) {
            if (this.pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) this.pager.getAdapter()).getPageIconDrawable(i), this.pager.getAdapter().getPageTitle(i));
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: org.telegram.ui.Components.PagerSlidingTabStrip.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                PagerSlidingTabStrip.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                PagerSlidingTabStrip pagerSlidingTabStrip = PagerSlidingTabStrip.this;
                pagerSlidingTabStrip.currentPosition = pagerSlidingTabStrip.pager.getCurrentItem();
                PagerSlidingTabStrip pagerSlidingTabStrip2 = PagerSlidingTabStrip.this;
                pagerSlidingTabStrip2.scrollToChild(pagerSlidingTabStrip2.currentPosition, 0);
            }
        });
    }

    public View getTab(int position) {
        if (position < 0 || position >= this.tabsContainer.getChildCount()) {
            return null;
        }
        return this.tabsContainer.getChildAt(position);
    }

    private void addIconTab(final int position, Drawable drawable, CharSequence contentDescription) {
        ImageView tab = new ImageView(getContext()) { // from class: org.telegram.ui.Components.PagerSlidingTabStrip.2
            @Override // android.widget.ImageView, android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (PagerSlidingTabStrip.this.pager.getAdapter() instanceof IconTabProvider) {
                    ((IconTabProvider) PagerSlidingTabStrip.this.pager.getAdapter()).customOnDraw(canvas, position);
                }
            }

            @Override // android.widget.ImageView, android.view.View
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                Drawable background = getBackground();
                if (Build.VERSION.SDK_INT >= 21 && background != null) {
                    int color = PagerSlidingTabStrip.this.getThemedColor(selected ? Theme.key_chat_emojiPanelIconSelected : Theme.key_chat_emojiBottomPanelIcon);
                    Theme.setSelectorDrawableColor(background, Color.argb(30, Color.red(color), Color.green(color), Color.blue(color)), true);
                }
            }
        };
        boolean z = true;
        tab.setFocusable(true);
        if (Build.VERSION.SDK_INT >= 21) {
            RippleDrawable rippleDrawable = (RippleDrawable) Theme.createSelectorDrawable(getThemedColor(Theme.key_chat_emojiBottomPanelIcon));
            Theme.setRippleDrawableForceSoftware(rippleDrawable);
            tab.setBackground(rippleDrawable);
        }
        tab.setImageDrawable(drawable);
        tab.setScaleType(ImageView.ScaleType.CENTER);
        tab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PagerSlidingTabStrip$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PagerSlidingTabStrip.this.m2769x6f4153ef(position, view);
            }
        });
        this.tabsContainer.addView(tab);
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
        tab.setContentDescription(contentDescription);
    }

    /* renamed from: lambda$addIconTab$0$org-telegram-ui-Components-PagerSlidingTabStrip */
    public /* synthetic */ void m2769x6f4153ef(int position, View v) {
        if ((this.pager.getAdapter() instanceof IconTabProvider) && !((IconTabProvider) this.pager.getAdapter()).canScrollToTab(position)) {
            return;
        }
        this.pager.setCurrentItem(position, false);
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View v = this.tabsContainer.getChildAt(i);
            v.setLayoutParams(this.defaultTabLayoutParams);
            if (this.shouldExpand) {
                v.setPadding(0, 0, 0, 0);
                v.setLayoutParams(new LinearLayout.LayoutParams(-1, -1, 1.0f));
            } else {
                int i2 = this.tabPadding;
                v.setPadding(i2, 0, i2, 0);
            }
        }
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!this.shouldExpand || View.MeasureSpec.getMode(widthMeasureSpec) == 0) {
            return;
        }
        int myWidth = getMeasuredWidth();
        this.tabsContainer.measure(1073741824 | myWidth, heightMeasureSpec);
    }

    public void scrollToChild(int position, int offset) {
        if (this.tabCount == 0) {
            return;
        }
        int newScrollX = this.tabsContainer.getChildAt(position).getLeft() + offset;
        if (position > 0 || offset > 0) {
            newScrollX -= this.scrollOffset;
        }
        if (newScrollX != this.lastScrollX) {
            this.lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        if (isInEditMode() || this.tabCount == 0) {
            return;
        }
        int height = getHeight();
        if (this.underlineHeight != 0) {
            this.rectPaint.setColor(this.underlineColor);
            canvas.drawRect(0.0f, height - this.underlineHeight, this.tabsContainer.getWidth(), height, this.rectPaint);
        }
        View currentTab = this.tabsContainer.getChildAt(this.currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();
        if (this.currentPositionOffset > 0.0f && (i = this.currentPosition) < this.tabCount - 1) {
            View nextTab = this.tabsContainer.getChildAt(i + 1);
            float nextTabLeft = nextTab.getLeft();
            float nextTabRight = nextTab.getRight();
            float f = this.currentPositionOffset;
            lineLeft = (f * nextTabLeft) + ((1.0f - f) * lineLeft);
            lineRight = (f * nextTabRight) + ((1.0f - f) * lineRight);
        }
        if (this.indicatorHeight != 0) {
            this.rectPaint.setColor(this.indicatorColor);
            canvas.drawRect(lineLeft, height - this.indicatorHeight, lineRight, height, this.rectPaint);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class PageListener implements ViewPager.OnPageChangeListener {
        private PageListener() {
            PagerSlidingTabStrip.this = r1;
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            PagerSlidingTabStrip.this.currentPosition = position;
            PagerSlidingTabStrip.this.currentPositionOffset = positionOffset;
            PagerSlidingTabStrip pagerSlidingTabStrip = PagerSlidingTabStrip.this;
            pagerSlidingTabStrip.scrollToChild(position, (int) (pagerSlidingTabStrip.tabsContainer.getChildAt(position).getWidth() * positionOffset));
            PagerSlidingTabStrip.this.invalidate();
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int state) {
            if (state == 0) {
                PagerSlidingTabStrip pagerSlidingTabStrip = PagerSlidingTabStrip.this;
                pagerSlidingTabStrip.scrollToChild(pagerSlidingTabStrip.pager.getCurrentItem(), 0);
            }
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int position) {
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageSelected(position);
            }
            int a = 0;
            while (a < PagerSlidingTabStrip.this.tabsContainer.getChildCount()) {
                PagerSlidingTabStrip.this.tabsContainer.getChildAt(a).setSelected(a == position);
                a++;
            }
        }
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    public void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        if (!this.shouldExpand) {
            post(new Runnable() { // from class: org.telegram.ui.Components.PagerSlidingTabStrip$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    PagerSlidingTabStrip.this.notifyDataSetChanged();
                }
            });
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return this.indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return this.underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return this.dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return this.scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        updateTabStyles();
        requestLayout();
    }

    public boolean getShouldExpand() {
        return this.shouldExpand;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return this.tabPadding;
    }
}
