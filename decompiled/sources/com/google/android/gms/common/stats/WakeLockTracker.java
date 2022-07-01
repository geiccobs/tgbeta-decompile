package com.google.android.gms.common.stats;

import android.content.Context;
import androidx.annotation.RecentlyNonNull;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
@Deprecated
/* loaded from: classes.dex */
public class WakeLockTracker {
    private static WakeLockTracker zza = new WakeLockTracker();

    public void registerEvent(@RecentlyNonNull Context context, @RecentlyNonNull String str, int i, @RecentlyNonNull String str2, @RecentlyNonNull String str3, @RecentlyNonNull String str4, int i2, @RecentlyNonNull List<String> list) {
    }

    public void registerEvent(@RecentlyNonNull Context context, @RecentlyNonNull String str, int i, @RecentlyNonNull String str2, @RecentlyNonNull String str3, @RecentlyNonNull String str4, int i2, @RecentlyNonNull List<String> list, long j) {
    }

    @RecentlyNonNull
    public static WakeLockTracker getInstance() {
        return zza;
    }
}
