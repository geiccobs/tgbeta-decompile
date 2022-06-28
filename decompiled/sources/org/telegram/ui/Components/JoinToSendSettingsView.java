package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
/* loaded from: classes5.dex */
public class JoinToSendSettingsView extends LinearLayout {
    private final int MAXSPEC = View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE);
    private TLRPC.Chat currentChat;
    public boolean isJoinRequest;
    public boolean isJoinToSend;
    public HeaderCell joinHeaderCell;
    public TextCheckCell joinRequestCell;
    public TextInfoPrivacyCell joinRequestInfoCell;
    public TextCheckCell joinToSendCell;
    public TextInfoPrivacyCell joinToSendInfoCell;
    private ValueAnimator toggleAnimator;
    private float toggleValue;

    public JoinToSendSettingsView(Context context, TLRPC.Chat currentChat) {
        super(context);
        this.currentChat = currentChat;
        this.isJoinToSend = currentChat.join_to_send;
        this.isJoinRequest = currentChat.join_request;
        boolean z = true;
        setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, 23);
        this.joinHeaderCell = headerCell;
        headerCell.setText(LocaleController.getString("ChannelSettingsJoinTitle", R.string.ChannelSettingsJoinTitle));
        this.joinHeaderCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        addView(this.joinHeaderCell);
        TextCheckCell textCheckCell = new TextCheckCell(context) { // from class: org.telegram.ui.Components.JoinToSendSettingsView.1
        };
        this.joinToSendCell = textCheckCell;
        textCheckCell.setBackground(Theme.getSelectorDrawable(true));
        TextCheckCell textCheckCell2 = this.joinToSendCell;
        String string = LocaleController.getString("ChannelSettingsJoinToSend", R.string.ChannelSettingsJoinToSend);
        boolean z2 = this.isJoinToSend;
        textCheckCell2.setTextAndCheck(string, z2, z2);
        int i = 0;
        this.joinToSendCell.setEnabled(currentChat.creator || (currentChat.admin_rights != null && currentChat.admin_rights.ban_users));
        this.joinToSendCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                JoinToSendSettingsView.this.m2740lambda$new$2$orgtelegramuiComponentsJoinToSendSettingsView(view);
            }
        });
        addView(this.joinToSendCell);
        TextCheckCell textCheckCell3 = new TextCheckCell(context) { // from class: org.telegram.ui.Components.JoinToSendSettingsView.2
        };
        this.joinRequestCell = textCheckCell3;
        textCheckCell3.setBackground(Theme.getSelectorDrawable(true));
        this.joinRequestCell.setTextAndCheck(LocaleController.getString("ChannelSettingsJoinRequest", R.string.ChannelSettingsJoinRequest), this.isJoinRequest, false);
        float f = 0.0f;
        this.joinRequestCell.setPivotY(0.0f);
        TextCheckCell textCheckCell4 = this.joinRequestCell;
        if (!currentChat.creator && (currentChat.admin_rights == null || !currentChat.admin_rights.ban_users)) {
            z = false;
        }
        textCheckCell4.setEnabled(z);
        this.joinRequestCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                JoinToSendSettingsView.this.m2743lambda$new$5$orgtelegramuiComponentsJoinToSendSettingsView(view);
            }
        });
        addView(this.joinRequestCell);
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
        this.joinToSendInfoCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setText(LocaleController.getString("ChannelSettingsJoinToSendInfo", R.string.ChannelSettingsJoinToSendInfo));
        addView(this.joinToSendInfoCell);
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
        this.joinRequestInfoCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.setText(LocaleController.getString("ChannelSettingsJoinRequestInfo", R.string.ChannelSettingsJoinRequestInfo));
        addView(this.joinRequestInfoCell);
        boolean z3 = this.isJoinToSend;
        this.toggleValue = z3 ? 1.0f : f;
        this.joinRequestCell.setVisibility(!z3 ? 8 : i);
        updateToggleValue(this.toggleValue);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-JoinToSendSettingsView */
    public /* synthetic */ void m2740lambda$new$2$orgtelegramuiComponentsJoinToSendSettingsView(View e) {
        final boolean oldValue = this.isJoinToSend;
        boolean newValue = !this.isJoinToSend;
        final boolean oldJoinToRequest = this.isJoinRequest;
        if (onJoinToSendToggle(newValue, new Runnable() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                JoinToSendSettingsView.this.m2739lambda$new$1$orgtelegramuiComponentsJoinToSendSettingsView(oldJoinToRequest, oldValue);
            }
        })) {
            m2741lambda$new$3$orgtelegramuiComponentsJoinToSendSettingsView(false);
            setJoinToSend(newValue);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-JoinToSendSettingsView */
    public /* synthetic */ void m2739lambda$new$1$orgtelegramuiComponentsJoinToSendSettingsView(final boolean oldJoinToRequest, final boolean oldValue) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                JoinToSendSettingsView.this.m2738lambda$new$0$orgtelegramuiComponentsJoinToSendSettingsView(oldJoinToRequest, oldValue);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-JoinToSendSettingsView */
    public /* synthetic */ void m2738lambda$new$0$orgtelegramuiComponentsJoinToSendSettingsView(boolean oldJoinToRequest, boolean oldValue) {
        m2741lambda$new$3$orgtelegramuiComponentsJoinToSendSettingsView(oldJoinToRequest);
        setJoinToSend(oldValue);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-JoinToSendSettingsView */
    public /* synthetic */ void m2743lambda$new$5$orgtelegramuiComponentsJoinToSendSettingsView(View e) {
        final boolean oldValue = this.isJoinRequest;
        boolean newValue = !this.isJoinRequest;
        if (onJoinRequestToggle(newValue, new Runnable() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                JoinToSendSettingsView.this.m2742lambda$new$4$orgtelegramuiComponentsJoinToSendSettingsView(oldValue);
            }
        })) {
            m2741lambda$new$3$orgtelegramuiComponentsJoinToSendSettingsView(newValue);
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-JoinToSendSettingsView */
    public /* synthetic */ void m2742lambda$new$4$orgtelegramuiComponentsJoinToSendSettingsView(final boolean oldValue) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                JoinToSendSettingsView.this.m2741lambda$new$3$orgtelegramuiComponentsJoinToSendSettingsView(oldValue);
            }
        });
    }

    public void setChat(TLRPC.Chat chat) {
        this.currentChat = chat;
        boolean z = false;
        this.joinToSendCell.setEnabled(chat.creator || (this.currentChat.admin_rights != null && this.currentChat.admin_rights.ban_users));
        TextCheckCell textCheckCell = this.joinRequestCell;
        if (this.currentChat.creator || (this.currentChat.admin_rights != null && this.currentChat.admin_rights.ban_users)) {
            z = true;
        }
        textCheckCell.setEnabled(z);
    }

    public boolean onJoinToSendToggle(boolean newValue, Runnable cancel) {
        return true;
    }

    public boolean onJoinRequestToggle(boolean newValue, Runnable cancel) {
        return true;
    }

    private void updateToggleValue(float value) {
        this.toggleValue = value;
        this.joinRequestCell.setAlpha(value);
        this.joinRequestCell.setTranslationY((1.0f - value) * (-AndroidUtilities.dp(16.0f)));
        this.joinRequestCell.setScaleY(1.0f - ((1.0f - value) * 0.1f));
        int joinRequestCellHeight = this.joinRequestCell.getMeasuredHeight() <= 0 ? AndroidUtilities.dp(50.0f) : this.joinRequestCell.getMeasuredHeight();
        this.joinToSendInfoCell.setAlpha(1.0f - value);
        this.joinToSendInfoCell.setTranslationY(((-joinRequestCellHeight) * (1.0f - value)) + ((-AndroidUtilities.dp(4.0f)) * value));
        this.joinRequestInfoCell.setAlpha(value);
        this.joinRequestInfoCell.setTranslationY(((-joinRequestCellHeight) * (1.0f - value)) + (AndroidUtilities.dp(4.0f) * (1.0f - value)));
        requestLayout();
    }

    /* renamed from: setJoinRequest */
    public void m2741lambda$new$3$orgtelegramuiComponentsJoinToSendSettingsView(boolean newJoinRequest) {
        this.isJoinRequest = newJoinRequest;
        this.joinRequestCell.setChecked(newJoinRequest);
    }

    public void setJoinToSend(boolean newJoinToSend) {
        this.isJoinToSend = newJoinToSend;
        this.joinToSendCell.setChecked(newJoinToSend);
        this.joinToSendCell.setDivider(this.isJoinToSend);
        this.joinRequestCell.setChecked(this.isJoinRequest);
        ValueAnimator valueAnimator = this.toggleAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float[] fArr = new float[2];
        fArr[0] = this.toggleValue;
        fArr[1] = this.isJoinToSend ? 1.0f : 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        this.toggleAnimator = ofFloat;
        ofFloat.setDuration(200L);
        this.toggleAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.toggleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.JoinToSendSettingsView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                JoinToSendSettingsView.this.m2744x8640bff6(valueAnimator2);
            }
        });
        this.toggleAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.JoinToSendSettingsView.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!JoinToSendSettingsView.this.isJoinToSend) {
                    JoinToSendSettingsView.this.joinRequestCell.setVisibility(8);
                }
            }
        });
        this.joinRequestCell.setVisibility(0);
        this.toggleAnimator.start();
    }

    /* renamed from: lambda$setJoinToSend$6$org-telegram-ui-Components-JoinToSendSettingsView */
    public /* synthetic */ void m2744x8640bff6(ValueAnimator a) {
        float floatValue = ((Float) a.getAnimatedValue()).floatValue();
        this.toggleValue = floatValue;
        updateToggleValue(floatValue);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        HeaderCell headerCell = this.joinHeaderCell;
        int y = headerCell.getMeasuredHeight() + 0;
        headerCell.layout(0, 0, r - l, y);
        TextCheckCell textCheckCell = this.joinToSendCell;
        int y2 = textCheckCell.getMeasuredHeight() + y;
        textCheckCell.layout(0, y, r - l, y2);
        TextCheckCell textCheckCell2 = this.joinRequestCell;
        int y3 = textCheckCell2.getMeasuredHeight() + y2;
        textCheckCell2.layout(0, y2, r - l, y3);
        TextInfoPrivacyCell textInfoPrivacyCell = this.joinToSendInfoCell;
        textInfoPrivacyCell.layout(0, y3, r - l, textInfoPrivacyCell.getMeasuredHeight() + y3);
        TextInfoPrivacyCell textInfoPrivacyCell2 = this.joinRequestInfoCell;
        textInfoPrivacyCell2.layout(0, y3, r - l, textInfoPrivacyCell2.getMeasuredHeight() + y3);
    }

    private int calcHeight() {
        return (int) (this.joinHeaderCell.getMeasuredHeight() + this.joinToSendCell.getMeasuredHeight() + (this.joinRequestCell.getMeasuredHeight() * this.toggleValue) + AndroidUtilities.lerp(this.joinToSendInfoCell.getMeasuredHeight(), this.joinRequestInfoCell.getMeasuredHeight(), this.toggleValue));
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.joinHeaderCell.measure(widthMeasureSpec, this.MAXSPEC);
        this.joinToSendCell.measure(widthMeasureSpec, this.MAXSPEC);
        this.joinRequestCell.measure(widthMeasureSpec, this.MAXSPEC);
        this.joinToSendInfoCell.measure(widthMeasureSpec, this.MAXSPEC);
        this.joinRequestInfoCell.measure(widthMeasureSpec, this.MAXSPEC);
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(calcHeight(), C.BUFFER_FLAG_ENCRYPTED));
    }
}
