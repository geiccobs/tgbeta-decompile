package org.telegram.ui.Charts.view_data;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
/* loaded from: classes4.dex */
public class LegendSignatureView extends FrameLayout {
    Drawable background;
    Drawable backgroundDrawable;
    public ImageView chevron;
    LinearLayout content;
    Holder[] holdes;
    TextView hourTime;
    public boolean isTopHourChart;
    private RadialProgressView progressView;
    Drawable shadowDrawable;
    public boolean showPercentage;
    TextView time;
    public boolean useHour;
    public boolean useWeek;
    public boolean zoomEnabled;
    SimpleDateFormat format = new SimpleDateFormat("E, ");
    SimpleDateFormat format2 = new SimpleDateFormat("MMM dd");
    SimpleDateFormat format3 = new SimpleDateFormat("d MMM yyyy");
    SimpleDateFormat format4 = new SimpleDateFormat("d MMM");
    SimpleDateFormat hourFormat = new SimpleDateFormat(" HH:mm");
    public boolean canGoZoom = true;
    Runnable showProgressRunnable = new Runnable() { // from class: org.telegram.ui.Charts.view_data.LegendSignatureView.1
        @Override // java.lang.Runnable
        public void run() {
            LegendSignatureView.this.chevron.animate().setDuration(120L).alpha(0.0f);
            LegendSignatureView.this.progressView.animate().setListener(null).start();
            if (LegendSignatureView.this.progressView.getVisibility() != 0) {
                LegendSignatureView.this.progressView.setVisibility(0);
                LegendSignatureView.this.progressView.setAlpha(0.0f);
            }
            LegendSignatureView.this.progressView.animate().setDuration(120L).alpha(1.0f).start();
        }
    };

