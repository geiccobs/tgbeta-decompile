package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class DrawerActionCell extends FrameLayout {
    private int currentId;
    private RectF rect = new RectF();
    private TextView textView;

    public DrawerActionCell(Context context) {
        super(context);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(19);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(29.0f));
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 51, 19.0f, 0.0f, 16.0f, 0.0f));
        setWillNotDraw(false);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.currentId == 8) {
            Set<String> suggestions = MessagesController.getInstance(UserConfig.selectedAccount).pendingSuggestions;
            if (suggestions.contains("VALIDATE_PHONE_NUMBER") || suggestions.contains("VALIDATE_PASSWORD")) {
                int countTop = AndroidUtilities.dp(12.5f);
                int countWidth = AndroidUtilities.dp(9.0f);
                int countLeft = (getMeasuredWidth() - countWidth) - AndroidUtilities.dp(25.0f);
                int x = countLeft - AndroidUtilities.dp(5.5f);
                this.rect.set(x, countTop, x + countWidth + AndroidUtilities.dp(14.0f), AndroidUtilities.dp(23.0f) + countTop);
                Theme.chat_docBackPaint.setColor(Theme.getColor(Theme.key_chats_archiveBackground));
                canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, Theme.chat_docBackPaint);
                int w = Theme.dialogs_errorDrawable.getIntrinsicWidth();
                int h = Theme.dialogs_errorDrawable.getIntrinsicHeight();
                Theme.dialogs_errorDrawable.setBounds((int) (this.rect.centerX() - (w / 2)), (int) (this.rect.centerY() - (h / 2)), (int) (this.rect.centerX() + (w / 2)), (int) (this.rect.centerY() + (h / 2)));
                Theme.dialogs_errorDrawable.draw(canvas);
            }
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
    }

    public void setTextAndIcon(int id, String text, int resId) {
        this.currentId = id;
        try {
            this.textView.setText(text);
            Drawable drawable = getResources().getDrawable(resId).mutate();
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_menuItemIcon), PorterDuff.Mode.MULTIPLY));
            }
            this.textView.setCompoundDrawablesWithIntrinsicBounds(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.addAction(16);
        info.addAction(32);
    }
}
