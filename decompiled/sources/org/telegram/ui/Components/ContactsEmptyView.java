package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ContactsEmptyView extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final String stickerSetName = "tg_placeholders_android";
    public static final String svg = "m418 282.6c13.4-21.1 20.2-44.9 20.2-70.8 0-88.3-79.8-175.3-178.9-175.3-100.1 0-178.9 88-178.9 175.3 0 46.6 16.9 73.1 29.1 86.1-19.3 23.4-30.9 52.3-34.6 86.1-2.5 22.7 3.2 41.4 17.4 57.3 14.3 16 51.7 35 148.1 35 41.2 0 119.9-5.3 156.7-18.3 49.5-17.4 59.2-41.1 59.2-76.2 0-41.5-12.9-74.8-38.3-99.2z";
    private LoadingStickerDrawable drawable;
    private BackupImageView stickerView;
    private TextView titleTextView;
    private ArrayList<TextView> textViews = new ArrayList<>();
    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private int currentAccount = UserConfig.selectedAccount;

    public ContactsEmptyView(Context context) {
        super(context);
        setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        setOrientation(1);
        this.stickerView = new BackupImageView(context);
        LoadingStickerDrawable loadingStickerDrawable = new LoadingStickerDrawable(this.stickerView, svg, AndroidUtilities.dp(130.0f), AndroidUtilities.dp(130.0f));
        this.drawable = loadingStickerDrawable;
        this.stickerView.setImageDrawable(loadingStickerDrawable);
        if (!AndroidUtilities.isTablet()) {
            addView(this.stickerView, LayoutHelper.createLinear((int) TsExtractor.TS_STREAM_TYPE_HDMV_DTS, (int) TsExtractor.TS_STREAM_TYPE_HDMV_DTS, 49, 0, 2, 0, 0));
        }
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setTextSize(1, 20.0f);
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleTextView.setGravity(1);
        this.titleTextView.setText(LocaleController.getString("NoContactsYet", R.string.NoContactsYet));
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.titleTextView.setMaxWidth(AndroidUtilities.dp(260.0f));
        addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 18, 0, 14));
        LinearLayout linesContainer = new LinearLayout(context);
        linesContainer.setOrientation(1);
        addView(linesContainer, LayoutHelper.createLinear(-2, -2, 49));
        int a = 0;
        while (true) {
            int i = 3;
            if (a < 3) {
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(0);
                linesContainer.addView(linearLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
                ImageView imageView = new ImageView(context);
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText), PorterDuff.Mode.MULTIPLY));
                imageView.setImageResource(R.drawable.list_circle);
                this.imageViews.add(imageView);
                TextView textView2 = new TextView(context);
                textView2.setTextSize(1, 15.0f);
                textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                textView2.setMaxWidth(AndroidUtilities.dp(260.0f));
                this.textViews.add(textView2);
                textView2.setGravity((LocaleController.isRTL ? 5 : i) | 16);
                switch (a) {
                    case 0:
                        textView2.setText(LocaleController.getString("NoContactsYetLine1", R.string.NoContactsYetLine1));
                        break;
                    case 1:
                        textView2.setText(LocaleController.getString("NoContactsYetLine2", R.string.NoContactsYetLine2));
                        break;
                    case 2:
                        textView2.setText(LocaleController.getString("NoContactsYetLine3", R.string.NoContactsYetLine3));
                        break;
                }
                if (LocaleController.isRTL) {
                    linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2));
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                    linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2));
                }
                a++;
            } else {
                return;
            }
        }
    }

    public void setColors() {
        for (int a = 0; a < this.textViews.size(); a++) {
            this.textViews.get(a).setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        }
        for (int a2 = 0; a2 < this.imageViews.size(); a2++) {
            this.imageViews.get(a2).setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText), PorterDuff.Mode.MULTIPLY));
        }
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    private void setSticker() {
        TLRPC.TL_messages_stickerSet set = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("tg_placeholders_android");
        if (set == null) {
            set = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders_android");
        }
        if (set == null || set.documents.size() < 1) {
            MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, true);
            this.stickerView.setImageDrawable(this.drawable);
            return;
        }
        TLRPC.Document document = set.documents.get(0);
        ImageLocation imageLocation = ImageLocation.getForDocument(document);
        this.stickerView.setImage(imageLocation, "130_130", "tgs", this.drawable, set);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSticker();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.diceStickersDidLoad) {
            String name = (String) args[0];
            if ("tg_placeholders_android".equals(name)) {
                setSticker();
            }
        }
    }
}
