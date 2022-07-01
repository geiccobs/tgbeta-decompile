package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public interface ICameraUpdateFactoryDelegate extends IInterface {
    @RecentlyNonNull
    IObjectWrapper newLatLng(@RecentlyNonNull LatLng latLng) throws RemoteException;

    @RecentlyNonNull
    IObjectWrapper newLatLngBounds(@RecentlyNonNull LatLngBounds latLngBounds, int i) throws RemoteException;

    @RecentlyNonNull
    IObjectWrapper newLatLngZoom(@RecentlyNonNull LatLng latLng, float f) throws RemoteException;
}
