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
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
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
    private RectF rect = new RectF();
    private float radius = AndroidUtilities.dp(32.0f);

    public ReactionTabHolderView(Context context) {
        super(context);
        new Path();
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
        this.counterView.setTextColor(Theme.getColor("avatar_nameInMessageBlue"));
        this.counterView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        addView(this.counterView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388627, 40.0f, 0.0f, 8.0f, 0.0f));
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        this.outlinePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        setWillNotDraw(false);
        setOutlineProgress(this.outlineProgress);
    }

    public void setOutlineProgress(float f) {
        this.outlineProgress = f;
        int color = Theme.getColor("chat_inReactionButtonBackground");
        int alphaComponent = ColorUtils.setAlphaComponent(Theme.getColor("chat_inReactionButtonBackground"), 16);
        int blendARGB = ColorUtils.blendARGB(Theme.getColor("chat_inReactionButtonText"), Theme.getColor("chat_inReactionButtonTextSelected"), f);
        this.bgPaint.setColor(ColorUtils.blendARGB(alphaComponent, color, f));
        this.counterView.setTextColor(blendARGB);
        this.drawable.setColorFilter(new PorterDuffColorFilter(blendARGB, PorterDuff.Mode.MULTIPLY));
        if (f == 1.0f) {
            this.overlaySelectorView.setBackground(Theme.createSimpleSelectorRoundRectDrawable((int) this.radius, 0, ColorUtils.setAlphaComponent(Theme.getColor("chat_inReactionButtonTextSelected"), 76)));
        } else if (f == 0.0f) {
            this.overlaySelectorView.setBackground(Theme.createSimpleSelectorRoundRectDrawable((int) this.radius, 0, ColorUtils.setAlphaComponent(color, 76)));
        }
        invalidate();
    }

    public void setCounter(int i) {
        this.count = i;
        this.counterView.setText(String.format("%s", LocaleController.formatShortNumber(i, null)));
        this.iconView.setVisibility(0);
        this.reactView.setVisibility(8);
    }

    public void setCounter(int i, TLRPC$TL_reactionCount tLRPC$TL_reactionCount) {
        int i2 = tLRPC$TL_reactionCount.count;
        this.count = i2;
        this.counterView.setText(String.format("%s", LocaleController.formatShortNumber(i2, null)));
        String str = tLRPC$TL_reactionCount.reaction;
        this.reaction = null;
        for (TLRPC$TL_availableReaction tLRPC$TL_availableReaction : MediaDataController.getInstance(i).getReactionsList()) {
            if (tLRPC$TL_availableReaction.reaction.equals(str)) {
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$TL_availableReaction.static_icon, "windowBackgroundGray", 1.0f);
                this.reaction = tLRPC$TL_availableReaction.reaction;
                this.reactView.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.center_icon), "40_40_lastframe", "webp", svgThumb, tLRPC$TL_availableReaction);
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
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.Button");
        accessibilityNodeInfo.setClickable(true);
        if (this.outlineProgress > 0.5d) {
            accessibilityNodeInfo.setSelected(true);
        }
        String str = this.reaction;
        if (str != null) {
            accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrNumberOfPeopleReactions", this.count, str));
        } else {
            accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrNumberOfReactions", this.count, new Object[0]));
        }
    }
}
