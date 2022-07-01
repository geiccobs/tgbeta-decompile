package com.google.android.gms.common;

import android.content.Context;
import android.util.Log;
import javax.annotation.CheckReturnValue;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
@CheckReturnValue
/* loaded from: classes.dex */
final class zzc {
    private static Context zzc;

    public static synchronized void zza(Context context) {
        synchronized (zzc.class) {
            if (zzc != null) {
                Log.w("GoogleCertificates", "GoogleCertificates has been initialized already");
            } else if (context != null) {
                zzc = context.getApplicationContext();
            }
        }
    }
}
