package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.C;
import com.google.android.gms.maps.model.LatLng;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
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
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LocationActivity;
/* loaded from: classes4.dex */
public class SharingLiveLocationCell extends FrameLayout {
    private BackupImageView avatarImageView;
    private LocationController.SharingLocationInfo currentInfo;
    private SimpleTextView distanceTextView;
    private LocationActivity.LiveLocation liveLocation;
    private SimpleTextView nameTextView;
    private final Theme.ResourcesProvider resourcesProvider;
    private RectF rect = new RectF();
    private Location location = new Location("network");
    private int currentAccount = UserConfig.selectedAccount;
    private Runnable invalidateRunnable = new Runnable() { // from class: org.telegram.ui.Cells.SharingLiveLocationCell.1
        @Override // java.lang.Runnable
        public void run() {
            SharingLiveLocationCell sharingLiveLocationCell = SharingLiveLocationCell.this;
            sharingLiveLocationCell.invalidate(((int) sharingLiveLocationCell.rect.left) - 5, ((int) SharingLiveLocationCell.this.rect.top) - 5, ((int) SharingLiveLocationCell.this.rect.right) + 5, ((int) SharingLiveLocationCell.this.rect.bottom) + 5);
            AndroidUtilities.runOnUIThread(SharingLiveLocationCell.this.invalidateRunnable, 1000L);
        }
    };
    private AvatarDrawable avatarDrawable = new AvatarDrawable();

