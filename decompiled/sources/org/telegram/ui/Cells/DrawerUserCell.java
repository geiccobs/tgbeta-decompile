package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
/* loaded from: classes4.dex */
public class DrawerUserCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private int accountNumber;
    private AvatarDrawable avatarDrawable;
    private GroupCreateCheckBox checkBox;
    private BackupImageView imageView;
    private RectF rect = new RectF();
    private SimpleTextView textView;

    public DrawerUserCell(Context context) {
        super(context);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable;
        avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
        addView(this.imageView, LayoutHelper.createFrame(36, 36.0f, 51, 14.0f, 6.0f, 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        this.textView.setTextSize(15);
        this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textView.setMaxLines(1);
        this.textView.setGravity(19);
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 19, 72.0f, 0.0f, 60.0f, 0.0f));
        GroupCreateCheckBox groupCreateCheckBox = new GroupCreateCheckBox(context);
        this.checkBox = groupCreateCheckBox;
        groupCreateCheckBox.setChecked(true, false);
        this.checkBox.setCheckScale(0.9f);
        this.checkBox.setInnerRadDiff(AndroidUtilities.dp(1.5f));
        this.checkBox.setColorKeysOverrides(Theme.key_chats_unreadCounterText, Theme.key_chats_unreadCounter, Theme.key_chats_menuBackground);
        addView(this.checkBox, LayoutHelper.createFrame(18, 18.0f, 51, 37.0f, 27.0f, 0.0f, 0.0f));
        setWillNotDraw(false);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.currentUserPremiumStatusChanged) {
            int i = this.accountNumber;
            if (account == i) {
                setAccount(i);
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            this.textView.invalidate();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setAccount(int account) {
        this.accountNumber = account;
        TLRPC.User user = UserConfig.getInstance(account).getCurrentUser();
        if (user == null) {
            return;
        }
        this.avatarDrawable.setInfo(user);
        CharSequence text = ContactsController.formatName(user.first_name, user.last_name);
        int i = 0;
        try {
            text = Emoji.replaceEmoji(text, this.textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        } catch (Exception e) {
        }
        this.textView.setText(text);
        if (MessagesController.getInstance(account).isPremiumUser(user)) {
            this.textView.setDrawablePadding(AndroidUtilities.dp(6.0f));
            this.textView.setRightDrawable(PremiumGradient.getInstance().premiumStarDrawableMini);
        } else {
            this.textView.setRightDrawable((Drawable) null);
        }
        this.imageView.getImageReceiver().setCurrentAccount(account);
        this.imageView.setForUserOrChat(user, this.avatarDrawable);
        GroupCreateCheckBox groupCreateCheckBox = this.checkBox;
        if (account != UserConfig.selectedAccount) {
            i = 4;
        }
        groupCreateCheckBox.setVisibility(i);
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int counter;
        if (UserConfig.getActivatedAccountsCount() <= 1 || !NotificationsController.getInstance(this.accountNumber).showBadgeNumber || (counter = MessagesStorage.getInstance(this.accountNumber).getMainUnreadCount()) <= 0) {
            return;
        }
        String text = String.format("%d", Integer.valueOf(counter));
        int countTop = AndroidUtilities.dp(12.5f);
        int textWidth = (int) Math.ceil(Theme.dialogs_countTextPaint.measureText(text));
        int countWidth = Math.max(AndroidUtilities.dp(10.0f), textWidth);
        int countLeft = (getMeasuredWidth() - countWidth) - AndroidUtilities.dp(25.0f);
        int x = countLeft - AndroidUtilities.dp(5.5f);
        this.rect.set(x, countTop, x + countWidth + AndroidUtilities.dp(14.0f), AndroidUtilities.dp(23.0f) + countTop);
        canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, Theme.dialogs_countPaint);
        canvas.drawText(text, this.rect.left + ((this.rect.width() - textWidth) / 2.0f), AndroidUtilities.dp(16.0f) + countTop, Theme.dialogs_countTextPaint);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.addAction(16);
    }
}
