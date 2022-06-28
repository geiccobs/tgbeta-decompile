package com.google.android.gms.maps;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.maps.internal.zzca;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.RuntimeRemoteException;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class MapsInitializer {
    private static boolean zza = false;

    private MapsInitializer() {
    }

    public static synchronized int initialize(Context context) {
        synchronized (MapsInitializer.class) {
            Preconditions.checkNotNull(context, "Context is null");
            if (!zza) {
                try {
                    com.google.android.gms.maps.internal.zzf zza2 = zzca.zza(context);
                    try {
                        CameraUpdateFactory.zza(zza2.zzf());
                        BitmapDescriptorFactory.zza(zza2.zzg());
                        zza = true;
                    } catch (RemoteException e) {
                        throw new RuntimeRemoteException(e);
                    }
                } catch (GooglePlayServicesNotAvailableException e2) {
                    return e2.errorCode;
                }
            }
        }
        return 0;
    }
}
