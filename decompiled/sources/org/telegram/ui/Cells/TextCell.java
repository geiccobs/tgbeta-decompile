package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
/* loaded from: classes4.dex */
public class TextCell extends FrameLayout {
    public int imageLeft;
    public final RLottieImageView imageView;
    private boolean inDialogs;
    private int leftPadding;
    private boolean needDivider;
    private int offsetFromImage;
    private boolean prioritizeTitleOverValue;
    private Theme.ResourcesProvider resourcesProvider;
    public final SimpleTextView textView;
    private ImageView valueImageView;
    public final SimpleTextView valueTextView;

    public TextCell(Context context) {
        this(context, 23, false, null);
    }

    public TextCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, 23, false, resourcesProvider);
    }

    public TextCell(Context context, int left, boolean dialog) {
        this(context, left, dialog, null);
    }

    public TextCell(Context context, int left, boolean dialog, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.offsetFromImage = 71;
        this.imageLeft = 21;
        this.resourcesProvider = resourcesProvider;
        this.leftPadding = left;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(dialog ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        simpleTextView.setTextSize(16);
        int i = 5;
        simpleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        simpleTextView.setImportantForAccessibility(2);
        addView(simpleTextView, LayoutHelper.createFrame(-2, -1.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.valueTextView = simpleTextView2;
        simpleTextView2.setTextColor(Theme.getColor(dialog ? Theme.key_dialogTextBlue2 : Theme.key_windowBackgroundWhiteValueText, resourcesProvider));
        simpleTextView2.setTextSize(16);
        simpleTextView2.setGravity(LocaleController.isRTL ? 3 : i);
        simpleTextView2.setImportantForAccessibility(2);
        addView(simpleTextView2);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        rLottieImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(dialog ? Theme.key_dialogIcon : Theme.key_windowBackgroundWhiteGrayIcon, resourcesProvider), PorterDuff.Mode.MULTIPLY));
        addView(rLottieImageView);
        ImageView imageView = new ImageView(context);
        this.valueImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.valueImageView);
        setFocusable(true);
    }

    public void setIsInDialogs() {
        this.inDialogs = true;
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    public RLottieImageView getImageView() {
        return this.imageView;
    }

    public SimpleTextView getValueTextView() {
        return this.valueTextView;
    }

    public ImageView getValueImageView() {
        return this.valueImageView;
    }

    public void setPrioritizeTitleOverValue(boolean prioritizeTitleOverValue) {
        this.prioritizeTitleOverValue = prioritizeTitleOverValue;
        requestLayout();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = AndroidUtilities.dp(48.0f);
        if (this.prioritizeTitleOverValue) {
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(this.leftPadding + 71), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), C.BUFFER_FLAG_ENCRYPTED));
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec((width - AndroidUtilities.dp(this.leftPadding + 103)) - this.textView.getTextWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), C.BUFFER_FLAG_ENCRYPTED));
        } else {
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(this.leftPadding), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), C.BUFFER_FLAG_ENCRYPTED));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec((width - AndroidUtilities.dp(this.leftPadding + 71)) - this.valueTextView.getTextWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), C.BUFFER_FLAG_ENCRYPTED));
        }
        if (this.imageView.getVisibility() == 0) {
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        }
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        }
        setMeasuredDimension(width, AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int viewLeft;
        int height = bottom - top;
        int width = right - left;
        int viewTop = (height - this.valueTextView.getTextHeight()) / 2;
        int viewLeft2 = LocaleController.isRTL ? AndroidUtilities.dp(this.leftPadding) : 0;
        if (this.prioritizeTitleOverValue && !LocaleController.isRTL) {
            viewLeft2 = (width - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(this.leftPadding);
        }
        SimpleTextView simpleTextView = this.valueTextView;
        simpleTextView.layout(viewLeft2, viewTop, simpleTextView.getMeasuredWidth() + viewLeft2, this.valueTextView.getMeasuredHeight() + viewTop);
        int viewTop2 = (height - this.textView.getTextHeight()) / 2;
        if (LocaleController.isRTL) {
            viewLeft = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? this.offsetFromImage : this.leftPadding);
        } else {
            viewLeft = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? this.offsetFromImage : this.leftPadding);
        }
        SimpleTextView simpleTextView2 = this.textView;
        simpleTextView2.layout(viewLeft, viewTop2, simpleTextView2.getMeasuredWidth() + viewLeft, this.textView.getMeasuredHeight() + viewTop2);
        if (this.imageView.getVisibility() == 0) {
            int viewTop3 = AndroidUtilities.dp(5.0f);
            int viewLeft3 = !LocaleController.isRTL ? AndroidUtilities.dp(this.imageLeft) : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageLeft);
            RLottieImageView rLottieImageView = this.imageView;
            rLottieImageView.layout(viewLeft3, viewTop3, rLottieImageView.getMeasuredWidth() + viewLeft3, this.imageView.getMeasuredHeight() + viewTop3);
        }
        if (this.valueImageView.getVisibility() == 0) {
            int viewTop4 = (height - this.valueImageView.getMeasuredHeight()) / 2;
            int viewLeft4 = LocaleController.isRTL ? AndroidUtilities.dp(23.0f) : (width - this.valueImageView.getMeasuredWidth()) - AndroidUtilities.dp(23.0f);
            ImageView imageView = this.valueImageView;
            imageView.layout(viewLeft4, viewTop4, imageView.getMeasuredWidth() + viewLeft4, this.valueImageView.getMeasuredHeight() + viewTop4);
        }
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setColors(String icon, String text) {
        this.textView.setTextColor(Theme.getColor(text, this.resourcesProvider));
        this.textView.setTag(text);
        if (icon != null) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(icon, this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
            this.imageView.setTag(icon);
        }
    }

    public void setText(String text, boolean divider) {
        this.imageLeft = 21;
        this.textView.setText(text);
        this.valueTextView.setText(null);
        this.imageView.setVisibility(8);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndIcon(String text, int resId, boolean divider) {
        this.imageLeft = 21;
        this.offsetFromImage = 71;
        this.textView.setText(text);
        this.valueTextView.setText(null);
        this.imageView.setImageResource(resId);
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndIcon(String text, Drawable drawable, boolean divider) {
        this.offsetFromImage = 68;
        this.imageLeft = 18;
        this.textView.setText(text);
        this.valueTextView.setText(null);
        this.imageView.setColorFilter((ColorFilter) null);
        if (drawable instanceof RLottieDrawable) {
            this.imageView.setAnimation((RLottieDrawable) drawable);
        } else {
            this.imageView.setImageDrawable(drawable);
        }
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(6.0f), 0, 0);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setOffsetFromImage(int value) {
        this.offsetFromImage = value;
    }

    public void setImageLeft(int imageLeft) {
        this.imageLeft = imageLeft;
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        this.imageLeft = 21;
        this.offsetFromImage = 71;
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.valueTextView.setVisibility(0);
        this.imageView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValueAndIcon(String text, String value, int resId, boolean divider) {
        this.imageLeft = 21;
        this.offsetFromImage = 71;
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.valueTextView.setVisibility(0);
        this.valueImageView.setVisibility(8);
        this.imageView.setVisibility(0);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.imageView.setImageResource(resId);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValueDrawable(String text, Drawable drawable, boolean divider) {
        this.imageLeft = 21;
        this.offsetFromImage = 71;
        this.textView.setText(text);
        this.valueTextView.setText(null);
        this.valueImageView.setVisibility(0);
        this.valueImageView.setImageDrawable(drawable);
        this.valueTextView.setVisibility(8);
        this.imageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float f;
        int i;
        float f2;
        if (this.needDivider) {
            int i2 = 72;
            float f3 = 20.0f;
            if (LocaleController.isRTL) {
                f = 0.0f;
            } else {
                if (this.imageView.getVisibility() == 0) {
                    f2 = this.inDialogs ? 72 : 68;
                } else {
                    f2 = 20.0f;
                }
                f = AndroidUtilities.dp(f2);
            }
            float measuredHeight = getMeasuredHeight() - 1;
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                if (this.imageView.getVisibility() == 0) {
                    if (!this.inDialogs) {
                        i2 = 68;
                    }
                    f3 = i2;
                }
                i = AndroidUtilities.dp(f3);
            } else {
                i = 0;
            }
            canvas.drawLine(f, measuredHeight, measuredWidth - i, getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        CharSequence text = this.textView.getText();
        if (!TextUtils.isEmpty(text)) {
            CharSequence valueText = this.valueTextView.getText();
            if (!TextUtils.isEmpty(valueText)) {
                info.setText(((Object) text) + ": " + ((Object) valueText));
            } else {
                info.setText(text);
            }
        }
        info.addAction(16);
    }

    public void setNeedDivider(boolean needDivider) {
        if (this.needDivider != needDivider) {
            this.needDivider = needDivider;
            setWillNotDraw(!needDivider);
            invalidate();
        }
    }
}
