package com.google.firebase.appindexing.internal;

import android.util.Log;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes.dex */
public final class zzw {
    public static int zza(String str) {
        if (zzb(3)) {
            return Log.d("FirebaseAppIndex", str);
        }
        return 0;
    }

    public static boolean zzb(int i) {
        if (Log.isLoggable("FirebaseAppIndex", i)) {
            return true;
        }
        return Log.isLoggable("FirebaseAppIndex", i);
    }
}
