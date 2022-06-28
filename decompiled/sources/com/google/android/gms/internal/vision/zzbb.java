package com.google.android.gms.internal.vision;

import android.os.Binder;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final /* synthetic */ class zzbb {
    public static <V> V zza(zzba<V> zzbaVar) {
        try {
            return zzbaVar.zza();
        } catch (SecurityException e) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return zzbaVar.zza();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }
}
