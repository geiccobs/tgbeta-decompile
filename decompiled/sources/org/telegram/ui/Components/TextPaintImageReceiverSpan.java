package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;
import android.view.View;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes5.dex */
public class TextPaintImageReceiverSpan extends ReplacementSpan {
    private boolean alignTop;
    private int height;
    private ImageReceiver imageReceiver;
    private int width;

    public TextPaintImageReceiverSpan(View parentView, TLRPC.Document document, Object parentObject, int w, int h, boolean top, boolean invert) {
        String filter = String.format(Locale.US, "%d_%d_i", Integer.valueOf(w), Integer.valueOf(h));
        this.width = w;
        this.height = h;
        ImageReceiver imageReceiver = new ImageReceiver(parentView);
        this.imageReceiver = imageReceiver;
        imageReceiver.setInvalidateAll(true);
        if (invert) {
            this.imageReceiver.setDelegate(TextPaintImageReceiverSpan$$ExternalSyntheticLambda0.INSTANCE);
        }
        TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
        this.imageReceiver.setImage(ImageLocation.getForDocument(document), filter, ImageLocation.getForDocument(thumb, document), filter, -1L, null, parentObject, 1);
        this.alignTop = top;
    }

    public static /* synthetic */ void lambda$new$0(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
        if (!imageReceiver.canInvertBitmap()) {
            return;
        }
        float[] NEGATIVE = {-1.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, -1.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, -1.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
        imageReceiver.setColorFilter(new ColorMatrixColorFilter(NEGATIVE));
    }

    @Override // android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (fm != null) {
            if (this.alignTop) {
                int h = (fm.descent - fm.ascent) - AndroidUtilities.dp(4.0f);
                int i = this.height - h;
                fm.descent = i;
                fm.bottom = i;
                int i2 = 0 - h;
                fm.ascent = i2;
                fm.top = i2;
            } else {
                int dp = ((-this.height) / 2) - AndroidUtilities.dp(4.0f);
                fm.ascent = dp;
                fm.top = dp;
                int i3 = this.height;
                int dp2 = (i3 - (i3 / 2)) - AndroidUtilities.dp(4.0f);
                fm.descent = dp2;
                fm.bottom = dp2;
            }
        }
        return this.width;
    }

    @Override // android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        canvas.save();
        if (this.alignTop) {
            this.imageReceiver.setImageCoords((int) x, top - 1, this.width, this.height);
        } else {
            int h = (bottom - AndroidUtilities.dp(4.0f)) - top;
            int i = this.height;
            this.imageReceiver.setImageCoords((int) x, ((h - i) / 2) + top, this.width, i);
        }
        this.imageReceiver.draw(canvas);
        canvas.restore();
    }
}
