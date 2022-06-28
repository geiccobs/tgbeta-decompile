package com.google.android.gms.maps.model;

import android.graphics.Bitmap;
import android.os.RemoteException;
import com.google.android.gms.common.internal.Preconditions;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class BitmapDescriptorFactory {
    public static final float HUE_AZURE = 210.0f;
    public static final float HUE_BLUE = 240.0f;
    public static final float HUE_CYAN = 180.0f;
    public static final float HUE_GREEN = 120.0f;
    public static final float HUE_MAGENTA = 300.0f;
    public static final float HUE_ORANGE = 30.0f;
    public static final float HUE_RED = 0.0f;
    public static final float HUE_ROSE = 330.0f;
    public static final float HUE_VIOLET = 270.0f;
    public static final float HUE_YELLOW = 60.0f;
    private static com.google.android.gms.internal.maps.zzi zza;

    private BitmapDescriptorFactory() {
    }

    public static BitmapDescriptor defaultMarker() {
        try {
            return new BitmapDescriptor(zzb().zzg());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static BitmapDescriptor fromAsset(String assetName) {
        Preconditions.checkNotNull(assetName, "assetName must not be null");
        try {
            return new BitmapDescriptor(zzb().zze(assetName));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static BitmapDescriptor fromBitmap(Bitmap image) {
        Preconditions.checkNotNull(image, "image must not be null");
        try {
            return new BitmapDescriptor(zzb().zzi(image));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static BitmapDescriptor fromFile(String fileName) {
        Preconditions.checkNotNull(fileName, "fileName must not be null");
        try {
            return new BitmapDescriptor(zzb().zzf(fileName));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static BitmapDescriptor fromPath(String absolutePath) {
        Preconditions.checkNotNull(absolutePath, "absolutePath must not be null");
        try {
            return new BitmapDescriptor(zzb().zzj(absolutePath));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static BitmapDescriptor fromResource(int resourceId) {
        try {
            return new BitmapDescriptor(zzb().zzd(resourceId));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public static void zza(com.google.android.gms.internal.maps.zzi zziVar) {
        if (zza != null) {
            return;
        }
        zza = (com.google.android.gms.internal.maps.zzi) Preconditions.checkNotNull(zziVar, "delegate must not be null");
    }

    private static com.google.android.gms.internal.maps.zzi zzb() {
        return (com.google.android.gms.internal.maps.zzi) Preconditions.checkNotNull(zza, "IBitmapDescriptorFactory is not initialized");
    }

    public static BitmapDescriptor defaultMarker(float hue) {
        try {
            return new BitmapDescriptor(zzb().zzh(hue));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
