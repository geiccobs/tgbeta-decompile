package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.vision.zzah;
import com.google.android.gms.internal.vision.zzao;
import java.util.ArrayList;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-vision@@20.1.3 */
/* loaded from: classes3.dex */
public class Line implements Text {
    private zzah zza;
    private List<Element> zzb;

    public Line(zzah zzahVar) {
        this.zza = zzahVar;
    }

    @Override // com.google.android.gms.vision.text.Text
    public String getLanguage() {
        return this.zza.zzd;
    }

    @Override // com.google.android.gms.vision.text.Text
    public String getValue() {
        return this.zza.zzc;
    }

    @Override // com.google.android.gms.vision.text.Text
    public Rect getBoundingBox() {
        return zzc.zza(this);
    }

    @Override // com.google.android.gms.vision.text.Text
    public Point[] getCornerPoints() {
        return zzc.zza(this.zza.zzb);
    }

    @Override // com.google.android.gms.vision.text.Text
    public List<? extends Text> getComponents() {
        if (this.zza.zza.length == 0) {
            return new ArrayList(0);
        }
        if (this.zzb == null) {
            this.zzb = new ArrayList(this.zza.zza.length);
            for (zzao zzaoVar : this.zza.zza) {
                this.zzb.add(new Element(zzaoVar));
            }
        }
        return this.zzb;
    }

    public float getAngle() {
        return this.zza.zzb.zze;
    }

    public boolean isVertical() {
        return this.zza.zze;
    }
}
