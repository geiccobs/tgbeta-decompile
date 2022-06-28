package org.telegram.ui;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PopupSwipeBackLayout;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.Switch;
/* loaded from: classes4.dex */
public class vibroslider extends BaseFragment {
    private long duration1 = 50;
    private long duration2 = 50;
    private long duration3 = 50;
    private int amplitude1 = 50;
    private int amplitude2 = 50;
    private int amplitude3 = 50;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        FrameLayout fragmentView = new FrameLayout(context);
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(1);
        fragmentView.addView(ll, LayoutHelper.createFrame(-1, -1.0f, 48, 16.0f, 16.0f, 16.0f, 16.0f));
        ll.addView(new Slider(context, 0, 100, 50, new PopupSwipeBackLayout.IntCallback() { // from class: org.telegram.ui.vibroslider$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.PopupSwipeBackLayout.IntCallback
            public final void run(int i) {
                vibroslider.this.m4820lambda$createView$0$orgtelegramuivibroslider(i);
            }
        }), LayoutHelper.createLinear(-1, -2));
        ll.addView(new Slider(context, 0, 100, 50, new PopupSwipeBackLayout.IntCallback() { // from class: org.telegram.ui.vibroslider$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.PopupSwipeBackLayout.IntCallback
            public final void run(int i) {
                vibroslider.this.m4821lambda$createView$1$orgtelegramuivibroslider(i);
            }
        }), LayoutHelper.createLinear(-1, -2));
        ll.addView(new Slider(context, 0, 100, 50, new PopupSwipeBackLayout.IntCallback() { // from class: org.telegram.ui.vibroslider$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.PopupSwipeBackLayout.IntCallback
            public final void run(int i) {
                vibroslider.this.m4822lambda$createView$2$orgtelegramuivibroslider(i);
            }
        }), LayoutHelper.createLinear(-1, -2));
        ll.addView(new Slider(context, 0, 255, 50, new PopupSwipeBackLayout.IntCallback() { // from class: org.telegram.ui.vibroslider$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.PopupSwipeBackLayout.IntCallback
            public final void run(int i) {
                vibroslider.this.m4823lambda$createView$3$orgtelegramuivibroslider(i);
            }
        }), LayoutHelper.createLinear(-1, -2));
        ll.addView(new Slider(context, 0, 255, 50, new PopupSwipeBackLayout.IntCallback() { // from class: org.telegram.ui.vibroslider$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.Components.PopupSwipeBackLayout.IntCallback
            public final void run(int i) {
                vibroslider.this.m4824lambda$createView$4$orgtelegramuivibroslider(i);
            }
        }), LayoutHelper.createLinear(-1, -2));
        ll.addView(new Slider(context, 0, 255, 50, new PopupSwipeBackLayout.IntCallback() { // from class: org.telegram.ui.vibroslider$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.PopupSwipeBackLayout.IntCallback
            public final void run(int i) {
                vibroslider.this.m4825lambda$createView$5$orgtelegramuivibroslider(i);
            }
        }), LayoutHelper.createLinear(-1, -2));
        FrameLayout button = new FrameLayout(context);
        button.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
        button.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.vibroslider$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                vibroslider.lambda$createView$6(context, view);
            }
        });
        ll.addView(button, LayoutHelper.createLinear(-1, 48, 4.0f, 80.0f, 4.0f, 4.0f));
        final Switch switchView = new Switch(context);
        switchView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.vibroslider$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                Switch r0 = Switch.this;
                r0.setChecked(!r0.isChecked(), true);
            }
        });
        ll.addView(switchView);
        return fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-vibroslider */
    public /* synthetic */ void m4820lambda$createView$0$orgtelegramuivibroslider(int a) {
        this.duration1 = a;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-vibroslider */
    public /* synthetic */ void m4821lambda$createView$1$orgtelegramuivibroslider(int a) {
        this.duration2 = a;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-vibroslider */
    public /* synthetic */ void m4822lambda$createView$2$orgtelegramuivibroslider(int a) {
        this.duration3 = a;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-vibroslider */
    public /* synthetic */ void m4823lambda$createView$3$orgtelegramuivibroslider(int a) {
        this.amplitude1 = a;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-vibroslider */
    public /* synthetic */ void m4824lambda$createView$4$orgtelegramuivibroslider(int a) {
        this.amplitude2 = a;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-vibroslider */
    public /* synthetic */ void m4825lambda$createView$5$orgtelegramuivibroslider(int a) {
        this.amplitude3 = a;
    }

    public static /* synthetic */ void lambda$createView$6(Context context, View e) {
        if (Build.VERSION.SDK_INT >= 26) {
            Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(new long[]{100, 20, 10}, new int[]{5, 0, 255}, -1);
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect);
        }
    }

    /* loaded from: classes4.dex */
    public class Slider extends FrameLayout {
        private int max;
        private int min;
        private PopupSwipeBackLayout.IntCallback onChange;
        private int value;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public Slider(Context context, final int min, final int max, int initialValue, final PopupSwipeBackLayout.IntCallback onChange) {
            super(context);
            vibroslider.this = this$0;
            this.min = min;
            this.max = max;
            this.value = initialValue;
            this.onChange = onChange;
            final TextView textView = new TextView(context);
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(textView, LayoutHelper.createFrame(-2, -2, 19));
            SeekBarView seekBarView = new SeekBarView(context);
            seekBarView.setReportChanges(true);
            seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.vibroslider.Slider.1
                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public /* synthetic */ CharSequence getContentDescription() {
                    return SeekBarView.SeekBarViewDelegate.CC.$default$getContentDescription(this);
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public /* synthetic */ int getStepsCount() {
                    return SeekBarView.SeekBarViewDelegate.CC.$default$getStepsCount(this);
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarDrag(boolean stop, float progress) {
                    Slider.this.value = AndroidUtilities.lerp(min, max, progress);
                    TextView textView2 = textView;
                    textView2.setText(Slider.this.value + "");
                    onChange.run(Slider.this.value);
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarPressed(boolean pressed) {
                }
            });
            textView.setText(this.value + "");
            seekBarView.setProgress(((float) (this.value - min)) / ((float) (max - min)));
            addView(seekBarView, LayoutHelper.createFrame(-1, 38.0f, 23, 24.0f, 0.0f, 0.0f, 0.0f));
        }
    }
}
