package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class ThemeCell extends FrameLayout {
    private static byte[] bytes = new byte[1024];
    private ImageView checkImage;
    private Theme.ThemeInfo currentThemeInfo;
    private boolean isNightTheme;
    private boolean needDivider;
    private ImageView optionsButton;
    private Paint paint = new Paint(1);
    private Paint paintStroke;
    private TextView textView;

    public ThemeCell(Context context, boolean nightTheme) {
        super(context);
        setWillNotDraw(false);
        this.isNightTheme = nightTheme;
        Paint paint = new Paint(1);
        this.paintStroke = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.paintStroke.setStrokeWidth(AndroidUtilities.dp(2.0f));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 105.0f : 60.0f, 0.0f, LocaleController.isRTL ? 60.0f : 105.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.checkImage = imageView;
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY));
        this.checkImage.setImageResource(R.drawable.sticker_added);
        if (!this.isNightTheme) {
            addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : 5) | 16, 59.0f, 0.0f, 59.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.optionsButton = imageView2;
            imageView2.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            this.optionsButton.setImageResource(R.drawable.ic_ab_other);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            addView(this.optionsButton, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : i) | 48));
            return;
        }
        addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : i) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY));
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
    }

    public void setOnOptionsClick(View.OnClickListener listener) {
        this.optionsButton.setOnClickListener(listener);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public Theme.ThemeInfo getCurrentThemeInfo() {
        return this.currentThemeInfo;
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x00b6, code lost:
        r0 = r0.substring(r0 + 1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00c2, code lost:
        if (r0.length() <= 0) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00c4, code lost:
        r2 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00d1, code lost:
        if (r2.charAt(0) != '#') goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00d3, code lost:
        r0 = android.graphics.Color.parseColor(r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00db, code lost:
        r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2).intValue();
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00e5, code lost:
        r2 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00eb, code lost:
        r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2).intValue();
     */
    /* JADX WARN: Removed duplicated region for block: B:105:0x013f A[EDGE_INSN: B:105:0x013f->B:60:0x013f ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:112:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x012c A[LOOP:0: B:16:0x0061->B:57:0x012c, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0152 A[Catch: Exception -> 0x0143, TRY_ENTER, TRY_LEAVE, TryCatch #5 {Exception -> 0x0143, blocks: (B:60:0x013f, B:67:0x0152), top: B:99:0x0053 }] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x016b  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0178  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x017f  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0189  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r23, boolean r24) {
        /*
            Method dump skipped, instructions count: 405
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemeCell.setTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean):void");
    }

    public void updateCurrentThemeCheck() {
        Theme.ThemeInfo currentTheme;
        if (this.isNightTheme) {
            currentTheme = Theme.getCurrentNightTheme();
        } else {
            currentTheme = Theme.getCurrentTheme();
        }
        int newVisibility = this.currentThemeInfo == currentTheme ? 0 : 4;
        if (this.checkImage.getVisibility() != newVisibility) {
            this.checkImage.setVisibility(newVisibility);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
        int x = AndroidUtilities.dp(31.0f);
        if (LocaleController.isRTL) {
            x = getWidth() - x;
        }
        canvas.drawCircle(x, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(11.0f), this.paint);
        canvas.drawCircle(x, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(10.0f), this.paintStroke);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        setSelected(this.checkImage.getVisibility() == 0);
    }
}
