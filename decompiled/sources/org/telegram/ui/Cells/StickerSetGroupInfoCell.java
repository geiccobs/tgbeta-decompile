package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class StickerSetGroupInfoCell extends LinearLayout {
    private TextView addButton;
    private boolean isLast;

    public StickerSetGroupInfoCell(Context context) {
        super(context);
        setOrientation(1);
        TextView infoTextView = new TextView(context);
        infoTextView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelTrendingDescription));
        infoTextView.setTextSize(1, 14.0f);
        infoTextView.setText(LocaleController.getString("GroupStickersInfo", R.string.GroupStickersInfo));
        addView(infoTextView, LayoutHelper.createLinear(-1, -2, 51, 17, 4, 17, 0));
        TextView textView = new TextView(context);
        this.addButton = textView;
        textView.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(17.0f), 0);
        this.addButton.setGravity(17);
        this.addButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.addButton.setTextSize(1, 14.0f);
        this.addButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.addButton.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
        this.addButton.setText(LocaleController.getString("ChooseStickerSet", R.string.ChooseStickerSet).toUpperCase());
        addView(this.addButton, LayoutHelper.createLinear(-2, 28, 51, 17, 10, 14, 8));
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View parent;
        int height;
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
        if (this.isLast && (parent = (View) getParent()) != null && getMeasuredHeight() < (height = ((parent.getMeasuredHeight() - parent.getPaddingBottom()) - parent.getPaddingTop()) - AndroidUtilities.dp(24.0f))) {
            setMeasuredDimension(getMeasuredWidth(), height);
        }
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        this.addButton.setOnClickListener(onClickListener);
    }

    public void setIsLast(boolean last) {
        this.isLast = last;
        requestLayout();
    }
}
