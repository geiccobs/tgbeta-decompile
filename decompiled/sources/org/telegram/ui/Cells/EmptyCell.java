package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
/* loaded from: classes4.dex */
public class EmptyCell extends FrameLayout {
    private int cellHeight;

    public EmptyCell(Context context) {
        this(context, 8);
    }

    public EmptyCell(Context context, int height) {
        super(context);
        this.cellHeight = height;
    }

    public void setHeight(int height) {
        if (this.cellHeight != height) {
            this.cellHeight = height;
            requestLayout();
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(this.cellHeight, C.BUFFER_FLAG_ENCRYPTED));
    }
}
