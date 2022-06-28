package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes4.dex */
public class PremiumFeatureCell extends FrameLayout {
    public PremiumPreviewFragment.PremiumFeatureData data;
    private final TextView description;
    boolean drawDivider;
    public ImageView imageView;
    private final TextView title;

    public PremiumFeatureCell(Context context) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        this.title = textView;
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setTextSize(1, 15.0f);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        TextView textView2 = new TextView(context);
        this.description = textView2;
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        textView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 1, 0, 0));
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 62.0f, 8.0f, 48.0f, 9.0f));
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(this.imageView, LayoutHelper.createFrame(28, 28.0f, 0, 18.0f, 12.0f, 0.0f, 0.0f));
        ImageView nextIcon = new ImageView(context);
        nextIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        nextIcon.setImageResource(R.drawable.msg_arrowright);
        nextIcon.setColorFilter(Theme.getColor(Theme.key_switchTrack));
        addView(nextIcon, LayoutHelper.createFrame(24, 24.0f, 21, 0.0f, 0.0f, 18.0f, 0.0f));
    }

    public void setData(PremiumPreviewFragment.PremiumFeatureData data, boolean drawDivider) {
        this.data = data;
        this.title.setText(data.title);
        this.description.setText(data.description);
        this.imageView.setImageResource(data.icon);
        this.drawDivider = drawDivider;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.drawDivider) {
            canvas.drawRect(AndroidUtilities.dp(62.0f), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight(), Theme.dividerPaint);
        }
    }
}
