package com.google.android.gms.maps;

import android.location.Location;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public interface LocationSource {

    /* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
    /* loaded from: classes3.dex */
    public interface OnLocationChangedListener {
        void onLocationChanged(Location location);
    }

    void activate(OnLocationChangedListener onLocationChangedListener);

    void deactivate();
}
