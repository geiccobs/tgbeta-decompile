package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class MemberRequestCell extends FrameLayout {
    private final AvatarDrawable avatarDrawable = new AvatarDrawable();
    private final BackupImageView avatarImageView;
    private TLRPC.TL_chatInviteImporter importer;
    private boolean isNeedDivider;
    private final SimpleTextView nameTextView;
    private final SimpleTextView statusTextView;

    /* loaded from: classes4.dex */
    public interface OnClickListener {
        void onAddClicked(TLRPC.TL_chatInviteImporter tL_chatInviteImporter);

        void onDismissClicked(TLRPC.TL_chatInviteImporter tL_chatInviteImporter);
    }

    public MemberRequestCell(Context context, final OnClickListener clickListener, boolean isChannel) {
        super(context);
        String str;
        int i;
        BackupImageView backupImageView = new BackupImageView(getContext());
        this.avatarImageView = backupImageView;
        SimpleTextView simpleTextView = new SimpleTextView(getContext());
        this.nameTextView = simpleTextView;
        SimpleTextView simpleTextView2 = new SimpleTextView(getContext());
        this.statusTextView = simpleTextView2;
        backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
        int i2 = 5;
        addView(backupImageView, LayoutHelper.createFrame(46, 46.0f, LocaleController.isRTL ? 5 : 3, 12.0f, 8.0f, 12.0f, 0.0f));
        simpleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        simpleTextView.setMaxLines(1);
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        simpleTextView.setTextSize(17);
        simpleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, 48, LocaleController.isRTL ? 12.0f : 74.0f, 12.0f, LocaleController.isRTL ? 74.0f : 12.0f, 0.0f));
        simpleTextView2.setGravity(LocaleController.isRTL ? 5 : 3);
        simpleTextView2.setMaxLines(1);
        simpleTextView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        simpleTextView2.setTextSize(14);
        addView(simpleTextView2, LayoutHelper.createFrame(-1, -2.0f, 48, LocaleController.isRTL ? 12.0f : 74.0f, 36.0f, LocaleController.isRTL ? 74.0f : 12.0f, 0.0f));
        int btnPadding = AndroidUtilities.dp(17.0f);
        TextView addButton = new TextView(getContext());
        int i3 = 0;
        addButton.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
        addButton.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addButton.setMaxLines(1);
        addButton.setPadding(btnPadding, 0, btnPadding, 0);
        if (isChannel) {
            i = R.string.AddToChannel;
            str = "AddToChannel";
        } else {
            i = R.string.AddToGroup;
            str = "AddToGroup";
        }
        addButton.setText(LocaleController.getString(str, i));
        addButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        addButton.setTextSize(14.0f);
        addButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        addButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.MemberRequestCell$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MemberRequestCell.this.m1659lambda$new$0$orgtelegramuiCellsMemberRequestCell(clickListener, view);
            }
        });
        addView(addButton, LayoutHelper.createFrame(-2, 32.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 0.0f : 73.0f, 62.0f, LocaleController.isRTL ? 73.0f : 0.0f, 0.0f));
        float addButtonWidth = addButton.getPaint().measureText(addButton.getText().toString()) + (btnPadding * 2);
        TextView dismissButton = new TextView(getContext());
        dismissButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), 0, Theme.getColor(Theme.key_listSelector), -16777216));
        dismissButton.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        dismissButton.setMaxLines(1);
        dismissButton.setPadding(btnPadding, 0, btnPadding, 0);
        dismissButton.setText(LocaleController.getString("Dismiss", R.string.Dismiss));
        dismissButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        dismissButton.setTextSize(14.0f);
        dismissButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        dismissButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.MemberRequestCell$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MemberRequestCell.this.m1660lambda$new$1$orgtelegramuiCellsMemberRequestCell(clickListener, view);
            }
        });
        FrameLayout.LayoutParams dismissLayoutParams = new FrameLayout.LayoutParams(-2, AndroidUtilities.dp(32.0f), !LocaleController.isRTL ? 3 : i2);
        dismissLayoutParams.topMargin = AndroidUtilities.dp(62.0f);
        dismissLayoutParams.leftMargin = LocaleController.isRTL ? 0 : (int) (AndroidUtilities.dp(79.0f) + addButtonWidth);
        dismissLayoutParams.rightMargin = LocaleController.isRTL ? (int) (AndroidUtilities.dp(79.0f) + addButtonWidth) : i3;
        addView(dismissButton, dismissLayoutParams);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-MemberRequestCell */
    public /* synthetic */ void m1659lambda$new$0$orgtelegramuiCellsMemberRequestCell(OnClickListener clickListener, View v) {
        TLRPC.TL_chatInviteImporter tL_chatInviteImporter;
        if (clickListener != null && (tL_chatInviteImporter = this.importer) != null) {
            clickListener.onAddClicked(tL_chatInviteImporter);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-MemberRequestCell */
    public /* synthetic */ void m1660lambda$new$1$orgtelegramuiCellsMemberRequestCell(OnClickListener clickListener, View v) {
        TLRPC.TL_chatInviteImporter tL_chatInviteImporter;
        if (clickListener != null && (tL_chatInviteImporter = this.importer) != null) {
            clickListener.onDismissClicked(tL_chatInviteImporter);
        }
    }

    public void setData(LongSparseArray<TLRPC.User> users, TLRPC.TL_chatInviteImporter importer, boolean isNeedDivider) {
        this.importer = importer;
        this.isNeedDivider = isNeedDivider;
        setWillNotDraw(!isNeedDivider);
        TLRPC.User user = users.get(importer.user_id);
        this.avatarDrawable.setInfo(user);
        this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
        this.nameTextView.setText(UserObject.getUserName(user));
        String dateText = LocaleController.formatDateAudio(importer.date, false);
        if (importer.approved_by == 0) {
            this.statusTextView.setText(LocaleController.formatString("RequestedToJoinAt", R.string.RequestedToJoinAt, dateText));
            return;
        }
        TLRPC.User approvedByUser = users.get(importer.approved_by);
        if (approvedByUser != null) {
            this.statusTextView.setText(LocaleController.formatString("AddedBy", R.string.AddedBy, UserObject.getFirstName(approvedByUser), dateText));
        } else {
            this.statusTextView.setText("");
        }
    }

    public TLRPC.TL_chatInviteImporter getImporter() {
        return this.importer;
    }

    public BackupImageView getAvatarImageView() {
        return this.avatarImageView;
    }

    public String getStatus() {
        return this.statusTextView.getText().toString();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(107.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.isNeedDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(72.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }
}
