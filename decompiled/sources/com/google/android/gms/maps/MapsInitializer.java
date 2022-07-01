package com.google.android.gms.maps;

import android.content.Context;
import android.os.RemoteException;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.maps.internal.zzca;
import com.google.android.gms.maps.internal.zzf;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import javax.annotation.concurrent.GuardedBy;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public final class MapsInitializer {
    @GuardedBy("MapsInitializer.class")
    private static boolean zza = false;

    public static synchronized int initialize(@RecentlyNonNull Context context) {
        synchronized (MapsInitializer.class) {
            Preconditions.checkNotNull(context, "Context is null");
            if (!zza) {
                try {
                    zzf zza2 = zzca.zza(context);
                    try {
                        CameraUpdateFactory.zza(zza2.zzf());
                        BitmapDescriptorFactory.zza(zza2.zzg());
                        zza = true;
                        return 0;
                    } catch (RemoteException e) {
                        throw new RuntimeRemoteException(e);
                    }
                } catch (GooglePlayServicesNotAvailableException e2) {
                    return e2.errorCode;
                }
            }
            return 0;
        }
    }
}
