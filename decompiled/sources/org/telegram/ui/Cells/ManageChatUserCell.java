package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
/* loaded from: classes4.dex */
public class ManageChatUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private int currentAccount;
    private CharSequence currentName;
    private Object currentObject;
    private CharSequence currrntStatus;
    private ImageView customImageView;
    private ManageChatUserCellDelegate delegate;
    private String dividerColor;
    private boolean isAdmin;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private int namePadding;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private ImageView optionsButton;
    private Theme.ResourcesProvider resourcesProvider;
    private int statusColor;
    private int statusOnlineColor;
    private SimpleTextView statusTextView;

    /* loaded from: classes4.dex */
    public interface ManageChatUserCellDelegate {
        boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z);
    }

    public ManageChatUserCell(Context context, int avatarPadding, int nPadding, boolean needOption) {
        this(context, avatarPadding, nPadding, needOption, null);
    }

    public ManageChatUserCell(Context context, int avatarPadding, int nPadding, boolean needOption, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.resourcesProvider = resourcesProvider;
        this.statusColor = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider);
        this.statusOnlineColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, resourcesProvider);
        this.namePadding = nPadding;
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
        int i = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : avatarPadding + 7, 8.0f, LocaleController.isRTL ? avatarPadding + 7 : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 46.0f : this.namePadding + 68, 11.5f, LocaleController.isRTL ? this.namePadding + 68 : 46.0f, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : this.namePadding + 68, 34.5f, LocaleController.isRTL ? this.namePadding + 68 : 28.0f, 0.0f));
        if (needOption) {
            ImageView imageView = new ImageView(context);
            this.optionsButton = imageView;
            imageView.setFocusable(false);
            this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector, resourcesProvider)));
            this.optionsButton.setImageResource(R.drawable.ic_ab_other);
            this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu, resourcesProvider), PorterDuff.Mode.MULTIPLY));
            this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.optionsButton, LayoutHelper.createFrame(60, 64, (LocaleController.isRTL ? 3 : i) | 48));
            this.optionsButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.ManageChatUserCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ManageChatUserCell.this.m1658lambda$new$0$orgtelegramuiCellsManageChatUserCell(view);
                }
            });
            this.optionsButton.setContentDescription(LocaleController.getString("AccDescrUserOptions", R.string.AccDescrUserOptions));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-ManageChatUserCell */
    public /* synthetic */ void m1658lambda$new$0$orgtelegramuiCellsManageChatUserCell(View v) {
        this.delegate.onOptionsButtonCheck(this, true);
    }

    public void setCustomRightImage(int resId) {
        ImageView imageView = new ImageView(getContext());
        this.customImageView = imageView;
        imageView.setImageResource(resId);
        this.customImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.customImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_voipgroup_mutedIconUnscrolled, this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
        addView(this.customImageView, LayoutHelper.createFrame(52, 64, (LocaleController.isRTL ? 3 : 5) | 48));
    }

    public void setCustomImageVisible(boolean visible) {
        ImageView imageView = this.customImageView;
        if (imageView == null) {
            return;
        }
        imageView.setVisibility(visible ? 0 : 8);
    }

    public void setData(Object object, CharSequence name, CharSequence status, boolean divider) {
        int i;
        int i2;
        int i3;
        float f;
        int i4;
        int i5;
        int i6;
        float f2;
        if (object == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.currrntStatus = status;
        this.currentName = name;
        this.currentObject = object;
        int i7 = 5;
        int i8 = 28;
        if (this.optionsButton != null) {
            boolean visible = this.delegate.onOptionsButtonCheck(this, false);
            this.optionsButton.setVisibility(visible ? 0 : 4);
            SimpleTextView simpleTextView = this.nameTextView;
            int i9 = (LocaleController.isRTL ? 5 : 3) | 48;
            if (LocaleController.isRTL) {
                i4 = visible ? 46 : 28;
            } else {
                i4 = this.namePadding + 68;
            }
            float f3 = i4;
            float f4 = (status == null || status.length() > 0) ? 11.5f : 20.5f;
            if (LocaleController.isRTL) {
                i5 = this.namePadding + 68;
            } else {
                i5 = visible ? 46 : 28;
            }
            simpleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i9, f3, f4, i5, 0.0f));
            SimpleTextView simpleTextView2 = this.statusTextView;
            if (!LocaleController.isRTL) {
                i7 = 3;
            }
            int i10 = i7 | 48;
            if (LocaleController.isRTL) {
                i6 = visible ? 46 : 28;
            } else {
                i6 = this.namePadding + 68;
            }
            float f5 = i6;
            if (LocaleController.isRTL) {
                f2 = this.namePadding + 68;
            } else {
                if (visible) {
                    i8 = 46;
                }
                f2 = i8;
            }
            simpleTextView2.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i10, f5, 34.5f, f2, 0.0f));
        } else {
            ImageView imageView = this.customImageView;
            if (imageView != null) {
                boolean visible2 = imageView.getVisibility() == 0;
                SimpleTextView simpleTextView3 = this.nameTextView;
                int i11 = (LocaleController.isRTL ? 5 : 3) | 48;
                if (LocaleController.isRTL) {
                    i = visible2 ? 54 : 28;
                } else {
                    i = this.namePadding + 68;
                }
                float f6 = i;
                float f7 = (status == null || status.length() > 0) ? 11.5f : 20.5f;
                if (LocaleController.isRTL) {
                    i2 = this.namePadding + 68;
                } else {
                    i2 = visible2 ? 54 : 28;
                }
                simpleTextView3.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i11, f6, f7, i2, 0.0f));
                SimpleTextView simpleTextView4 = this.statusTextView;
                if (!LocaleController.isRTL) {
                    i7 = 3;
                }
                int i12 = i7 | 48;
                if (LocaleController.isRTL) {
                    i3 = visible2 ? 54 : 28;
                } else {
                    i3 = this.namePadding + 68;
                }
                float f8 = i3;
                if (LocaleController.isRTL) {
                    f = this.namePadding + 68;
                } else {
                    if (visible2) {
                        i8 = 54;
                    }
                    f = i8;
                }
                simpleTextView4.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, i12, f8, 34.5f, f, 0.0f));
            }
        }
        this.needDivider = divider;
        setWillNotDraw(!divider);
        update(0);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + (this.needDivider ? 1 : 0), C.BUFFER_FLAG_ENCRYPTED));
    }

    public long getUserId() {
        Object obj = this.currentObject;
        if (obj instanceof TLRPC.User) {
            return ((TLRPC.User) obj).id;
        }
        return 0L;
    }

    public void setStatusColors(int color, int onlineColor) {
        this.statusColor = color;
        this.statusOnlineColor = onlineColor;
    }

    public void setIsAdmin(boolean value) {
        this.isAdmin = value;
    }

    public boolean hasAvatarSet() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    public void setNameColor(int color) {
        this.nameTextView.setTextColor(color);
    }

    public void setDividerColor(String key) {
        this.dividerColor = key;
    }

    public void update(int mask) {
        TLRPC.FileLocation fileLocation;
        TLRPC.FileLocation fileLocation2;
        Object obj = this.currentObject;
        if (obj == null) {
            return;
        }
        if (!(obj instanceof TLRPC.User)) {
            if (obj instanceof TLRPC.Chat) {
                TLRPC.Chat currentChat = (TLRPC.Chat) obj;
                TLRPC.FileLocation photo = null;
                String newName = null;
                if (currentChat.photo != null) {
                    photo = currentChat.photo.photo_small;
                }
                if (mask != 0) {
                    boolean continueUpdate = false;
                    if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 && (((fileLocation = this.lastAvatar) != null && photo == null) || ((fileLocation == null && photo != null) || (fileLocation != null && (fileLocation.volume_id != photo.volume_id || this.lastAvatar.local_id != photo.local_id))))) {
                        continueUpdate = true;
                    }
                    if (!continueUpdate && this.currentName == null && this.lastName != null && (MessagesController.UPDATE_MASK_NAME & mask) != 0) {
                        newName = currentChat.title;
                        if (!newName.equals(this.lastName)) {
                            continueUpdate = true;
                        }
                    }
                    if (!continueUpdate) {
                        return;
                    }
                }
                this.avatarDrawable.setInfo(currentChat);
                CharSequence charSequence = this.currentName;
                if (charSequence != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence);
                } else {
                    String str = newName == null ? currentChat.title : newName;
                    this.lastName = str;
                    this.nameTextView.setText(str);
                }
                if (this.currrntStatus != null) {
                    this.statusTextView.setTextColor(this.statusColor);
                    this.statusTextView.setText(this.currrntStatus);
                } else {
                    this.statusTextView.setTextColor(this.statusColor);
                    if (currentChat.participants_count != 0) {
                        if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                            this.statusTextView.setText(LocaleController.formatPluralString("Subscribers", currentChat.participants_count, new Object[0]));
                        } else {
                            this.statusTextView.setText(LocaleController.formatPluralString("Members", currentChat.participants_count, new Object[0]));
                        }
                    } else if (currentChat.has_geo) {
                        this.statusTextView.setText(LocaleController.getString("MegaLocation", R.string.MegaLocation));
                    } else if (TextUtils.isEmpty(currentChat.username)) {
                        this.statusTextView.setText(LocaleController.getString("MegaPrivate", R.string.MegaPrivate));
                    } else {
                        this.statusTextView.setText(LocaleController.getString("MegaPublic", R.string.MegaPublic));
                    }
                }
                this.lastAvatar = photo;
                this.avatarImageView.setForUserOrChat(currentChat, this.avatarDrawable);
                return;
            } else if (obj instanceof Integer) {
                this.nameTextView.setText(this.currentName);
                this.statusTextView.setTextColor(this.statusColor);
                this.statusTextView.setText(this.currrntStatus);
                this.avatarDrawable.setAvatarType(3);
                this.avatarImageView.setImage(null, "50_50", this.avatarDrawable);
                return;
            } else {
                return;
            }
        }
        TLRPC.User currentUser = (TLRPC.User) obj;
        TLRPC.FileLocation photo2 = null;
        String newName2 = null;
        if (currentUser.photo != null) {
            photo2 = currentUser.photo.photo_small;
        }
        if (mask != 0) {
            boolean continueUpdate2 = false;
            if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 && (((fileLocation2 = this.lastAvatar) != null && photo2 == null) || ((fileLocation2 == null && photo2 != null) || (fileLocation2 != null && (fileLocation2.volume_id != photo2.volume_id || this.lastAvatar.local_id != photo2.local_id))))) {
                continueUpdate2 = true;
            }
            if (!continueUpdate2 && (MessagesController.UPDATE_MASK_STATUS & mask) != 0) {
                int newStatus = 0;
                if (currentUser.status != null) {
                    newStatus = currentUser.status.expires;
                }
                if (newStatus != this.lastStatus) {
                    continueUpdate2 = true;
                }
            }
            if (!continueUpdate2 && this.currentName == null && this.lastName != null && (MessagesController.UPDATE_MASK_NAME & mask) != 0) {
                newName2 = UserObject.getUserName(currentUser);
                if (!newName2.equals(this.lastName)) {
                    continueUpdate2 = true;
                }
            }
            if (!continueUpdate2) {
                return;
            }
        }
        this.avatarDrawable.setInfo(currentUser);
        if (currentUser.status != null) {
            this.lastStatus = currentUser.status.expires;
        } else {
            this.lastStatus = 0;
        }
        CharSequence charSequence2 = this.currentName;
        if (charSequence2 != null) {
            this.lastName = null;
            this.nameTextView.setText(charSequence2);
        } else {
            String userName = newName2 == null ? UserObject.getUserName(currentUser) : newName2;
            this.lastName = userName;
            this.nameTextView.setText(userName);
        }
        if (this.currrntStatus != null) {
            this.statusTextView.setTextColor(this.statusColor);
            this.statusTextView.setText(this.currrntStatus);
        } else if (currentUser.bot) {
            this.statusTextView.setTextColor(this.statusColor);
            if (currentUser.bot_chat_history || this.isAdmin) {
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
        this.lastAvatar = photo2;
        this.avatarImageView.setForUserOrChat(currentUser, this.avatarDrawable);
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setDelegate(ManageChatUserCellDelegate manageChatUserCellDelegate) {
        this.delegate = manageChatUserCellDelegate;
    }

    public Object getCurrentObject() {
        return this.currentObject;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            if (this.dividerColor != null) {
                Theme.dividerExtraPaint.setColor(Theme.getColor(this.dividerColor, this.resourcesProvider));
            }
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(68.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0), getMeasuredHeight() - 1, this.dividerColor != null ? Theme.dividerExtraPaint : Theme.dividerPaint);
        }
    }
}
