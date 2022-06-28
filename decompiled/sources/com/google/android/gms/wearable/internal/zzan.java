package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.ChannelClient;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final /* synthetic */ class zzan implements PendingResultUtil.ResultConverter {
    static final PendingResultUtil.ResultConverter zza = new zzan();

    private zzan() {
    }

    @Override // com.google.android.gms.common.internal.PendingResultUtil.ResultConverter
    public final Object convert(Result result) {
        ChannelClient.Channel zzd;
        zzd = zzav.zzd(((ChannelApi.OpenChannelResult) result).getChannel());
        return zzd;
    }
}
