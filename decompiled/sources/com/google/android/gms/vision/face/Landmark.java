package com.google.android.gms.vision.face;

import android.graphics.PointF;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes3.dex */
public final class Landmark {
    public static final int BOTTOM_MOUTH = 0;
    public static final int LEFT_CHEEK = 1;
    public static final int LEFT_EAR = 3;
    public static final int LEFT_EAR_TIP = 2;
    public static final int LEFT_EYE = 4;
    public static final int LEFT_MOUTH = 5;
    public static final int NOSE_BASE = 6;
    public static final int RIGHT_CHEEK = 7;
    public static final int RIGHT_EAR = 9;
    public static final int RIGHT_EAR_TIP = 8;
    public static final int RIGHT_EYE = 10;
    public static final int RIGHT_MOUTH = 11;
    private final PointF zza;
    private final int zzb;

    public final PointF getPosition() {
        return this.zza;
    }

    public final int getType() {
        return this.zzb;
    }

    public Landmark(PointF pointF, int i) {
        this.zza = pointF;
        this.zzb = i;
    }
}
