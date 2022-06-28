package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.Premium.StarParticlesView;
/* loaded from: classes5.dex */
public class ProfilePremiumCell extends TextCell {
    StarParticlesView.Drawable drawable;

    public ProfilePremiumCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(6);
        this.drawable = drawable;
        drawable.size1 = 6;
        this.drawable.size2 = 6;
        this.drawable.size3 = 6;
        this.drawable.useGradient = true;
        this.drawable.speedScale = 3.0f;
        this.drawable.minLifeTime = 600L;
        this.drawable.randLifeTime = 500;
        this.drawable.startFromCenter = true;
        this.drawable.type = 101;
        this.drawable.init();
    }

    @Override // org.telegram.ui.Cells.TextCell, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float cx = this.imageView.getX() + (this.imageView.getWidth() / 2.0f);
        float cy = ((this.imageView.getPaddingTop() + this.imageView.getY()) + (this.imageView.getHeight() / 2.0f)) - AndroidUtilities.dp(3.0f);
        this.drawable.rect.set(cx - AndroidUtilities.dp(4.0f), cy - AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f) + cx, AndroidUtilities.dp(4.0f) + cy);
        if (changed) {
            this.drawable.resetPositions();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        this.drawable.onDraw(canvas);
        invalidate();
        super.dispatchDraw(canvas);
    }
}
