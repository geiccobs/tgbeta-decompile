package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;
/* loaded from: classes4.dex */
public class ChatListCell extends LinearLayout {
    private ListView[] listView = new ListView[2];

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ListView extends FrameLayout {
        private RadioButton button;
        private boolean isThreeLines;
        private RectF rect = new RectF();
        private TextPaint textPaint = new TextPaint(1);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ListView(Context context, boolean threeLines) {
            super(context);
            String str;
            int i;
            ChatListCell.this = r10;
            boolean z = true;
            setWillNotDraw(false);
            this.isThreeLines = threeLines;
            if (threeLines) {
                i = R.string.ChatListExpanded;
                str = "ChatListExpanded";
            } else {
                i = R.string.ChatListDefault;
                str = "ChatListDefault";
            }
            setContentDescription(LocaleController.getString(str, i));
            this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
            RadioButton radioButton = new RadioButton(context) { // from class: org.telegram.ui.Cells.ChatListCell.ListView.1
                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    ListView.this.invalidate();
                }
            };
            this.button = radioButton;
            radioButton.setSize(AndroidUtilities.dp(20.0f));
            addView(this.button, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 26.0f, 10.0f, 0.0f));
            RadioButton radioButton2 = this.button;
            if ((!this.isThreeLines || !SharedConfig.useThreeLinesLayout) && (this.isThreeLines || SharedConfig.useThreeLinesLayout)) {
                z = false;
            }
            radioButton2.setChecked(z, false);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            String str;
            int i;
            int color = Theme.getColor(Theme.key_switchTrack);
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            this.button.setColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_radioBackgroundChecked));
            this.rect.set(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), getMeasuredWidth() - AndroidUtilities.dp(1.0f), AndroidUtilities.dp(73.0f));
            Theme.chat_instantViewRectPaint.setColor(Color.argb((int) (this.button.getProgress() * 43.0f), r, g, b));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
            this.rect.set(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(74.0f));
            Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) ((1.0f - this.button.getProgress()) * 31.0f), r, g, b));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.dialogs_onlineCirclePaint);
            if (this.isThreeLines) {
                i = R.string.ChatListExpanded;
                str = "ChatListExpanded";
            } else {
                i = R.string.ChatListDefault;
                str = "ChatListDefault";
            }
            String text = LocaleController.getString(str, i);
            int width = (int) Math.ceil(this.textPaint.measureText(text));
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            canvas.drawText(text, (getMeasuredWidth() - width) / 2, AndroidUtilities.dp(96.0f), this.textPaint);
            int a = 0;
            for (int i2 = 2; a < i2; i2 = 2) {
                int cy = AndroidUtilities.dp(a == 0 ? 21.0f : 53.0f);
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb(a == 0 ? 204 : 90, r, g, b));
                canvas.drawCircle(AndroidUtilities.dp(22.0f), cy, AndroidUtilities.dp(11.0f), Theme.dialogs_onlineCirclePaint);
                int i3 = 0;
                while (true) {
                    if (i3 < (this.isThreeLines ? 3 : 2)) {
                        Theme.dialogs_onlineCirclePaint.setColor(Color.argb(i3 == 0 ? 204 : 90, r, g, b));
                        float f = 72.0f;
                        if (this.isThreeLines) {
                            RectF rectF = this.rect;
                            float dp = AndroidUtilities.dp(41.0f);
                            float dp2 = cy - AndroidUtilities.dp(8.3f - (i3 * 7));
                            int measuredWidth = getMeasuredWidth();
                            if (i3 != 0) {
                                f = 48.0f;
                            }
                            rectF.set(dp, dp2, measuredWidth - AndroidUtilities.dp(f), cy - AndroidUtilities.dp(5.3f - (i3 * 7)));
                            canvas.drawRoundRect(this.rect, AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), Theme.dialogs_onlineCirclePaint);
                        } else {
                            RectF rectF2 = this.rect;
                            float dp3 = AndroidUtilities.dp(41.0f);
                            float dp4 = cy - AndroidUtilities.dp(7 - (i3 * 10));
                            int measuredWidth2 = getMeasuredWidth();
                            if (i3 != 0) {
                                f = 48.0f;
                            }
                            rectF2.set(dp3, dp4, measuredWidth2 - AndroidUtilities.dp(f), cy - AndroidUtilities.dp(3 - (i3 * 10)));
                            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                        }
                        i3++;
                    }
                }
                a++;
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            String str;
            int i;
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(RadioButton.class.getName());
            info.setChecked(this.button.isChecked());
            info.setCheckable(true);
            if (this.isThreeLines) {
                i = R.string.ChatListExpanded;
                str = "ChatListExpanded";
            } else {
                i = R.string.ChatListDefault;
                str = "ChatListDefault";
            }
            info.setContentDescription(LocaleController.getString(str, i));
        }
    }

    public ChatListCell(Context context) {
        super(context);
        setOrientation(0);
        setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), 0);
        int a = 0;
        while (true) {
            ListView[] listViewArr = this.listView;
            if (a < listViewArr.length) {
                final boolean isThreeLines = a == 1;
                listViewArr[a] = new ListView(context, isThreeLines);
                addView(this.listView[a], LayoutHelper.createLinear(-1, -1, 0.5f, a == 1 ? 10 : 0, 0, 0, 0));
                this.listView[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.ChatListCell$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ChatListCell.this.m1634lambda$new$0$orgtelegramuiCellsChatListCell(isThreeLines, view);
                    }
                });
                a++;
            } else {
                return;
            }
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-ChatListCell */
    public /* synthetic */ void m1634lambda$new$0$orgtelegramuiCellsChatListCell(boolean isThreeLines, View v) {
        for (int b = 0; b < 2; b++) {
            this.listView[b].button.setChecked(this.listView[b] == v, true);
        }
        didSelectChatType(isThreeLines);
    }

    protected void didSelectChatType(boolean threeLines) {
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        int a = 0;
        while (true) {
            ListView[] listViewArr = this.listView;
            if (a < listViewArr.length) {
                listViewArr[a].invalidate();
                a++;
            } else {
                return;
            }
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(123.0f), C.BUFFER_FLAG_ENCRYPTED));
    }
}
