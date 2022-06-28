package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class LocationCell extends FrameLayout {
    private static FlickerLoadingView globalGradientView;
    private TextView addressTextView;
    private ShapeDrawable circleDrawable;
    private float enterAlpha = 0.0f;
    private ValueAnimator enterAnimator;
    private BackupImageView imageView;
    private TextView nameTextView;
    private boolean needDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean wrapContent;

    public LocationCell(Context context, boolean wrap, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        this.wrapContent = wrap;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        ShapeDrawable createCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(42.0f), -1);
        this.circleDrawable = createCircleDrawable;
        backupImageView.setBackground(createCircleDrawable);
        this.imageView.setSize(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 11.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextSize(1, 16.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16 : 73, 10.0f, LocaleController.isRTL ? 73 : 16, 0.0f));
        TextView textView2 = new TextView(context);
        this.addressTextView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.addressTextView.setMaxLines(1);
        this.addressTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.addressTextView.setSingleLine(true);
        this.addressTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.addressTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.addressTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 16 : 73, 35.0f, LocaleController.isRTL ? 73 : 16, 0.0f));
        this.imageView.setAlpha(this.enterAlpha);
        this.nameTextView.setAlpha(this.enterAlpha);
        this.addressTextView.setAlpha(this.enterAlpha);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.wrapContent) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
        }
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public void setLocation(TLRPC.TL_messageMediaVenue location, String icon, int pos, boolean divider) {
        setLocation(location, icon, null, pos, divider);
    }

    public static int getColorForIndex(int index) {
        switch (index % 7) {
            case 0:
                return -1351584;
            case 1:
                return -868277;
            case 2:
                return -12214795;
            case 3:
                return -13187226;
            case 4:
                return -7900675;
            case 5:
                return -12338729;
            default:
                return -1285237;
        }
    }

    public void setLocation(TLRPC.TL_messageMediaVenue location, String icon, String label, int pos, boolean divider) {
        this.needDivider = divider;
        this.circleDrawable.getPaint().setColor(getColorForIndex(pos));
        if (location != null) {
            this.nameTextView.setText(location.title);
        }
        if (label != null) {
            this.addressTextView.setText(label);
        } else if (location != null) {
            this.addressTextView.setText(location.address);
        }
        if (icon != null) {
            this.imageView.setImage(icon, null, null);
        }
        setWillNotDraw(false);
        setClickable(location == null);
        ValueAnimator valueAnimator = this.enterAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        boolean loading = location == null;
        final float fromEnterAlpha = this.enterAlpha;
        final float toEnterAlpha = loading ? 0.0f : 1.0f;
        final long duration = Math.abs(fromEnterAlpha - toEnterAlpha) * 150.0f;
        this.enterAnimator = ValueAnimator.ofFloat(fromEnterAlpha, toEnterAlpha);
        final long start = SystemClock.elapsedRealtime();
        this.enterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.LocationCell$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                LocationCell.this.m1657lambda$setLocation$0$orgtelegramuiCellsLocationCell(start, duration, fromEnterAlpha, toEnterAlpha, valueAnimator2);
            }
        });
        this.enterAnimator.setDuration(loading ? Long.MAX_VALUE : duration);
        this.enterAnimator.start();
        this.imageView.setAlpha(fromEnterAlpha);
        this.nameTextView.setAlpha(fromEnterAlpha);
        this.addressTextView.setAlpha(fromEnterAlpha);
        invalidate();
    }

    /* renamed from: lambda$setLocation$0$org-telegram-ui-Cells-LocationCell */
    public /* synthetic */ void m1657lambda$setLocation$0$orgtelegramuiCellsLocationCell(long start, long duration, float fromEnterAlpha, float toEnterAlpha, ValueAnimator a) {
        float t = Math.min(Math.max(((float) (SystemClock.elapsedRealtime() - start)) / ((float) duration), 0.0f), 1.0f);
        if (duration <= 0) {
            t = 1.0f;
        }
        float lerp = AndroidUtilities.lerp(fromEnterAlpha, toEnterAlpha, t);
        this.enterAlpha = lerp;
        this.imageView.setAlpha(lerp);
        this.nameTextView.setAlpha(this.enterAlpha);
        this.addressTextView.setAlpha(this.enterAlpha);
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (globalGradientView == null) {
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(getContext());
            globalGradientView = flickerLoadingView;
            flickerLoadingView.setIsSingleCell(true);
        }
        int index = getParent() instanceof ViewGroup ? ((ViewGroup) getParent()).indexOfChild(this) : 0;
        globalGradientView.setParentSize(getMeasuredWidth(), getMeasuredHeight(), (-index) * AndroidUtilities.dp(56.0f));
        globalGradientView.setViewType(4);
        globalGradientView.updateColors();
        globalGradientView.updateGradient();
        canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) ((1.0f - this.enterAlpha) * 255.0f), 31);
        canvas.translate(AndroidUtilities.dp(2.0f), (getMeasuredHeight() - AndroidUtilities.dp(56.0f)) / 2);
        globalGradientView.draw(canvas);
        canvas.restore();
        super.onDraw(canvas);
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(72.0f), getHeight() - 1, LocaleController.isRTL ? getWidth() - AndroidUtilities.dp(72.0f) : getWidth(), getHeight() - 1, Theme.dividerPaint);
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
