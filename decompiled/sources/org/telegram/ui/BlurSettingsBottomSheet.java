package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;
/* loaded from: classes4.dex */
public class BlurSettingsBottomSheet extends BottomSheet {
    BaseFragment fragment;
    public static float saturation = 0.2f;
    public static float blurRadius = 1.0f;
    public static float blurAlpha = 0.176f;

    public static void show(ChatActivity fragment) {
        new BlurSettingsBottomSheet(fragment).show();
    }

    private BlurSettingsBottomSheet(final ChatActivity fragment) {
        super(fragment.getParentActivity(), false);
        this.fragment = fragment;
        Context context = fragment.getParentActivity();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        final TextView saturationTextView = new TextView(context);
        saturationTextView.setText("Saturation " + (saturation * 5.0f));
        saturationTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        saturationTextView.setTextSize(1, 16.0f);
        saturationTextView.setLines(1);
        saturationTextView.setMaxLines(1);
        saturationTextView.setSingleLine(true);
        int i = 3;
        saturationTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(saturationTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBar = new SeekBarView(context);
        seekBar.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.BlurSettingsBottomSheet.1
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
                BlurSettingsBottomSheet.saturation = progress;
                TextView textView = saturationTextView;
                textView.setText("Saturation " + (5.0f * progress));
                fragment.contentView.invalidateBlurredViews();
                fragment.contentView.invalidateBlur();
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarPressed(boolean pressed) {
            }
        });
        seekBar.setReportChanges(true);
        linearLayout.addView(seekBar, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        final TextView alphaTextView = new TextView(context);
        alphaTextView.setText("Alpha " + blurAlpha);
        alphaTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        alphaTextView.setTextSize(1, 16.0f);
        alphaTextView.setLines(1);
        alphaTextView.setMaxLines(1);
        alphaTextView.setSingleLine(true);
        alphaTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(alphaTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBar3 = new SeekBarView(context);
        seekBar3.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.BlurSettingsBottomSheet.2
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
                TextView textView = alphaTextView;
                textView.setText("Alpha " + BlurSettingsBottomSheet.blurAlpha);
                BlurSettingsBottomSheet.blurAlpha = progress;
                fragment.contentView.invalidateBlur();
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarPressed(boolean pressed) {
            }
        });
        seekBar3.setReportChanges(true);
        linearLayout.addView(seekBar3, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        TextView radiusTextView = new TextView(context);
        radiusTextView.setText("Blur Radius");
        radiusTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        radiusTextView.setTextSize(1, 16.0f);
        radiusTextView.setLines(1);
        radiusTextView.setMaxLines(1);
        radiusTextView.setSingleLine(true);
        radiusTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        linearLayout.addView(radiusTextView, LayoutHelper.createFrame(-2, -1.0f, (!LocaleController.isRTL ? 5 : i) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        final SeekBarView seekBar2 = new SeekBarView(context);
        seekBar2.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.BlurSettingsBottomSheet.3
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
                BlurSettingsBottomSheet.blurRadius = progress;
                fragment.contentView.invalidateBlur();
                fragment.contentView.invalidateBlurredViews();
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarPressed(boolean pressed) {
                fragment.contentView.invalidateBlurredViews();
            }
        });
        seekBar2.setReportChanges(true);
        linearLayout.addView(seekBar2, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        linearLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: org.telegram.ui.BlurSettingsBottomSheet.4
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i2, int i1, int i22, int i3, int i4, int i5, int i6, int i7) {
                seekBar.setProgress(BlurSettingsBottomSheet.saturation);
                seekBar2.setProgress(BlurSettingsBottomSheet.blurRadius);
                seekBar3.setProgress(BlurSettingsBottomSheet.blurAlpha);
            }
        });
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }
}
