package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
final class zze extends com.google.android.gms.maps.internal.zzae {
    final /* synthetic */ GoogleMap.OnInfoWindowCloseListener zza;

    public zze(GoogleMap googleMap, GoogleMap.OnInfoWindowCloseListener onInfoWindowCloseListener) {
        this.zza = onInfoWindowCloseListener;
    }

    @Override // com.google.android.gms.maps.internal.zzaf
    public final void zzb(com.google.android.gms.internal.maps.zzx zzxVar) {
        this.zza.onInfoWindowClose(new Marker(zzxVar));
    }
}
