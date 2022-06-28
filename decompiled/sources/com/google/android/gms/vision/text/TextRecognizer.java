package com.google.android.gms.vision.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.vision.zzah;
import com.google.android.gms.internal.vision.zzaj;
import com.google.android.gms.internal.vision.zzam;
import com.google.android.gms.internal.vision.zzan;
import com.google.android.gms.internal.vision.zzs;
import com.google.android.gms.internal.vision.zzw;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes3.dex */
public final class TextRecognizer extends Detector<TextBlock> {
    private final zzan zza;

    private TextRecognizer() {
        throw new IllegalStateException("Default constructor called");
    }

    private TextRecognizer(zzan zzanVar) {
        this.zza = zzanVar;
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private Context zza;
        private zzam zzb = new zzam();

        public Builder(Context context) {
            this.zza = context;
        }

        public TextRecognizer build() {
            return new TextRecognizer(new zzan(this.zza, this.zzb));
        }
    }

    @Override // com.google.android.gms.vision.Detector
    public final SparseArray<TextBlock> detect(Frame frame) {
        Bitmap bitmap;
        byte[] bArr;
        zzaj zzajVar = new zzaj(new Rect());
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        zzs zza = zzs.zza(frame);
        if (frame.getBitmap() != null) {
            bitmap = frame.getBitmap();
        } else {
            Frame.Metadata metadata = frame.getMetadata();
            ByteBuffer byteBuffer = (ByteBuffer) Preconditions.checkNotNull(frame.getGrayscaleImageData());
            int format = metadata.getFormat();
            int i = zza.zza;
            int i2 = zza.zzb;
            if (byteBuffer.hasArray() && byteBuffer.arrayOffset() == 0) {
                bArr = byteBuffer.array();
            } else {
                byte[] bArr2 = new byte[byteBuffer.capacity()];
                byteBuffer.get(bArr2);
                bArr = bArr2;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new YuvImage(bArr, format, i, i2, null).compressToJpeg(new Rect(0, 0, i, i2), 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        Bitmap zza2 = zzw.zza((Bitmap) Preconditions.checkNotNull(bitmap), zza);
        if (!zzajVar.zza.isEmpty()) {
            Rect rect = zzajVar.zza;
            int width = frame.getMetadata().getWidth();
            int height = frame.getMetadata().getHeight();
            switch (zza.zze) {
                case 1:
                    rect = new Rect(height - rect.bottom, rect.left, height - rect.top, rect.right);
                    break;
                case 2:
                    rect = new Rect(width - rect.right, height - rect.bottom, width - rect.left, height - rect.top);
                    break;
                case 3:
                    rect = new Rect(rect.top, width - rect.right, rect.bottom, width - rect.left);
                    break;
            }
            zzajVar.zza.set(rect);
        }
        zza.zze = 0;
        zzah[] zza3 = this.zza.zza(zza2, zza, zzajVar);
        SparseArray sparseArray = new SparseArray();
        for (zzah zzahVar : zza3) {
            SparseArray sparseArray2 = (SparseArray) sparseArray.get(zzahVar.zzf);
            if (sparseArray2 == null) {
                sparseArray2 = new SparseArray();
                sparseArray.append(zzahVar.zzf, sparseArray2);
            }
            sparseArray2.append(zzahVar.zzg, zzahVar);
        }
        SparseArray<TextBlock> sparseArray3 = new SparseArray<>(sparseArray.size());
        for (int i3 = 0; i3 < sparseArray.size(); i3++) {
            sparseArray3.append(sparseArray.keyAt(i3), new TextBlock((SparseArray) sparseArray.valueAt(i3)));
        }
        return sparseArray3;
    }

    @Override // com.google.android.gms.vision.Detector
    public final boolean isOperational() {
        return this.zza.zzb();
    }

    @Override // com.google.android.gms.vision.Detector
    public final void release() {
        super.release();
        this.zza.zzc();
    }
}
