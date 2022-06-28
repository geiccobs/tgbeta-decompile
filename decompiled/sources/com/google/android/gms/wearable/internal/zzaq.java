package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.wearable.Channel;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final /* synthetic */ class zzaq implements PendingResultUtil.ResultConverter {
    static final PendingResultUtil.ResultConverter zza = new zzaq();

    private zzaq() {
    }

    @Override // com.google.android.gms.common.internal.PendingResultUtil.ResultConverter
    public final Object convert(Result result) {
        return ((Channel.GetInputStreamResult) result).getInputStream();
    }
}
