package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Frame;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public abstract class Detector<T> {
    private final Object zza = new Object();
    @Nullable
    private Processor<T> zzb;

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public interface Processor<T> {
        void receiveDetections(Detections<T> detections);

        void release();
    }

    public abstract SparseArray<T> detect(Frame frame);

    public void release() {
        synchronized (this.zza) {
            Processor<T> processor = this.zzb;
            if (processor != null) {
                processor.release();
                this.zzb = null;
            }
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static class Detections<T> {
        private final SparseArray<T> zza;
        private final Frame.Metadata zzb;
        private final boolean zzc;

        public Detections(SparseArray<T> sparseArray, Frame.Metadata metadata, boolean z) {
            this.zza = sparseArray;
            this.zzb = metadata;
            this.zzc = z;
        }

        public SparseArray<T> getDetectedItems() {
            return this.zza;
        }

        public Frame.Metadata getFrameMetadata() {
            return this.zzb;
        }

        public boolean detectorIsOperational() {
            return this.zzc;
        }
    }

    public boolean setFocus(int i) {
        return true;
    }

    public void receiveFrame(Frame frame) {
        Frame.Metadata metadata = new Frame.Metadata(frame.getMetadata());
        metadata.zza();
        Detections<T> detections = new Detections<>(detect(frame), metadata, isOperational());
        synchronized (this.zza) {
            Processor<T> processor = this.zzb;
            if (processor == null) {
                throw new IllegalStateException("Detector processor must first be set with setProcessor in order to receive detection results.");
            }
            processor.receiveDetections(detections);
        }
    }

    public void setProcessor(Processor<T> processor) {
        synchronized (this.zza) {
            Processor<T> processor2 = this.zzb;
            if (processor2 != null) {
                processor2.release();
            }
            this.zzb = processor;
        }
    }

    public boolean isOperational() {
        return true;
    }
}
