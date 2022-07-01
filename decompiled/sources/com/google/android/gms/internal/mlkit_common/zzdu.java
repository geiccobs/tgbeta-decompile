package com.google.android.gms.internal.mlkit_common;

import com.google.mlkit.common.sdkinternal.SharedPrefManager;
import java.util.concurrent.Callable;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
final /* synthetic */ class zzdu implements Callable {
    private final SharedPrefManager zza;

    private zzdu(SharedPrefManager sharedPrefManager) {
        this.zza = sharedPrefManager;
    }

    public static Callable zza(SharedPrefManager sharedPrefManager) {
        return new zzdu(sharedPrefManager);
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        return this.zza.getMlSdkInstanceId();
    }
}
