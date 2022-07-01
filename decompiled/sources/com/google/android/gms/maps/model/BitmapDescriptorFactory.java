package com.google.android.gms.maps.model;

import android.graphics.Bitmap;
import android.os.RemoteException;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.internal.Preconditions;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public final class BitmapDescriptorFactory {
    private static com.google.android.gms.internal.maps.zzi zza;

    @RecentlyNonNull
    public static BitmapDescriptor fromBitmap(@RecentlyNonNull Bitmap bitmap) {
        Preconditions.checkNotNull(bitmap, "image must not be null");
        try {
            return new BitmapDescriptor(zzb().zzi(bitmap));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @RecentlyNonNull
    public static BitmapDescriptor fromResource(int i) {
        try {
            return new BitmapDescriptor(zzb().zzd(i));
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
}
