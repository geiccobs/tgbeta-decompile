package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.SparseArray;
import com.google.android.gms.internal.vision.zzab;
import com.google.android.gms.internal.vision.zzah;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes3.dex */
public class TextBlock implements Text {
    private zzah[] zza;
    private Point[] zzb;
    private List<Line> zzc;
    private String zzd;
    private Rect zze;

    public TextBlock(SparseArray<zzah> sparseArray) {
        this.zza = new zzah[sparseArray.size()];
        int i = 0;
        while (true) {
            zzah[] zzahVarArr = this.zza;
            if (i < zzahVarArr.length) {
                zzahVarArr[i] = sparseArray.valueAt(i);
                i++;
            } else {
                return;
            }
        }
    }

    @Override // com.google.android.gms.vision.text.Text
    public String getLanguage() {
        zzah[] zzahVarArr;
        int i;
        String str = this.zzd;
        if (str != null) {
            return str;
        }
        HashMap hashMap = new HashMap();
        for (zzah zzahVar : this.zza) {
            if (!hashMap.containsKey(zzahVar.zzd)) {
                i = 0;
            } else {
                i = ((Integer) hashMap.get(zzahVar.zzd)).intValue();
            }
            hashMap.put(zzahVar.zzd, Integer.valueOf(i + 1));
        }
        String str2 = (String) ((Map.Entry) Collections.max(hashMap.entrySet(), new zza(this))).getKey();
        this.zzd = str2;
        if (str2 == null || str2.isEmpty()) {
            this.zzd = "und";
        }
        return this.zzd;
    }

    @Override // com.google.android.gms.vision.text.Text
    public String getValue() {
        zzah[] zzahVarArr = this.zza;
        if (zzahVarArr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(zzahVarArr[0].zzc);
        for (int i = 1; i < this.zza.length; i++) {
            sb.append("\n");
            sb.append(this.zza[i].zzc);
        }
        return sb.toString();
    }

    @Override // com.google.android.gms.vision.text.Text
    public Point[] getCornerPoints() {
        zzah[] zzahVarArr;
        TextBlock textBlock = this;
        if (textBlock.zzb == null) {
            char c = 0;
            if (textBlock.zza.length == 0) {
                textBlock.zzb = new Point[0];
            } else {
                int i = Integer.MIN_VALUE;
                int i2 = Integer.MIN_VALUE;
                int i3 = Integer.MAX_VALUE;
                int i4 = Integer.MAX_VALUE;
                int i5 = 0;
                while (true) {
                    zzahVarArr = textBlock.zza;
                    if (i5 >= zzahVarArr.length) {
                        break;
                    }
                    zzab zzabVar = zzahVarArr[i5].zzb;
                    zzab zzabVar2 = textBlock.zza[c].zzb;
                    double sin = Math.sin(Math.toRadians(zzabVar2.zze));
                    double cos = Math.cos(Math.toRadians(zzabVar2.zze));
                    Point[] pointArr = new Point[4];
                    pointArr[c] = new Point(zzabVar.zza, zzabVar.zzb);
                    pointArr[c].offset(-zzabVar2.zza, -zzabVar2.zzb);
                    double d = pointArr[c].x;
                    Double.isNaN(d);
                    int i6 = i2;
                    double d2 = pointArr[c].y;
                    Double.isNaN(d2);
                    int i7 = (int) ((d * cos) + (d2 * sin));
                    double d3 = -pointArr[0].x;
                    Double.isNaN(d3);
                    double d4 = d3 * sin;
                    double d5 = pointArr[0].y;
                    Double.isNaN(d5);
                    int i8 = (int) (d4 + (d5 * cos));
                    pointArr[0].x = i7;
                    pointArr[0].y = i8;
                    pointArr[1] = new Point(zzabVar.zzc + i7, i8);
                    pointArr[2] = new Point(zzabVar.zzc + i7, zzabVar.zzd + i8);
                    pointArr[3] = new Point(i7, i8 + zzabVar.zzd);
                    i2 = i6;
                    for (int i9 = 0; i9 < 4; i9++) {
                        Point point = pointArr[i9];
                        i3 = Math.min(i3, point.x);
                        i = Math.max(i, point.x);
                        i4 = Math.min(i4, point.y);
                        i2 = Math.max(i2, point.y);
                    }
                    i5++;
                    c = 0;
                    textBlock = this;
                }
                int i10 = i2;
                zzab zzabVar3 = zzahVarArr[0].zzb;
                int i11 = zzabVar3.zza;
                int i12 = zzabVar3.zzb;
                double sin2 = Math.sin(Math.toRadians(zzabVar3.zze));
                double cos2 = Math.cos(Math.toRadians(zzabVar3.zze));
                Point[] pointArr2 = {new Point(i3, i4), new Point(i, i4), new Point(i, i10), new Point(i3, i10)};
                for (int i13 = 0; i13 < 4; i13++) {
                    double d6 = pointArr2[i13].x;
                    Double.isNaN(d6);
                    double d7 = pointArr2[i13].y;
                    Double.isNaN(d7);
                    int i14 = (int) ((d6 * cos2) - (d7 * sin2));
                    double d8 = pointArr2[i13].x;
                    Double.isNaN(d8);
                    double d9 = pointArr2[i13].y;
                    Double.isNaN(d9);
                    pointArr2[i13].x = i14;
                    pointArr2[i13].y = (int) ((d8 * sin2) + (d9 * cos2));
                    pointArr2[i13].offset(i11, i12);
                }
                textBlock = this;
                textBlock.zzb = pointArr2;
            }
        }
        return textBlock.zzb;
    }

    @Override // com.google.android.gms.vision.text.Text
    public List<? extends Text> getComponents() {
        if (this.zza.length == 0) {
            return new ArrayList(0);
        }
        if (this.zzc == null) {
            this.zzc = new ArrayList(this.zza.length);
            for (zzah zzahVar : this.zza) {
                this.zzc.add(new Line(zzahVar));
            }
        }
        return this.zzc;
    }

    @Override // com.google.android.gms.vision.text.Text
    public Rect getBoundingBox() {
        if (this.zze == null) {
            this.zze = zzc.zza(this);
        }
        return this.zze;
    }
}
