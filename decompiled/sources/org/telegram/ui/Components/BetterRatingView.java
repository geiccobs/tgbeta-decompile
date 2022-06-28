package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class BetterRatingView extends View {
    private OnRatingChangeListener listener;
    private Paint paint = new Paint();
    private int numStars = 5;
    private int selectedRating = 0;
    private Bitmap filledStar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rating_star_filled).extractAlpha();
    private Bitmap hollowStar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rating_star).extractAlpha();

    /* loaded from: classes5.dex */
    public interface OnRatingChangeListener {
        void onRatingChanged(int i);
    }

    public BetterRatingView(Context context) {
        super(context);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((this.numStars * AndroidUtilities.dp(32.0f)) + ((this.numStars - 1) * AndroidUtilities.dp(16.0f)), AndroidUtilities.dp(32.0f));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i = 0;
        while (i < this.numStars) {
            this.paint.setColor(Theme.getColor(i < this.selectedRating ? Theme.key_dialogTextBlue : Theme.key_dialogTextHint));
            canvas.drawBitmap(i < this.selectedRating ? this.filledStar : this.hollowStar, AndroidUtilities.dp(48.0f) * i, 0.0f, this.paint);
            i++;
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        float offset = AndroidUtilities.dp(-8.0f);
        for (int i = 0; i < this.numStars; i++) {
            if (event.getX() > offset && event.getX() < AndroidUtilities.dp(48.0f) + offset && this.selectedRating != i + 1) {
                int i2 = i + 1;
                this.selectedRating = i2;
                OnRatingChangeListener onRatingChangeListener = this.listener;
                if (onRatingChangeListener != null) {
                    onRatingChangeListener.onRatingChanged(i2);
                }
                invalidate();
                return true;
            }
            offset += AndroidUtilities.dp(48.0f);
        }
        return true;
    }

    public int getRating() {
        return this.selectedRating;
    }

    public void setOnRatingChangeListener(OnRatingChangeListener l) {
        this.listener = l;
    }
}
