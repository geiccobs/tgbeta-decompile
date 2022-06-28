package org.telegram.ui.Components;

import android.content.Context;
import android.util.AttributeSet;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
/* loaded from: classes5.dex */
public class CircularViewPager extends ViewPager {
    private Adapter adapter;

    public CircularViewPager(Context context) {
        super(context);
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Components.CircularViewPager.1
            private int scrollState;

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == CircularViewPager.this.getCurrentItem() && positionOffset == 0.0f && this.scrollState == 1) {
                    checkCurrentItem();
                }
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    checkCurrentItem();
                }
                this.scrollState = state;
            }

            private void checkCurrentItem() {
                if (CircularViewPager.this.adapter != null) {
                    int position = CircularViewPager.this.getCurrentItem();
                    int newPosition = CircularViewPager.this.adapter.getExtraCount() + CircularViewPager.this.adapter.getRealPosition(position);
                    if (position != newPosition) {
                        CircularViewPager.this.setCurrentItem(newPosition, false);
                    }
                }
            }
        });
    }

    public CircularViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Components.CircularViewPager.1
            private int scrollState;

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == CircularViewPager.this.getCurrentItem() && positionOffset == 0.0f && this.scrollState == 1) {
                    checkCurrentItem();
                }
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    checkCurrentItem();
                }
                this.scrollState = state;
            }

            private void checkCurrentItem() {
                if (CircularViewPager.this.adapter != null) {
                    int position = CircularViewPager.this.getCurrentItem();
                    int newPosition = CircularViewPager.this.adapter.getExtraCount() + CircularViewPager.this.adapter.getRealPosition(position);
                    if (position != newPosition) {
                        CircularViewPager.this.setCurrentItem(newPosition, false);
                    }
                }
            }
        });
    }

    @Override // androidx.viewpager.widget.ViewPager
    @Deprecated
    public void setAdapter(PagerAdapter adapter) {
        if (adapter instanceof Adapter) {
            setAdapter((Adapter) adapter);
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        super.setAdapter((PagerAdapter) adapter);
        if (adapter != null) {
            setCurrentItem(adapter.getExtraCount(), false);
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class Adapter extends PagerAdapter {
        public abstract int getExtraCount();

        public int getRealPosition(int adapterPosition) {
            int count = getCount();
            int extraCount = getExtraCount();
            if (adapterPosition < extraCount) {
                return ((count - (extraCount * 2)) - ((extraCount - adapterPosition) - 1)) - 1;
            }
            if (adapterPosition >= count - extraCount) {
                return adapterPosition - (count - extraCount);
            }
            return adapterPosition - extraCount;
        }
    }
}
