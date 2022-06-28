package com.google.android.gms.vision.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.vision.zzs;
import com.google.android.gms.internal.vision.zzw;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.internal.client.zzb;
import com.google.android.gms.vision.face.internal.client.zzf;
import com.google.android.gms.vision.zzc;
import java.nio.ByteBuffer;
import java.util.HashSet;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes3.dex */
public final class FaceDetector extends Detector<Face> {
    public static final int ACCURATE_MODE = 1;
    public static final int ALL_CLASSIFICATIONS = 1;
    public static final int ALL_LANDMARKS = 1;
    public static final int CONTOUR_LANDMARKS = 2;
    public static final int FAST_MODE = 0;
    public static final int NO_CLASSIFICATIONS = 0;
    public static final int NO_LANDMARKS = 0;
    public static final int SELFIE_MODE = 2;
    private final zzc zza;
    private final zzb zzb;
    private final Object zzc;
    private boolean zzd;

    @Override // com.google.android.gms.vision.Detector
    public final void release() {
        super.release();
        synchronized (this.zzc) {
            if (!this.zzd) {
                return;
            }
            this.zzb.zzc();
            this.zzd = false;
        }
    }

    protected final void finalize() throws Throwable {
        try {
            synchronized (this.zzc) {
                if (this.zzd) {
                    Log.w("FaceDetector", "FaceDetector was not released with FaceDetector.release()");
                    release();
                }
            }
        } finally {
            super.finalize();
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private final Context zza;
        private int zzb = 0;
        private boolean zzc = false;
        private int zzd = 0;
        private boolean zze = true;
        private int zzf = 0;
        private float zzg = -1.0f;

        public Builder(Context context) {
            this.zza = context;
        }

        public Builder setLandmarkType(int i) {
            if (i != 0 && i != 1 && i != 2) {
                StringBuilder sb = new StringBuilder(34);
                sb.append("Invalid landmark type: ");
                sb.append(i);
                throw new IllegalArgumentException(sb.toString());
            }
            this.zzb = i;
            return this;
        }

        public Builder setProminentFaceOnly(boolean z) {
            this.zzc = z;
            return this;
        }

        public Builder setClassificationType(int i) {
            if (i != 0 && i != 1) {
                StringBuilder sb = new StringBuilder(40);
                sb.append("Invalid classification type: ");
                sb.append(i);
                throw new IllegalArgumentException(sb.toString());
            }
            this.zzd = i;
            return this;
        }

        public Builder setTrackingEnabled(boolean z) {
            this.zze = z;
            return this;
        }

        public Builder setMode(int i) {
            switch (i) {
                case 0:
                case 1:
                case 2:
                    this.zzf = i;
                    return this;
                default:
                    StringBuilder sb = new StringBuilder(25);
                    sb.append("Invalid mode: ");
                    sb.append(i);
                    throw new IllegalArgumentException(sb.toString());
            }
        }

        public Builder setMinFaceSize(float f) {
            if (f < 0.0f || f > 1.0f) {
                StringBuilder sb = new StringBuilder(47);
                sb.append("Invalid proportional face size: ");
                sb.append(f);
                throw new IllegalArgumentException(sb.toString());
            }
            this.zzg = f;
            return this;
        }

        public FaceDetector build() {
            zzf zzfVar = new zzf();
            zzfVar.zza = this.zzf;
            zzfVar.zzb = this.zzb;
            zzfVar.zzc = this.zzd;
            zzfVar.zzd = this.zzc;
            zzfVar.zze = this.zze;
            zzfVar.zzf = this.zzg;
            if (!FaceDetector.zzb(zzfVar)) {
                throw new IllegalArgumentException("Invalid build options");
            }
            return new FaceDetector(new zzb(this.zza, zzfVar));
        }
    }

    @Override // com.google.android.gms.vision.Detector
    public final SparseArray<Face> detect(Frame frame) {
        Face[] faceArr;
        ByteBuffer byteBuffer;
        if (frame == null) {
            throw new IllegalArgumentException("No frame supplied.");
        }
        if (Build.VERSION.SDK_INT >= 19 && frame.getPlanes() != null && ((Image.Plane[]) Preconditions.checkNotNull(frame.getPlanes())).length == 3) {
            synchronized (this.zzc) {
                if (!this.zzd) {
                    throw new IllegalStateException("Cannot use detector after release()");
                }
                faceArr = this.zzb.zza((Image.Plane[]) Preconditions.checkNotNull(frame.getPlanes()), zzs.zza(frame));
            }
        } else {
            if (frame.getBitmap() != null) {
                byteBuffer = zzw.zza((Bitmap) Preconditions.checkNotNull(frame.getBitmap()), true);
            } else {
                byteBuffer = frame.getGrayscaleImageData();
            }
            synchronized (this.zzc) {
                if (!this.zzd) {
                    throw new IllegalStateException("Cannot use detector after release()");
                }
                faceArr = this.zzb.zza((ByteBuffer) Preconditions.checkNotNull(byteBuffer), zzs.zza(frame));
            }
        }
        HashSet hashSet = new HashSet();
        SparseArray<Face> sparseArray = new SparseArray<>(faceArr.length);
        int i = 0;
        for (Face face : faceArr) {
            int id = face.getId();
            i = Math.max(i, id);
            if (hashSet.contains(Integer.valueOf(id))) {
                id = i + 1;
                i = id;
            }
            hashSet.add(Integer.valueOf(id));
            sparseArray.append(this.zza.zza(id), face);
        }
        return sparseArray;
    }

    @Override // com.google.android.gms.vision.Detector
    public final boolean setFocus(int i) {
        boolean zza;
        int zzb = this.zza.zzb(i);
        synchronized (this.zzc) {
            if (!this.zzd) {
                throw new RuntimeException("Cannot use detector after release()");
            }
            zza = this.zzb.zza(zzb);
        }
        return zza;
    }

    @Override // com.google.android.gms.vision.Detector
    public final boolean isOperational() {
        return this.zzb.zzb();
    }

    private FaceDetector() {
        this.zza = new zzc();
        this.zzc = new Object();
        this.zzd = true;
        throw new IllegalStateException("Default constructor called");
    }

    private FaceDetector(zzb zzbVar) {
        this.zza = new zzc();
        this.zzc = new Object();
        this.zzd = true;
        this.zzb = zzbVar;
    }

    public static boolean zzb(zzf zzfVar) {
        boolean z;
        if (zzfVar.zza != 2 && zzfVar.zzb == 2) {
            Log.e("FaceDetector", "Contour is not supported for non-SELFIE mode.");
            z = false;
        } else {
            z = true;
        }
        if (zzfVar.zzb == 2 && zzfVar.zzc == 1) {
            Log.e("FaceDetector", "Classification is not supported with contour.");
            return false;
        }
        return z;
    }
}
