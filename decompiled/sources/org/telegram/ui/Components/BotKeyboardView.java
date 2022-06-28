package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class BotKeyboardView extends LinearLayout {
    private TLRPC.TL_replyKeyboardMarkup botButtons;
    private int buttonHeight;
    private LinearLayout container;
    private BotKeyboardViewDelegate delegate;
    private boolean isFullSize;
    private int panelHeight;
    private final Theme.ResourcesProvider resourcesProvider;
    private ScrollView scrollView;
    private ArrayList<TextView> buttonViews = new ArrayList<>();
    private ArrayList<ImageView> buttonIcons = new ArrayList<>();

    /* loaded from: classes5.dex */
    public interface BotKeyboardViewDelegate {
        void didPressedButton(TLRPC.KeyboardButton keyboardButton);
    }

    public BotKeyboardView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setOrientation(1);
        ScrollView scrollView = new ScrollView(context);
        this.scrollView = scrollView;
        addView(scrollView);
        LinearLayout linearLayout = new LinearLayout(context);
        this.container = linearLayout;
        linearLayout.setOrientation(1);
        this.scrollView.addView(this.container);
        updateColors();
    }

    public void updateColors() {
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, getThemedColor(Theme.key_chat_emojiPanelBackground));
        setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
        for (int i = 0; i < this.buttonViews.size(); i++) {
            this.buttonViews.get(i).setTextColor(getThemedColor(Theme.key_chat_botKeyboardButtonText));
            this.buttonViews.get(i).setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor(Theme.key_chat_botKeyboardButtonBackground), getThemedColor(Theme.key_chat_botKeyboardButtonBackgroundPressed)));
            this.buttonIcons.get(i).setColorFilter(getThemedColor(Theme.key_chat_botKeyboardButtonText));
        }
        invalidate();
    }

    public void setDelegate(BotKeyboardViewDelegate botKeyboardViewDelegate) {
        this.delegate = botKeyboardViewDelegate;
    }

    public void setPanelHeight(int height) {
        TLRPC.TL_replyKeyboardMarkup tL_replyKeyboardMarkup;
        this.panelHeight = height;
        if (this.isFullSize && (tL_replyKeyboardMarkup = this.botButtons) != null && tL_replyKeyboardMarkup.rows.size() != 0) {
            this.buttonHeight = !this.isFullSize ? 42 : (int) Math.max(42.0f, (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size()) / AndroidUtilities.density);
            int count = this.container.getChildCount();
            int newHeight = AndroidUtilities.dp(this.buttonHeight);
            for (int a = 0; a < count; a++) {
                View v = this.container.getChildAt(a);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
                if (layoutParams.height != newHeight) {
                    layoutParams.height = newHeight;
                    v.setLayoutParams(layoutParams);
                }
            }
        }
    }

    public void invalidateViews() {
        for (int a = 0; a < this.buttonViews.size(); a++) {
            this.buttonViews.get(a).invalidate();
            this.buttonIcons.get(a).invalidate();
        }
    }

    public boolean isFullSize() {
        return this.isFullSize;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v12 */
    /* JADX WARN: Type inference failed for: r3v19 */
    /* JADX WARN: Type inference failed for: r3v2, types: [int, boolean] */
    public void setButtons(TLRPC.TL_replyKeyboardMarkup buttons) {
        this.botButtons = buttons;
        this.container.removeAllViews();
        this.buttonViews.clear();
        this.buttonIcons.clear();
        boolean z = false;
        this.scrollView.scrollTo(0, 0);
        if (buttons != null && this.botButtons.rows.size() != 0) {
            int i = 1;
            boolean z2 = !buttons.resize;
            this.isFullSize = z2;
            this.buttonHeight = !z2 ? 42 : (int) Math.max(42.0f, (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size()) / AndroidUtilities.density);
            int a = 0;
            while (a < buttons.rows.size()) {
                TLRPC.TL_keyboardButtonRow row = buttons.rows.get(a);
                LinearLayout layout = new LinearLayout(getContext());
                int i2 = z ? 1 : 0;
                int i3 = z ? 1 : 0;
                int i4 = z ? 1 : 0;
                layout.setOrientation(i2);
                this.container.addView(layout, LayoutHelper.createLinear(-1, this.buttonHeight, 15.0f, a == 0 ? 15.0f : 10.0f, 15.0f, a == buttons.rows.size() - i ? 15.0f : 0.0f));
                float weight = 1.0f / row.buttons.size();
                int b = 0;
                ?? r3 = z;
                while (b < row.buttons.size()) {
                    TLRPC.KeyboardButton button = row.buttons.get(b);
                    TextView textView = new TextView(getContext());
                    textView.setTag(button);
                    textView.setTextColor(getThemedColor(Theme.key_chat_botKeyboardButtonText));
                    textView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor(Theme.key_chat_botKeyboardButtonBackground), getThemedColor(Theme.key_chat_botKeyboardButtonBackgroundPressed)));
                    textView.setTextSize(i, 16.0f);
                    textView.setGravity(17);
                    FrameLayout frame = new FrameLayout(getContext());
                    frame.addView(textView, LayoutHelper.createFrame(-1, -1.0f));
                    textView.setPadding(AndroidUtilities.dp(4.0f), r3 == true ? 1 : 0, AndroidUtilities.dp(4.0f), r3);
                    textView.setText(Emoji.replaceEmoji(button.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), r3));
                    layout.addView(frame, LayoutHelper.createLinear(0, -1, weight, 0, 0, b != row.buttons.size() + (-1) ? 10 : 0, 0));
                    textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.BotKeyboardView$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            BotKeyboardView.this.m2226lambda$setButtons$0$orgtelegramuiComponentsBotKeyboardView(view);
                        }
                    });
                    this.buttonViews.add(textView);
                    ImageView icon = new ImageView(getContext());
                    icon.setColorFilter(getThemedColor(Theme.key_chat_botKeyboardButtonText));
                    if ((button instanceof TLRPC.TL_keyboardButtonWebView) || (button instanceof TLRPC.TL_keyboardButtonSimpleWebView)) {
                        icon.setImageResource(R.drawable.bot_webview);
                        icon.setVisibility(0);
                    } else {
                        icon.setVisibility(8);
                    }
                    this.buttonIcons.add(icon);
                    frame.addView(icon, LayoutHelper.createFrame(12, 12.0f, 53, 0.0f, 8.0f, 8.0f, 0.0f));
                    b++;
                    r3 = 0;
                    i = 1;
                }
                a++;
                z = false;
                i = 1;
            }
        }
    }

    /* renamed from: lambda$setButtons$0$org-telegram-ui-Components-BotKeyboardView */
    public /* synthetic */ void m2226lambda$setButtons$0$orgtelegramuiComponentsBotKeyboardView(View v) {
        this.delegate.didPressedButton((TLRPC.KeyboardButton) v.getTag());
    }

    public int getKeyboardHeight() {
        TLRPC.TL_replyKeyboardMarkup tL_replyKeyboardMarkup = this.botButtons;
        if (tL_replyKeyboardMarkup == null) {
            return 0;
        }
        return this.isFullSize ? this.panelHeight : (tL_replyKeyboardMarkup.rows.size() * AndroidUtilities.dp(this.buttonHeight)) + AndroidUtilities.dp(30.0f) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f));
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
