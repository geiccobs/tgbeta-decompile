package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class zza extends com.google.android.gms.maps.internal.zzas {
    final /* synthetic */ GoogleMap.OnMarkerClickListener zza;

    public zza(GoogleMap googleMap, GoogleMap.OnMarkerClickListener onMarkerClickListener) {
        this.zza = onMarkerClickListener;
    }

    @Override // com.google.android.gms.maps.internal.zzat
    public final boolean zzb(com.google.android.gms.internal.maps.zzx zzxVar) {
        return this.zza.onMarkerClick(new Marker(zzxVar));
    }
}
