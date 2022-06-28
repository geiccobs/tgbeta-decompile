package com.google.android.gms.vision;

import android.util.SparseArray;
import com.google.android.gms.vision.Detector;
import java.util.HashSet;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public class MultiProcessor<T> implements Detector.Processor<T> {
    private Factory<T> zza;
    private SparseArray<zza> zzb;
    private int zzc;

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public interface Factory<T> {
        Tracker<T> create(T t);
    }

    @Override // com.google.android.gms.vision.Detector.Processor
    public void release() {
        for (int i = 0; i < this.zzb.size(); i++) {
            this.zzb.valueAt(i).zza.onDone();
        }
        this.zzb.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public class zza {
        private Tracker<T> zza;
        private int zzb;

        private zza(MultiProcessor multiProcessor) {
            this.zzb = 0;
        }

        static /* synthetic */ int zzb(zza zzaVar) {
            int i = zzaVar.zzb;
            zzaVar.zzb = i + 1;
            return i;
        }
    }

    /* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
    /* loaded from: classes3.dex */
    public static class Builder<T> {
        private MultiProcessor<T> zza;

        public Builder(Factory<T> factory) {
            MultiProcessor<T> multiProcessor = new MultiProcessor<>();
            this.zza = multiProcessor;
            if (factory == null) {
                throw new IllegalArgumentException("No factory supplied.");
            }
            ((MultiProcessor) multiProcessor).zza = factory;
        }

        public Builder<T> setMaxGapFrames(int i) {
            if (i < 0) {
                StringBuilder sb = new StringBuilder(28);
                sb.append("Invalid max gap: ");
                sb.append(i);
                throw new IllegalArgumentException(sb.toString());
            }
            ((MultiProcessor) this.zza).zzc = i;
            return this;
        }

        public MultiProcessor<T> build() {
            return this.zza;
        }
    }

    @Override // com.google.android.gms.vision.Detector.Processor
    public void receiveDetections(Detector.Detections<T> detections) {
        SparseArray<T> detectedItems = detections.getDetectedItems();
        for (int i = 0; i < detectedItems.size(); i++) {
            int keyAt = detectedItems.keyAt(i);
            T valueAt = detectedItems.valueAt(i);
            if (this.zzb.get(keyAt) == null) {
                zza zzaVar = new zza();
                zzaVar.zza = this.zza.create(valueAt);
                zzaVar.zza.onNewItem(keyAt, valueAt);
                this.zzb.append(keyAt, zzaVar);
            }
        }
        SparseArray<T> detectedItems2 = detections.getDetectedItems();
        HashSet<Integer> hashSet = new HashSet();
        for (int i2 = 0; i2 < this.zzb.size(); i2++) {
            int keyAt2 = this.zzb.keyAt(i2);
            if (detectedItems2.get(keyAt2) == null) {
                zza valueAt2 = this.zzb.valueAt(i2);
                zza.zzb(valueAt2);
                if (valueAt2.zzb >= this.zzc) {
                    valueAt2.zza.onDone();
                    hashSet.add(Integer.valueOf(keyAt2));
                } else {
                    valueAt2.zza.onMissing(detections);
                }
            }
        }
        for (Integer num : hashSet) {
            this.zzb.delete(num.intValue());
        }
        SparseArray<T> detectedItems3 = detections.getDetectedItems();
        for (int i3 = 0; i3 < detectedItems3.size(); i3++) {
            int keyAt3 = detectedItems3.keyAt(i3);
            T valueAt3 = detectedItems3.valueAt(i3);
            zza zzaVar2 = this.zzb.get(keyAt3);
            zzaVar2.zzb = 0;
            zzaVar2.zza.onUpdate(detections, valueAt3);
        }
    }

    private MultiProcessor() {
        this.zzb = new SparseArray<>();
        this.zzc = 3;
    }
}
