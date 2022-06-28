package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class ActionBarMenuSubItem extends FrameLayout {
    boolean bottom;
    private CheckBox2 checkView;
    private int iconColor;
    private ImageView imageView;
    private int itemHeight;
    Runnable openSwipeBackLayout;
    private final Theme.ResourcesProvider resourcesProvider;
    private ImageView rightIcon;
    private int selectorColor;
    private TextView subtextView;
    private int textColor;
    private TextView textView;
    boolean top;

    public ActionBarMenuSubItem(Context context, boolean top, boolean bottom) {
        this(context, false, top, bottom);
    }

    public ActionBarMenuSubItem(Context context, boolean needCheck, boolean top, boolean bottom) {
        this(context, needCheck, top, bottom, null);
    }

    public ActionBarMenuSubItem(Context context, boolean top, boolean bottom, Theme.ResourcesProvider resourcesProvider) {
        this(context, false, top, bottom, resourcesProvider);
    }

    public ActionBarMenuSubItem(Context context, boolean needCheck, boolean top, boolean bottom, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.itemHeight = 48;
        this.resourcesProvider = resourcesProvider;
        this.top = top;
        this.bottom = bottom;
        this.textColor = getThemedColor(Theme.key_actionBarDefaultSubmenuItem);
        this.iconColor = getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon);
        this.selectorColor = getThemedColor(Theme.key_dialogButtonSelector);
        updateBackground();
        setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(this.iconColor, PorterDuff.Mode.MULTIPLY));
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(-2, 40, (LocaleController.isRTL ? 5 : 3) | 16));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(3);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setTextColor(this.textColor);
        this.textView.setTextSize(1, 16.0f);
        addView(this.textView, LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 5 : 3) | 16));
        if (needCheck) {
            CheckBox2 checkBox2 = new CheckBox2(context, 26, resourcesProvider);
            this.checkView = checkBox2;
            checkBox2.setDrawUnchecked(false);
            this.checkView.setColor(null, null, Theme.key_radioBackgroundChecked);
            this.checkView.setDrawBackgroundAsArc(-1);
            addView(this.checkView, LayoutHelper.createFrame(26, -1, (!LocaleController.isRTL ? 3 : i) | 16));
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.itemHeight), C.BUFFER_FLAG_ENCRYPTED));
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setChecked(boolean checked) {
        CheckBox2 checkBox2 = this.checkView;
        if (checkBox2 == null) {
            return;
        }
        checkBox2.setChecked(checked, true);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setEnabled(isEnabled());
        CheckBox2 checkBox2 = this.checkView;
        if (checkBox2 != null && checkBox2.isChecked()) {
            info.setCheckable(true);
            info.setChecked(this.checkView.isChecked());
            info.setClassName("android.widget.CheckBox");
        }
    }

    public void setCheckColor(String colorKey) {
        this.checkView.setColor(null, null, colorKey);
    }

    public void setRightIcon(int icon) {
        if (this.rightIcon == null) {
            ImageView imageView = new ImageView(getContext());
            this.rightIcon = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.rightIcon.setColorFilter(this.iconColor, PorterDuff.Mode.MULTIPLY);
            if (LocaleController.isRTL) {
                this.rightIcon.setScaleX(-1.0f);
            }
            addView(this.rightIcon, LayoutHelper.createFrame(24, -1, (LocaleController.isRTL ? 3 : 5) | 16));
        }
        float f = 8.0f;
        int dp = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : 18.0f);
        if (LocaleController.isRTL) {
            f = 18.0f;
        }
        setPadding(dp, 0, AndroidUtilities.dp(f), 0);
        this.rightIcon.setImageResource(icon);
    }

    public void setTextAndIcon(CharSequence text, int icon) {
        setTextAndIcon(text, icon, null);
    }

    public void setMultiline() {
        this.textView.setLines(2);
        this.textView.setTextSize(1, 14.0f);
        this.textView.setSingleLine(false);
        this.textView.setGravity(16);
    }

    public void setTextAndIcon(CharSequence text, int icon, Drawable iconDrawable) {
        this.textView.setText(text);
        if (icon != 0 || iconDrawable != null || this.checkView != null) {
            if (iconDrawable != null) {
                this.imageView.setImageDrawable(iconDrawable);
            } else {
                this.imageView.setImageResource(icon);
            }
            this.imageView.setVisibility(0);
            this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(43.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(43.0f) : 0, 0);
            return;
        }
        this.imageView.setVisibility(4);
        this.textView.setPadding(0, 0, 0, 0);
    }

    public ActionBarMenuSubItem setColors(int textColor, int iconColor) {
        setTextColor(textColor);
        setIconColor(iconColor);
        return this;
    }

    public void setTextColor(int textColor) {
        if (this.textColor != textColor) {
            TextView textView = this.textView;
            this.textColor = textColor;
            textView.setTextColor(textColor);
        }
    }

    public void setIconColor(int iconColor) {
        if (this.iconColor != iconColor) {
            ImageView imageView = this.imageView;
            this.iconColor = iconColor;
            imageView.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setIcon(int resId) {
        this.imageView.setImageResource(resId);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public void setSubtextColor(int color) {
        this.subtextView.setTextColor(color);
    }

    public void setSubtext(String text) {
        int i = 8;
        boolean oldVisible = true;
        int i2 = 0;
        if (this.subtextView == null) {
            TextView textView = new TextView(getContext());
            this.subtextView = textView;
            textView.setLines(1);
            this.subtextView.setSingleLine(true);
            int i3 = 3;
            this.subtextView.setGravity(3);
            this.subtextView.setEllipsize(TextUtils.TruncateAt.END);
            this.subtextView.setTextColor(-8617338);
            this.subtextView.setVisibility(8);
            this.subtextView.setTextSize(1, 13.0f);
            this.subtextView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(43.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(43.0f) : 0, 0);
            TextView textView2 = this.subtextView;
            if (LocaleController.isRTL) {
                i3 = 5;
            }
            addView(textView2, LayoutHelper.createFrame(-2, -2.0f, i3 | 16, 0.0f, 10.0f, 0.0f, 0.0f));
        }
        boolean visible = !TextUtils.isEmpty(text);
        if (this.subtextView.getVisibility() != 0) {
            oldVisible = false;
        }
        if (visible != oldVisible) {
            TextView textView3 = this.subtextView;
            if (visible) {
                i = 0;
            }
            textView3.setVisibility(i);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
            if (visible) {
                i2 = AndroidUtilities.dp(10.0f);
            }
            layoutParams.bottomMargin = i2;
            this.textView.setLayoutParams(layoutParams);
        }
        this.subtextView.setText(text);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public void setSelectorColor(int selectorColor) {
        if (this.selectorColor != selectorColor) {
            this.selectorColor = selectorColor;
            updateBackground();
        }
    }

    public void updateSelectorBackground(boolean top, boolean bottom) {
        if (this.top == top && this.bottom == bottom) {
            return;
        }
        this.top = top;
        this.bottom = bottom;
        updateBackground();
    }

    void updateBackground() {
        int bottomBackgroundRadius = 6;
        int topBackgroundRadius = this.top ? 6 : 0;
        if (!this.bottom) {
            bottomBackgroundRadius = 0;
        }
        setBackground(Theme.createRadSelectorDrawable(this.selectorColor, topBackgroundRadius, bottomBackgroundRadius));
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public CheckBox2 getCheckView() {
        return this.checkView;
    }

    public void openSwipeBack() {
        Runnable runnable = this.openSwipeBackLayout;
        if (runnable != null) {
            runnable.run();
        }
    }

    public ImageView getRightIcon() {
        return this.rightIcon;
    }
}
