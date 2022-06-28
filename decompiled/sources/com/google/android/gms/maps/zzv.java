package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class zzv extends com.google.android.gms.maps.internal.zzs {
    final /* synthetic */ GoogleMap.OnCameraMoveListener zza;

    public zzv(GoogleMap googleMap, GoogleMap.OnCameraMoveListener onCameraMoveListener) {
        this.zza = onCameraMoveListener;
    }

    @Override // com.google.android.gms.maps.internal.zzt
    public final void zzb() {
        this.zza.onCameraMove();
    }
}
