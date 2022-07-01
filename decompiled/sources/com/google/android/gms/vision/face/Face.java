package com.google.android.gms.vision.face;

import android.graphics.PointF;
import androidx.annotation.RecentlyNonNull;
import java.util.Arrays;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes.dex */
public class Face {
    private int zza;
    private List<Landmark> zzh;
    private final List<Contour> zzi;

    private static float zza(float f) {
        if (f < 0.0f || f > 1.0f) {
            return -1.0f;
        }
        return f;
    }

    @RecentlyNonNull
    public List<Landmark> getLandmarks() {
        return this.zzh;
    }

    public int getId() {
        return this.zza;
    }

    public Face(int i, @RecentlyNonNull PointF pointF, float f, float f2, float f3, float f4, float f5, @RecentlyNonNull Landmark[] landmarkArr, @RecentlyNonNull Contour[] contourArr, float f6, float f7, float f8, float f9) {
        this.zza = i;
        this.zzh = Arrays.asList(landmarkArr);
        this.zzi = Arrays.asList(contourArr);
        zza(f6);
        zza(f7);
        zza(f8);
        zza(f9);
    }
}
