package com.google.android.gms.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.dynamite.DynamiteModule;
import java.util.concurrent.Callable;
import javax.annotation.CheckReturnValue;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
@CheckReturnValue
/* loaded from: classes.dex */
public final class zzc {
    private static volatile com.google.android.gms.common.internal.zzr zza;
    private static final Object zzb = new Object();
    private static Context zzc;

    public static synchronized void zza(Context context) {
        synchronized (zzc.class) {
            if (zzc == null) {
                if (context != null) {
                    zzc = context.getApplicationContext();
                }
            } else {
                Log.w("GoogleCertificates", "GoogleCertificates has been initialized already");
            }
        }
    }

    private static void zzb() throws DynamiteModule.LoadingException {
        if (zza != null) {
            return;
        }
        Preconditions.checkNotNull(zzc);
        synchronized (zzb) {
            if (zza == null) {
                zza = com.google.android.gms.common.internal.zzq.zza(DynamiteModule.load(zzc, DynamiteModule.PREFER_HIGHEST_OR_LOCAL_VERSION_NO_FORCE_STAGING, "com.google.android.gms.googlecertificates").instantiate("com.google.android.gms.common.GoogleCertificatesImpl"));
            }
        }
    }

    public static zzs zza(String str, boolean z, boolean z2, boolean z3) {
        StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
        try {
            return zzb(str, z, false, false);
        } finally {
            StrictMode.setThreadPolicy(allowThreadDiskReads);
        }
    }

    public static zzs zza(String str, zzd zzdVar, boolean z, boolean z2) {
        StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
        try {
            return zzb(str, zzdVar, z, z2);
        } finally {
            StrictMode.setThreadPolicy(allowThreadDiskReads);
        }
    }

    public static boolean zza() {
        StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
        try {
            try {
                zzb();
                return zza.zza();
            } finally {
                StrictMode.setThreadPolicy(allowThreadDiskReads);
            }
        } catch (RemoteException | DynamiteModule.LoadingException e) {
            Log.e("GoogleCertificates", "Failed to get Google certificates from remote", e);
            StrictMode.setThreadPolicy(allowThreadDiskReads);
            return false;
        }
    }

    private static zzs zzb(String str, boolean z, boolean z2, boolean z3) {
        Preconditions.checkNotNull(zzc);
        try {
            zzb();
            try {
                zzl zza2 = zza.zza(new zzj(str, z, z2, ObjectWrapper.wrap(zzc).asBinder(), false));
                if (zza2.zza()) {
                    return zzs.zza();
                }
                String zzb2 = zza2.zzb();
                if (zzb2 == null) {
                    zzb2 = "error checking package certificate";
                }
                if (zza2.zzc().equals(zzo.PACKAGE_NOT_FOUND)) {
                    return zzs.zza(zzb2, new PackageManager.NameNotFoundException());
                }
                return zzs.zza(zzb2);
            } catch (RemoteException e) {
                Log.e("GoogleCertificates", "Failed to get Google certificates from remote", e);
                return zzs.zza("module call", e);
            }
        } catch (DynamiteModule.LoadingException e2) {
            Log.e("GoogleCertificates", "Failed to get Google certificates from remote", e2);
            String valueOf = String.valueOf(e2.getMessage());
            return zzs.zza(valueOf.length() != 0 ? "module init: ".concat(valueOf) : new String("module init: "), e2);
        }
    }

    private static zzs zzb(String str, zzd zzdVar, boolean z, boolean z2) {
        try {
            zzb();
            Preconditions.checkNotNull(zzc);
            try {
                if (zza.zza(new zzq(str, zzdVar, z, z2), ObjectWrapper.wrap(zzc.getPackageManager()))) {
                    return zzs.zza();
                }
                return zzs.zza(new Callable(z, str, zzdVar) { // from class: com.google.android.gms.common.zze
                    private final boolean zza;
                    private final String zzb;
                    private final zzd zzc;

                    /* JADX INFO: Access modifiers changed from: package-private */
                    {
                        this.zza = z;
                        this.zzb = str;
                        this.zzc = zzdVar;
                    }

                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        return zzc.zza(this.zza, this.zzb, this.zzc);
                    }
                });
            } catch (RemoteException e) {
                Log.e("GoogleCertificates", "Failed to get Google certificates from remote", e);
                return zzs.zza("module call", e);
            }
        } catch (DynamiteModule.LoadingException e2) {
            Log.e("GoogleCertificates", "Failed to get Google certificates from remote", e2);
            String valueOf = String.valueOf(e2.getMessage());
            return zzs.zza(valueOf.length() != 0 ? "module init: ".concat(valueOf) : new String("module init: "), e2);
        }
    }

    public static final /* synthetic */ String zza(boolean z, String str, zzd zzdVar) throws Exception {
        boolean z2 = true;
        if (z || !zzb(str, zzdVar, true, false).zza) {
            z2 = false;
        }
        return zzs.zza(str, zzdVar, z, z2);
    }
}
