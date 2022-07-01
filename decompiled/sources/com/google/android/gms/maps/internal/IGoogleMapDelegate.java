package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.maps.zzx;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public interface IGoogleMapDelegate extends IInterface {
    com.google.android.gms.internal.maps.zzl addCircle(CircleOptions circleOptions) throws RemoteException;

    zzx addMarker(MarkerOptions markerOptions) throws RemoteException;

    void animateCamera(@RecentlyNonNull IObjectWrapper iObjectWrapper) throws RemoteException;

    void animateCameraWithDurationAndCallback(IObjectWrapper iObjectWrapper, int i, @Nullable zzd zzdVar) throws RemoteException;

    @RecentlyNonNull
    CameraPosition getCameraPosition() throws RemoteException;

    float getMaxZoomLevel() throws RemoteException;

    @RecentlyNonNull
    IProjectionDelegate getProjection() throws RemoteException;

    @RecentlyNonNull
    IUiSettingsDelegate getUiSettings() throws RemoteException;

    void moveCamera(@RecentlyNonNull IObjectWrapper iObjectWrapper) throws RemoteException;

    boolean setMapStyle(@Nullable MapStyleOptions mapStyleOptions) throws RemoteException;

    void setMapType(int i) throws RemoteException;

    void setMyLocationEnabled(boolean z) throws RemoteException;

    void setOnCameraMoveListener(@Nullable zzt zztVar) throws RemoteException;

    void setOnCameraMoveStartedListener(@Nullable zzv zzvVar) throws RemoteException;

    void setOnMapLoadedCallback(@Nullable zzan zzanVar) throws RemoteException;

    void setOnMarkerClickListener(@Nullable zzat zzatVar) throws RemoteException;

    void setOnMyLocationChangeListener(@Nullable zzaz zzazVar) throws RemoteException;

    void setPadding(int i, int i2, int i3, int i4) throws RemoteException;
}
