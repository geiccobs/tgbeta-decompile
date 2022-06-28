package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes4.dex */
public class FixedHeightEmptyCell extends View {
    int heightInDp;

    public FixedHeightEmptyCell(Context context, int heightInDp) {
        super(context);
        this.heightInDp = heightInDp;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.heightInDp), C.BUFFER_FLAG_ENCRYPTED));
    }
}
