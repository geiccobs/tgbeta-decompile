package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
final class zzb extends com.google.android.gms.maps.internal.zzau {
    final /* synthetic */ GoogleMap.OnMarkerDragListener zza;

    public zzb(GoogleMap googleMap, GoogleMap.OnMarkerDragListener onMarkerDragListener) {
        this.zza = onMarkerDragListener;
    }

    @Override // com.google.android.gms.maps.internal.zzav
    public final void zzb(com.google.android.gms.internal.maps.zzx zzxVar) {
        this.zza.onMarkerDragStart(new Marker(zzxVar));
    }

    @Override // com.google.android.gms.maps.internal.zzav
    public final void zzc(com.google.android.gms.internal.maps.zzx zzxVar) {
        this.zza.onMarkerDragEnd(new Marker(zzxVar));
    }

    @Override // com.google.android.gms.maps.internal.zzav
    public final void zzd(com.google.android.gms.internal.maps.zzx zzxVar) {
        this.zza.onMarkerDrag(new Marker(zzxVar));
    }
}
