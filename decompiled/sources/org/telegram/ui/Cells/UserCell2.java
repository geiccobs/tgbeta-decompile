package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class UserCell2 extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox checkBox;
    private CheckBoxSquare checkBoxBig;
    private int currentAccount;
    private int currentDrawable;
    private int currentId;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currentStatus;
    private ImageView imageView;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private Theme.ResourcesProvider resourcesProvider;
    private int statusColor;
    private int statusOnlineColor;
    private SimpleTextView statusTextView;

    public UserCell2(Context context, int padding, int checkbox) {
        this(context, padding, checkbox, null);
    }

    public UserCell2(Context context, int padding, int checkbox, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        int i;
        int i2;
        this.currentAccount = UserConfig.selectedAccount;
        this.resourcesProvider = resourcesProvider;
        this.statusColor = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider);
        this.statusOnlineColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, resourcesProvider);
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : padding + 7, 11.0f, LocaleController.isRTL ? padding + 7 : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        View view = this.nameTextView;
        int i3 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i4 = 18;
        if (LocaleController.isRTL) {
            i = (checkbox == 2 ? 18 : 0) + 28;
        } else {
            i = padding + 68;
        }
        float f = i;
        if (LocaleController.isRTL) {
            i2 = padding + 68;
        } else {
            i2 = (checkbox != 2 ? 0 : i4) + 28;
        }
        addView(view, LayoutHelper.createFrame(-1, 20.0f, i3, f, 14.5f, i2, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : padding + 68, 37.5f, LocaleController.isRTL ? padding + 68 : 28.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.imageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon, resourcesProvider), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (checkbox == 2) {
            CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context, false, resourcesProvider);
            this.checkBoxBig = checkBoxSquare;
            addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, (LocaleController.isRTL ? 3 : 5) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (checkbox == 1) {
            CheckBox checkBox = new CheckBox(context, R.drawable.round_check2);
            this.checkBox = checkBox;
            checkBox.setVisibility(4);
            this.checkBox.setColor(Theme.getColor(Theme.key_checkbox, resourcesProvider), Theme.getColor(Theme.key_checkboxCheck, resourcesProvider));
            addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : padding + 37, 41.0f, LocaleController.isRTL ? padding + 37 : 0.0f, 0.0f));
        }
    }

    public void setData(TLObject object, CharSequence name, CharSequence status, int resId) {
        if (object == null && name == null && status == null) {
            this.currentStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currentStatus = status;
        this.currentName = name;
        this.currentObject = object;
        this.currentDrawable = resId;
        update(0);
    }

    public void setNameTypeface(Typeface typeface) {
        this.nameTextView.setTypeface(typeface);
    }

    public void setCurrentId(int id) {
        this.currentId = id;
    }

    public void setChecked(boolean checked, boolean animated) {
        CheckBox checkBox = this.checkBox;
        if (checkBox != null) {
            if (checkBox.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
            return;
        }
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            if (checkBoxSquare.getVisibility() != 0) {
                this.checkBoxBig.setVisibility(0);
            }
            this.checkBoxBig.setChecked(checked, animated);
        }
    }

    public void setCheckDisabled(boolean disabled) {
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.setDisabled(disabled);
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(70.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    public void setStatusColors(int color, int onlineColor) {
        this.statusColor = color;
        this.statusOnlineColor = onlineColor;
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.invalidate();
        }
    }

    public void update(int mask) {
        TLRPC.FileLocation fileLocation;
        TLRPC.FileLocation photo = null;
        String newName = null;
        TLRPC.User currentUser = null;
        TLRPC.Chat currentChat = null;
        TLObject tLObject = this.currentObject;
        if (tLObject instanceof TLRPC.User) {
            currentUser = (TLRPC.User) tLObject;
            if (currentUser.photo != null) {
                photo = currentUser.photo.photo_small;
            }
        } else if (tLObject instanceof TLRPC.Chat) {
            currentChat = (TLRPC.Chat) tLObject;
            if (currentChat.photo != null) {
                photo = currentChat.photo.photo_small;
            }
        }
        if (mask != 0) {
            boolean continueUpdate = false;
            if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 && (((fileLocation = this.lastAvatar) != null && photo == null) || ((fileLocation == null && photo != null) || (fileLocation != null && photo != null && (fileLocation.volume_id != photo.volume_id || this.lastAvatar.local_id != photo.local_id))))) {
                continueUpdate = true;
            }
            if (currentUser != null && !continueUpdate && (MessagesController.UPDATE_MASK_STATUS & mask) != 0) {
                int newStatus = 0;
                if (currentUser.status != null) {
                    newStatus = currentUser.status.expires;
                }
                if (newStatus != this.lastStatus) {
                    continueUpdate = true;
                }
            }
            if (!continueUpdate && this.currentName == null && this.lastName != null && (MessagesController.UPDATE_MASK_NAME & mask) != 0) {
                if (currentUser != null) {
                    newName = UserObject.getUserName(currentUser);
                } else {
                    newName = currentChat.title;
                }
                if (!newName.equals(this.lastName)) {
                    continueUpdate = true;
                }
            }
            if (!continueUpdate) {
                return;
            }
        }
        this.lastAvatar = photo;
        int i = 0;
        if (currentUser != null) {
            this.avatarDrawable.setInfo(currentUser);
            if (currentUser.status != null) {
                this.lastStatus = currentUser.status.expires;
            } else {
                this.lastStatus = 0;
            }
        } else if (currentChat != null) {
            this.avatarDrawable.setInfo(currentChat);
        } else {
            CharSequence charSequence = this.currentName;
            if (charSequence != null) {
                this.avatarDrawable.setInfo(this.currentId, charSequence.toString(), null);
            } else {
                this.avatarDrawable.setInfo(this.currentId, "#", null);
            }
        }
        CharSequence charSequence2 = this.currentName;
        if (charSequence2 != null) {
            this.lastName = null;
            this.nameTextView.setText(charSequence2);
        } else {
            if (currentUser != null) {
                this.lastName = newName == null ? UserObject.getUserName(currentUser) : newName;
            } else {
                this.lastName = newName == null ? currentChat.title : newName;
            }
            this.nameTextView.setText(this.lastName);
        }
        if (this.currentStatus != null) {
            this.statusTextView.setTextColor(this.statusColor);
            this.statusTextView.setText(this.currentStatus);
            BackupImageView backupImageView = this.avatarImageView;
            if (backupImageView != null) {
                backupImageView.setForUserOrChat(currentUser, this.avatarDrawable);
            }
        } else if (currentUser != null) {
            if (currentUser.bot) {
                this.statusTextView.setTextColor(this.statusColor);
                if (currentUser.bot_chat_history) {
                    this.statusTextView.setText(LocaleController.getString("BotStatusRead", R.string.BotStatusRead));
                } else {
                    this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", R.string.BotStatusCantRead));
                }
            } else if (currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((currentUser.status != null && currentUser.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Long.valueOf(currentUser.id)))) {
                this.statusTextView.setTextColor(this.statusOnlineColor);
                this.statusTextView.setText(LocaleController.getString("Online", R.string.Online));
            } else {
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, currentUser));
            }
            this.avatarImageView.setForUserOrChat(currentUser, this.avatarDrawable);
        } else if (currentChat != null) {
            this.statusTextView.setTextColor(this.statusColor);
            if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                if (currentChat.participants_count != 0) {
                    this.statusTextView.setText(LocaleController.formatPluralString("Subscribers", currentChat.participants_count, new Object[0]));
                } else if (TextUtils.isEmpty(currentChat.username)) {
                    this.statusTextView.setText(LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate));
                } else {
                    this.statusTextView.setText(LocaleController.getString("ChannelPublic", R.string.ChannelPublic));
                }
            } else if (currentChat.participants_count != 0) {
                this.statusTextView.setText(LocaleController.formatPluralString("Members", currentChat.participants_count, new Object[0]));
            } else if (currentChat.has_geo) {
                this.statusTextView.setText(LocaleController.getString("MegaLocation", R.string.MegaLocation));
            } else if (TextUtils.isEmpty(currentChat.username)) {
                this.statusTextView.setText(LocaleController.getString("MegaPrivate", R.string.MegaPrivate));
            } else {
                this.statusTextView.setText(LocaleController.getString("MegaPublic", R.string.MegaPublic));
            }
            this.avatarImageView.setForUserOrChat(currentChat, this.avatarDrawable);
        } else {
            this.avatarImageView.setImageDrawable(this.avatarDrawable);
        }
        if ((this.imageView.getVisibility() == 0 && this.currentDrawable == 0) || (this.imageView.getVisibility() == 8 && this.currentDrawable != 0)) {
            ImageView imageView = this.imageView;
            if (this.currentDrawable == 0) {
                i = 8;
            }
            imageView.setVisibility(i);
            this.imageView.setImageResource(this.currentDrawable);
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }
}
