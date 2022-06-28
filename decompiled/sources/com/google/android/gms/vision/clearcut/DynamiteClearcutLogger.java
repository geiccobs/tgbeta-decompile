package com.google.android.gms.vision.clearcut;

import android.content.Context;
import com.google.android.gms.internal.vision.zze;
import com.google.android.gms.internal.vision.zzfi;
import com.google.android.gms.internal.vision.zzi;
import com.google.android.gms.vision.L;
import java.util.concurrent.ExecutorService;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public class DynamiteClearcutLogger {
    private static final ExecutorService zza = zze.zza().zza(2, zzi.zza);
    private zzb zzb = new zzb(0.03333333333333333d);
    private VisionClearcutLogger zzc;

    public DynamiteClearcutLogger(Context context) {
        this.zzc = new VisionClearcutLogger(context);
    }

    public final void zza(int i, zzfi.zzo zzoVar) {
        if (i == 3 && !this.zzb.zza()) {
            L.v("Skipping image analysis log due to rate limiting", new Object[0]);
        } else {
            zza.execute(new zza(this, i, zzoVar));
        }
    }
}
