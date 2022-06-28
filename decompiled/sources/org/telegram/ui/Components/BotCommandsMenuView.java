package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class BotCommandsMenuView extends View {
    final MenuDrawable backDrawable;
    Drawable backgroundDrawable;
    float expandProgress;
    boolean expanded;
    boolean isOpened;
    boolean isWebView;
    boolean isWebViewOpened;
    int lastSize;
    StaticLayout menuTextLayout;
    final TextPaint textPaint;
    final RectF rectTmp = new RectF();
    final Paint paint = new Paint(1);
    RLottieDrawable webViewAnimation = new RLottieDrawable(R.raw.bot_webview_sheet_to_cross, String.valueOf((int) R.raw.bot_webview_sheet_to_cross) + hashCode(), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f)) { // from class: org.telegram.ui.Components.BotCommandsMenuView.2
        @Override // android.graphics.drawable.Drawable
        public void invalidateSelf() {
            super.invalidateSelf();
            BotCommandsMenuView.this.invalidate();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.RLottieDrawable
        public void invalidateInternal() {
            super.invalidateInternal();
            BotCommandsMenuView.this.invalidate();
        }
    };
    private String menuText = LocaleController.getString((int) R.string.BotsMenuTitle);
    boolean drawBackgroundDrawable = true;

    public BotCommandsMenuView(Context context) {
        super(context);
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        MenuDrawable menuDrawable = new MenuDrawable() { // from class: org.telegram.ui.Components.BotCommandsMenuView.1
            @Override // android.graphics.drawable.Drawable
            public void invalidateSelf() {
                super.invalidateSelf();
                BotCommandsMenuView.this.invalidate();
            }
        };
        this.backDrawable = menuDrawable;
        updateColors();
        menuDrawable.setMiniIcon(true);
        menuDrawable.setRotateToBack(false);
        menuDrawable.setRotation(0.0f, false);
        menuDrawable.setCallback(this);
        textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        menuDrawable.setRoundCap();
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), 0, Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
        this.backgroundDrawable = createSimpleSelectorRoundRectDrawable;
        createSimpleSelectorRoundRectDrawable.setCallback(this);
        setContentDescription(LocaleController.getString("AccDescrBotMenu", R.string.AccDescrBotMenu));
    }

    public void setDrawBackgroundDrawable(boolean drawBackgroundDrawable) {
        this.drawBackgroundDrawable = drawBackgroundDrawable;
        invalidate();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.webViewAnimation.addParentView(this);
        this.webViewAnimation.setCurrentParentView(this);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.webViewAnimation.removeParentView(this);
    }

    public void setWebView(boolean webView) {
        this.isWebView = webView;
        invalidate();
    }

    private void updateColors() {
        this.paint.setColor(Theme.getColor(Theme.key_chat_messagePanelVoiceBackground));
        int textColor = Theme.getColor(Theme.key_chat_messagePanelVoicePressed);
        this.backDrawable.setBackColor(textColor);
        this.backDrawable.setIconColor(textColor);
        this.textPaint.setColor(textColor);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (View.MeasureSpec.getSize(widthMeasureSpec) + View.MeasureSpec.getSize(heightMeasureSpec)) << 16;
        if (this.lastSize != size || this.menuTextLayout == null) {
            this.backDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.textPaint.setTextSize(AndroidUtilities.dp(15.0f));
            this.lastSize = size;
            int w = (int) this.textPaint.measureText(this.menuText);
            this.menuTextLayout = StaticLayoutEx.createStaticLayout(this.menuText, this.textPaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, w, 1);
        }
        onTranslationChanged((this.menuTextLayout.getWidth() + AndroidUtilities.dp(4.0f)) * this.expandProgress);
        int width = AndroidUtilities.dp(40.0f);
        if (this.expanded) {
            width += this.menuTextLayout.getWidth() + AndroidUtilities.dp(4.0f);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0058  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00ad  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00db  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00f9  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x011f  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void dispatchDraw(android.graphics.Canvas r10) {
        /*
            Method dump skipped, instructions count: 308
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotCommandsMenuView.dispatchDraw(android.graphics.Canvas):void");
    }

    protected void onTranslationChanged(float translationX) {
    }

    public boolean setMenuText(String menuText) {
        if (menuText == null) {
            menuText = LocaleController.getString((int) R.string.BotsMenuTitle);
        }
        String str = this.menuText;
        boolean changed = str == null || !str.equals(menuText);
        this.menuText = menuText;
        this.menuTextLayout = null;
        requestLayout();
        return changed;
    }

    public void setExpanded(boolean expanded, boolean animated) {
        if (this.expanded != expanded) {
            this.expanded = expanded;
            if (!animated) {
                this.expandProgress = expanded ? 1.0f : 0.0f;
            }
            requestLayout();
            invalidate();
        }
    }

    public boolean isOpened() {
        return this.isOpened;
    }

    /* loaded from: classes5.dex */
    public static class BotCommandsAdapter extends RecyclerListView.SelectionAdapter {
        ArrayList<String> newResult = new ArrayList<>();
        ArrayList<String> newResultHelp = new ArrayList<>();

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            BotCommandView view = new BotCommandView(parent.getContext());
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            BotCommandView view = (BotCommandView) holder.itemView;
            view.command.setText(this.newResult.get(position));
            view.description.setText(this.newResultHelp.get(position));
            view.commandStr = this.newResult.get(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.newResult.size();
        }

        public void setBotInfo(LongSparseArray<TLRPC.BotInfo> botInfo) {
            this.newResult.clear();
            this.newResultHelp.clear();
            for (int b = 0; b < botInfo.size(); b++) {
                TLRPC.BotInfo info = botInfo.valueAt(b);
                for (int a = 0; a < info.commands.size(); a++) {
                    TLRPC.TL_botCommand botCommand = info.commands.get(a);
                    if (botCommand != null && botCommand.command != null) {
                        ArrayList<String> arrayList = this.newResult;
                        arrayList.add("/" + botCommand.command);
                        if (botCommand.description != null && botCommand.description.length() > 1) {
                            ArrayList<String> arrayList2 = this.newResultHelp;
                            arrayList2.add(botCommand.description.substring(0, 1).toUpperCase() + botCommand.description.substring(1).toLowerCase());
                        } else {
                            this.newResultHelp.add(botCommand.description);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setOpened(boolean opened) {
        if (this.isOpened != opened) {
            this.isOpened = opened;
        }
        int i = 1;
        if (this.isWebView) {
            if (this.isWebViewOpened != opened) {
                RLottieDrawable drawable = this.webViewAnimation;
                if (!drawable.hasParentView()) {
                    drawable.addParentView(this);
                }
                drawable.stop();
                drawable.setPlayInDirectionOfCustomEndFrame(true);
                if (opened) {
                    i = drawable.getFramesCount();
                }
                drawable.setCustomEndFrame(i);
                drawable.start();
                this.isWebViewOpened = opened;
                return;
            }
            return;
        }
        this.backDrawable.setRotation(opened ? 1.0f : 0.0f, true);
    }

    /* loaded from: classes5.dex */
    public static class BotCommandView extends LinearLayout {
        TextView command;
        String commandStr;
        TextView description;

        public BotCommandView(Context context) {
            super(context);
            setOrientation(0);
            setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            TextView textView = new TextView(context);
            this.description = textView;
            textView.setTextSize(1, 16.0f);
            this.description.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.description.setTag(Theme.key_windowBackgroundWhiteBlackText);
            this.description.setLines(1);
            this.description.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.description, LayoutHelper.createLinear(-1, -2, 1.0f, 16, 0, 0, AndroidUtilities.dp(8.0f), 0));
            TextView textView2 = new TextView(context);
            this.command = textView2;
            textView2.setTextSize(1, 14.0f);
            this.command.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            this.command.setTag(Theme.key_windowBackgroundWhiteGrayText);
            addView(this.command, LayoutHelper.createLinear(-2, -2, 0.0f, 16));
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public String getCommand() {
            return this.commandStr;
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || this.backgroundDrawable == who;
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.backgroundDrawable.setState(getDrawableState());
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.backgroundDrawable.jumpToCurrentState();
    }
}
