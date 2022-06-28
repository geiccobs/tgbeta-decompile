package com.google.android.gms.common;

import android.util.Log;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.AndroidUtilsLight;
import com.google.android.gms.common.util.Hex;
import java.security.MessageDigest;
import java.util.concurrent.Callable;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
@CheckReturnValue
/* loaded from: classes.dex */
public class zzs {
    private static final zzs zzb = new zzs(true, null, null);
    final boolean zza;
    @Nullable
    private final String zzc;
    @Nullable
    private final Throwable zzd;

    public zzs(boolean z, @Nullable String str, @Nullable Throwable th) {
        this.zza = z;
        this.zzc = str;
        this.zzd = th;
    }

    public static zzs zza() {
        return zzb;
    }

    public static zzs zza(Callable<String> callable) {
        return new zzt(callable);
    }

    public static zzs zza(String str) {
        return new zzs(false, str, null);
    }

    public static zzs zza(String str, Throwable th) {
        return new zzs(false, str, th);
    }

    @Nullable
    String zzb() {
        return this.zzc;
    }

    public final void zzc() {
        if (!this.zza && Log.isLoggable("GoogleCertificatesRslt", 3)) {
            if (this.zzd != null) {
                Log.d("GoogleCertificatesRslt", zzb(), this.zzd);
            } else {
                Log.d("GoogleCertificatesRslt", zzb());
            }
        }
    }

    public static String zza(String str, zzd zzdVar, boolean z, boolean z2) {
        String str2 = z2 ? "debug cert rejected" : "not allowed";
        StringBuilder sb = new StringBuilder(17);
        sb.append(12451000);
        sb.append(".false");
        return String.format("%s: pkg=%s, sha1=%s, atk=%s, ver=%s", str2, str, Hex.bytesToStringLowercase(((MessageDigest) Preconditions.checkNotNull(AndroidUtilsLight.zza("SHA-1"))).digest(zzdVar.zza())), Boolean.valueOf(z), sb.toString());
    }
}
