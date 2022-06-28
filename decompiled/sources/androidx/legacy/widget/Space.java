package androidx.legacy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.exoplayer2.C;
@Deprecated
/* loaded from: classes3.dex */
public class Space extends View {
    @Deprecated
    public Space(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (getVisibility() == 0) {
            setVisibility(4);
        }
    }

    @Deprecated
    public Space(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Deprecated
    public Space(Context context) {
        this(context, null);
    }

    @Override // android.view.View
    @Deprecated
    public void draw(Canvas canvas) {
    }

    private static int getDefaultSize2(int size, int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integer.MIN_VALUE:
                int result = Math.min(size, specSize);
                return result;
            case 0:
                return size;
            case C.BUFFER_FLAG_ENCRYPTED /* 1073741824 */:
                return specSize;
            default:
                return size;
        }
    }

    @Override // android.view.View
    @Deprecated
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize2(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize2(getSuggestedMinimumHeight(), heightMeasureSpec));
    }
}
