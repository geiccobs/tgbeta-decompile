package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
final class zzy extends com.google.android.gms.maps.internal.zzak {
    final /* synthetic */ GoogleMap.OnMapClickListener zza;

    public zzy(GoogleMap googleMap, GoogleMap.OnMapClickListener onMapClickListener) {
        this.zza = onMapClickListener;
    }

    @Override // com.google.android.gms.maps.internal.zzal
    public final void zzb(LatLng latLng) {
        this.zza.onMapClick(latLng);
    }
}
