package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
/* loaded from: classes3.dex */
public final class zzj {
    private static SharedPreferences zzw = null;

    public static SharedPreferences zza(Context context) throws Exception {
        SharedPreferences sharedPreferences;
        synchronized (SharedPreferences.class) {
            if (zzw == null) {
                zzw = (SharedPreferences) com.google.android.gms.internal.flags.zze.zza(new zzk(context));
            }
            sharedPreferences = zzw;
        }
        return sharedPreferences;
    }
}
