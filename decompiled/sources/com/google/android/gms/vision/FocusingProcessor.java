package com.google.android.gms.vision;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public abstract class FocusingProcessor<T> implements Detector.Processor<T> {
    private Detector<T> zza;
    private Tracker<T> zzb;
    private int zze;
    private int zzc = 3;
    private boolean zzd = false;
    private int zzf = 0;

    public FocusingProcessor(Detector<T> detector, Tracker<T> tracker) {
        this.zza = detector;
        this.zzb = tracker;
    }

    public abstract int selectFocus(Detector.Detections<T> detections);

    @Override // com.google.android.gms.vision.Detector.Processor
    public void release() {
        this.zzb.onDone();
    }

    @Override // com.google.android.gms.vision.Detector.Processor
    public void receiveDetections(Detector.Detections<T> detections) {
        SparseArray<T> detectedItems = detections.getDetectedItems();
        if (detectedItems.size() == 0) {
            if (this.zzf == this.zzc) {
                this.zzb.onDone();
                this.zzd = false;
            } else {
                this.zzb.onMissing(detections);
            }
            this.zzf++;
            return;
        }
        this.zzf = 0;
        if (this.zzd) {
            T t = detectedItems.get(this.zze);
            if (t != null) {
                this.zzb.onUpdate(detections, t);
                return;
            } else {
                this.zzb.onDone();
                this.zzd = false;
            }
        }
        int selectFocus = selectFocus(detections);
        T t2 = detectedItems.get(selectFocus);
        if (t2 == null) {
            StringBuilder sb = new StringBuilder(35);
            sb.append("Invalid focus selected: ");
            sb.append(selectFocus);
            Log.w("FocusingProcessor", sb.toString());
            return;
        }
        this.zzd = true;
        this.zze = selectFocus;
        this.zza.setFocus(selectFocus);
        this.zzb.onNewItem(this.zze, t2);
        this.zzb.onUpdate(detections, t2);
    }

    public final void zza(int i) {
        if (i < 0) {
            StringBuilder sb = new StringBuilder(28);
            sb.append("Invalid max gap: ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }
        this.zzc = i;
    }
}
