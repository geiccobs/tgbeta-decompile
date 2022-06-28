package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
/* loaded from: classes5.dex */
public class StroageUsageView extends FrameLayout {
    private boolean calculating;
    float calculatingProgress;
    boolean calculatingProgressIncrement;
    TextView calculatingTextView;
    View divider;
    EllipsizeSpanAnimator ellipsizeSpanAnimator;
    TextView freeSizeTextView;
    int lastProgressColor;
    public ViewGroup legendLayout;
    float progress;
    float progress2;
    ProgressView progressView;
    TextView telegramCacheTextView;
    TextView telegramDatabaseTextView;
    TextSettingsCell textSettingsCell;
    private long totalDeviceFreeSize;
    private long totalDeviceSize;
    private long totalSize;
    TextView totlaSizeTextView;
    ValueAnimator valueAnimator;
    ValueAnimator valueAnimator2;
    private Paint paintFill = new Paint(1);
    private Paint paintCalculcating = new Paint(1);
    private Paint paintProgress = new Paint(1);
    private Paint paintProgress2 = new Paint(1);
    private Paint bgPaint = new Paint();
    CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable(220, 255);

    public StroageUsageView(Context context) {
        super(context);
        setWillNotDraw(false);
        this.cellFlickerDrawable.drawFrame = false;
        this.paintFill.setStrokeWidth(AndroidUtilities.dp(6.0f));
        this.paintCalculcating.setStrokeWidth(AndroidUtilities.dp(6.0f));
        this.paintProgress.setStrokeWidth(AndroidUtilities.dp(6.0f));
        this.paintProgress2.setStrokeWidth(AndroidUtilities.dp(6.0f));
        this.paintFill.setStrokeCap(Paint.Cap.ROUND);
        this.paintCalculcating.setStrokeCap(Paint.Cap.ROUND);
        this.paintProgress.setStrokeCap(Paint.Cap.ROUND);
        this.paintProgress2.setStrokeCap(Paint.Cap.ROUND);
        ProgressView progressView = new ProgressView(context);
        this.progressView = progressView;
        addView(progressView, LayoutHelper.createFrame(-1, -2.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f));
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.StroageUsageView.1
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                int currentW = 0;
                int currentH = 0;
                int n = getChildCount();
                int lastChildH = 0;
                for (int i = 0; i < n; i++) {
                    if (getChildAt(i).getVisibility() != 8) {
                        if (getChildAt(i).getMeasuredWidth() + currentW > View.MeasureSpec.getSize(widthMeasureSpec)) {
                            currentW = 0;
                            currentH += getChildAt(i).getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        }
                        currentW += getChildAt(i).getMeasuredWidth() + AndroidUtilities.dp(16.0f);
                        lastChildH = getChildAt(i).getMeasuredHeight() + currentH;
                    }
                }
                int i2 = getMeasuredWidth();
                setMeasuredDimension(i2, lastChildH);
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int currentW = 0;
                int currentH = 0;
                int n = getChildCount();
                for (int i = 0; i < n; i++) {
                    if (getChildAt(i).getVisibility() != 8) {
                        if (getChildAt(i).getMeasuredWidth() + currentW > getMeasuredWidth()) {
                            currentW = 0;
                            currentH += getChildAt(i).getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                        }
                        getChildAt(i).layout(currentW, currentH, getChildAt(i).getMeasuredWidth() + currentW, getChildAt(i).getMeasuredHeight() + currentH);
                        currentW += getChildAt(i).getMeasuredWidth() + AndroidUtilities.dp(16.0f);
                    }
                }
            }
        };
        this.legendLayout = frameLayout;
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 21.0f, 40.0f, 21.0f, 16.0f));
        TextView textView = new TextView(context);
        this.calculatingTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        String calculatingString = LocaleController.getString("CalculatingSize", R.string.CalculatingSize);
        int indexOfDots = calculatingString.indexOf("...");
        if (indexOfDots >= 0) {
            SpannableString spannableString = new SpannableString(calculatingString);
            EllipsizeSpanAnimator ellipsizeSpanAnimator = new EllipsizeSpanAnimator(this.calculatingTextView);
            this.ellipsizeSpanAnimator = ellipsizeSpanAnimator;
            ellipsizeSpanAnimator.wrap(spannableString, indexOfDots);
            this.calculatingTextView.setText(spannableString);
        } else {
            this.calculatingTextView.setText(calculatingString);
        }
        TextView textView2 = new TextView(context);
        this.telegramCacheTextView = textView2;
        textView2.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.telegramCacheTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        TextView textView3 = new TextView(context);
        this.telegramDatabaseTextView = textView3;
        textView3.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.telegramDatabaseTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        TextView textView4 = new TextView(context);
        this.freeSizeTextView = textView4;
        textView4.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.freeSizeTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        TextView textView5 = new TextView(context);
        this.totlaSizeTextView = textView5;
        textView5.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.totlaSizeTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.lastProgressColor = Theme.getColor(Theme.key_player_progress);
        this.telegramCacheTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), this.lastProgressColor), (Drawable) null, (Drawable) null, (Drawable) null);
        this.telegramCacheTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.freeSizeTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), ColorUtils.setAlphaComponent(this.lastProgressColor, 64)), (Drawable) null, (Drawable) null, (Drawable) null);
        this.freeSizeTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.totlaSizeTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), ColorUtils.setAlphaComponent(this.lastProgressColor, 127)), (Drawable) null, (Drawable) null, (Drawable) null);
        this.totlaSizeTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.telegramDatabaseTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), this.lastProgressColor), (Drawable) null, (Drawable) null, (Drawable) null);
        this.telegramDatabaseTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        this.legendLayout.addView(this.calculatingTextView, LayoutHelper.createFrame(-2, -2.0f));
        this.legendLayout.addView(this.telegramDatabaseTextView, LayoutHelper.createFrame(-2, -2.0f));
        this.legendLayout.addView(this.telegramCacheTextView, LayoutHelper.createFrame(-2, -2.0f));
        this.legendLayout.addView(this.totlaSizeTextView, LayoutHelper.createFrame(-2, -2.0f));
        this.legendLayout.addView(this.freeSizeTextView, LayoutHelper.createFrame(-2, -2.0f));
        View view = new View(getContext());
        this.divider = view;
        linearLayout.addView(view, LayoutHelper.createLinear(-1, -2, 0, 21, 0, 0, 0));
        this.divider.getLayoutParams().height = 1;
        this.divider.setBackgroundColor(Theme.getColor(Theme.key_divider));
        TextSettingsCell textSettingsCell = new TextSettingsCell(getContext());
        this.textSettingsCell = textSettingsCell;
        linearLayout.addView(textSettingsCell, LayoutHelper.createLinear(-1, -2));
    }

    public void setStorageUsage(boolean calculating, long database, long totalSize, long totalDeviceFreeSize, long totalDeviceSize) {
        this.calculating = calculating;
        this.totalSize = totalSize;
        this.totalDeviceFreeSize = totalDeviceFreeSize;
        this.totalDeviceSize = totalDeviceSize;
        this.freeSizeTextView.setText(LocaleController.formatString("TotalDeviceFreeSize", R.string.TotalDeviceFreeSize, AndroidUtilities.formatFileSize(totalDeviceFreeSize)));
        this.totlaSizeTextView.setText(LocaleController.formatString("TotalDeviceSize", R.string.TotalDeviceSize, AndroidUtilities.formatFileSize(totalDeviceSize - totalDeviceFreeSize)));
        if (calculating) {
            this.calculatingTextView.setVisibility(0);
            this.telegramCacheTextView.setVisibility(8);
            this.freeSizeTextView.setVisibility(8);
            this.totlaSizeTextView.setVisibility(8);
            this.telegramDatabaseTextView.setVisibility(8);
            this.divider.setVisibility(8);
            this.textSettingsCell.setVisibility(8);
            this.progress = 0.0f;
            this.progress2 = 0.0f;
            EllipsizeSpanAnimator ellipsizeSpanAnimator = this.ellipsizeSpanAnimator;
            if (ellipsizeSpanAnimator != null) {
                ellipsizeSpanAnimator.addView(this.calculatingTextView);
            }
        } else {
            EllipsizeSpanAnimator ellipsizeSpanAnimator2 = this.ellipsizeSpanAnimator;
            if (ellipsizeSpanAnimator2 != null) {
                ellipsizeSpanAnimator2.removeView(this.calculatingTextView);
            }
            this.calculatingTextView.setVisibility(8);
            if (totalSize <= 0) {
                this.telegramCacheTextView.setVisibility(8);
                this.telegramDatabaseTextView.setVisibility(0);
                this.telegramDatabaseTextView.setText(LocaleController.formatString("LocalDatabaseSize", R.string.LocalDatabaseSize, AndroidUtilities.formatFileSize(database)));
                this.divider.setVisibility(8);
                this.textSettingsCell.setVisibility(8);
            } else {
                this.divider.setVisibility(0);
                this.textSettingsCell.setVisibility(0);
                this.telegramCacheTextView.setVisibility(0);
                this.telegramDatabaseTextView.setVisibility(8);
                this.textSettingsCell.setText(LocaleController.getString("ClearTelegramCache", R.string.ClearTelegramCache), false);
                this.telegramCacheTextView.setText(LocaleController.formatString("TelegramCacheSize", R.string.TelegramCacheSize, AndroidUtilities.formatFileSize(totalSize + database)));
            }
            this.freeSizeTextView.setVisibility(0);
            this.totlaSizeTextView.setVisibility(0);
            float p = ((float) (totalSize + database)) / ((float) totalDeviceSize);
            float p2 = ((float) (totalDeviceSize - totalDeviceFreeSize)) / ((float) totalDeviceSize);
            if (this.progress != p) {
                ValueAnimator valueAnimator = this.valueAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.progress, p);
                this.valueAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.StroageUsageView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        StroageUsageView.this.m3115xb842a2b9(valueAnimator2);
                    }
                });
                this.valueAnimator.start();
            }
            if (this.progress2 != p2) {
                ValueAnimator valueAnimator2 = this.valueAnimator2;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(this.progress2, p2);
                this.valueAnimator2 = ofFloat2;
                ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.StroageUsageView$$ExternalSyntheticLambda1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                        StroageUsageView.this.m3116x52e3653a(valueAnimator3);
                    }
                });
                this.valueAnimator2.start();
            }
        }
        this.textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        requestLayout();
    }

    /* renamed from: lambda$setStorageUsage$0$org-telegram-ui-Components-StroageUsageView */
    public /* synthetic */ void m3115xb842a2b9(ValueAnimator animation) {
        this.progress = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$setStorageUsage$1$org-telegram-ui-Components-StroageUsageView */
    public /* synthetic */ void m3116x52e3653a(ValueAnimator animation) {
        this.progress2 = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        this.progressView.invalidate();
        if (this.lastProgressColor != Theme.getColor(Theme.key_player_progress)) {
            this.lastProgressColor = Theme.getColor(Theme.key_player_progress);
            this.telegramCacheTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), this.lastProgressColor), (Drawable) null, (Drawable) null, (Drawable) null);
            this.telegramCacheTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
            this.telegramDatabaseTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), this.lastProgressColor), (Drawable) null, (Drawable) null, (Drawable) null);
            this.telegramDatabaseTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
            this.freeSizeTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), ColorUtils.setAlphaComponent(this.lastProgressColor, 64)), (Drawable) null, (Drawable) null, (Drawable) null);
            this.freeSizeTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
            this.totlaSizeTextView.setCompoundDrawablesWithIntrinsicBounds(Theme.createCircleDrawable(AndroidUtilities.dp(10.0f), ColorUtils.setAlphaComponent(this.lastProgressColor, 127)), (Drawable) null, (Drawable) null, (Drawable) null);
            this.totlaSizeTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6.0f));
        }
        this.textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.divider.setBackgroundColor(Theme.getColor(Theme.key_divider));
    }

    /* loaded from: classes5.dex */
    public class ProgressView extends View {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ProgressView(Context context) {
            super(context);
            StroageUsageView.this = r1;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int color = Theme.getColor(Theme.key_player_progress);
            StroageUsageView.this.paintFill.setColor(color);
            StroageUsageView.this.paintProgress.setColor(color);
            StroageUsageView.this.paintProgress2.setColor(color);
            StroageUsageView.this.paintProgress.setAlpha(255);
            StroageUsageView.this.paintProgress2.setAlpha(82);
            StroageUsageView.this.paintFill.setAlpha(46);
            StroageUsageView.this.bgPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            canvas.drawLine(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(20.0f), getMeasuredWidth() - AndroidUtilities.dp(24.0f), AndroidUtilities.dp(20.0f), StroageUsageView.this.paintFill);
            if (StroageUsageView.this.calculating || StroageUsageView.this.calculatingProgress != 0.0f) {
                if (StroageUsageView.this.calculating) {
                    if (StroageUsageView.this.calculatingProgressIncrement) {
                        StroageUsageView.this.calculatingProgress += 0.024615385f;
                        if (StroageUsageView.this.calculatingProgress > 1.0f) {
                            StroageUsageView.this.calculatingProgress = 1.0f;
                            StroageUsageView.this.calculatingProgressIncrement = false;
                        }
                    } else {
                        StroageUsageView.this.calculatingProgress -= 0.024615385f;
                        if (StroageUsageView.this.calculatingProgress < 0.0f) {
                            StroageUsageView.this.calculatingProgress = 0.0f;
                            StroageUsageView.this.calculatingProgressIncrement = true;
                        }
                    }
                } else {
                    StroageUsageView.this.calculatingProgress -= 0.10666667f;
                    if (StroageUsageView.this.calculatingProgress < 0.0f) {
                        StroageUsageView.this.calculatingProgress = 0.0f;
                    }
                }
                invalidate();
                AndroidUtilities.rectTmp.set(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(17.0f), getMeasuredWidth() - AndroidUtilities.dp(24.0f), AndroidUtilities.dp(23.0f));
                StroageUsageView.this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
                StroageUsageView.this.cellFlickerDrawable.draw(canvas, AndroidUtilities.rectTmp, AndroidUtilities.dp(3.0f), null);
            }
            int currentP = AndroidUtilities.dp(24.0f);
            if (!StroageUsageView.this.calculating) {
                int progressWidth = (int) ((getMeasuredWidth() - (AndroidUtilities.dp(24.0f) * 2)) * StroageUsageView.this.progress2);
                int left = AndroidUtilities.dp(24.0f) + progressWidth;
                canvas.drawLine(currentP, AndroidUtilities.dp(20.0f), AndroidUtilities.dp(24.0f) + progressWidth, AndroidUtilities.dp(20.0f), StroageUsageView.this.paintProgress2);
                canvas.drawRect(left, AndroidUtilities.dp(20.0f) - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f) + left, AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(3.0f), StroageUsageView.this.bgPaint);
            }
            if (!StroageUsageView.this.calculating) {
                int progressWidth2 = (int) ((getMeasuredWidth() - (AndroidUtilities.dp(24.0f) * 2)) * StroageUsageView.this.progress);
                if (progressWidth2 < AndroidUtilities.dp(1.0f)) {
                    progressWidth2 = AndroidUtilities.dp(1.0f);
                }
                int left2 = AndroidUtilities.dp(24.0f) + progressWidth2;
                canvas.drawLine(currentP, AndroidUtilities.dp(20.0f), AndroidUtilities.dp(24.0f) + progressWidth2, AndroidUtilities.dp(20.0f), StroageUsageView.this.paintProgress);
                canvas.drawRect(left2, AndroidUtilities.dp(20.0f) - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f) + left2, AndroidUtilities.dp(20.0f) + AndroidUtilities.dp(3.0f), StroageUsageView.this.bgPaint);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EllipsizeSpanAnimator ellipsizeSpanAnimator = this.ellipsizeSpanAnimator;
        if (ellipsizeSpanAnimator != null) {
            ellipsizeSpanAnimator.onAttachedToWindow();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EllipsizeSpanAnimator ellipsizeSpanAnimator = this.ellipsizeSpanAnimator;
        if (ellipsizeSpanAnimator != null) {
            ellipsizeSpanAnimator.onDetachedFromWindow();
        }
    }
}
