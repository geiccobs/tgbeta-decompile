package com.google.android.gms.maps.model;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class Gap extends PatternItem {
    public final float length;

    public Gap(float length) {
        super(2, Float.valueOf(Math.max(length, 0.0f)));
        this.length = Math.max(length, 0.0f);
    }

    @Override // com.google.android.gms.maps.model.PatternItem
    public String toString() {
        float f = this.length;
        StringBuilder sb = new StringBuilder(29);
        sb.append("[Gap: length=");
        sb.append(f);
        sb.append("]");
        return sb.toString();
    }
}