    public LegendSignatureView(Context context) {
        super(context);
        setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        LinearLayout linearLayout = new LinearLayout(getContext());
        this.content = linearLayout;
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        this.time = textView;
        textView.setTextSize(1, 14.0f);
        this.time.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        TextView textView2 = new TextView(context);
        this.hourTime = textView2;
        textView2.setTextSize(1, 14.0f);
        this.hourTime.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        ImageView imageView = new ImageView(context);
        this.chevron = imageView;
        imageView.setImageResource(R.drawable.ic_chevron_right_black_18dp);
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(12.0f));
        this.progressView.setStrokeWidth(AndroidUtilities.dp(0.5f));
        this.progressView.setVisibility(8);
        addView(this.content, LayoutHelper.createFrame(-2, -2.0f, 0, 0.0f, 22.0f, 0.0f, 0.0f));
        addView(this.time, LayoutHelper.createFrame(-2, -2.0f, GravityCompat.START, 4.0f, 0.0f, 4.0f, 0.0f));
        addView(this.hourTime, LayoutHelper.createFrame(-2, -2.0f, GravityCompat.END, 4.0f, 0.0f, 4.0f, 0.0f));
        addView(this.chevron, LayoutHelper.createFrame(18, 18.0f, 8388661, 0.0f, 2.0f, 0.0f, 0.0f));
        addView(this.progressView, LayoutHelper.createFrame(18, 18.0f, 8388661, 0.0f, 2.0f, 0.0f, 0.0f));
        recolor();
    }

    public void recolor() {
        this.time.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.hourTime.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.chevron.setColorFilter(Theme.getColor(Theme.key_statisticChartChevronColor));
        this.progressView.setProgressColor(Theme.getColor(Theme.key_statisticChartChevronColor));
        this.shadowDrawable = getContext().getResources().getDrawable(R.drawable.stats_tooltip).mutate();
        this.backgroundDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_dialogBackground), Theme.getColor(Theme.key_listSelector), -16777216);
        CombinedDrawable drawable = new CombinedDrawable(this.shadowDrawable, this.backgroundDrawable, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
        drawable.setFullsize(true);
        setBackground(drawable);
    }

    public void setSize(int n) {
        this.content.removeAllViews();
        this.holdes = new Holder[n];
        for (int i = 0; i < n; i++) {
            this.holdes[i] = new Holder();
            this.content.addView(this.holdes[i].root);
        }
    }

    public void setData(int index, long date, ArrayList<LineViewData> lines, boolean animateChanges) {
        int n = this.holdes.length;
        int i = 0;
        if (animateChanges && Build.VERSION.SDK_INT >= 19) {
            TransitionSet transition = new TransitionSet();
            transition.addTransition(new Fade(2).setDuration(150L)).addTransition(new ChangeBounds().setDuration(150L)).addTransition(new Fade(1).setDuration(150L));
            transition.setOrdering(0);
            TransitionManager.beginDelayedTransition(this, transition);
        }
        if (this.isTopHourChart) {
            this.time.setText(String.format(Locale.ENGLISH, "%02d:00", Long.valueOf(date)));
        } else {
            if (!this.useWeek) {
                this.time.setText(formatData(new Date(date)));
            } else {
                this.time.setText(String.format("%s — %s", this.format4.format(new Date(date)), this.format3.format(new Date(604800000 + date))));
            }
            if (this.useHour) {
                this.hourTime.setText(this.hourFormat.format(Long.valueOf(date)));
            }
        }
        int sum = 0;
        for (int i2 = 0; i2 < n; i2++) {
            if (lines.get(i2).enabled) {
                sum += lines.get(i2).line.y[index];
            }
        }
        int i3 = 0;
        while (i3 < n) {
            Holder h = this.holdes[i3];
            if (!lines.get(i3).enabled) {
                h.root.setVisibility(8);
            } else {
                ChartData.Line l = lines.get(i3).line;
                if (h.root.getMeasuredHeight() == 0) {
                    h.root.requestLayout();
                }
                h.root.setVisibility(i);
                h.value.setText(formatWholeNumber(l.y[index]));
                h.signature.setText(l.name);
                if (l.colorKey != null && Theme.hasThemeKey(l.colorKey)) {
                    h.value.setTextColor(Theme.getColor(l.colorKey));
                } else {
                    h.value.setTextColor(Theme.getCurrentTheme().isDark() ? l.colorDark : l.color);
                }
                h.signature.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                if (this.showPercentage && h.percentage != null) {
                    h.percentage.setVisibility(i);
                    h.percentage.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    float v = lines.get(i3).line.y[index] / sum;
                    if (v < 0.1f && v != 0.0f) {
                        h.percentage.setText(String.format(Locale.ENGLISH, "%.1f%s", Float.valueOf(100.0f * v), "%"));
                    } else {
                        h.percentage.setText(String.format(Locale.ENGLISH, "%d%s", Integer.valueOf(Math.round(100.0f * v)), "%"));
                    }
                }
            }
            i3++;
            i = 0;
        }
        if (this.zoomEnabled) {
            this.canGoZoom = sum > 0;
            this.chevron.setVisibility(sum > 0 ? 0 : 8);
            return;
        }
        this.canGoZoom = false;
        this.chevron.setVisibility(8);
    }

    private String formatData(Date date) {
        if (this.useHour) {
            return capitalize(this.format2.format(date));
        }
        return capitalize(this.format.format(date)) + capitalize(this.format2.format(date));
    }

    private String capitalize(String s) {
        if (s.length() > 0) {
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
        return s;
    }

    public String formatWholeNumber(int v) {
        float num_ = v;
        int count = 0;
        if (v < 10000) {
            return String.format("%d", Integer.valueOf(v));
        }
        while (num_ >= 10000.0f && count < AndroidUtilities.numbersSignatureArray.length - 1) {
            num_ /= 1000.0f;
            count++;
        }
        return String.format("%.2f", Float.valueOf(num_)) + AndroidUtilities.numbersSignatureArray[count];
    }

    public void showProgress(boolean show, boolean force) {
        if (show) {
            AndroidUtilities.runOnUIThread(this.showProgressRunnable, 300L);
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.showProgressRunnable);
        if (force) {
            this.progressView.setVisibility(8);
            return;
        }
        this.chevron.animate().setDuration(80L).alpha(1.0f).start();
        if (this.progressView.getVisibility() == 0) {
            this.progressView.animate().setDuration(80L).alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Charts.view_data.LegendSignatureView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    LegendSignatureView.this.progressView.setVisibility(8);
                }
            }).start();
        }
    }

    public void setUseWeek(boolean useWeek) {
        this.useWeek = useWeek;
    }

    /* loaded from: classes4.dex */
    public class Holder {
        TextView percentage;
        final LinearLayout root;
        final TextView signature;
        final TextView value;

        Holder() {
            LegendSignatureView.this = this$0;
            LinearLayout linearLayout = new LinearLayout(this$0.getContext());
            this.root = linearLayout;
            linearLayout.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f));
            if (this$0.showPercentage) {
                TextView textView = new TextView(this$0.getContext());
                this.percentage = textView;
                linearLayout.addView(textView);
                this.percentage.getLayoutParams().width = AndroidUtilities.dp(36.0f);
                this.percentage.setVisibility(8);
                this.percentage.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.percentage.setTextSize(1, 13.0f);
            }
            TextView textView2 = new TextView(this$0.getContext());
            this.signature = textView2;
            linearLayout.addView(textView2);
            textView2.getLayoutParams().width = AndroidUtilities.dp(this$0.showPercentage ? 80.0f : 96.0f);
            TextView textView3 = new TextView(this$0.getContext());
            this.value = textView3;
            linearLayout.addView(textView3, LayoutHelper.createLinear(-1, -2));
            textView2.setGravity(GravityCompat.START);
            textView3.setGravity(GravityCompat.END);
            textView3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView3.setTextSize(1, 13.0f);
            textView3.setMinEms(4);
            textView3.setMaxEms(4);
            textView2.setTextSize(1, 13.0f);
        }
    }
}
