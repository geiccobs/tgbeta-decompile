package org.telegram.ui.Components;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class QRCodeBottomSheet extends BottomSheet {
    private final TextView buttonTextView;
    private final TextView help;
    RLottieImageView iconImage;
    int imageSize;
    Bitmap qrCode;

    public QRCodeBottomSheet(final Context context, String link, String helpMessage) {
        super(context, false);
        fixNavigationBar();
        setTitle(LocaleController.getString("InviteByQRCode", R.string.InviteByQRCode), true);
        final ImageView imageView = new ImageView(context) { // from class: org.telegram.ui.Components.QRCodeBottomSheet.1
            @Override // android.widget.ImageView, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int size = View.MeasureSpec.getSize(widthMeasureSpec);
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(size, C.BUFFER_FLAG_ENCRYPTED));
            }
        };
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (Build.VERSION.SDK_INT >= 21) {
            imageView.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.QRCodeBottomSheet.2
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), AndroidUtilities.dp(12.0f));
                }
            });
            imageView.setClipToOutline(true);
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(16.0f), 0, 0);
        Bitmap createQR = createQR(context, link, this.qrCode);
        this.qrCode = createQR;
        imageView.setImageBitmap(createQR);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.iconImage = rLottieImageView;
        rLottieImageView.setBackgroundColor(-1);
        this.iconImage.setAutoRepeat(true);
        this.iconImage.setAnimation(R.raw.qr_code_logo, 60, 60);
        this.iconImage.playAnimation();
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.QRCodeBottomSheet.3
            float lastX;

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                float x = (QRCodeBottomSheet.this.imageSize / 768.0f) * imageView.getMeasuredHeight();
                if (this.lastX != x) {
                    this.lastX = x;
                    ViewGroup.LayoutParams layoutParams = QRCodeBottomSheet.this.iconImage.getLayoutParams();
                    int i = (int) x;
                    QRCodeBottomSheet.this.iconImage.getLayoutParams().width = i;
                    layoutParams.height = i;
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }
        };
        frameLayout.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout.addView(this.iconImage, LayoutHelper.createFrame(60, 60, 17));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(220, 220, 1, 30, 0, 30, 0));
        TextView textView = new TextView(context);
        this.help = textView;
        textView.setTextSize(1, 14.0f);
        textView.setText(helpMessage);
        textView.setGravity(1);
        linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 0, 40.0f, 8.0f, 40.0f, 8.0f));
        TextView textView2 = new TextView(context);
        this.buttonTextView = textView2;
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView2.setGravity(17);
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView2.setText(LocaleController.getString("ShareQrCode", R.string.ShareQrCode));
        textView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.QRCodeBottomSheet$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QRCodeBottomSheet.this.m2924lambda$new$0$orgtelegramuiComponentsQRCodeBottomSheet(context, view);
            }
        });
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, 48, 80, 16, 15, 16, 16));
        updateColors();
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-QRCodeBottomSheet */
    public /* synthetic */ void m2924lambda$new$0$orgtelegramuiComponentsQRCodeBottomSheet(Context context, View view) {
        Uri uri = AndroidUtilities.getBitmapShareUri(this.qrCode, "qr_tmp.png", Bitmap.CompressFormat.PNG);
        if (uri != null) {
            Intent i = new Intent("android.intent.action.SEND");
            i.setType("image/*");
            i.putExtra("android.intent.extra.STREAM", uri);
            try {
                AndroidUtilities.findActivity(context).startActivityForResult(Intent.createChooser(i, LocaleController.getString("InviteByQRCode", R.string.InviteByQRCode)), 500);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Bitmap createQR(Context context, String key, Bitmap oldBitmap) {
        try {
            HashMap<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 0);
            QRCodeWriter writer = new QRCodeWriter();
            Bitmap bitmap = writer.encode(key, 768, 768, hints, oldBitmap);
            this.imageSize = writer.getImageSize();
            return bitmap;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public void updateColors() {
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        this.help.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        if (getTitleView() != null) {
            getTitleView().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        }
        setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
    }
}
