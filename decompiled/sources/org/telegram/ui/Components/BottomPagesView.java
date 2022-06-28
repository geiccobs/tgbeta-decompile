package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class BottomPagesView extends View {
    private float animatedProgress;
    private String colorKey;
    private int currentPage;
    private int pagesCount;
    private float progress;
    private int scrollPosition;
    private String selectedColorKey;
    private ViewPager viewPager;
    private Paint paint = new Paint(1);
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private RectF rect = new RectF();

    public BottomPagesView(Context context, ViewPager pager, int count) {
        super(context);
        this.viewPager = pager;
        this.pagesCount = count;
    }

    public void setPageOffset(int position, float offset) {
        this.progress = offset;
        this.scrollPosition = position;
        invalidate();
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
        invalidate();
    }

    public void setColor(String key, String selectedKey) {
        this.colorKey = key;
        this.selectedColorKey = selectedKey;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int x;
        AndroidUtilities.dp(5.0f);
        String str = this.colorKey;
        if (str != null) {
            this.paint.setColor((Theme.getColor(str) & ViewCompat.MEASURED_SIZE_MASK) | (-1275068416));
        } else {
            this.paint.setColor(Theme.getCurrentTheme().isDark() ? -11184811 : -4473925);
        }
        this.currentPage = this.viewPager.getCurrentItem();
        for (int a = 0; a < this.pagesCount; a++) {
            if (a != this.currentPage) {
                this.rect.set(AndroidUtilities.dp(11.0f) * a, 0.0f, AndroidUtilities.dp(5.0f) + x, AndroidUtilities.dp(5.0f));
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.5f), AndroidUtilities.dp(2.5f), this.paint);
            }
        }
        String str2 = this.selectedColorKey;
        if (str2 != null) {
            this.paint.setColor(Theme.getColor(str2));
        } else {
            this.paint.setColor(-13851168);
        }
        int x2 = this.currentPage * AndroidUtilities.dp(11.0f);
        if (this.progress == 0.0f) {
            this.rect.set(x2, 0.0f, AndroidUtilities.dp(5.0f) + x2, AndroidUtilities.dp(5.0f));
        } else if (this.scrollPosition >= this.currentPage) {
            this.rect.set(x2, 0.0f, AndroidUtilities.dp(5.0f) + x2 + (AndroidUtilities.dp(11.0f) * this.progress), AndroidUtilities.dp(5.0f));
        } else {
            this.rect.set(x2 - (AndroidUtilities.dp(11.0f) * (1.0f - this.progress)), 0.0f, AndroidUtilities.dp(5.0f) + x2, AndroidUtilities.dp(5.0f));
        }
        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.5f), AndroidUtilities.dp(2.5f), this.paint);
    }
}
