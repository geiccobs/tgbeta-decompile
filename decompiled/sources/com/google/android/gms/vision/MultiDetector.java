package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.vision.Detector;
import java.util.ArrayList;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public class MultiDetector extends Detector<Object> {
    private List<Detector<? extends Object>> zza;

    @Override // com.google.android.gms.vision.Detector
    public void release() {
        for (Detector<? extends Object> detector : this.zza) {
            detector.release();
        }
        this.zza.clear();
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private MultiDetector zza = new MultiDetector();

        public Builder add(Detector<? extends Object> detector) {
            this.zza.zza.add(detector);
            return this;
        }

        public MultiDetector build() {
            if (this.zza.zza.size() == 0) {
                throw new RuntimeException("No underlying detectors added to MultiDetector.");
            }
            return this.zza;
        }
    }

    @Override // com.google.android.gms.vision.Detector
    public SparseArray<Object> detect(Frame frame) {
        SparseArray<Object> sparseArray = new SparseArray<>();
        for (Detector<? extends Object> detector : this.zza) {
            SparseArray<? extends Object> detect = detector.detect(frame);
            for (int i = 0; i < detect.size(); i++) {
                int keyAt = detect.keyAt(i);
                if (sparseArray.get(keyAt) != null) {
                    StringBuilder sb = new StringBuilder((int) LocationRequest.PRIORITY_LOW_POWER);
                    sb.append("Detection ID overlap for id = ");
                    sb.append(keyAt);
                    sb.append("  This means that one of the detectors is not using global IDs.");
                    throw new IllegalStateException(sb.toString());
                }
                sparseArray.append(keyAt, detect.valueAt(i));
            }
        }
        return sparseArray;
    }

    @Override // com.google.android.gms.vision.Detector
    public void receiveFrame(Frame frame) {
        for (Detector<? extends Object> detector : this.zza) {
            detector.receiveFrame(frame);
        }
    }

    @Override // com.google.android.gms.vision.Detector
    public void setProcessor(Detector.Processor<Object> processor) {
        throw new UnsupportedOperationException("MultiDetector.setProcessor is not supported.  You should set a processor instance on each underlying detector instead.");
    }

    @Override // com.google.android.gms.vision.Detector
    public boolean isOperational() {
        for (Detector<? extends Object> detector : this.zza) {
            if (!detector.isOperational()) {
                return false;
            }
        }
        return true;
    }

    private MultiDetector() {
        this.zza = new ArrayList();
    }
}
