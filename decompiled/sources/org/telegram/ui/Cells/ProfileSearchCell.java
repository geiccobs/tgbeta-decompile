package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import com.google.android.exoplayer2.C;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.NotificationsSettingsActivity;
/* loaded from: classes4.dex */
public class ProfileSearchCell extends BaseCell implements NotificationCenter.NotificationCenterDelegate {
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private TLRPC.Chat chat;
    CheckBox2 checkBox;
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop;
    private int countWidth;
    private int currentAccount;
    private CharSequence currentName;
    private long dialog_id;
    private boolean drawCheck;
    private boolean drawCount;
    private boolean drawNameLock;
    private boolean drawPremium;
    private TLRPC.EncryptedChat encryptedChat;
    private boolean[] isOnline;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private int lastUnreadCount;
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameTop;
    private int nameWidth;
    private RectF rect;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean savedMessages;
    private StaticLayout statusLayout;
    private int statusLeft;
    private CharSequence subLabel;
    private int sublabelOffsetX;
    private int sublabelOffsetY;
    public boolean useSeparator;
    private TLRPC.User user;

    public ProfileSearchCell(Context context) {
        this(context, null);
    }

    public ProfileSearchCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.countTop = AndroidUtilities.dp(19.0f);
        this.rect = new RectF();
        this.resourcesProvider = resourcesProvider;
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.avatarImage = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(23.0f));
        this.avatarDrawable = new AvatarDrawable();
        CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
        this.checkBox = checkBox2;
        checkBox2.setColor(null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(3);
        addView(this.checkBox);
    }

    public void setData(TLObject object, TLRPC.EncryptedChat ec, CharSequence n, CharSequence s, boolean needCount, boolean saved) {
        this.currentName = n;
        if (object instanceof TLRPC.User) {
            this.user = (TLRPC.User) object;
            this.chat = null;
        } else if (object instanceof TLRPC.Chat) {
            this.chat = (TLRPC.Chat) object;
            this.user = null;
        }
        this.encryptedChat = ec;
        this.subLabel = s;
        this.drawCount = needCount;
        this.savedMessages = saved;
        update(0);
    }

    public void setException(NotificationsSettingsActivity.NotificationException exception, CharSequence name) {
        String text;
        TLRPC.User user;
        boolean enabled;
        boolean custom = exception.hasCustom;
        int value = exception.notify;
        int delta = exception.muteUntil;
        if (value == 3 && delta != Integer.MAX_VALUE) {
            int delta2 = delta - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (delta2 <= 0) {
                if (custom) {
                    text = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                } else {
                    text = LocaleController.getString("NotificationsUnmuted", R.string.NotificationsUnmuted);
                }
            } else if (delta2 < 3600) {
                text = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta2 / 60, new Object[0]));
            } else if (delta2 < 86400) {
                text = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((delta2 / 60.0f) / 60.0f), new Object[0]));
            } else {
                text = delta2 < 31536000 ? LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil(((delta2 / 60.0f) / 60.0f) / 24.0f), new Object[0])) : null;
            }
        } else {
            if (value == 0) {
                enabled = true;
            } else if (value == 1) {
                enabled = true;
            } else if (value == 2) {
                enabled = false;
            } else {
                enabled = false;
            }
            if (enabled && custom) {
                text = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
            } else {
                text = enabled ? LocaleController.getString("NotificationsUnmuted", R.string.NotificationsUnmuted) : LocaleController.getString("NotificationsMuted", R.string.NotificationsMuted);
            }
        }
        if (text == null) {
            text = LocaleController.getString("NotificationsOff", R.string.NotificationsOff);
        }
        if (DialogObject.isEncryptedDialog(exception.did)) {
            TLRPC.EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(exception.did)));
            if (encryptedChat != null && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(encryptedChat.user_id))) != null) {
                setData(user, encryptedChat, name, text, false, false);
            }
        } else if (DialogObject.isUserDialog(exception.did)) {
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(exception.did));
            if (user2 != null) {
                setData(user2, null, name, text, false, false);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-exception.did));
            if (chat != null) {
                setData(chat, null, name, text, false, false);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            invalidate();
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), C.BUFFER_FLAG_ENCRYPTED));
        }
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(60.0f) + (this.useSeparator ? 1 : 0));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.user == null && this.chat == null && this.encryptedChat == null) {
            return;
        }
        if (this.checkBox != null) {
            int x = LocaleController.isRTL ? (right - left) - AndroidUtilities.dp(42.0f) : AndroidUtilities.dp(42.0f);
            int y = AndroidUtilities.dp(36.0f);
            CheckBox2 checkBox2 = this.checkBox;
            checkBox2.layout(x, y, checkBox2.getMeasuredWidth() + x, this.checkBox.getMeasuredHeight() + y);
        }
        if (changed) {
            buildLayout();
        }
    }

    public TLRPC.User getUser() {
        return this.user;
    }

    public TLRPC.Chat getChat() {
        return this.chat;
    }

    public void setSublabelOffset(int x, int y) {
        this.sublabelOffsetX = x;
        this.sublabelOffsetY = y;
    }

    public void buildLayout() {
        TLRPC.EncryptedChat encryptedChat;
        CharSequence nameString;
        TextPaint currentNamePaint;
        int statusWidth;
        int statusWidth2;
        CharSequence statusString;
        int statusWidth3;
        int avatarLeft;
        this.drawNameLock = false;
        this.drawCheck = false;
        this.drawPremium = false;
        if (this.encryptedChat != null) {
            this.drawNameLock = true;
            this.dialog_id = DialogObject.makeEncryptedDialogId(encryptedChat.id);
            if (!LocaleController.isRTL) {
                this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
                this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
            } else {
                this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline + 2)) - Theme.dialogs_lockDrawable.getIntrinsicWidth();
                this.nameLeft = AndroidUtilities.dp(11.0f);
            }
            this.nameLockTop = AndroidUtilities.dp(22.0f);
        } else {
            TLRPC.Chat chat = this.chat;
            if (chat != null) {
                this.dialog_id = -chat.id;
                this.drawCheck = this.chat.verified;
                if (!LocaleController.isRTL) {
                    this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
                } else {
                    this.nameLeft = AndroidUtilities.dp(11.0f);
                }
            } else {
                TLRPC.User user = this.user;
                if (user != null) {
                    this.dialog_id = user.id;
                    if (!LocaleController.isRTL) {
                        this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
                    } else {
                        this.nameLeft = AndroidUtilities.dp(11.0f);
                    }
                    this.nameLockTop = AndroidUtilities.dp(21.0f);
                    this.drawCheck = this.user.verified;
                    this.drawPremium = !this.user.self && MessagesController.getInstance(this.currentAccount).isPremiumUser(this.user);
                }
            }
        }
        if (this.currentName != null) {
            nameString = this.currentName;
        } else {
            String nameString2 = "";
            TLRPC.Chat chat2 = this.chat;
            if (chat2 != null) {
                nameString2 = chat2.title;
            } else {
                TLRPC.User user2 = this.user;
                if (user2 != null) {
                    nameString2 = UserObject.getUserName(user2);
                }
            }
            nameString = nameString2.replace('\n', ' ');
        }
        if (nameString.length() == 0) {
            TLRPC.User user3 = this.user;
            if (user3 != null && user3.phone != null && this.user.phone.length() != 0) {
                nameString = PhoneFormat.getInstance().format("+" + this.user.phone);
            } else {
                nameString = LocaleController.getString("HiddenName", R.string.HiddenName);
            }
        }
        if (this.encryptedChat != null) {
            currentNamePaint = Theme.dialogs_searchNameEncryptedPaint;
        } else {
            currentNamePaint = Theme.dialogs_searchNamePaint;
        }
        if (!LocaleController.isRTL) {
            statusWidth = (getMeasuredWidth() - this.nameLeft) - AndroidUtilities.dp(14.0f);
            this.nameWidth = statusWidth;
        } else {
            int statusWidth4 = getMeasuredWidth();
            statusWidth = (statusWidth4 - this.nameLeft) - AndroidUtilities.dp(AndroidUtilities.leftBaseline);
            this.nameWidth = statusWidth;
        }
        if (this.drawNameLock) {
            this.nameWidth -= AndroidUtilities.dp(6.0f) + Theme.dialogs_lockDrawable.getIntrinsicWidth();
        }
        this.nameWidth -= getPaddingLeft() + getPaddingRight();
        int statusWidth5 = statusWidth - (getPaddingLeft() + getPaddingRight());
        if (this.drawCount) {
            TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (dialog != null && dialog.unread_count != 0) {
                this.lastUnreadCount = dialog.unread_count;
                String countString = String.format("%d", Integer.valueOf(dialog.unread_count));
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil(Theme.dialogs_countTextPaint.measureText(countString)));
                this.countLayout = new StaticLayout(countString, Theme.dialogs_countTextPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                int w = this.countWidth + AndroidUtilities.dp(18.0f);
                this.nameWidth -= w;
                statusWidth5 -= w;
                if (!LocaleController.isRTL) {
                    this.countLeft = (getMeasuredWidth() - this.countWidth) - AndroidUtilities.dp(19.0f);
                } else {
                    this.countLeft = AndroidUtilities.dp(19.0f);
                    this.nameLeft += w;
                }
            } else {
                this.lastUnreadCount = 0;
                this.countLayout = null;
            }
            statusWidth2 = statusWidth5;
        } else {
            this.lastUnreadCount = 0;
            this.countLayout = null;
            statusWidth2 = statusWidth5;
        }
        int statusWidth6 = this.nameWidth;
        if (statusWidth6 < 0) {
            this.nameWidth = 0;
        }
        CharSequence nameStringFinal = TextUtils.ellipsize(nameString, currentNamePaint, this.nameWidth - AndroidUtilities.dp(12.0f), TextUtils.TruncateAt.END);
        int statusWidth7 = statusWidth2;
        this.nameLayout = new StaticLayout(nameStringFinal != null ? Emoji.replaceEmoji(nameStringFinal, currentNamePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false) : nameStringFinal, currentNamePaint, this.nameWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        CharSequence statusString2 = null;
        TextPaint currentStatusPaint = Theme.dialogs_offlinePaint;
        if (!LocaleController.isRTL) {
            this.statusLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
        } else {
            this.statusLeft = AndroidUtilities.dp(11.0f);
        }
        TLRPC.Chat chat3 = this.chat;
        if (chat3 == null || this.subLabel != null) {
            if (this.subLabel != null) {
                statusString2 = this.subLabel;
            } else {
                TLRPC.User user4 = this.user;
                if (user4 != null) {
                    if (MessagesController.isSupportUser(user4)) {
                        statusString2 = LocaleController.getString("SupportStatus", R.string.SupportStatus);
                    } else if (this.user.bot) {
                        statusString2 = LocaleController.getString("Bot", R.string.Bot);
                    } else if (this.user.id == 333000 || this.user.id == 777000) {
                        statusString2 = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
                    } else {
                        if (this.isOnline == null) {
                            this.isOnline = new boolean[1];
                        }
                        boolean[] zArr = this.isOnline;
                        zArr[0] = false;
                        statusString2 = LocaleController.formatUserStatus(this.currentAccount, this.user, zArr);
                        if (this.isOnline[0]) {
                            currentStatusPaint = Theme.dialogs_onlinePaint;
                        }
                        TLRPC.User user5 = this.user;
                        if (user5 != null && (user5.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || (this.user.status != null && this.user.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()))) {
                            TextPaint currentStatusPaint2 = Theme.dialogs_onlinePaint;
                            statusString2 = LocaleController.getString("Online", R.string.Online);
                            currentStatusPaint = currentStatusPaint2;
                        }
                    }
                }
            }
            if (this.savedMessages || UserObject.isReplyUser(this.user)) {
                statusString = null;
                this.nameTop = AndroidUtilities.dp(20.0f);
            } else {
                statusString = statusString2;
            }
        } else {
            if (ChatObject.isChannel(chat3) && !this.chat.megagroup) {
                if (this.chat.participants_count != 0) {
                    statusString = LocaleController.formatPluralString("Subscribers", this.chat.participants_count, new Object[0]);
                } else if (TextUtils.isEmpty(this.chat.username)) {
                    statusString = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                } else {
                    statusString = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
                }
            } else if (this.chat.participants_count != 0) {
                statusString = LocaleController.formatPluralString("Members", this.chat.participants_count, new Object[0]);
            } else if (this.chat.has_geo) {
                statusString = LocaleController.getString("MegaLocation", R.string.MegaLocation);
            } else if (TextUtils.isEmpty(this.chat.username)) {
                statusString = LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase();
            } else {
                statusString = LocaleController.getString("MegaPublic", R.string.MegaPublic).toLowerCase();
            }
            this.nameTop = AndroidUtilities.dp(19.0f);
        }
        if (!TextUtils.isEmpty(statusString)) {
            statusWidth3 = statusWidth7;
            CharSequence statusStringFinal = TextUtils.ellipsize(statusString, currentStatusPaint, statusWidth3 - AndroidUtilities.dp(12.0f), TextUtils.TruncateAt.END);
            this.statusLayout = new StaticLayout(statusStringFinal, currentStatusPaint, statusWidth3, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.nameTop = AndroidUtilities.dp(9.0f);
            this.nameLockTop -= AndroidUtilities.dp(10.0f);
        } else {
            statusWidth3 = statusWidth7;
            this.nameTop = AndroidUtilities.dp(20.0f);
            this.statusLayout = null;
        }
        if (LocaleController.isRTL) {
            avatarLeft = (getMeasuredWidth() - AndroidUtilities.dp(57.0f)) - getPaddingRight();
        } else {
            int avatarLeft2 = AndroidUtilities.dp(11.0f);
            avatarLeft = avatarLeft2 + getPaddingLeft();
        }
        this.avatarImage.setImageCoords(avatarLeft, AndroidUtilities.dp(7.0f), AndroidUtilities.dp(46.0f), AndroidUtilities.dp(46.0f));
        if (!LocaleController.isRTL) {
            if (this.nameLayout.getLineCount() > 0) {
                float left = this.nameLayout.getLineRight(0);
                if (left == this.nameWidth) {
                    double widthpx = Math.ceil(this.nameLayout.getLineWidth(0));
                    int i = this.nameWidth;
                    if (widthpx < i) {
                        double d = this.nameLeft;
                        double d2 = i;
                        Double.isNaN(d2);
                        Double.isNaN(d);
                        this.nameLeft = (int) (d - (d2 - widthpx));
                    }
                }
            }
            StaticLayout staticLayout = this.statusLayout;
            if (staticLayout != null && staticLayout.getLineCount() > 0) {
                float left2 = this.statusLayout.getLineRight(0);
                if (left2 == statusWidth3) {
                    double widthpx2 = Math.ceil(this.statusLayout.getLineWidth(0));
                    if (widthpx2 < statusWidth3) {
                        double d3 = this.statusLeft;
                        double d4 = statusWidth3;
                        Double.isNaN(d4);
                        Double.isNaN(d3);
                        this.statusLeft = (int) (d3 - (d4 - widthpx2));
                    }
                }
            }
        } else {
            if (this.nameLayout.getLineCount() > 0) {
                float left3 = this.nameLayout.getLineLeft(0);
                if (left3 == 0.0f) {
                    double widthpx3 = Math.ceil(this.nameLayout.getLineWidth(0));
                    int i2 = this.nameWidth;
                    if (widthpx3 < i2) {
                        double d5 = this.nameLeft;
                        double d6 = i2;
                        Double.isNaN(d6);
                        Double.isNaN(d5);
                        this.nameLeft = (int) (d5 + (d6 - widthpx3));
                    }
                }
            }
            StaticLayout staticLayout2 = this.statusLayout;
            if (staticLayout2 != null && staticLayout2.getLineCount() > 0) {
                float left4 = this.statusLayout.getLineLeft(0);
                if (left4 == 0.0f) {
                    double widthpx4 = Math.ceil(this.statusLayout.getLineWidth(0));
                    if (widthpx4 < statusWidth3) {
                        double d7 = this.statusLeft;
                        double d8 = statusWidth3;
                        Double.isNaN(d8);
                        Double.isNaN(d7);
                        this.statusLeft = (int) (d7 + (d8 - widthpx4));
                    }
                }
            }
        }
        this.nameLeft += getPaddingLeft();
        this.statusLeft += getPaddingLeft();
        this.nameLockLeft += getPaddingLeft();
    }

    public void update(int mask) {
        TLRPC.Dialog dialog;
        String newName;
        TLRPC.User user;
        TLRPC.FileLocation fileLocation;
        Drawable thumb;
        TLRPC.FileLocation photo = null;
        TLRPC.User user2 = this.user;
        if (user2 != null) {
            this.avatarDrawable.setInfo(user2);
            if (UserObject.isReplyUser(this.user)) {
                this.avatarDrawable.setAvatarType(12);
                this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
            } else if (this.savedMessages) {
                this.avatarDrawable.setAvatarType(1);
                this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
            } else {
                Drawable thumb2 = this.avatarDrawable;
                if (this.user.photo != null) {
                    photo = this.user.photo.photo_small;
                    if (this.user.photo.strippedBitmap != null) {
                        thumb2 = this.user.photo.strippedBitmap;
                    }
                }
                this.avatarImage.setImage(ImageLocation.getForUserOrChat(this.user, 1), "50_50", ImageLocation.getForUserOrChat(this.user, 2), "50_50", thumb2, this.user, 0);
            }
        } else {
            TLRPC.Chat chat = this.chat;
            if (chat != null) {
                Drawable thumb3 = this.avatarDrawable;
                if (chat.photo == null) {
                    thumb = thumb3;
                } else {
                    photo = this.chat.photo.photo_small;
                    if (this.chat.photo.strippedBitmap == null) {
                        thumb = thumb3;
                    } else {
                        thumb = this.chat.photo.strippedBitmap;
                    }
                }
                this.avatarDrawable.setInfo(this.chat);
                this.avatarImage.setImage(ImageLocation.getForUserOrChat(this.chat, 1), "50_50", ImageLocation.getForUserOrChat(this.chat, 2), "50_50", thumb, this.chat, 0);
            } else {
                this.avatarDrawable.setInfo(0L, null, null);
                this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
            }
        }
        if (mask != 0) {
            boolean continueUpdate = false;
            if ((((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 && this.user != null) || ((MessagesController.UPDATE_MASK_CHAT_AVATAR & mask) != 0 && this.chat != null)) && (((fileLocation = this.lastAvatar) != null && photo == null) || ((fileLocation == null && photo != null) || (fileLocation != null && (fileLocation.volume_id != photo.volume_id || this.lastAvatar.local_id != photo.local_id))))) {
                continueUpdate = true;
            }
            if (!continueUpdate && (MessagesController.UPDATE_MASK_STATUS & mask) != 0 && (user = this.user) != null) {
                int newStatus = 0;
                if (user.status != null) {
                    newStatus = this.user.status.expires;
                }
                if (newStatus != this.lastStatus) {
                    continueUpdate = true;
                }
            }
            if ((!continueUpdate && (MessagesController.UPDATE_MASK_NAME & mask) != 0 && this.user != null) || ((MessagesController.UPDATE_MASK_CHAT_NAME & mask) != 0 && this.chat != null)) {
                if (this.user != null) {
                    newName = this.user.first_name + this.user.last_name;
                } else {
                    newName = this.chat.title;
                }
                if (!newName.equals(this.lastName)) {
                    continueUpdate = true;
                }
            }
            if (!continueUpdate && this.drawCount && (MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE & mask) != 0 && (dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id)) != null && dialog.unread_count != this.lastUnreadCount) {
                continueUpdate = true;
            }
            if (!continueUpdate) {
                return;
            }
        }
        TLRPC.User user3 = this.user;
        if (user3 != null) {
            if (user3.status != null) {
                this.lastStatus = this.user.status.expires;
            } else {
                this.lastStatus = 0;
            }
            this.lastName = this.user.first_name + this.user.last_name;
        } else {
            TLRPC.Chat chat2 = this.chat;
            if (chat2 != null) {
                this.lastName = chat2.title;
            }
        }
        this.lastAvatar = photo;
        if (getMeasuredWidth() != 0 || getMeasuredHeight() != 0) {
            buildLayout();
        } else {
            requestLayout();
        }
        postInvalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int x;
        int x2;
        if (this.user == null && this.chat == null && this.encryptedChat == null) {
            return;
        }
        if (this.useSeparator) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
            } else {
                canvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
        if (this.drawNameLock) {
            setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_lockDrawable.draw(canvas);
        }
        if (this.nameLayout != null) {
            canvas.save();
            canvas.translate(this.nameLeft, this.nameTop);
            this.nameLayout.draw(canvas);
            canvas.restore();
            boolean z = this.drawCheck;
            if (z || this.drawPremium) {
                Drawable drawable = z ? Theme.dialogs_verifiedDrawable : PremiumGradient.getInstance().premiumStarDrawableMini;
                if (LocaleController.isRTL) {
                    if (this.nameLayout.getLineLeft(0) == 0.0f) {
                        x2 = (this.nameLeft - AndroidUtilities.dp(6.0f)) - drawable.getIntrinsicWidth();
                    } else {
                        float w = this.nameLayout.getLineWidth(0);
                        double d = this.nameLeft + this.nameWidth;
                        double ceil = Math.ceil(w);
                        Double.isNaN(d);
                        double d2 = d - ceil;
                        double dp = AndroidUtilities.dp(6.0f);
                        Double.isNaN(dp);
                        double d3 = d2 - dp;
                        double intrinsicWidth = drawable.getIntrinsicWidth();
                        Double.isNaN(intrinsicWidth);
                        x2 = (int) (d3 - intrinsicWidth);
                    }
                } else {
                    x2 = (int) (this.nameLeft + this.nameLayout.getLineRight(0) + AndroidUtilities.dp(6.0f));
                }
                if (this.drawCheck) {
                    setDrawableBounds(Theme.dialogs_verifiedDrawable, x2, this.nameTop + AndroidUtilities.dp(3.0f));
                    setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, x2, this.nameTop + AndroidUtilities.dp(3.0f));
                    Theme.dialogs_verifiedDrawable.draw(canvas);
                    Theme.dialogs_verifiedCheckDrawable.draw(canvas);
                } else if (this.drawPremium) {
                    setDrawableBounds(drawable, x2, this.nameTop + AndroidUtilities.dp(1.5f));
                    drawable.draw(canvas);
                }
            }
        }
        if (this.statusLayout != null) {
            canvas.save();
            canvas.translate(this.statusLeft + this.sublabelOffsetX, AndroidUtilities.dp(33.0f) + this.sublabelOffsetY);
            this.statusLayout.draw(canvas);
            canvas.restore();
        }
        if (this.countLayout != null) {
            this.rect.set(this.countLeft - AndroidUtilities.dp(5.5f), this.countTop, this.countWidth + x + AndroidUtilities.dp(11.0f), this.countTop + AndroidUtilities.dp(23.0f));
            canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
            canvas.save();
            canvas.translate(this.countLeft, this.countTop + AndroidUtilities.dp(4.0f));
            this.countLayout.draw(canvas);
            canvas.restore();
        }
        this.avatarImage.draw(canvas);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder builder = new StringBuilder();
        StaticLayout staticLayout = this.nameLayout;
        if (staticLayout != null) {
            builder.append(staticLayout.getText());
        }
        if (this.drawCheck) {
            builder.append(", ");
            builder.append(LocaleController.getString("AccDescrVerified", R.string.AccDescrVerified));
            builder.append("\n");
        }
        if (this.statusLayout != null) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(this.statusLayout.getText());
        }
        info.setText(builder.toString());
        if (this.checkBox.isChecked()) {
            info.setCheckable(true);
            info.setChecked(this.checkBox.isChecked());
            info.setClassName("android.widget.CheckBox");
        }
    }

    public long getDialogId() {
        return this.dialog_id;
    }

    public void setChecked(boolean checked, boolean animated) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 == null) {
            return;
        }
        checkBox2.setChecked(checked, animated);
    }
}
