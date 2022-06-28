package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.NumberPicker;
/* loaded from: classes5.dex */
public class SwipeGestureSettingsView extends FrameLayout {
    public static final int SWIPE_GESTURE_ARCHIVE = 2;
    public static final int SWIPE_GESTURE_DELETE = 4;
    public static final int SWIPE_GESTURE_FOLDERS = 5;
    public static final int SWIPE_GESTURE_MUTE = 3;
    public static final int SWIPE_GESTURE_PIN = 0;
    public static final int SWIPE_GESTURE_READ = 1;
    String currentColorKey;
    int currentIconIndex;
    int currentIconValue;
    int fromColor;
    boolean hasTabs;
    private NumberPicker picker;
    float progressToSwipeFolders;
    String[] strings;
    Runnable swapIconRunnable;
    Paint outlinePaint = new Paint(1);
    Paint filledPaint = new Paint(1);
    Paint linePaint = new Paint(1);
    Paint pickerDividersPaint = new Paint(1);
    RectF rect = new RectF();
    String[] backgroundKeys = new String[6];
    RLottieDrawable[] icons = new RLottieDrawable[6];
    RLottieImageView[] iconViews = new RLottieImageView[2];
    float colorProgress = 1.0f;

    public SwipeGestureSettingsView(Context context, int currentAccount) {
        super(context);
        int i;
        String[] strArr = new String[6];
        this.strings = strArr;
        float f = 1.0f;
        strArr[0] = LocaleController.getString("SwipeSettingsPin", R.string.SwipeSettingsPin);
        this.strings[1] = LocaleController.getString("SwipeSettingsRead", R.string.SwipeSettingsRead);
        this.strings[2] = LocaleController.getString("SwipeSettingsArchive", R.string.SwipeSettingsArchive);
        this.strings[3] = LocaleController.getString("SwipeSettingsMute", R.string.SwipeSettingsMute);
        this.strings[4] = LocaleController.getString("SwipeSettingsDelete", R.string.SwipeSettingsDelete);
        this.strings[5] = LocaleController.getString("SwipeSettingsFolders", R.string.SwipeSettingsFolders);
        String[] strArr2 = this.backgroundKeys;
        strArr2[0] = Theme.key_chats_archiveBackground;
        strArr2[1] = Theme.key_chats_archiveBackground;
        strArr2[2] = Theme.key_chats_archiveBackground;
        strArr2[3] = Theme.key_chats_archiveBackground;
        strArr2[4] = Theme.key_dialogSwipeRemove;
        strArr2[5] = Theme.key_chats_archivePinBackground;
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        this.outlinePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setStrokeCap(Paint.Cap.ROUND);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(5.0f));
        this.pickerDividersPaint.setStyle(Paint.Style.STROKE);
        this.pickerDividersPaint.setStrokeCap(Paint.Cap.ROUND);
        this.pickerDividersPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        NumberPicker numberPicker = new NumberPicker(context, 13) { // from class: org.telegram.ui.Components.SwipeGestureSettingsView.1
            @Override // org.telegram.ui.Components.NumberPicker, android.widget.LinearLayout, android.view.View
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float y = AndroidUtilities.dp(31.0f);
                SwipeGestureSettingsView.this.pickerDividersPaint.setColor(Theme.getColor(Theme.key_radioBackgroundChecked));
                canvas.drawLine(AndroidUtilities.dp(2.0f), y, getMeasuredWidth() - AndroidUtilities.dp(2.0f), y, SwipeGestureSettingsView.this.pickerDividersPaint);
                float y2 = getMeasuredHeight() - AndroidUtilities.dp(31.0f);
                canvas.drawLine(AndroidUtilities.dp(2.0f), y2, getMeasuredWidth() - AndroidUtilities.dp(2.0f), y2, SwipeGestureSettingsView.this.pickerDividersPaint);
            }
        };
        this.picker = numberPicker;
        numberPicker.setMinValue(0);
        this.picker.setDrawDividers(false);
        boolean z = !MessagesController.getInstance(currentAccount).dialogFilters.isEmpty();
        this.hasTabs = z;
        NumberPicker numberPicker2 = this.picker;
        if (!z) {
            i = this.strings.length - 2;
        } else {
            i = this.strings.length - 1;
        }
        numberPicker2.setMaxValue(i);
        this.picker.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.SwipeGestureSettingsView$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.NumberPicker.Formatter
            public final String format(int i2) {
                return SwipeGestureSettingsView.this.m3118lambda$new$0$orgtelegramuiComponentsSwipeGestureSettingsView(i2);
            }
        });
        this.picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.Components.SwipeGestureSettingsView$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
            public final void onValueChange(NumberPicker numberPicker3, int i2, int i3) {
                SwipeGestureSettingsView.this.m3119lambda$new$1$orgtelegramuiComponentsSwipeGestureSettingsView(numberPicker3, i2, i3);
            }
        });
        this.picker.setImportantForAccessibility(2);
        this.picker.setValue(SharedConfig.getChatSwipeAction(currentAccount));
        addView(this.picker, LayoutHelper.createFrame(132, -1.0f, 5, 21.0f, 0.0f, 21.0f, 0.0f));
        setWillNotDraw(false);
        this.currentIconIndex = 0;
        for (int i2 = 0; i2 < 2; i2++) {
            this.iconViews[i2] = new RLottieImageView(context);
            addView(this.iconViews[i2], LayoutHelper.createFrame(28, 28.0f, 21, 0.0f, 0.0f, 184.0f, 0.0f));
        }
        RLottieDrawable currentIcon = getIcon(this.picker.getValue());
        if (currentIcon != null) {
            this.iconViews[0].setImageDrawable(currentIcon);
            currentIcon.setCurrentFrame(currentIcon.getFramesCount() - 1);
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[0], true, 0.5f, false);
        AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[1], false, 0.5f, false);
        this.progressToSwipeFolders = this.picker.getValue() != 5 ? 0.0f : f;
        this.currentIconValue = this.picker.getValue();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SwipeGestureSettingsView */
    public /* synthetic */ String m3118lambda$new$0$orgtelegramuiComponentsSwipeGestureSettingsView(int value) {
        return this.strings[value];
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-SwipeGestureSettingsView */
    public /* synthetic */ void m3119lambda$new$1$orgtelegramuiComponentsSwipeGestureSettingsView(NumberPicker picker, int oldVal, int newVal) {
        swapIcons();
        SharedConfig.updateChatListSwipeSetting(newVal);
        invalidate();
        picker.performHapticFeedback(3, 2);
    }

    private void swapIcons() {
        int newValue;
        if (this.swapIconRunnable == null && this.currentIconValue != (newValue = this.picker.getValue())) {
            this.currentIconValue = newValue;
            int nextIconIndex = (this.currentIconIndex + 1) % 2;
            RLottieDrawable drawable = getIcon(newValue);
            if (drawable != null) {
                if (this.iconViews[nextIconIndex].getVisibility() != 0) {
                    drawable.setCurrentFrame(0, false);
                }
                this.iconViews[nextIconIndex].setAnimation(drawable);
                this.iconViews[nextIconIndex].playAnimation();
            } else {
                this.iconViews[nextIconIndex].clearAnimationDrawable();
            }
            AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[this.currentIconIndex], false, 0.5f, true);
            AndroidUtilities.updateViewVisibilityAnimated(this.iconViews[nextIconIndex], true, 0.5f, true);
            this.currentIconIndex = nextIconIndex;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.SwipeGestureSettingsView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SwipeGestureSettingsView.this.m3120x6d65954c();
                }
            };
            this.swapIconRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 150L);
        }
    }

    /* renamed from: lambda$swapIcons$2$org-telegram-ui-Components-SwipeGestureSettingsView */
    public /* synthetic */ void m3120x6d65954c() {
        this.swapIconRunnable = null;
        swapIcons();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(102.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x00b9  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x00d8  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0116  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onDraw(android.graphics.Canvas r18) {
        /*
            Method dump skipped, instructions count: 589
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SwipeGestureSettingsView.onDraw(android.graphics.Canvas):void");
    }

    public RLottieDrawable getIcon(int i) {
        int rawId;
        RLottieDrawable[] rLottieDrawableArr = this.icons;
        if (rLottieDrawableArr[i] == null) {
            switch (i) {
                case 1:
                    rawId = R.raw.swipe_read;
                    break;
                case 2:
                    rawId = R.raw.chats_archive;
                    break;
                case 3:
                    rawId = R.raw.swipe_mute;
                    break;
                case 4:
                    rawId = R.raw.swipe_delete;
                    break;
                case 5:
                    rawId = R.raw.swipe_disabled;
                    break;
                default:
                    rawId = R.raw.swipe_pin;
                    break;
            }
            rLottieDrawableArr[i] = new RLottieDrawable(rawId, "" + rawId, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);
            updateIconColor(i);
        }
        return this.icons[i];
    }

    public void updateIconColor(int i) {
        if (this.icons[i] != null) {
            int backgroundColor = ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhite), Theme.getColor(Theme.key_chats_archiveBackground), 0.9f);
            int iconColor = Theme.getColor(Theme.key_chats_archiveIcon);
            if (i == 2) {
                this.icons[i].setLayerColor("Arrow.**", backgroundColor);
                this.icons[i].setLayerColor("Box2.**", iconColor);
                this.icons[i].setLayerColor("Box1.**", iconColor);
                return;
            }
            this.icons[i].setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void updateColors() {
        for (int i = 0; i < this.icons.length; i++) {
            updateIconColor(i);
        }
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        updateColors();
        this.picker.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.picker.invalidate();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setEnabled(true);
        info.setContentDescription(this.strings[this.picker.getValue()]);
        if (Build.VERSION.SDK_INT >= 21) {
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, null));
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (event.getEventType() == 1) {
            int newValue = this.picker.getValue() + 1;
            if (newValue > this.picker.getMaxValue() || newValue < 0) {
                newValue = 0;
            }
            setContentDescription(this.strings[newValue]);
            this.picker.changeValueByOne(true);
        }
    }
}
