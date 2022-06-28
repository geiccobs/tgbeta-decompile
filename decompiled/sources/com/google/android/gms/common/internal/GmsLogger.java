package com.google.android.gms.common.internal;

import android.util.Log;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public final class GmsLogger {
    private static final int zza = 15;
    private static final String zzb = null;
    private final String zzc;
    private final String zzd;

    public GmsLogger(String str, String str2) {
        Preconditions.checkNotNull(str, "log tag cannot be null");
        Preconditions.checkArgument(str.length() <= 23, "tag \"%s\" is longer than the %d character maximum", str, 23);
        this.zzc = str;
        if (str2 == null || str2.length() <= 0) {
            this.zzd = null;
        } else {
            this.zzd = str2;
        }
    }

    public GmsLogger(String str) {
        this(str, null);
    }

    public final boolean canLog(int i) {
        return Log.isLoggable(this.zzc, i);
    }

    public final boolean canLogPii() {
        return false;
    }

    public final void d(String str, String str2) {
        if (canLog(3)) {
            Log.d(str, zza(str2));
        }
    }

    public final void d(String str, String str2, Throwable th) {
        if (canLog(3)) {
            Log.d(str, zza(str2), th);
        }
    }

    public final void v(String str, String str2) {
        if (canLog(2)) {
            Log.v(str, zza(str2));
        }
    }

    public final void v(String str, String str2, Throwable th) {
        if (canLog(2)) {
            Log.v(str, zza(str2), th);
        }
    }

    public final void i(String str, String str2) {
        if (canLog(4)) {
            Log.i(str, zza(str2));
        }
    }

    public final void i(String str, String str2, Throwable th) {
        if (canLog(4)) {
            Log.i(str, zza(str2), th);
        }
    }

    public final void w(String str, String str2) {
        if (canLog(5)) {
            Log.w(str, zza(str2));
        }
    }

    public final void w(String str, String str2, Throwable th) {
        if (canLog(5)) {
            Log.w(str, zza(str2), th);
        }
    }

    public final void wfmt(String str, String str2, Object... objArr) {
        if (canLog(5)) {
            Log.w(this.zzc, zza(str2, objArr));
        }
    }

    public final void e(String str, String str2) {
        if (canLog(6)) {
            Log.e(str, zza(str2));
        }
    }

    public final void e(String str, String str2, Throwable th) {
        if (canLog(6)) {
            Log.e(str, zza(str2), th);
        }
    }

    public final void efmt(String str, String str2, Object... objArr) {
        if (canLog(6)) {
            Log.e(str, zza(str2, objArr));
        }
    }

    public final void wtf(String str, String str2, Throwable th) {
        if (canLog(7)) {
            Log.e(str, zza(str2), th);
            Log.wtf(str, zza(str2), th);
        }
    }

    public final void pii(String str, String str2) {
        if (canLogPii()) {
            String valueOf = String.valueOf(str);
            String valueOf2 = String.valueOf(" PII_LOG");
            Log.i(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf), zza(str2));
        }
    }

    public final void pii(String str, String str2, Throwable th) {
        if (canLogPii()) {
            String valueOf = String.valueOf(str);
            String valueOf2 = String.valueOf(" PII_LOG");
            Log.i(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf), zza(str2), th);
        }
    }

    private final String zza(String str) {
        String str2 = this.zzd;
        if (str2 == null) {
            return str;
        }
        return str2.concat(str);
    }

    private final String zza(String str, Object... objArr) {
        String format = String.format(str, objArr);
        String str2 = this.zzd;
        if (str2 == null) {
            return format;
        }
        return str2.concat(format);
    }
}
