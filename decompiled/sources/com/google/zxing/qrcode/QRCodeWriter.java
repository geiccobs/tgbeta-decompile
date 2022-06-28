package com.google.zxing.qrcode;

import android.graphics.Bitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import java.util.Map;
/* loaded from: classes3.dex */
public final class QRCodeWriter {
    private static final int QUIET_ZONE_SIZE = 4;
    private int imageBlockX;
    private int imageBloks;
    private int imageSize;
    private ByteMatrix input;
    private float[] radii = new float[8];
    private int sideQuadSize;

    public Bitmap encode(String contents, int width, int height, Map<EncodeHintType, ?> hints, Bitmap bitmap) throws WriterException {
        return encode(contents, width, height, hints, bitmap, 1.0f, -1, -16777216);
    }

    /* JADX WARN: Removed duplicated region for block: B:128:0x0406 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00e3  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00fe  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0100  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0116  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x027c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.graphics.Bitmap encode(java.lang.String r42, int r43, int r44, java.util.Map<com.google.zxing.EncodeHintType, ?> r45, android.graphics.Bitmap r46, float r47, int r48, int r49) throws com.google.zxing.WriterException {
        /*
            Method dump skipped, instructions count: 1116
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.qrcode.QRCodeWriter.encode(java.lang.String, int, int, java.util.Map, android.graphics.Bitmap, float, int, int):android.graphics.Bitmap");
    }

    private boolean has(int x, int y) {
        int i = this.imageBlockX;
        if (x >= i) {
            int i2 = this.imageBloks;
            if (x < i + i2 && y >= i && y < i + i2) {
                return false;
            }
        }
        if ((x < this.sideQuadSize || x >= this.input.getWidth() - this.sideQuadSize) && y < this.sideQuadSize) {
            return false;
        }
        return (x >= this.sideQuadSize || y < this.input.getHeight() - this.sideQuadSize) && x >= 0 && y >= 0 && x < this.input.getWidth() && y < this.input.getHeight() && this.input.get(x, y) == 1;
    }

    public int getImageSize() {
        return this.imageSize;
    }
}
