package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class SlidingTabView extends LinearLayout {
    private SlidingTabViewDelegate delegate;
    private int selectedTab = 0;
    private int tabCount = 0;
    private float tabWidth = 0.0f;
    private float tabX = 0.0f;
    private float animateTabXTo = 0.0f;
    private Paint paint = new Paint();
    private long startAnimationTime = 0;
    private long totalAnimationDiff = 0;
    private float startAnimationX = 0.0f;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();

    /* loaded from: classes5.dex */
    public interface SlidingTabViewDelegate {
        void didSelectTab(int i);
    }

    public SlidingTabView(Context context) {
        super(context);
        setOrientation(0);
        setWeightSum(100.0f);
        this.paint.setColor(-1);
        setWillNotDraw(false);
    }

    public void addTextTab(final int position, String title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setFocusable(true);
        tab.setGravity(17);
        tab.setSingleLine();
        tab.setTextColor(-1);
        tab.setTextSize(1, 14.0f);
        tab.setTypeface(Typeface.DEFAULT_BOLD);
        tab.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, 0));
        tab.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.SlidingTabView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SlidingTabView.this.didSelectTab(position);
            }
        });
        addView(tab);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tab.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = 0;
        layoutParams.weight = 50.0f;
        tab.setLayoutParams(layoutParams);
        this.tabCount++;
    }

    public void setDelegate(SlidingTabViewDelegate delegate) {
        this.delegate = delegate;
    }

    public int getSeletedTab() {
        return this.selectedTab;
    }

    public void didSelectTab(int tab) {
        if (this.selectedTab == tab) {
            return;
        }
        this.selectedTab = tab;
        animateToTab(tab);
        SlidingTabViewDelegate slidingTabViewDelegate = this.delegate;
        if (slidingTabViewDelegate != null) {
            slidingTabViewDelegate.didSelectTab(tab);
        }
    }

    private void animateToTab(int tab) {
        this.animateTabXTo = tab * this.tabWidth;
        this.startAnimationX = this.tabX;
        this.totalAnimationDiff = 0L;
        this.startAnimationTime = System.currentTimeMillis();
        invalidate();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        float f = (r - l) / this.tabCount;
        this.tabWidth = f;
        float f2 = f * this.selectedTab;
        this.tabX = f2;
        this.animateTabXTo = f2;
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.tabX != this.animateTabXTo) {
            long dt = System.currentTimeMillis() - this.startAnimationTime;
            this.startAnimationTime = System.currentTimeMillis();
            long j = this.totalAnimationDiff + dt;
            this.totalAnimationDiff = j;
            if (j > 200) {
                this.totalAnimationDiff = 200L;
                this.tabX = this.animateTabXTo;
            } else {
                this.tabX = this.startAnimationX + (this.interpolator.getInterpolation(((float) j) / 200.0f) * (this.animateTabXTo - this.startAnimationX));
                invalidate();
            }
        }
        canvas.drawRect(this.tabX, getHeight() - AndroidUtilities.dp(2.0f), this.tabX + this.tabWidth, getHeight(), this.paint);
    }
}
