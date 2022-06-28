package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;
/* loaded from: classes4.dex */
public class AvailableReactionCell extends FrameLayout {
    private CheckBox2 checkBox;
    private BackupImageView imageView;
    private View overlaySelectorView;
    public TLRPC.TL_availableReaction react;
    private Switch switchView;
    private TextView textView;

    public AvailableReactionCell(Context context, boolean checkbox) {
        super(context);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity(LayoutHelper.getAbsoluteGravityStart() | 16);
        addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 81.0f, 0.0f, 61.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrameRelatively(32.0f, 32.0f, 8388627, 23.0f, 0.0f, 0.0f, 0.0f));
        if (checkbox) {
            CheckBox2 checkBox2 = new CheckBox2(context, 26, null);
            this.checkBox = checkBox2;
            checkBox2.setDrawUnchecked(false);
            this.checkBox.setColor(null, null, Theme.key_radioBackgroundChecked);
            this.checkBox.setDrawBackgroundAsArc(-1);
            addView(this.checkBox, LayoutHelper.createFrameRelatively(26.0f, 26.0f, 8388629, 0.0f, 0.0f, 22.0f, 0.0f));
        } else {
            Switch r2 = new Switch(context);
            this.switchView = r2;
            r2.setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
            addView(this.switchView, LayoutHelper.createFrameRelatively(37.0f, 20.0f, 8388629, 0.0f, 0.0f, 22.0f, 0.0f));
        }
        View view = new View(context);
        this.overlaySelectorView = view;
        view.setBackground(Theme.getSelectorDrawable(false));
        addView(this.overlaySelectorView, LayoutHelper.createFrame(-1, -1.0f));
        setWillNotDraw(false);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) (AndroidUtilities.dp(58.0f) + Theme.dividerPaint.getStrokeWidth()), C.BUFFER_FLAG_ENCRYPTED));
    }

    public void bind(TLRPC.TL_availableReaction react, boolean checked) {
        boolean animated = false;
        if (react != null && this.react != null && react.reaction.equals(this.react.reaction)) {
            animated = true;
        }
        this.react = react;
        this.textView.setText(react.title);
        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(react.static_icon, Theme.key_windowBackgroundGray, 1.0f);
        this.imageView.setImage(ImageLocation.getForDocument(react.center_icon), "40_40_lastframe", "webp", svgThumb, react);
        setChecked(checked, animated);
    }

    public void setChecked(boolean checked) {
        setChecked(checked, false);
    }

    public void setChecked(boolean checked, boolean animated) {
        Switch r0 = this.switchView;
        if (r0 != null) {
            r0.setChecked(checked, animated);
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(checked, animated);
        }
    }

    public boolean isChecked() {
        Switch r0 = this.switchView;
        if (r0 != null) {
            return r0.isChecked();
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            return checkBox2.isChecked();
        }
        return false;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        float w = Theme.dividerPaint.getStrokeWidth();
        int l = 0;
        int r = 0;
        int pad = AndroidUtilities.dp(81.0f);
        if (LocaleController.isRTL) {
            r = pad;
        } else {
            l = pad;
        }
        canvas.drawLine(getPaddingLeft() + l, getHeight() - w, (getWidth() - getPaddingRight()) - r, getHeight() - w, Theme.dividerPaint);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setEnabled(true);
        info.setClickable(true);
        if (this.switchView != null) {
            info.setCheckable(true);
            info.setChecked(isChecked());
            info.setClassName("android.widget.Switch");
        } else if (isChecked()) {
            info.setSelected(true);
        }
        info.setContentDescription(this.textView.getText());
    }
}
