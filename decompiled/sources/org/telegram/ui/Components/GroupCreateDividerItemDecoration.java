package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCreateSectionCell;
/* loaded from: classes5.dex */
public class GroupCreateDividerItemDecoration extends RecyclerView.ItemDecoration {
    private boolean searching;
    private boolean single;
    private int skipRows;

    public void setSearching(boolean value) {
        this.searching = value;
    }

    public void setSingle(boolean value) {
        this.single = value;
    }

    public void setSkipRows(int value) {
        this.skipRows = value;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int width = parent.getWidth();
        int childCount = parent.getChildCount() - (!this.single ? 1 : 0);
        int i = 0;
        while (i < childCount) {
            View child = parent.getChildAt(i);
            View nextChild = i < childCount + (-1) ? parent.getChildAt(i + 1) : null;
            int position = parent.getChildAdapterPosition(child);
            if (position >= this.skipRows && !(child instanceof GroupCreateSectionCell) && !(nextChild instanceof GroupCreateSectionCell)) {
                int top = child.getBottom();
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(72.0f), top, width - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0), top, Theme.dividerPaint);
            }
            i++;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = 1;
    }
}
