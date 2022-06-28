package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class ShareDialogCell extends FrameLayout {
    public static final int TYPE_CALL = 1;
    public static final int TYPE_CREATE = 2;
    public static final int TYPE_SHARE = 0;
    private CheckBox2 checkBox;
    private long currentDialog;
    private int currentType;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private TextView nameTextView;
    private float onlineProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    private TLRPC.User user;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private int currentAccount = UserConfig.selectedAccount;

    public ShareDialogCell(Context context, int type, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setWillNotDraw(false);
        this.currentType = type;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(28.0f));
        if (type == 2) {
            addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        } else {
            addView(this.imageView, LayoutHelper.createFrame(56, 56.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        }
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(getThemedColor(type == 1 ? Theme.key_voipgroup_nameText : Theme.key_dialogTextBlack));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(2);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(2);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, this.currentType == 2 ? 58.0f : 66.0f, 6.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
        this.checkBox = checkBox2;
        checkBox2.setColor(Theme.key_dialogRoundCheckBox, type == 1 ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_dialogBackground, Theme.key_dialogRoundCheckBoxCheck);
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(4);
        this.checkBox.setProgressDelegate(new CheckBoxBase.ProgressDelegate() { // from class: org.telegram.ui.Cells.ShareDialogCell$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.Components.CheckBoxBase.ProgressDelegate
            public final void setProgress(float f) {
                ShareDialogCell.this.m1667lambda$new$0$orgtelegramuiCellsShareDialogCell(f);
            }
        });
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 49, 19.0f, this.currentType == 2 ? -40.0f : 42.0f, 0.0f, 0.0f));
        setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f)));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-ShareDialogCell */
    public /* synthetic */ void m1667lambda$new$0$orgtelegramuiCellsShareDialogCell(float progress) {
        float scale = 1.0f - (this.checkBox.getProgress() * 0.143f);
        this.imageView.setScaleX(scale);
        this.imageView.setScaleY(scale);
        invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.currentType == 2 ? 95.0f : 103.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    public void setDialog(long uid, boolean checked, CharSequence name) {
        if (DialogObject.isUserDialog(uid)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(uid));
            this.user = user;
            this.avatarDrawable.setInfo(user);
            if (this.currentType != 2 && UserObject.isReplyUser(this.user)) {
                this.nameTextView.setText(LocaleController.getString("RepliesTitle", R.string.RepliesTitle));
                this.avatarDrawable.setAvatarType(12);
                this.imageView.setImage((ImageLocation) null, (String) null, this.avatarDrawable, this.user);
            } else if (this.currentType != 2 && UserObject.isUserSelf(this.user)) {
                this.nameTextView.setText(LocaleController.getString("SavedMessages", R.string.SavedMessages));
                this.avatarDrawable.setAvatarType(1);
                this.imageView.setImage((ImageLocation) null, (String) null, this.avatarDrawable, this.user);
            } else {
                if (name != null) {
                    this.nameTextView.setText(name);
                } else {
                    TLRPC.User user2 = this.user;
                    if (user2 != null) {
                        this.nameTextView.setText(ContactsController.formatName(user2.first_name, this.user.last_name));
                    } else {
                        this.nameTextView.setText("");
                    }
                }
                this.imageView.setForUserOrChat(this.user, this.avatarDrawable);
            }
        } else {
            this.user = null;
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-uid));
            if (name != null) {
                this.nameTextView.setText(name);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(chat);
            this.imageView.setForUserOrChat(chat, this.avatarDrawable);
        }
        this.currentDialog = uid;
        this.checkBox.setChecked(checked, false);
    }

    public long getCurrentDialog() {
        return this.currentDialog;
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        TLRPC.User user;
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.imageView && this.currentType != 2 && (user = this.user) != null && !MessagesController.isSupportUser(user)) {
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastUpdateTime = newTime;
            boolean isOnline = !this.user.self && !this.user.bot && ((this.user.status != null && this.user.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Long.valueOf(this.user.id)));
            if (isOnline || this.onlineProgress != 0.0f) {
                int top = this.imageView.getBottom() - AndroidUtilities.dp(6.0f);
                int left = this.imageView.getRight() - AndroidUtilities.dp(10.0f);
                Theme.dialogs_onlineCirclePaint.setColor(getThemedColor(this.currentType == 1 ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_windowBackgroundWhite));
                canvas.drawCircle(left, top, AndroidUtilities.dp(7.0f) * this.onlineProgress, Theme.dialogs_onlineCirclePaint);
                Theme.dialogs_onlineCirclePaint.setColor(getThemedColor(Theme.key_chats_onlineCircle));
                canvas.drawCircle(left, top, AndroidUtilities.dp(5.0f) * this.onlineProgress, Theme.dialogs_onlineCirclePaint);
                if (isOnline) {
                    float f = this.onlineProgress;
                    if (f < 1.0f) {
                        float f2 = f + (((float) dt) / 150.0f);
                        this.onlineProgress = f2;
                        if (f2 > 1.0f) {
                            this.onlineProgress = 1.0f;
                        }
                        this.imageView.invalidate();
                        invalidate();
                    }
                } else {
                    float f3 = this.onlineProgress;
                    if (f3 > 0.0f) {
                        float f4 = f3 - (((float) dt) / 150.0f);
                        this.onlineProgress = f4;
                        if (f4 < 0.0f) {
                            this.onlineProgress = 0.0f;
                        }
                        this.imageView.invalidate();
                        invalidate();
                    }
                }
            }
        }
        return result;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int cx = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2);
        int cy = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
        Theme.checkboxSquare_checkPaint.setColor(getThemedColor(Theme.key_dialogRoundCheckBox));
        Theme.checkboxSquare_checkPaint.setAlpha((int) (this.checkBox.getProgress() * 255.0f));
        canvas.drawCircle(cx, cy, AndroidUtilities.dp(this.currentType == 2 ? 24.0f : 28.0f), Theme.checkboxSquare_checkPaint);
        super.onDraw(canvas);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.checkBox.isChecked()) {
            info.setSelected(true);
        }
    }
}
