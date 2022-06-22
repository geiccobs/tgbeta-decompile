package com.google.android.gms.vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import androidx.annotation.RecentlyNonNull;
import androidx.annotation.RecentlyNullable;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public class Frame {
    private final Metadata zza;
    @Nullable
    private ByteBuffer zzb;
    @Nullable
    private zza zzc;
    @Nullable
    private Bitmap zzd;

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public static class zza {
    }

    @RecentlyNonNull
    public Metadata getMetadata() {
        return this.zza;
    }

    @RecentlyNullable
    public Image.Plane[] getPlanes() {
        return null;
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public static class Builder {
        private final Frame zza = new Frame();

        @RecentlyNonNull
        public Builder setBitmap(@RecentlyNonNull Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            this.zza.zzd = bitmap;
            Metadata metadata = this.zza.getMetadata();
            metadata.zza = width;
            metadata.zzb = height;
            return this;
        }

        @RecentlyNonNull
        public Builder setImageData(@RecentlyNonNull ByteBuffer byteBuffer, int i, int i2, int i3) {
            if (byteBuffer == null) {
                throw new IllegalArgumentException("Null image data supplied.");
            }
            if (byteBuffer.capacity() < i * i2) {
                throw new IllegalArgumentException("Invalid image data size.");
            }
            if (i3 != 16 && i3 != 17 && i3 != 842094169) {
                StringBuilder sb = new StringBuilder(37);
                sb.append("Unsupported image format: ");
                sb.append(i3);
                throw new IllegalArgumentException(sb.toString());
            }
            this.zza.zzb = byteBuffer;
            Metadata metadata = this.zza.getMetadata();
            metadata.zza = i;
            metadata.zzb = i2;
            metadata.zzf = i3;
            return this;
        }

        @RecentlyNonNull
        public Builder setRotation(int i) {
            this.zza.getMetadata().zze = i;
            return this;
        }

        @RecentlyNonNull
        public Frame build() {
            if (this.zza.zzb == null && this.zza.zzd == null) {
                zza unused = this.zza.zzc;
                throw new IllegalStateException("Missing image data.  Call either setBitmap or setImageData to specify the image");
            }
            return this.zza;
        }
    }

    @RecentlyNullable
    public ByteBuffer getGrayscaleImageData() {
        Bitmap bitmap = this.zzd;
        if (bitmap != null) {
            if (bitmap == null) {
                return null;
            }
            int width = bitmap.getWidth();
            int height = this.zzd.getHeight();
            int i = width * height;
            int[] iArr = new int[i];
            this.zzd.getPixels(iArr, 0, width, 0, 0, width, height);
            byte[] bArr = new byte[i];
            for (int i2 = 0; i2 < i; i2++) {
                bArr[i2] = (byte) ((Color.red(iArr[i2]) * 0.299f) + (Color.green(iArr[i2]) * 0.587f) + (Color.blue(iArr[i2]) * 0.114f));
            }
            return ByteBuffer.wrap(bArr);
        }
        return this.zzb;
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes.dex */
    public static class Metadata {
        private int zza;
        private int zzb;
        private int zzc;
        private long zzd;
        private int zze;
        private int zzf;

        public int getWidth() {
            return this.zza;
        }

        public int getHeight() {
            return this.zzb;
        }

        public int getId() {
            return this.zzc;
        }

        public long getTimestampMillis() {
            return this.zzd;
        }

        public int getRotation() {
            return this.zze;
        }
    }

    @RecentlyNullable
    public Bitmap getBitmap() {
        return this.zzd;
    }

    private Frame() {
        this.zza = new Metadata();
        this.zzb = null;
        this.zzd = null;
    }
}
