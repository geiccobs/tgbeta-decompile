package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.BaseImplementation;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.wearable.DataItemBuffer;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzhi extends zzgx<DataItemBuffer> {
    public zzhi(BaseImplementation.ResultHolder<DataItemBuffer> resultHolder) {
        super(resultHolder);
    }

    @Override // com.google.android.gms.wearable.internal.zza, com.google.android.gms.wearable.internal.zzeq
    public final void zzi(DataHolder dataHolder) {
        zzF(new DataItemBuffer(dataHolder));
    }
}
