package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class JoinCallByUrlAlert extends BottomSheet {
    private boolean joinAfterDismiss;

    /* loaded from: classes5.dex */
    public static class BottomSheetCell extends FrameLayout {
        private View background;
        private LinearLayout linearLayout;
        private TextView textView;

        public BottomSheetCell(Context context) {
            super(context);
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(17);
            this.textView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setText(CharSequence text) {
            this.textView.setText(text);
        }
    }

    public JoinCallByUrlAlert(Context context, TLRPC.Chat chat) {
        super(context, true);
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        setCustomView(linearLayout);
        BackupImageView avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(45.0f));
        linearLayout.addView(avatarImageView, LayoutHelper.createLinear(90, 90, 49, 0, 29, 0, 0));
        AvatarDrawable avatarDrawable = new AvatarDrawable(chat);
        avatarImageView.setForUserOrChat(chat, avatarDrawable);
        TextView percentTextView = new TextView(context);
        percentTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        percentTextView.setTextSize(1, 18.0f);
        percentTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        percentTextView.setGravity(1);
        linearLayout.addView(percentTextView, LayoutHelper.createLinear(-2, -2, 49, 17, 24, 17, 0));
        TextView infoTextView = new TextView(context);
        infoTextView.setTextSize(1, 14.0f);
        infoTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        infoTextView.setGravity(1);
        linearLayout.addView(infoTextView, LayoutHelper.createLinear(-2, -2, 49, 30, 8, 30, 0));
        ChatObject.Call call = AccountInstance.getInstance(this.currentAccount).getMessagesController().getGroupCall(chat.id, false);
        if (call != null) {
            if (TextUtils.isEmpty(call.call.title)) {
                percentTextView.setText(chat.title);
            } else {
                percentTextView.setText(call.call.title);
            }
            if (call.call.participants_count != 0) {
                infoTextView.setText(LocaleController.formatPluralString("Participants", call.call.participants_count, new Object[0]));
            } else {
                infoTextView.setText(LocaleController.getString("NoOneJoinedYet", R.string.NoOneJoinedYet));
            }
        } else {
            percentTextView.setText(chat.title);
            infoTextView.setText(LocaleController.getString("NoOneJoinedYet", R.string.NoOneJoinedYet));
        }
        BottomSheetCell clearButton = new BottomSheetCell(context);
        clearButton.setBackground(null);
        if (ChatObject.isChannelOrGiga(chat)) {
            clearButton.setText(LocaleController.getString("VoipChannelJoinVoiceChatUrl", R.string.VoipChannelJoinVoiceChatUrl));
        } else {
            clearButton.setText(LocaleController.getString("VoipGroupJoinVoiceChatUrl", R.string.VoipGroupJoinVoiceChatUrl));
        }
        clearButton.background.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinCallByUrlAlert$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                JoinCallByUrlAlert.this.m2725lambda$new$0$orgtelegramuiComponentsJoinCallByUrlAlert(view);
            }
        });
        linearLayout.addView(clearButton, LayoutHelper.createLinear(-1, 50, 51, 0, 30, 0, 0));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-JoinCallByUrlAlert */
    public /* synthetic */ void m2725lambda$new$0$orgtelegramuiComponentsJoinCallByUrlAlert(View v) {
        this.joinAfterDismiss = true;
        dismiss();
    }

    protected void onJoin() {
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        super.dismissInternal();
        if (this.joinAfterDismiss) {
            onJoin();
        }
    }
}
