package com.google.android.gms.maps.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.maps.model.RuntimeRemoteException;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public final class zzca {
    private static final String zza = zzca.class.getSimpleName();
    private static Context zzb = null;
    private static zzf zzc;

    public static zzf zza(Context context) throws GooglePlayServicesNotAvailableException {
        zzf zzfVar;
        Preconditions.checkNotNull(context);
        zzf zzfVar2 = zzc;
        if (zzfVar2 == null) {
            int isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context, 13400000);
            switch (isGooglePlayServicesAvailable) {
                case 0:
                    Log.i(zza, "Making Creator dynamically");
                    try {
                        IBinder iBinder = (IBinder) zzc(((ClassLoader) Preconditions.checkNotNull(zzb(context).getClassLoader())).loadClass("com.google.android.gms.maps.internal.CreatorImpl"));
                        if (iBinder == null) {
                            zzfVar = null;
                        } else {
                            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.maps.internal.ICreator");
                            if (queryLocalInterface instanceof zzf) {
                                zzfVar = (zzf) queryLocalInterface;
                            } else {
                                zzfVar = new zze(iBinder);
                            }
                        }
                        zzc = zzfVar;
                        try {
                            zzfVar.zzh(ObjectWrapper.wrap(zzb(context).getResources()), GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE);
                            return zzc;
                        } catch (RemoteException e) {
                            throw new RuntimeRemoteException(e);
                        }
                    } catch (ClassNotFoundException e2) {
                        throw new IllegalStateException("Unable to find dynamic class com.google.android.gms.maps.internal.CreatorImpl");
                    }
                default:
                    throw new GooglePlayServicesNotAvailableException(isGooglePlayServicesAvailable);
            }
        }
        return zzfVar2;
    }

    private static Context zzb(Context context) {
        Context context2;
        Context context3 = zzb;
        if (context3 == null) {
            try {
                context2 = DynamiteModule.load(context, DynamiteModule.PREFER_REMOTE, "com.google.android.gms.maps_dynamite").getModuleContext();
            } catch (Exception e) {
                Log.e(zza, "Failed to load maps module, use legacy", e);
                context2 = GooglePlayServicesUtil.getRemoteContext(context);
            }
            zzb = context2;
            return context2;
        }
        return context3;
    }

    private static <T> T zzc(Class cls) {
        try {
            return (T) cls.newInstance();
        } catch (IllegalAccessException e) {
            String valueOf = String.valueOf(cls.getName());
            throw new IllegalStateException(valueOf.length() != 0 ? "Unable to call the default constructor of ".concat(valueOf) : new String("Unable to call the default constructor of "));
        } catch (InstantiationException e2) {
            String valueOf2 = String.valueOf(cls.getName());
            throw new IllegalStateException(valueOf2.length() != 0 ? "Unable to instantiate the dynamic class ".concat(valueOf2) : new String("Unable to instantiate the dynamic class "));
        }
    }
}
