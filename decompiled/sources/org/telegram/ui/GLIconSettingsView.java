package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer;
import org.telegram.ui.Components.SeekBarView;
/* loaded from: classes4.dex */
public class GLIconSettingsView extends LinearLayout {
    public static float smallStarsSize = 1.0f;

    public GLIconSettingsView(final Context context, final GLIconRenderer mRenderer) {
        super(context);
        setOrientation(1);
        TextView saturationTextView = new TextView(context);
        saturationTextView.setText("Spectral top ");
        saturationTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        saturationTextView.setTextSize(1, 16.0f);
        saturationTextView.setLines(1);
        saturationTextView.setMaxLines(1);
        saturationTextView.setSingleLine(true);
        int i = 3;
        saturationTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        addView(saturationTextView, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        SeekBarView seekBar = new SeekBarView(context);
        seekBar.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.GLIconSettingsView.1
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
                mRenderer.star.spec1 = 2.0f * progress;
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarPressed(boolean pressed) {
            }
        });
        seekBar.setProgress(mRenderer.star.spec1 / 2.0f);
        seekBar.setReportChanges(true);
        addView(seekBar, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        TextView saturationTextView2 = new TextView(context);
        saturationTextView2.setText("Spectral bottom ");
        saturationTextView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        saturationTextView2.setTextSize(1, 16.0f);
        saturationTextView2.setLines(1);
        saturationTextView2.setMaxLines(1);
        saturationTextView2.setSingleLine(true);
        saturationTextView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        addView(saturationTextView2, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        SeekBarView seekBar2 = new SeekBarView(context);
        seekBar2.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.GLIconSettingsView.2
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
                mRenderer.star.spec2 = 2.0f * progress;
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarPressed(boolean pressed) {
            }
        });
        seekBar2.setProgress(mRenderer.star.spec2 / 2.0f);
        seekBar2.setReportChanges(true);
        addView(seekBar2, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        TextView saturationTextView3 = new TextView(context);
        saturationTextView3.setText("Setup spec color");
        saturationTextView3.setTextSize(1, 16.0f);
        saturationTextView3.setLines(1);
        saturationTextView3.setGravity(17);
        saturationTextView3.setMaxLines(1);
        saturationTextView3.setSingleLine(true);
        saturationTextView3.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        saturationTextView3.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(Theme.key_featuredStickers_addButton), 4.0f));
        saturationTextView3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GLIconSettingsView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                ColorPicker colorPicker = new ColorPicker(context, false, new ColorPicker.ColorPickerDelegate() { // from class: org.telegram.ui.GLIconSettingsView.3.1
                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public /* synthetic */ void deleteTheme() {
                        ColorPicker.ColorPickerDelegate.CC.$default$deleteTheme(this);
                    }

                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public /* synthetic */ int getDefaultColor(int i2) {
                        return ColorPicker.ColorPickerDelegate.CC.$default$getDefaultColor(this, i2);
                    }

                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public /* synthetic */ boolean hasChanges() {
                        return ColorPicker.ColorPickerDelegate.CC.$default$hasChanges(this);
                    }

                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public /* synthetic */ void openThemeCreate(boolean z) {
                        ColorPicker.ColorPickerDelegate.CC.$default$openThemeCreate(this, z);
                    }

                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public /* synthetic */ void rotateColors() {
                        ColorPicker.ColorPickerDelegate.CC.$default$rotateColors(this);
                    }

                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public void setColor(int color, int num, boolean applyNow) {
                        mRenderer.star.specColor = color;
                    }
                }) { // from class: org.telegram.ui.GLIconSettingsView.3.2
                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(300.0f), C.BUFFER_FLAG_ENCRYPTED));
                    }
                };
                colorPicker.setColor(mRenderer.star.specColor, 0);
                colorPicker.setType(-1, true, 1, 1, false, 0, false);
                BottomSheet bottomSheet = new BottomSheet(context, false);
                bottomSheet.setCustomView(colorPicker);
                bottomSheet.setDimBehind(false);
                bottomSheet.show();
            }
        });
        addView(saturationTextView3, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
        TextView saturationTextView4 = new TextView(context);
        saturationTextView4.setText("Diffuse ");
        saturationTextView4.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        saturationTextView4.setTextSize(1, 16.0f);
        saturationTextView4.setLines(1);
        saturationTextView4.setMaxLines(1);
        saturationTextView4.setSingleLine(true);
        saturationTextView4.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        addView(saturationTextView4, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        SeekBarView seekBar3 = new SeekBarView(context);
        seekBar3.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.GLIconSettingsView.4
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
                mRenderer.star.diffuse = progress;
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarPressed(boolean pressed) {
            }
        });
        seekBar3.setProgress(mRenderer.star.diffuse);
        seekBar3.setReportChanges(true);
        addView(seekBar3, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        TextView saturationTextView5 = new TextView(context);
        saturationTextView5.setText("Normal map spectral");
        saturationTextView5.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        saturationTextView5.setTextSize(1, 16.0f);
        saturationTextView5.setLines(1);
        saturationTextView5.setMaxLines(1);
        saturationTextView5.setSingleLine(true);
        saturationTextView5.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        addView(saturationTextView5, LayoutHelper.createFrame(-2, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        SeekBarView seekBar4 = new SeekBarView(context);
        seekBar4.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.GLIconSettingsView.5
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
                mRenderer.star.normalSpec = 2.0f * progress;
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarPressed(boolean pressed) {
            }
        });
        seekBar4.setProgress(mRenderer.star.normalSpec / 2.0f);
        seekBar4.setReportChanges(true);
        addView(seekBar4, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
        TextView saturationTextView6 = new TextView(context);
        saturationTextView6.setText("Setup normal spec color");
        saturationTextView6.setTextSize(1, 16.0f);
        saturationTextView6.setLines(1);
        saturationTextView6.setGravity(17);
        saturationTextView6.setMaxLines(1);
        saturationTextView6.setSingleLine(true);
        saturationTextView6.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        saturationTextView6.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(Theme.key_featuredStickers_addButton), 4.0f));
        saturationTextView6.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GLIconSettingsView.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                ColorPicker colorPicker = new ColorPicker(context, false, new ColorPicker.ColorPickerDelegate() { // from class: org.telegram.ui.GLIconSettingsView.6.1
                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public /* synthetic */ void deleteTheme() {
                        ColorPicker.ColorPickerDelegate.CC.$default$deleteTheme(this);
                    }

                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public /* synthetic */ int getDefaultColor(int i2) {
                        return ColorPicker.ColorPickerDelegate.CC.$default$getDefaultColor(this, i2);
                    }

                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public /* synthetic */ boolean hasChanges() {
                        return ColorPicker.ColorPickerDelegate.CC.$default$hasChanges(this);
                    }

                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public /* synthetic */ void openThemeCreate(boolean z) {
                        ColorPicker.ColorPickerDelegate.CC.$default$openThemeCreate(this, z);
                    }

                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public /* synthetic */ void rotateColors() {
                        ColorPicker.ColorPickerDelegate.CC.$default$rotateColors(this);
                    }

                    @Override // org.telegram.ui.Components.ColorPicker.ColorPickerDelegate
                    public void setColor(int color, int num, boolean applyNow) {
                        if (num == 0) {
                            mRenderer.star.normalSpecColor = color;
                        }
                    }
                }) { // from class: org.telegram.ui.GLIconSettingsView.6.2
                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(300.0f), C.BUFFER_FLAG_ENCRYPTED));
                    }
                };
                colorPicker.setColor(mRenderer.star.normalSpecColor, 0);
                colorPicker.setType(-1, true, 1, 1, false, 0, false);
                BottomSheet bottomSheet = new BottomSheet(context, false);
                bottomSheet.setCustomView(colorPicker);
                bottomSheet.setDimBehind(false);
                bottomSheet.show();
            }
        });
        addView(saturationTextView6, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
        TextView saturationTextView7 = new TextView(context);
        saturationTextView7.setText("Small starts size");
        saturationTextView7.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        saturationTextView7.setTextSize(1, 16.0f);
        saturationTextView7.setLines(1);
        saturationTextView7.setMaxLines(1);
        saturationTextView7.setSingleLine(true);
        saturationTextView7.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        addView(saturationTextView7, LayoutHelper.createFrame(-2, -1.0f, (!LocaleController.isRTL ? 5 : i) | 48, 21.0f, 13.0f, 21.0f, 0.0f));
        SeekBarView seekBar5 = new SeekBarView(context);
        seekBar5.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: org.telegram.ui.GLIconSettingsView.7
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
                GLIconSettingsView.smallStarsSize = 2.0f * progress;
            }

            @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
            public void onSeekBarPressed(boolean pressed) {
            }
        });
        seekBar5.setProgress(smallStarsSize / 2.0f);
        seekBar5.setReportChanges(true);
        addView(seekBar5, LayoutHelper.createFrame(-1, 38.0f, 0, 5.0f, 4.0f, 5.0f, 0.0f));
    }
}
