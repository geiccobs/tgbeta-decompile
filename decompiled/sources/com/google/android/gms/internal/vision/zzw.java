package com.google.android.gms.internal.vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import java.nio.ByteBuffer;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzw {
    public static Bitmap zza(Bitmap bitmap, zzs zzsVar) {
        int i;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (zzsVar.zze != 0) {
            Matrix matrix = new Matrix();
            switch (zzsVar.zze) {
                case 0:
                    i = 0;
                    break;
                case 1:
                    i = 90;
                    break;
                case 2:
                    i = 180;
                    break;
                case 3:
                    i = 270;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported rotation degree.");
            }
            matrix.postRotate(i);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        }
        if (zzsVar.zze == 1 || zzsVar.zze == 3) {
            zzsVar.zza = height;
            zzsVar.zzb = width;
        }
        return bitmap;
    }

    public static ByteBuffer zza(Bitmap bitmap, boolean z) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int i = width * height;
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(((((width + 1) / 2) * ((height + 1) / 2)) << 1) + i);
        int i2 = i;
        for (int i3 = 0; i3 < i; i3++) {
            int i4 = i3 % width;
            int i5 = i3 / width;
            int pixel = bitmap.getPixel(i4, i5);
            float red = Color.red(pixel);
            float green = Color.green(pixel);
            float blue = Color.blue(pixel);
            allocateDirect.put(i3, (byte) ((0.299f * red) + (0.587f * green) + (0.114f * blue)));
            if (i5 % 2 == 0 && i4 % 2 == 0) {
                int i6 = i2 + 1;
                allocateDirect.put(i2, (byte) (((-0.169f) * red) + ((-0.331f) * green) + (blue * 0.5f) + 128.0f));
                i2 = i6 + 1;
                allocateDirect.put(i6, (byte) ((red * 0.5f) + (green * (-0.419f)) + (blue * (-0.081f)) + 128.0f));
            }
        }
        return allocateDirect;
    }
}
