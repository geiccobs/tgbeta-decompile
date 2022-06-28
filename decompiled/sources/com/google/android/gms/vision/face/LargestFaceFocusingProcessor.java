package com.google.android.gms.vision.face;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.FocusingProcessor;
import com.google.android.gms.vision.Tracker;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes3.dex */
public class LargestFaceFocusingProcessor extends FocusingProcessor<Face> {
    public LargestFaceFocusingProcessor(Detector<Face> detector, Tracker<Face> tracker) {
        super(detector, tracker);
    }

    /* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
    /* loaded from: classes3.dex */
    public static class Builder {
        private LargestFaceFocusingProcessor zza;

        public Builder(Detector<Face> detector, Tracker<Face> tracker) {
            this.zza = new LargestFaceFocusingProcessor(detector, tracker);
        }

        public Builder setMaxGapFrames(int i) {
            this.zza.zza(i);
            return this;
        }

        public LargestFaceFocusingProcessor build() {
            return this.zza;
        }
    }

    @Override // com.google.android.gms.vision.FocusingProcessor
    public int selectFocus(Detector.Detections<Face> detections) {
        SparseArray<Face> detectedItems = detections.getDetectedItems();
        if (detectedItems.size() == 0) {
            throw new IllegalArgumentException("No faces for selectFocus.");
        }
        int keyAt = detectedItems.keyAt(0);
        float width = detectedItems.valueAt(0).getWidth();
        for (int i = 1; i < detectedItems.size(); i++) {
            int keyAt2 = detectedItems.keyAt(i);
            float width2 = detectedItems.valueAt(i).getWidth();
            if (width2 > width) {
                keyAt = keyAt2;
                width = width2;
            }
        }
        return keyAt;
    }
}
