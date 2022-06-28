package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.BaseImplementation;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
class zzgx<T> extends zza {
    private BaseImplementation.ResultHolder<T> zza;

    public zzgx(BaseImplementation.ResultHolder<T> resultHolder) {
        this.zza = resultHolder;
    }

    public final void zzF(T t) {
        BaseImplementation.ResultHolder<T> resultHolder = this.zza;
        if (resultHolder != null) {
            resultHolder.setResult(t);
            this.zza = null;
        }
    }
}
