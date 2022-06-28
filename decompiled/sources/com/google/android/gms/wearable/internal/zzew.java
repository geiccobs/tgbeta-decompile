package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation;
import java.lang.ref.WeakReference;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public final class zzew<T> extends zzgx<Status> {
    private final WeakReference<Map<T, zzia<T>>> zza;
    private final WeakReference<T> zzb;

    public zzew(Map<T, zzia<T>> map, T t, BaseImplementation.ResultHolder<Status> resultHolder) {
        super(resultHolder);
        this.zza = new WeakReference<>(map);
        this.zzb = new WeakReference<>(t);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.wearable.internal.zza, com.google.android.gms.wearable.internal.zzeq
    public final void zzx(Status status) {
        Map<T, zzia<T>> map = this.zza.get();
        T t = this.zzb.get();
        if (status.getStatus().getStatusCode() == 4002 && map != null && t != null) {
            synchronized (map) {
                zzia<T> remove = map.remove(t);
                if (remove != null) {
                    remove.zzq();
                }
            }
        }
        zzF(status);
    }
}
