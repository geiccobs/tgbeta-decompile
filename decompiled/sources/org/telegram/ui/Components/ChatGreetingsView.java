package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ChatGreetingsView extends LinearLayout {
    private final int currentAccount;
    private TextView descriptionView;
    boolean ignoreLayot;
    private Listener listener;
    private TLRPC.Document preloadedGreetingsSticker;
    private final Theme.ResourcesProvider resourcesProvider;
    public BackupImageView stickerToSendView;
    private TextView titleView;
    boolean wasDraw;

    /* loaded from: classes5.dex */
    public interface Listener {
        void onGreetings(TLRPC.Document document);
    }

    public ChatGreetingsView(Context context, TLRPC.User user, int distance, int currentAccount, TLRPC.Document sticker, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        setOrientation(1);
        this.currentAccount = currentAccount;
        this.resourcesProvider = resourcesProvider;
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextSize(1, 14.0f);
        this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.titleView.setGravity(1);
        TextView textView2 = new TextView(context);
        this.descriptionView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.descriptionView.setGravity(1);
        this.stickerToSendView = new BackupImageView(context);
        addView(this.titleView, LayoutHelper.createLinear(-1, -2, 20.0f, 14.0f, 20.0f, 14.0f));
        addView(this.descriptionView, LayoutHelper.createLinear(-1, -2, 20.0f, 12.0f, 20.0f, 0.0f));
        addView(this.stickerToSendView, LayoutHelper.createLinear(112, 112, 1, 0, 16, 0, 16));
        updateColors();
        if (distance <= 0) {
            this.titleView.setText(LocaleController.getString("NoMessages", R.string.NoMessages));
            this.descriptionView.setText(LocaleController.getString("NoMessagesGreetingsDescription", R.string.NoMessagesGreetingsDescription));
        } else {
            this.titleView.setText(LocaleController.formatString("NearbyPeopleGreetingsMessage", R.string.NearbyPeopleGreetingsMessage, user.first_name, LocaleController.formatDistance(distance, 1)));
            this.descriptionView.setText(LocaleController.getString("NearbyPeopleGreetingsDescription", R.string.NearbyPeopleGreetingsDescription));
        }
        this.stickerToSendView.setContentDescription(this.descriptionView.getText());
        this.preloadedGreetingsSticker = sticker;
        if (sticker == null) {
            this.preloadedGreetingsSticker = MediaDataController.getInstance(currentAccount).getGreetingsSticker();
        }
    }

    private void setSticker(final TLRPC.Document sticker) {
        if (sticker == null) {
            return;
        }
        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(sticker, Theme.key_chat_serviceBackground, 1.0f);
        if (svgThumb != null) {
            this.stickerToSendView.setImage(ImageLocation.getForDocument(sticker), createFilter(sticker), svgThumb, 0, sticker);
        } else {
            TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(sticker.thumbs, 90);
            this.stickerToSendView.setImage(ImageLocation.getForDocument(sticker), createFilter(sticker), ImageLocation.getForDocument(thumb, sticker), (String) null, 0, sticker);
        }
        this.stickerToSendView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatGreetingsView$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatGreetingsView.this.m2515lambda$setSticker$0$orgtelegramuiComponentsChatGreetingsView(sticker, view);
            }
        });
    }

    /* renamed from: lambda$setSticker$0$org-telegram-ui-Components-ChatGreetingsView */
    public /* synthetic */ void m2515lambda$setSticker$0$orgtelegramuiComponentsChatGreetingsView(TLRPC.Document sticker, View v) {
        Listener listener = this.listener;
        if (listener != null) {
            listener.onGreetings(sticker);
        }
    }

    public static String createFilter(TLRPC.Document document) {
        float maxWidth;
        float maxHeight;
        int photoWidth = 0;
        int photoHeight = 0;
        if (AndroidUtilities.isTablet()) {
            maxHeight = AndroidUtilities.getMinTabletSide() * 0.4f;
            maxWidth = maxHeight;
        } else {
            maxHeight = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5f;
            maxWidth = maxHeight;
        }
        int a = 0;
        while (true) {
            if (a >= document.attributes.size()) {
                break;
            }
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (!(attribute instanceof TLRPC.TL_documentAttributeImageSize)) {
                a++;
            } else {
                photoWidth = attribute.w;
                photoHeight = attribute.h;
                break;
            }
        }
        if (MessageObject.isAnimatedStickerDocument(document, true) && photoWidth == 0 && photoHeight == 0) {
            photoHeight = 512;
            photoWidth = 512;
        }
        if (photoWidth == 0) {
            photoHeight = (int) maxHeight;
            photoWidth = photoHeight + AndroidUtilities.dp(100.0f);
        }
        int photoHeight2 = (int) (photoHeight * (maxWidth / photoWidth));
        int photoWidth2 = (int) maxWidth;
        if (photoHeight2 > maxHeight) {
            photoWidth2 = (int) (photoWidth2 * (maxHeight / photoHeight2));
            photoHeight2 = (int) maxHeight;
        }
        int w = (int) (photoWidth2 / AndroidUtilities.density);
        int h = (int) (photoHeight2 / AndroidUtilities.density);
        return String.format(Locale.US, "%d_%d", Integer.valueOf(w), Integer.valueOf(h));
    }

    private void updateColors() {
        this.titleView.setTextColor(getThemedColor(Theme.key_chat_serviceText));
        this.descriptionView.setTextColor(getThemedColor(Theme.key_chat_serviceText));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.ignoreLayot = true;
        this.descriptionView.setVisibility(0);
        this.stickerToSendView.setVisibility(0);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() > View.MeasureSpec.getSize(heightMeasureSpec)) {
            this.descriptionView.setVisibility(8);
            this.stickerToSendView.setVisibility(8);
        } else {
            this.descriptionView.setVisibility(0);
            this.stickerToSendView.setVisibility(0);
        }
        this.ignoreLayot = false;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (!this.wasDraw) {
            this.wasDraw = true;
            setSticker(this.preloadedGreetingsSticker);
        }
        super.dispatchDraw(canvas);
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayot) {
            return;
        }
        super.requestLayout();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        fetchSticker();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void fetchSticker() {
        if (this.preloadedGreetingsSticker == null) {
            TLRPC.Document greetingsSticker = MediaDataController.getInstance(this.currentAccount).getGreetingsSticker();
            this.preloadedGreetingsSticker = greetingsSticker;
            if (this.wasDraw) {
                setSticker(greetingsSticker);
            }
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
