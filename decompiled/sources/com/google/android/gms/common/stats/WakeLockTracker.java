package com.google.android.gms.common.stats;

import android.content.Context;
import android.content.Intent;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
@Deprecated
/* loaded from: classes3.dex */
public class WakeLockTracker {
    private static WakeLockTracker zza = new WakeLockTracker();
    private static boolean zzb = false;

    public static WakeLockTracker getInstance() {
        return zza;
    }

    public void registerAcquireEvent(Context context, Intent intent, String str, String str2, String str3, int i, String str4) {
    }

    public void registerReleaseEvent(Context context, Intent intent) {
    }

    public void registerEvent(Context context, String str, int i, String str2, String str3, String str4, int i2, List<String> list) {
    }

    public void registerEvent(Context context, String str, int i, String str2, String str3, String str4, int i2, List<String> list, long j) {
    }

    public void registerDeadlineEvent(Context context, String str, String str2, String str3, int i, List<String> list, boolean z, long j) {
    }
}
