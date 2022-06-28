package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.wearable.MessageApi;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final /* synthetic */ class zzfd implements PendingResultUtil.ResultConverter {
    static final PendingResultUtil.ResultConverter zza = new zzfd();

    private zzfd() {
    }

    @Override // com.google.android.gms.common.internal.PendingResultUtil.ResultConverter
    public final Object convert(Result result) {
        return Integer.valueOf(((MessageApi.SendMessageResult) result).getRequestId());
    }
}
