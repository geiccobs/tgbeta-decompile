package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.StatisticActivity;
/* loaded from: classes4.dex */
public class StatisticPostInfoCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private final TLRPC.ChatFull chat;
    private TextView date;
    private BackupImageView imageView;
    private TextView message;
    private TextView shares;
    private TextView views;

    public StatisticPostInfoCell(Context context, TLRPC.ChatFull chat) {
        super(context);
        this.chat = chat;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrame(46, 46.0f, 8388627, 12.0f, 0.0f, 16.0f, 0.0f));
        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(1);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        TextView textView = new TextView(context);
        this.message = textView;
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.message.setTextSize(1, 15.0f);
        this.message.setTextColor(-16777216);
        this.message.setLines(1);
        this.message.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView2 = new TextView(context);
        this.views = textView2;
        textView2.setTextSize(1, 15.0f);
        this.views.setTextColor(-16777216);
        linearLayout.addView(this.message, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 0, 16, 0));
        linearLayout.addView(this.views, LayoutHelper.createLinear(-2, -2));
        contentLayout.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 8.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.date = textView3;
        textView3.setTextSize(1, 13.0f);
        this.date.setTextColor(-16777216);
        this.date.setLines(1);
        this.date.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView4 = new TextView(context);
        this.shares = textView4;
        textView4.setTextSize(1, 13.0f);
        this.shares.setTextColor(-16777216);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.addView(this.date, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 0, 8, 0));
        linearLayout2.addView(this.shares, LayoutHelper.createLinear(-2, -2));
        contentLayout.addView(linearLayout2, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 2.0f, 0.0f, 8.0f));
        addView(contentLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 72.0f, 0.0f, 12.0f, 0.0f));
        this.message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.views.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.date.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.shares.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
    }

    public void setData(StatisticActivity.RecentPostInfo postInfo) {
        String text;
        MessageObject messageObject = postInfo.message;
        if (messageObject.photoThumbs != null) {
            TLRPC.PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
            TLRPC.PhotoSize thumbSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 50);
            this.imageView.setImage(ImageLocation.getForObject(size, messageObject.photoThumbsObject), "50_50", ImageLocation.getForObject(thumbSize, messageObject.photoThumbsObject), "b1", 0, messageObject);
            this.imageView.setRoundRadius(AndroidUtilities.dp(4.0f));
        } else if (this.chat.chat_photo.sizes.size() > 0) {
            this.imageView.setImage(ImageLocation.getForPhoto(this.chat.chat_photo.sizes.get(0), this.chat.chat_photo), "50_50", (String) null, (Drawable) null, this.chat);
            this.imageView.setRoundRadius(AndroidUtilities.dp(46.0f) >> 1);
        }
        if (messageObject.isMusic()) {
            text = String.format("%s, %s", messageObject.getMusicTitle().trim(), messageObject.getMusicAuthor().trim());
        } else {
            text = (messageObject.caption != null ? messageObject.caption : messageObject.messageText).toString();
        }
        this.message.setText(text.replace("\n", " ").trim());
        this.views.setText(String.format(LocaleController.getPluralString("Views", postInfo.counters.views), AndroidUtilities.formatCount(postInfo.counters.views)));
        this.date.setText(LocaleController.formatDateAudio(postInfo.message.messageOwner.date, false));
        this.shares.setText(String.format(LocaleController.getPluralString("Shares", postInfo.counters.forwards), AndroidUtilities.formatCount(postInfo.counters.forwards)));
    }

    public void setData(StatisticActivity.MemberData memberData) {
        this.avatarDrawable.setInfo(memberData.user);
        this.imageView.setForUserOrChat(memberData.user, this.avatarDrawable);
        this.imageView.setRoundRadius(AndroidUtilities.dp(46.0f) >> 1);
        this.message.setText(memberData.user.first_name);
        this.date.setText(memberData.description);
        this.views.setVisibility(8);
        this.shares.setVisibility(8);
    }
}