    public SharingLiveLocationCell(Context context, boolean distance, int padding, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextSize(16);
        this.nameTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        int i = 5;
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        if (distance) {
            addView(this.avatarImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 12.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
            addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? padding : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : padding, 0.0f));
            SimpleTextView simpleTextView2 = new SimpleTextView(context);
            this.distanceTextView = simpleTextView2;
            simpleTextView2.setTextSize(14);
            this.distanceTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
            this.distanceTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.distanceTextView, LayoutHelper.createFrame(-1, 20.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? padding : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : padding, 0.0f));
        } else {
            addView(this.avatarImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 6.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
            addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? padding : 74.0f, 17.0f, LocaleController.isRTL ? 74.0f : padding, 0.0f));
        }
        setWillNotDraw(false);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.distanceTextView != null ? 66.0f : 54.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AndroidUtilities.runOnUIThread(this.invalidateRunnable);
    }

    public void setDialog(long dialogId, TLRPC.TL_channelLocation chatLocation) {
        this.currentAccount = UserConfig.selectedAccount;
        String address = chatLocation.address;
        String name = "";
        this.avatarDrawable = null;
        if (DialogObject.isUserDialog(dialogId)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(dialogId));
            if (user != null) {
                this.avatarDrawable = new AvatarDrawable(user);
                name = UserObject.getUserName(user);
                this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialogId));
            if (chat != null) {
                this.avatarDrawable = new AvatarDrawable(chat);
                name = chat.title;
                this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
            }
        }
        this.nameTextView.setText(name);
        this.location.setLatitude(chatLocation.geo_point.lat);
        this.location.setLongitude(chatLocation.geo_point._long);
        this.distanceTextView.setText(address);
    }

    public void setDialog(MessageObject messageObject, Location userLocation, boolean userLocationDenied) {
        String name;
        long fromId = messageObject.getFromChatId();
        if (messageObject.isForwarded()) {
            fromId = MessageObject.getPeerId(messageObject.messageOwner.fwd_from.from_id);
        }
        this.currentAccount = messageObject.currentAccount;
        String address = null;
        if (!TextUtils.isEmpty(messageObject.messageOwner.media.address)) {
            address = messageObject.messageOwner.media.address;
        }
        if (!TextUtils.isEmpty(messageObject.messageOwner.media.title)) {
            name = messageObject.messageOwner.media.title;
            Drawable drawable = getResources().getDrawable(R.drawable.pin);
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_location_sendLocationIcon), PorterDuff.Mode.MULTIPLY));
            int color = getThemedColor(Theme.key_location_placeLocationBackground);
            Drawable circle = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(42.0f), color, color);
            CombinedDrawable combinedDrawable = new CombinedDrawable(circle, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.avatarImageView.setImageDrawable(combinedDrawable);
        } else {
            name = "";
            this.avatarDrawable = null;
            if (fromId > 0) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(fromId));
                if (user != null) {
                    this.avatarDrawable = new AvatarDrawable(user);
                    name = UserObject.getUserName(user);
                    this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
                }
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-fromId));
                if (chat != null) {
                    this.avatarDrawable = new AvatarDrawable(chat);
                    name = chat.title;
                    this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
                }
            }
        }
        this.nameTextView.setText(name);
        this.location.setLatitude(messageObject.messageOwner.media.geo.lat);
        this.location.setLongitude(messageObject.messageOwner.media.geo._long);
        if (userLocation != null) {
            float distance = this.location.distanceTo(userLocation);
            if (address == null) {
                this.distanceTextView.setText(LocaleController.formatDistance(distance, 0));
            } else {
                this.distanceTextView.setText(String.format("%s - %s", address, LocaleController.formatDistance(distance, 0)));
            }
        } else if (address != null) {
            this.distanceTextView.setText(address);
        } else if (!userLocationDenied) {
            this.distanceTextView.setText(LocaleController.getString("Loading", R.string.Loading));
        } else {
            this.distanceTextView.setText("");
        }
    }

    public void setDialog(LocationActivity.LiveLocation info, Location userLocation) {
        this.liveLocation = info;
        if (DialogObject.isUserDialog(info.id)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(info.id));
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-info.id));
            if (chat != null) {
                this.avatarDrawable.setInfo(chat);
                this.nameTextView.setText(chat.title);
                this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
            }
        }
        LatLng position = info.marker.getPosition();
        this.location.setLatitude(position.latitude);
        this.location.setLongitude(position.longitude);
        String time = LocaleController.formatLocationUpdateDate(info.object.edit_date != 0 ? info.object.edit_date : info.object.date);
        if (userLocation != null) {
            this.distanceTextView.setText(String.format("%s - %s", time, LocaleController.formatDistance(this.location.distanceTo(userLocation), 0)));
        } else {
            this.distanceTextView.setText(time);
        }
    }

    public void setDialog(LocationController.SharingLocationInfo info) {
        this.currentInfo = info;
        this.currentAccount = info.account;
        this.avatarImageView.getImageReceiver().setCurrentAccount(this.currentAccount);
        if (DialogObject.isUserDialog(info.did)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(info.did));
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
                return;
            }
            return;
        }
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-info.did));
        if (chat != null) {
            this.avatarDrawable.setInfo(chat);
            this.nameTextView.setText(chat.title);
            this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int period;
        int stopTime;
        int color;
        LocationController.SharingLocationInfo sharingLocationInfo = this.currentInfo;
        if (sharingLocationInfo == null && this.liveLocation == null) {
            return;
        }
        if (sharingLocationInfo != null) {
            stopTime = sharingLocationInfo.stopTime;
            period = this.currentInfo.period;
        } else {
            stopTime = this.liveLocation.object.date + this.liveLocation.object.media.period;
            period = this.liveLocation.object.media.period;
        }
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        if (stopTime < currentTime) {
            return;
        }
        float progress = Math.abs(stopTime - currentTime) / period;
        float f = 48.0f;
        float f2 = 18.0f;
        if (LocaleController.isRTL) {
            RectF rectF = this.rect;
            float dp = AndroidUtilities.dp(13.0f);
            if (this.distanceTextView == null) {
                f2 = 12.0f;
            }
            float dp2 = AndroidUtilities.dp(f2);
            float dp3 = AndroidUtilities.dp(43.0f);
            if (this.distanceTextView == null) {
                f = 42.0f;
            }
            rectF.set(dp, dp2, dp3, AndroidUtilities.dp(f));
        } else {
            RectF rectF2 = this.rect;
            float measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(43.0f);
            if (this.distanceTextView == null) {
                f2 = 12.0f;
            }
            float dp4 = AndroidUtilities.dp(f2);
            float measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp(13.0f);
            if (this.distanceTextView == null) {
                f = 42.0f;
            }
            rectF2.set(measuredWidth, dp4, measuredWidth2, AndroidUtilities.dp(f));
        }
        if (this.distanceTextView == null) {
            color = getThemedColor(Theme.key_dialog_liveLocationProgress);
        } else {
            color = getThemedColor(Theme.key_location_liveLocationProgress);
        }
        Theme.chat_radialProgress2Paint.setColor(color);
        Theme.chat_livePaint.setColor(color);
        canvas.drawArc(this.rect, -90.0f, progress * (-360.0f), false, Theme.chat_radialProgress2Paint);
        String text = LocaleController.formatLocationLeftTime(stopTime - currentTime);
        float size = Theme.chat_livePaint.measureText(text);
        canvas.drawText(text, this.rect.centerX() - (size / 2.0f), AndroidUtilities.dp(this.distanceTextView != null ? 37.0f : 31.0f), Theme.chat_livePaint);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
