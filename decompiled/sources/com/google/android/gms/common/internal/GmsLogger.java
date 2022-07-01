package com.google.android.gms.common.internal;

import android.util.Log;
import androidx.annotation.RecentlyNonNull;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes.dex */
public final class GmsLogger {
    private final String zzc;
    private final String zzd;

    public GmsLogger(@RecentlyNonNull String str, String str2) {
        Preconditions.checkNotNull(str, "log tag cannot be null");
        Preconditions.checkArgument(str.length() <= 23, "tag \"%s\" is longer than the %d character maximum", str, 23);
        this.zzc = str;
        if (str2 == null || str2.length() <= 0) {
            this.zzd = null;
        } else {
            this.zzd = str2;
        }
    }

    public GmsLogger(@RecentlyNonNull String str) {
        this(str, null);
    }

    public final boolean canLog(int i) {
        return Log.isLoggable(this.zzc, i);
    }

    public final void d(@RecentlyNonNull String str, @RecentlyNonNull String str2) {
        if (canLog(3)) {
            Log.d(str, zza(str2));
        }
    }

    public final void v(@RecentlyNonNull String str, @RecentlyNonNull String str2) {
        if (canLog(2)) {
            Log.v(str, zza(str2));
        }
    }

    public final void w(@RecentlyNonNull String str, @RecentlyNonNull String str2) {
        if (canLog(5)) {
            Log.w(str, zza(str2));
        }
    }

    public final void e(@RecentlyNonNull String str, @RecentlyNonNull String str2) {
        if (canLog(6)) {
            Log.e(str, zza(str2));
        }
    }

    public final void e(@RecentlyNonNull String str, @RecentlyNonNull String str2, @RecentlyNonNull Throwable th) {
        if (canLog(6)) {
            Log.e(str, zza(str2), th);
        }
    }

    private final String zza(String str) {
        String str2 = this.zzd;
        return str2 == null ? str : str2.concat(str);
    }
}
