package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes5.dex */
public class IdenticonDrawable extends Drawable {
    private byte[] data;
    private Paint paint = new Paint();
    private int[] colors = {-1, -2758925, -13805707, -13657655};

    private int getBits(int bitOffset) {
        return (this.data[bitOffset / 8] >> (bitOffset % 8)) & 3;
    }

    public void setEncryptedChat(TLRPC.EncryptedChat encryptedChat) {
        byte[] bArr = encryptedChat.key_hash;
        this.data = bArr;
        if (bArr == null) {
            byte[] calcAuthKeyHash = AndroidUtilities.calcAuthKeyHash(encryptedChat.auth_key);
            this.data = calcAuthKeyHash;
            encryptedChat.key_hash = calcAuthKeyHash;
        }
        invalidateSelf();
    }

    public void setColors(int[] value) {
        if (this.colors.length != 4) {
            throw new IllegalArgumentException("colors must have length of 4");
        }
        this.colors = value;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        byte[] bArr = this.data;
        if (bArr == null) {
            return;
        }
        if (bArr.length == 16) {
            int bitPointer = 0;
            float rectSize = (float) Math.floor(Math.min(getBounds().width(), getBounds().height()) / 8.0f);
            float xOffset = Math.max(0.0f, (getBounds().width() - (rectSize * 8.0f)) / 2.0f);
            float yOffset = Math.max(0.0f, (getBounds().height() - (8.0f * rectSize)) / 2.0f);
            for (int iy = 0; iy < 8; iy++) {
                for (int ix = 0; ix < 8; ix++) {
                    int byteValue = getBits(bitPointer);
                    bitPointer += 2;
                    int colorIndex = Math.abs(byteValue) % 4;
                    this.paint.setColor(this.colors[colorIndex]);
                    canvas.drawRect(xOffset + (ix * rectSize), (iy * rectSize) + yOffset, (ix * rectSize) + xOffset + rectSize, (iy * rectSize) + rectSize + yOffset, this.paint);
                }
            }
            return;
        }
        int bitPointer2 = 0;
        float rectSize2 = (float) Math.floor(Math.min(getBounds().width(), getBounds().height()) / 12.0f);
        float xOffset2 = Math.max(0.0f, (getBounds().width() - (rectSize2 * 12.0f)) / 2.0f);
        float yOffset2 = Math.max(0.0f, (getBounds().height() - (12.0f * rectSize2)) / 2.0f);
        for (int iy2 = 0; iy2 < 12; iy2++) {
            for (int ix2 = 0; ix2 < 12; ix2++) {
                int byteValue2 = getBits(bitPointer2);
                int colorIndex2 = Math.abs(byteValue2) % 4;
                this.paint.setColor(this.colors[colorIndex2]);
                canvas.drawRect(xOffset2 + (ix2 * rectSize2), (iy2 * rectSize2) + yOffset2, (ix2 * rectSize2) + xOffset2 + rectSize2, (iy2 * rectSize2) + rectSize2 + yOffset2, this.paint);
                bitPointer2 += 2;
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(32.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(32.0f);
    }
}
