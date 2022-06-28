package org.telegram.ui.Components.voip;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.GroupCallTabletGridAdapter;
/* loaded from: classes5.dex */
public class GroupCallGridCell extends FrameLayout {
    public static final int CELL_HEIGHT = 165;
    public boolean attached;
    public GroupCallTabletGridAdapter gridAdapter;
    private final boolean isTabletGrid;
    ChatObject.VideoParticipant participant;
    public int position;
    GroupCallMiniTextureView renderer;
    public int spanCount;

    public GroupCallGridCell(Context context, boolean isTabletGrid) {
        super(context);
        this.isTabletGrid = isTabletGrid;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float parentWidth;
        float h;
        if (this.isTabletGrid) {
            float measuredWidth = (((View) getParent()).getMeasuredWidth() / 6.0f) * this.spanCount;
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(this.gridAdapter.getItemHeight(this.position), C.BUFFER_FLAG_ENCRYPTED));
            return;
        }
        float spanCount = GroupCallActivity.isLandscapeMode ? 3.0f : 2.0f;
        if (getParent() != null) {
            parentWidth = ((View) getParent()).getMeasuredWidth();
        } else {
            parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        }
        if (GroupCallActivity.isTabletMode) {
            h = parentWidth / 2.0f;
        } else {
            h = parentWidth / spanCount;
        }
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec((int) (AndroidUtilities.dp(4.0f) + h), C.BUFFER_FLAG_ENCRYPTED));
    }

    public void setData(AccountInstance accountInstance, ChatObject.VideoParticipant participant, ChatObject.Call call, long selfPeerId) {
        this.participant = participant;
    }

    public ChatObject.VideoParticipant getParticipant() {
        return this.participant;
    }

    public void setRenderer(GroupCallMiniTextureView renderer) {
        this.renderer = renderer;
    }

    public GroupCallMiniTextureView getRenderer() {
        return this.renderer;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
    }

    public float getItemHeight() {
        GroupCallTabletGridAdapter groupCallTabletGridAdapter = this.gridAdapter;
        if (groupCallTabletGridAdapter != null) {
            return groupCallTabletGridAdapter.getItemHeight(this.position);
        }
        return getMeasuredHeight();
    }
}
