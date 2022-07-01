package com.google.android.gms.maps.model;

import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.IObjectWrapper;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public final class BitmapDescriptor {
    private final IObjectWrapper zza;

    public BitmapDescriptor(@RecentlyNonNull IObjectWrapper iObjectWrapper) {
        this.zza = (IObjectWrapper) Preconditions.checkNotNull(iObjectWrapper);
    }

    @RecentlyNonNull
    public final IObjectWrapper zza() {
        return this.zza;
    }
}
