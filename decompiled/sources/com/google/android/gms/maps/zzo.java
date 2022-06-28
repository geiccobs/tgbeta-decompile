package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.internal.zzbe;
import com.google.android.gms.maps.model.Polygon;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
final class zzo extends zzbe {
    final /* synthetic */ GoogleMap.OnPolygonClickListener zza;

    public zzo(GoogleMap googleMap, GoogleMap.OnPolygonClickListener onPolygonClickListener) {
        this.zza = onPolygonClickListener;
    }

    @Override // com.google.android.gms.maps.internal.zzbf
    public final void zzb(com.google.android.gms.internal.maps.zzaa zzaaVar) {
        this.zza.onPolygonClick(new Polygon(zzaaVar));
    }
}
