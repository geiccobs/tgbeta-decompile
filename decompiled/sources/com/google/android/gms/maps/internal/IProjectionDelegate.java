package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.LatLng;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public interface IProjectionDelegate extends IInterface {
    @RecentlyNonNull
    IObjectWrapper toScreenLocation(@RecentlyNonNull LatLng latLng) throws RemoteException;
}
