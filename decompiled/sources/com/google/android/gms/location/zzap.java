package com.google.android.gms.location;

import com.google.android.gms.common.api.internal.RemoteCall;
import com.google.android.gms.tasks.TaskCompletionSource;
/* compiled from: com.google.android.gms:play-services-location@@18.0.0 */
/* loaded from: classes3.dex */
abstract class zzap implements RemoteCall<com.google.android.gms.internal.location.zzaz, TaskCompletionSource<Boolean>> {
    private boolean zza = true;

    public final boolean zza() {
        return this.zza;
    }

    public final void zzb(boolean z) {
        this.zza = false;
    }
}
