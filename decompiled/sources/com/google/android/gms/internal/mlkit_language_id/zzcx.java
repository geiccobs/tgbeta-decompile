package com.google.android.gms.internal.mlkit_language_id;

import com.google.mlkit.common.sdkinternal.SharedPrefManager;
import java.util.concurrent.Callable;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
final /* synthetic */ class zzcx implements Callable {
    private final SharedPrefManager zza;

    private zzcx(SharedPrefManager sharedPrefManager) {
        this.zza = sharedPrefManager;
    }

    public static Callable zza(SharedPrefManager sharedPrefManager) {
        return new zzcx(sharedPrefManager);
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        return this.zza.getMlSdkInstanceId();
    }
}
