package org.telegram.ui.Components;

import android.content.Context;
import android.text.Layout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
/* loaded from: classes5.dex */
public class CorrectlyMeasuringTextView extends TextView {
    public CorrectlyMeasuringTextView(Context context) {
        super(context);
    }

    @Override // android.widget.TextView, android.view.View
    public void onMeasure(int wms, int hms) {
        super.onMeasure(wms, hms);
        try {
            Layout l = getLayout();
            if (l.getLineCount() <= 1) {
                return;
            }
            int maxw = 0;
            for (int i = l.getLineCount() - 1; i >= 0; i--) {
                maxw = Math.max(maxw, Math.round(l.getPaint().measureText(getText(), l.getLineStart(i), l.getLineEnd(i))));
            }
            super.onMeasure(Math.min(getPaddingLeft() + maxw + getPaddingRight(), getMeasuredWidth()) | C.BUFFER_FLAG_ENCRYPTED, 1073741824 | getMeasuredHeight());
        } catch (Exception e) {
        }
    }
}
