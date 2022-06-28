package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes4.dex */
public class CreationTextCell extends FrameLayout {
    boolean divider;
    private ImageView imageView;
    public int startPadding;
    private SimpleTextView textView;

    public CreationTextCell(Context context) {
        this(context, null);
    }

    public CreationTextCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.startPadding = 70;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextSize(16);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2, resourcesProvider));
        this.textView.setTag(Theme.key_windowBackgroundWhiteBlueText2);
        addView(this.textView);
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.imageView);
        setWillNotDraw(false);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        AndroidUtilities.dp(48.0f);
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(94.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), C.BUFFER_FLAG_ENCRYPTED));
        this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), C.BUFFER_FLAG_ENCRYPTED));
        setMeasuredDimension(width, AndroidUtilities.dp(50.0f));
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int viewLeft;
        int height = bottom - top;
        int width = right - left;
        int viewTop = (height - this.textView.getTextHeight()) / 2;
        if (LocaleController.isRTL) {
            viewLeft = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? this.startPadding : 25.0f);
        } else {
            viewLeft = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? this.startPadding : 25.0f);
        }
        SimpleTextView simpleTextView = this.textView;
        simpleTextView.layout(viewLeft, viewTop, simpleTextView.getMeasuredWidth() + viewLeft, this.textView.getMeasuredHeight() + viewTop);
        int viewLeft2 = !LocaleController.isRTL ? (AndroidUtilities.dp(this.startPadding) - this.imageView.getMeasuredWidth()) / 2 : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(25.0f);
        ImageView imageView = this.imageView;
        imageView.layout(viewLeft2, 0, imageView.getMeasuredWidth() + viewLeft2, this.imageView.getMeasuredHeight());
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.divider) {
            canvas.drawLine(AndroidUtilities.dp(this.startPadding), getMeasuredHeight() - 1, getMeasuredWidth() + AndroidUtilities.dp(23.0f), getMeasuredHeight(), Theme.dividerPaint);
        }
    }

    public void setTextAndIcon(String text, Drawable icon, boolean divider) {
        this.textView.setText(text);
        this.imageView.setImageDrawable(icon);
        this.divider = divider;
    }
}
