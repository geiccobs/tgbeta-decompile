package com.google.android.gms.maps.model;

import androidx.annotation.RecentlyNonNull;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public final class Gap extends PatternItem {
    public final float length;

    public Gap(float f) {
        super(2, Float.valueOf(Math.max(f, 0.0f)));
        this.length = Math.max(f, 0.0f);
    }

    @Override // com.google.android.gms.maps.model.PatternItem
    @RecentlyNonNull
    public String toString() {
        float f = this.length;
        StringBuilder sb = new StringBuilder(29);
        sb.append("[Gap: length=");
        sb.append(f);
        sb.append("]");
        return sb.toString();
    }
}
