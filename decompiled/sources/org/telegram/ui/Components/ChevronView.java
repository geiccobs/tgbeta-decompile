package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class ChevronView extends View {
    public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_RIGHT = 0;
    private int direction;
    private Paint paint = new Paint(1);
    private Path path = new Path();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Direction {
    }

    public ChevronView(Context context, int direction) {
        super(context);
        this.paint.setStrokeWidth(AndroidUtilities.dp(1.75f));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.direction = direction;
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float stroke = this.paint.getStrokeWidth();
        this.path.rewind();
        switch (this.direction) {
            case 0:
                this.path.moveTo(getPaddingLeft() + stroke, getPaddingTop() + stroke);
                this.path.lineTo((getWidth() - stroke) - getPaddingRight(), getHeight() / 2.0f);
                this.path.lineTo(getPaddingLeft() + stroke, (getHeight() - stroke) - getPaddingBottom());
                break;
            case 1:
                this.path.moveTo((getWidth() - stroke) - getPaddingRight(), getPaddingTop() + stroke);
                this.path.lineTo(getPaddingLeft() + stroke, getHeight() / 2.0f);
                this.path.lineTo((getWidth() - stroke) - getPaddingBottom(), (getHeight() - stroke) - getPaddingBottom());
                break;
        }
        canvas.drawPath(this.path, this.paint);
    }
}
