package com.google.android.gms.common.wrappers;

import android.content.Context;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public class Wrappers {
    private static Wrappers zzb = new Wrappers();
    private PackageManagerWrapper zza = null;

    private final synchronized PackageManagerWrapper zza(Context context) {
        if (this.zza == null) {
            if (context.getApplicationContext() != null) {
                context = context.getApplicationContext();
            }
            this.zza = new PackageManagerWrapper(context);
        }
        return this.zza;
    }

    public static PackageManagerWrapper packageManager(Context context) {
        return zzb.zza(context);
    }
}
