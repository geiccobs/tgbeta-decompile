package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ReactionTabHolderView extends FrameLayout {
    private int count;
    private TextView counterView;
    Drawable drawable;
    private ImageView iconView;
    private float outlineProgress;
    View overlaySelectorView;
    private BackupImageView reactView;
    private String reaction;
    private Paint outlinePaint = new Paint(1);
    private Paint bgPaint = new Paint(1);
    private Path path = new Path();
    private RectF rect = new RectF();
    private float radius = AndroidUtilities.dp(32.0f);

    public ReactionTabHolderView(Context context) {
        super(context);
        View view = new View(context);
        this.overlaySelectorView = view;
        addView(view, LayoutHelper.createFrame(-1, -1.0f));
        this.iconView = new ImageView(context);
        Drawable mutate = ContextCompat.getDrawable(context, R.drawable.msg_reactions_filled).mutate();
        this.drawable = mutate;
        this.iconView.setImageDrawable(mutate);
        addView(this.iconView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 8.0f, 0.0f, 8.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.reactView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 8.0f, 0.0f, 8.0f, 0.0f));
        TextView textView = new TextView(context);
        this.counterView = textView;
        textView.setImportantForAccessibility(2);
        this.counterView.setTextColor(Theme.getColor(Theme.key_avatar_nameInMessageBlue));
        this.counterView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        addView(this.counterView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388627, 40.0f, 0.0f, 8.0f, 0.0f));
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        this.outlinePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        setWillNotDraw(false);
        setOutlineProgress(this.outlineProgress);
    }

    public void setOutlineProgress(float outlineProgress) {
        this.outlineProgress = outlineProgress;
        int backgroundSelectedColor = Theme.getColor(Theme.key_chat_inReactionButtonBackground);
        int backgroundColor = ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_chat_inReactionButtonBackground), 16);
        int textSelectedColor = Theme.getColor(Theme.key_chat_inReactionButtonTextSelected);
        int textColor = Theme.getColor(Theme.key_chat_inReactionButtonText);
        int textFinalColor = ColorUtils.blendARGB(textColor, textSelectedColor, outlineProgress);
        this.bgPaint.setColor(ColorUtils.blendARGB(backgroundColor, backgroundSelectedColor, outlineProgress));
        this.counterView.setTextColor(textFinalColor);
        this.drawable.setColorFilter(new PorterDuffColorFilter(textFinalColor, PorterDuff.Mode.MULTIPLY));
        if (outlineProgress == 1.0f) {
            this.overlaySelectorView.setBackground(Theme.createSimpleSelectorRoundRectDrawable((int) this.radius, 0, ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_chat_inReactionButtonTextSelected), 76)));
        } else if (outlineProgress == 0.0f) {
            this.overlaySelectorView.setBackground(Theme.createSimpleSelectorRoundRectDrawable((int) this.radius, 0, ColorUtils.setAlphaComponent(backgroundSelectedColor, 76)));
        }
        invalidate();
    }

    public void setCounter(int count) {
        this.count = count;
        this.counterView.setText(String.format("%s", LocaleController.formatShortNumber(count, null)));
        this.iconView.setVisibility(0);
        this.reactView.setVisibility(8);
    }

    public void setCounter(int currentAccount, TLRPC.TL_reactionCount counter) {
        this.count = counter.count;
        this.counterView.setText(String.format("%s", LocaleController.formatShortNumber(counter.count, null)));
        String e = counter.reaction;
        this.reaction = null;
        for (TLRPC.TL_availableReaction r : MediaDataController.getInstance(currentAccount).getReactionsList()) {
            if (r.reaction.equals(e)) {
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(r.static_icon, Theme.key_windowBackgroundGray, 1.0f);
                this.reaction = r.reaction;
                this.reactView.setImage(ImageLocation.getForDocument(r.center_icon), "40_40_lastframe", "webp", svgThumb, r);
                this.reactView.setVisibility(0);
                this.iconView.setVisibility(8);
                return;
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        this.rect.set(0.0f, 0.0f, getWidth(), getHeight());
        RectF rectF = this.rect;
        float f = this.radius;
        canvas.drawRoundRect(rectF, f, f, this.bgPaint);
        super.dispatchDraw(canvas);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.Button");
        info.setClickable(true);
        if (this.outlineProgress > 0.5d) {
            info.setSelected(true);
        }
        String str = this.reaction;
        if (str == null) {
            info.setText(LocaleController.formatPluralString("AccDescrNumberOfReactions", this.count, new Object[0]));
        } else {
            info.setText(LocaleController.formatPluralString("AccDescrNumberOfPeopleReactions", this.count, str));
        }
    }
}
