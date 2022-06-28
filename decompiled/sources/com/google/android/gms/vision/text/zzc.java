package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.vision.zzab;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes3.dex */
final class zzc {
    public static Rect zza(Text text) {
        Point[] cornerPoints;
        int i = Integer.MIN_VALUE;
        int i2 = Integer.MIN_VALUE;
        int i3 = Integer.MAX_VALUE;
        int i4 = Integer.MAX_VALUE;
        for (Point point : text.getCornerPoints()) {
            i3 = Math.min(i3, point.x);
            i = Math.max(i, point.x);
            i4 = Math.min(i4, point.y);
            i2 = Math.max(i2, point.y);
        }
        return new Rect(i3, i4, i, i2);
    }

    public static Point[] zza(zzab zzabVar) {
        double sin = Math.sin(Math.toRadians(zzabVar.zze));
        double cos = Math.cos(Math.toRadians(zzabVar.zze));
        double d = zzabVar.zza;
        double d2 = zzabVar.zzc;
        Double.isNaN(d2);
        Double.isNaN(d);
        double d3 = zzabVar.zzb;
        double d4 = zzabVar.zzc;
        Double.isNaN(d4);
        Double.isNaN(d3);
        double d5 = r0[1].x;
        double d6 = zzabVar.zzd;
        Double.isNaN(d6);
        Double.isNaN(d5);
        int i = (int) (d5 - (d6 * sin));
        double d7 = r0[1].y;
        double d8 = zzabVar.zzd;
        Double.isNaN(d8);
        Double.isNaN(d7);
        Point[] pointArr = {new Point(zzabVar.zza, zzabVar.zzb), new Point((int) (d + (d2 * cos)), (int) (d3 + (d4 * sin))), new Point(i, (int) (d7 + (d8 * cos))), new Point(pointArr[0].x + (pointArr[2].x - pointArr[1].x), pointArr[0].y + (pointArr[2].y - pointArr[1].y))};
        return pointArr;
    }
}
